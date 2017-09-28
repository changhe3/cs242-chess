import models.Board;
import models.Piece;
import models.Player;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static util.Shorthand.pos;

public class OperationTest {

    private Board board;

    @Before
    public void setup() {
        board = Board.defaultBoard(Player.black(), Player.white());
    }

    @Test
    public void testAccept() {
        Piece piece = board.get(pos("c2"));
        Board.Operation move = Board.Operation.move(pos("c2"), pos("c4"));
        board.execute(move, board.WHITE);
        assertEquals(piece.getLocation(), pos("c4"));
        assertEquals(piece, board.get(pos("c4")));
        assertNull(board.get(pos("c2")));

        Board.Operation attack = Board.Operation.attack(pos("c4"), pos("c7"));
        Piece attacked = board.get(pos("c7"));
        board.execute(attack, board.WHITE);
        assertEquals(piece.getLocation(), pos("c7"));
        assertEquals(board.get(pos("c7")), piece);
        assertNull(board.get(pos("c4")));
        assertTrue(attacked.isCaptured());
    }

    @Test
    public void testReverse() {
        Board b0 = new Board(board);

        Piece piece = board.get(pos("c2"));
        Board.Operation move = Board.Operation.move(pos("c2"), pos("c4"));
        board.execute(move, board.WHITE);

        Board b1 = new Board(board);

        Board.Operation attack = Board.Operation.attack(pos("c4"), pos("c7"));
        Piece attacked = board.get(pos("c7"));
        board.execute(attack, board.WHITE);

        attack.reverse(board);
        assertEquals(b1, board);

        move.reverse(board);
        assertEquals(b0, board);
    }

}