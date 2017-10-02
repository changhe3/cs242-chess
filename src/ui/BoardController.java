package ui;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import models.Board;
import models.Player;
import util.Array;
import util.Pair;

import java.awt.*;
import java.net.URL;
import java.util.EmptyStackException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

public class BoardController implements Initializable {

    @FXML
    private Button forfeitButton;
    @FXML
    private Label scoreLabel;
    @FXML
    private Button resetButton;
    @FXML
    private Label resultLabel;
    @FXML
    private Button undoButton;
    @FXML
    private GridPane boardPane;
    @FXML
    private GridPane root;
    private Array<SlotController> slotControllers;
    private ObjectProperty<Point> selectedSlot;
    private MapProperty<Point, Set<Board.Operation>> allOperations;
    private SetProperty<Board.Operation> selectedOperations;
    private Board board;
    private Player firstPlayer;
    private ObjectProperty<Player> currentPlayer;
    private ObjectProperty<State> gameState;
    private ObservableMap<Player, Integer> scores;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reset();
    }

    public void init(Board board, Player firstPlayer, int blackScore, int whiteScore) throws Exception {
        this.board = board;
        this.firstPlayer = firstPlayer;
        this.currentPlayer = new SimpleObjectProperty<>(firstPlayer);

        slotControllers = new Array<>(board.N_COLS * board.N_ROWS);

        selectedSlot = new SimpleObjectProperty<>(null);

        allOperations = new SimpleMapProperty<>();
        allOperations.bind(Bindings.createObjectBinding(() -> board.generateMoves(getCurrentPlayer()).collect(
                Collectors.collectingAndThen(
                        Collectors.groupingBy(op -> op.FROM, Collectors.toSet()),
                        FXCollections::<Point, Set<Board.Operation>>observableMap)
        ), currentPlayer));

        selectedOperations = new SimpleSetProperty<>();
        selectedOperations.bind(Bindings.createObjectBinding(
                () -> {
                    final Set<Board.Operation> operations = allOperations.get(getSelectedSlot());
                    return operations == null ? FXCollections.observableSet() : FXCollections.observableSet(operations);
                },
                allOperations,
                selectedSlot
        ));

        gameState = new SimpleObjectProperty<>(State.NONE);
        gameState.bind(Bindings.createObjectBinding(() -> {
            if (!allOperations.isEmpty()) {
                return State.NONE;
            } else {
                if (board.inCheck(getCurrentPlayer())) {
                    final Player winner = board.theOther(getCurrentPlayer());
                    scores.put(winner, scores.get(winner) + 1);
                    return State.CHECKMATE;
                } else {
                    return State.STALEMATE;
                }
            }
        }, allOperations, currentPlayer));

        scores = FXCollections.observableHashMap();
        scores.put(board.WHITE, whiteScore);
        scores.put(board.BLACK, blackScore);

        for (int i = 0; i < board.N_ROWS; i++) {
            Node[] node = new Node[board.N_COLS];
            for (int j = 0; j < board.N_COLS; j++) {
                final FXMLLoader loader = new FXMLLoader(getClass().getResource("slot.fxml"));
                node[j] = loader.load();

                GridPane.setHalignment(node[j], HPos.CENTER);
                GridPane.setValignment(node[j], VPos.CENTER);
                final SlotController controller = loader.getController();

                Point boardLoc = new Point(j, board.N_ROWS - i - 1);
                controller.init(board, boardLoc, this);
                slotControllers.set(board.coord(boardLoc), controller);
            }
            this.boardPane.addRow(i, node);
        }

        resultLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            switch (gameState.get()) {
                case NONE:
                    return "In Progress";
                case CHECKMATE:
                    return String.format("%s checkmated!", currentPlayer.get());
                case STALEMATE:
                    return "Stalemate!";
                default:
                    throw new IllegalStateException();
            }
        }, gameState));


        resetButton.setOnMouseClicked(event ->
                reset(Player.black(board.BLACK.ID), Player.white(board.WHITE.ID), scores.get(board.BLACK), scores.get(board.WHITE)));
        forfeitButton.setOnMouseClicked(event -> {
            final Player winner = board.theOther(getCurrentPlayer());
            scores.put(winner, scores.get(winner) + 2);
            reset(Player.black(board.BLACK.ID), Player.white(board.WHITE.ID), scores.get(board.BLACK), scores.get(board.WHITE));
        });
        undoButton.setOnMouseClicked(event -> undo());
        final StringBinding isBlackTurn = Bindings.createStringBinding(() -> getCurrentPlayer().equals(board.BLACK) ? "★" : "", currentPlayer);
        final StringBinding isWhiteTurn = Bindings.createStringBinding(() -> getCurrentPlayer().equals(board.WHITE) ? "★" : "", currentPlayer);
        scoreLabel.textProperty().bind(Bindings.format("%s%s: %d\n%s%s: %d",
                isBlackTurn, board.BLACK, Bindings.valueAt(scores, board.BLACK),
                isWhiteTurn, board.WHITE, Bindings.valueAt(scores, board.WHITE)
        ));
    }

    public SlotController getSlotController(Point point) {
        return slotControllers.get(board.coord(point));
    }

    public Point getSelectedSlot() {
        return selectedSlot.get();
    }

    public void setSelectedSlot(Point selectedSlot) {
        this.selectedSlot.set(selectedSlot);
    }

    public ObjectProperty<Point> selectedSlotProperty() {
        return selectedSlot;
    }

    public SetProperty<Board.Operation> selectedOperationsProperty() {
        return selectedOperations;
    }

    public ObservableSet<Board.Operation> getSelectedOperations() {
        return selectedOperations.get();
    }

    public Player getCurrentPlayer() {
        return currentPlayer.get();
    }

    public ObjectProperty<Player> currentPlayerProperty() {
        return currentPlayer;
    }

    public void nextTurn() {
        currentPlayer.set(board.theOther(currentPlayer.get()));
    }

    private void reset() {
        final Player black = Player.black();
        final Player white = Player.white();
        reset(black, white, 0, 0);
    }

    private void reset(Player black, Player white, int blackScore, int whiteScore) {
        this.boardPane.getChildren().clear();
        try {
            init(Board.defaultBoard(black, white), white, blackScore, whiteScore);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void undo() {
        final Pair<Board.Operation, Player> lastOp;
        try {
            lastOp = board.getHistory().pop();
        } catch (EmptyStackException e) {
            return;
        }
        lastOp.first.reverse(board);
        currentPlayer.set(lastOp.second);
        setSelectedSlot(null);
    }

    public enum State {
        NONE, CHECKMATE, STALEMATE
    }
}
