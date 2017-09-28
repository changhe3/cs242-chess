import models.Board;
import models.PieceTypes;
import models.Player;
import org.junit.Before;
import org.junit.Test;
import util.Pair;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static models.Board.Operation.attack;
import static models.Board.Operation.move;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static util.Shorthand.pos;

public class PieceTypesTest {

    private Board board;

    @Before
    public void setup() {
        board = Board.defaultBoard(Player.black(), Player.white());
    }

    @Test
    public void testPawn() {
        Set<Board.Operation> result = board.generateMoves(pos("a2")).collect(Collectors.toSet());
        Set<Board.Operation> expected = Set.of(
                Board.Operation.move(pos("a2"), pos("a3")),
                Board.Operation.move(pos("a2"), pos("a4"))
        );
        assertEquals(expected, result);

        board.execute(Board.Operation.move(pos("a2"), pos("a4")), board.WHITE);
        result = board.generateMoves(pos("a4")).collect(Collectors.toSet());
        expected = Set.of(Board.Operation.move(pos("a4"), pos("a5")));
        assertEquals(expected, result);

        board.execute(Board.Operation.move(pos("a4"), pos("a6")), board.WHITE);
        result = board.generateMoves(pos("a6")).collect(Collectors.toSet());
        expected = Set.of(Board.Operation.attack(pos("a6"), pos("b7")));
        assertEquals(expected, result);
    }

    @Test
    public void testPawnDiagonalAttack() {
        board.execute(Board.Operation.move(pos("c2"), pos("c6")), board.WHITE);
        Set<Board.Operation> result = board.generateMoves(pos("c6")).collect(Collectors.toSet()),
                expected = Set.of(
                        Board.Operation.attack(pos("c6"), pos("b7")),
                        Board.Operation.attack(pos("c6"), pos("d7"))
                );
        assertEquals(expected, result);
    }

    @Test
    public void testCheck() {
        board.execute(Board.Operation.move(pos("e1"), pos("e6")), board.WHITE);
        Set<Board.Operation> result = board.generateMoves(board.WHITE).collect(Collectors.toSet()),
                expected = Set.of(
                        Board.Operation.move(pos("e6"), pos("e5")),
                        Board.Operation.move(pos("e6"), pos("d5")),
                        Board.Operation.move(pos("e6"), pos("f5"))
                );
        assertTrue(board.inCheck(board.WHITE));
        assertEquals(expected, result);
    }

    @Test
    public void testCheckmate() {
        board.execute(Board.Operation.attack(pos("e8"), pos("c2")), board.BLACK);
        Set<Board.Operation> result = board.generateMoves(board.BLACK).collect(Collectors.toSet());
        assertTrue(board.inCheck(board.BLACK));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testPawn2() {
        List<Pair<Board.Operation, Player>> moves = List.of(
                Pair.of(move(pos("c2"), pos("c4")), board.WHITE),
                Pair.of(move(pos("d7"), pos("d5")), board.BLACK)
        );
        board.execute(moves.stream());
        Set<Board.Operation> actual = board.generateMoves(pos("c4")).collect(Collectors.toSet()),
                expected = Set.of(move(pos("c4"), pos("c5")), attack(pos("c4"), pos("d5")));
        assertEquals(expected, actual);
    }

    @Test
    public void testPawn3() {
        board.execute(move(pos("c2"), pos("c3")), board.WHITE);
        final Set<Board.Operation> actual = board.generateMoves(pos("d2")).collect(Collectors.toSet());
        final Set<Board.Operation.Move> expected = Set.of(
                move(pos("d2"), pos("d3")),
                move(pos("d2"), pos("d4"))
        );
        assertEquals(expected, actual);
    }

    @Test
    public void testStalemate() {
        Board board = new Board(8, 8, new Player.Black(), new Player.White());
        board.addPiece(PieceTypes.KING, board.BLACK, pos("a8"));
        board.addPiece(PieceTypes.BISHOP, board.BLACK, pos("b8"));
        board.addPiece(PieceTypes.ROOK, board.WHITE, pos("h8"));
        board.addPiece(PieceTypes.KING, board.WHITE, pos("b6"));
        assertTrue(!board.generateMoves(board.BLACK).findAny().isPresent());
        assertTrue(!board.inCheck(board.BLACK));
    }

    @Test
    public void testRook() {
        board.execute(move(pos("a1"), pos("a4")), board.WHITE);
        board.execute(move(pos("f7"), pos("f4")), board.BLACK);
        Set<Board.Operation> actual = board.generateMoves(pos("a4")).collect(Collectors.toSet()),
                expected = Set.of(
                        move(pos("a4"), pos("a3")),
                        move(pos("a4"), pos("a5")),
                        move(pos("a4"), pos("a6")),
                        attack(pos("a4"), pos("a7")),
                        move(pos("a4"), pos("b4")),
                        move(pos("a4"), pos("c4")),
                        move(pos("a4"), pos("d4")),
                        move(pos("a4"), pos("e4")),
                        attack(pos("a4"), pos("f4"))
                );
        assertEquals(expected, actual);
    }

    @Test
    public void testKnight() {
        board.execute(move(pos("g8"), pos("f3")), board.BLACK);
        final Set<Board.Operation> actual = board.generateMoves(pos("f3")).collect(Collectors.toSet()),
                expected = Set.of(
                        move(pos("f3"), pos("d4")),
                        move(pos("f3"), pos("e5")),
                        move(pos("f3"), pos("h4")),
                        move(pos("f3"), pos("g5")),
                        attack(pos("f3"), pos("d2")),
                        attack(pos("f3"), pos("e1")),
                        attack(pos("f3"), pos("h2")),
                        attack(pos("f3"), pos("g1"))
                );
        assertEquals(expected, actual);
    }

    @Test
    public void testBishop() {
        board.execute(move(pos("c1"), pos("g5")), board.WHITE);
        final Set<Board.Operation> actual = board.generateMoves(pos("g5")).collect(Collectors.toSet());
        final Set<Board.Operation> expected = Set.of(
                move(pos("g5"), pos("h6")),
                move(pos("g5"), pos("h4")),
                move(pos("g5"), pos("f6")),
                attack(pos("g5"), pos("e7")),
                move(pos("g5"), pos("f4")),
                move(pos("g5"), pos("e3"))
        );
        assertEquals(expected, actual);
    }

    @Test
    public void testQueen() {
        board.execute(move(pos("d1"), pos("e4")), board.WHITE);
        final Set<Board.Operation> actual = board.generateMoves(pos("e4")).collect(Collectors.toSet());
        final Set<Board.Operation> expected = Set.of(
                move(pos("e4"), pos("a4")),
                move(pos("e4"), pos("b4")),
                move(pos("e4"), pos("c4")),
                move(pos("e4"), pos("d4")),
                move(pos("e4"), pos("f4")),
                move(pos("e4"), pos("g4")),
                move(pos("e4"), pos("h4")),
                move(pos("e4"), pos("e3")),
                move(pos("e4"), pos("e5")),
                move(pos("e4"), pos("e6")),
                attack(pos("e4"), pos("e7")),
                move(pos("e4"), pos("d3")),
                move(pos("e4"), pos("f5")),
                move(pos("e4"), pos("g6")),
                attack(pos("e4"), pos("h7")),
                move(pos("e4"), pos("f3")),
                move(pos("e4"), pos("d5")),
                move(pos("e4"), pos("c6")),
                attack(pos("e4"), pos("b7"))
        );
        assertEquals(expected, actual);
    }

    @Test
    public void testArtillery() {
        board.addPiece(PieceTypes.ARTILLERY, board.WHITE, pos("c5"));
        final Set<Board.Operation> expected = board.generateMoves(pos("c5")).collect(Collectors.toSet());
        final Set<Board.Operation> actual = Set.of(
                move(pos("c5"), pos("c6")),
                move(pos("c5"), pos("b6")),
                move(pos("c5"), pos("d6")),
                move(pos("c5"), pos("d5")),
                move(pos("c5"), pos("d4")),
                move(pos("c5"), pos("c4")),
                move(pos("c5"), pos("b4")),
                move(pos("c5"), pos("b5"))
        );
        assertEquals(expected, actual);
        board.WHITE.increment();
        final Set<Board.Operation> expected2 = board.generateMoves(pos("c5")).collect(Collectors.toSet());
        final Set<Board.Operation> actual2 = Set.of(
                move(pos("c5"), pos("c6")),
                move(pos("c5"), pos("b6")),
                move(pos("c5"), pos("d6")),
                move(pos("c5"), pos("d5")),
                move(pos("c5"), pos("d4")),
                move(pos("c5"), pos("c4")),
                move(pos("c5"), pos("b4")),
                move(pos("c5"), pos("b5")),
                attack(pos("c5"), pos("a7")),
                attack(pos("c5"), pos("c7")),
                attack(pos("c5"), pos("c8")),
                attack(pos("c5"), pos("e7")),
                attack(pos("c5"), pos("f8"))
        );
        assertEquals(expected2, actual2);
    }

    @Test
    public void testArcher() {
        board.addPiece(PieceTypes.ARCHER, board.WHITE, pos("c5"));
        final Set<Board.Operation> expect = board.generateMoves(pos("c5")).collect(Collectors.toSet());
        final Set<Board.Operation> actual = Set.of(
                move(pos("c5"), pos("c6")),
                move(pos("c5"), pos("b6")),
                move(pos("c5"), pos("d6")),
                attack(pos("c5"), pos("c7")),
                attack(pos("c5"), pos("a7")),
                attack(pos("c5"), pos("e7"))
        );
        assertEquals(expect, actual);
    }
}
