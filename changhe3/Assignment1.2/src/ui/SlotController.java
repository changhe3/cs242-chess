package ui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import models.Board;
import models.Piece;
import models.Player;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class SlotController implements Initializable //, ListChangeListener<Piece>
{


    public ImageView img;
    private Board board;
    private Point boardLoc;
    private BoardController boardController;

    @FXML
    private Node root;

    private ObjectProperty<Status> status;
    private ObjectProperty<Piece> piece;


    private enum Status {
        SELECTED, MOVABLE, ATTACKABLE, NONE;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        img.setFitHeight(60);
        img.setFitWidth(60);
    }

    public void init(Board board, Point loc, BoardController controller) {
        this.board = board;
        this.boardController = controller;
        boardLoc = loc;
        if (Math.floorMod(boardLoc.x + boardLoc.y, 2) == 0) {
            root.getStyleClass().add("black");
        } else {
            root.getStyleClass().add("white");
        }

        status = new SimpleObjectProperty<>();
        final SetProperty<Board.Operation> operations = boardController.selectedOperationsProperty();
        final ObjectProperty<Point> selectedSlot = boardController.selectedSlotProperty();
        status.bind(Bindings.createObjectBinding(() -> {
            if (Objects.equals(selectedSlot.get(), boardLoc)) {
                return Status.SELECTED;
            } else if (operations.stream().anyMatch(op -> op.TO.equals(boardLoc))) {
                final Optional<Board.Operation> op = operations.stream().filter(ops -> ops.TO.equals(boardLoc)).findAny();
                assert op.isPresent();
                if (op.get().getClass() == Board.Operation.Attack.class) return Status.ATTACKABLE;
                else return Status.MOVABLE;
            } else {
                return Status.NONE;
            }
        }, operations, selectedSlot));
        status.addListener((observable, oldValue, newValue) -> {
            root.getStyleClass().remove(oldValue.toString());
            root.getStyleClass().add(newValue.toString());
        });

        piece = new SimpleObjectProperty<>();
        piece.bind(Bindings.valueAt(board, board.coord(boardLoc)));

        img.imageProperty().bind(Bindings.createObjectBinding(() -> this.render(piece.get()), piece));

        root.setOnMouseClicked(this::mouseClicked);
    }

    private void mouseClicked(MouseEvent mouseEvent) {
        final Player player = Optional.ofNullable(piece.get()).map(p -> p.PLAYER).orElse(null);
        switch (getStatus()) {
            case SELECTED:
                boardController.setSelectedSlot(null);
                break;
            case MOVABLE:
                board.execute(Board.Operation.move(boardController.getSelectedSlot(), boardLoc), boardController.getCurrentPlayer());
                boardController.nextTurn();
                boardController.setSelectedSlot(null);
                break;
            case ATTACKABLE:
                board.execute(Board.Operation.attack(boardController.getSelectedSlot(), boardLoc), boardController.getCurrentPlayer());
                boardController.nextTurn();
                boardController.setSelectedSlot(null);
                break;
            case NONE:
                if (Objects.equals(player, boardController.getCurrentPlayer())) boardController.setSelectedSlot(boardLoc);
                break;
        }
        mouseEvent.consume();
    }

    public Image loadImage(String pathStr) {
        try {
            Path path = FileSystems.getDefault().getPath(pathStr);
            return new Image(Files.newInputStream(path));
        } catch (IOException e) {
            throw new IllegalArgumentException("Image Not Found", e);
        }
    }

    public Image render(Piece piece) {
        if (piece == null) return null;
        return loadImage(board.pieceResourcePaths.get(piece.TYPE)[piece.PLAYER.getAvatarId()]);
    }

    public Status getStatus() {
        return status.get();
    }

    public ObjectProperty<Status> statusProperty() {
        return status;
    }
}

