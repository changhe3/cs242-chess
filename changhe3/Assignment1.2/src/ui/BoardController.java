package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import models.Board;
import models.PieceType;
import models.PieceTypes;
import models.Player;

import java.awt.*;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import static util.Shorthand.arr;

public class BoardController implements Initializable {

    public static final Map<PieceType, String[]> pieceResourcePaths = Map.of(
            PieceTypes.KING, arr("assets/bk.png", "assets/wk.png"),
            PieceTypes.BISHOP, arr("assets/bb.png", "assets/wb.png"),
            PieceTypes.ROOK, arr("assets/br.png", "assets/wr.png"),
            PieceTypes.KNIGHT, arr("assets/bn.png", "assets/wn.png"),
            PieceTypes.PAWN, arr("assets/bp.png", "assets/wp.png"),
            PieceTypes.QUEEN, arr("assets/bq.png", "assets/wq.png")
    );

    @FXML
    public javafx.scene.control.Button button;
    @FXML
    private GridPane board;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            setBoard(Board.defaultBoard(Player.black(), Player.white()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBoard(Board board) throws Exception {
        for (int i = 0; i < board.N_ROWS; i++) {
            Node[] node = new Node[board.N_COLS];
            for (int j = 0; j < board.N_COLS; j++) {
                final FXMLLoader loader = new FXMLLoader(getClass().getResource("slot.fxml"));
                node[j] = loader.load();
                if ((i + j) % 2 == 0) {
                    node[j].setStyle("-fx-background-color: #FFFFFF;");
                } else {
                    node[j].setStyle("-fx-background-color: #A9A9A9;");
                }
                GridPane.setHalignment(node[j], HPos.CENTER);
                GridPane.setValignment(node[j], VPos.CENTER);
                final SlotController controller = loader.getController();

                Point boardLoc = new Point(j, board.N_ROWS - i - 1);
                board.getOptional(boardLoc).ifPresent(piece -> {
                    String[] resources = pieceResourcePaths.get(piece.TYPE);
                    String res;
                    if (piece.PLAYER.ID.equals("b")) {
                        res = resources[0];
                    } else {
                        res = resources[1];
                    }
                    controller.loadImage(res);
                });
            }
            this.board.addRow(i, node);
        }
    }
}
