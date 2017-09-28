package models;

import util.Array;

import java.util.stream.Stream;

import static util.Shorthand.pt;

/**
 * An implementation of the types of piece in normal chess games, including KING, PAWN, KNIGHT, ROOK, BISHOP, QUEEN
 */
public enum PieceTypes implements PieceType {

    /**
     * the movement rule of a King, allowed to move and attack to the eight squares around it.
     */
    KING(
            MovementRule.create(pt(0, 1)).setMaxMoves(1).finish(),
            MovementRule.create(pt(1, 0)).setMaxMoves(1).finish(),
            MovementRule.create(pt(0, -1)).setMaxMoves(1).finish(),
            MovementRule.create(pt(-1, 0)).setMaxMoves(1).finish(),
            MovementRule.create(pt(1, 1)).setMaxMoves(1).finish(),
            MovementRule.create(pt(-1, 1)).setMaxMoves(1).finish(),
            MovementRule.create(pt(1, -1)).setMaxMoves(1).finish(),
            MovementRule.create(pt(-1, -1)).setMaxMoves(1).finish()
    ) {
        @Override
        public String toString() {
            return "K";
        }
    },
    /**
     * the movement rule of a Pawn, allowed to move forward and attack toward either left or right of the location in front of it
     */
    PAWN(
            MovementRule.create(pt(0, 1)).setMaxMoves(2)
                    .precondition((board, point, piece) -> piece.getNumOperations() == 0).disallowAttack().finish(),
            MovementRule.create(pt(0, 1)).setMaxMoves(1)
                    .precondition((board, point, piece) -> piece.getNumOperations() > 0).disallowAttack().finish(),
            MovementRule.create(pt(1, 1)).setMaxMoves(1).disallowMove().finish(),
            MovementRule.create(pt(-1, 1)).setMaxMoves(1).disallowMove().finish()
    ) {
        @Override
        public String toString() {
            return "P";
        }
    },
    /**
     * the movement rule of a Knight, allowed to move in L shape, aka, one block and then two blocks in a perpendicular direction
     */
    KNIGHT(
            MovementRule.create(pt(1, 2)).setMaxMoves(1).finish(),
            MovementRule.create(pt(-1, 2)).setMaxMoves(1).finish(),
            MovementRule.create(pt(1, -2)).setMaxMoves(1).finish(),
            MovementRule.create(pt(-1, -2)).setMaxMoves(1).finish(),
            MovementRule.create(pt(2, 1)).setMaxMoves(1).finish(),
            MovementRule.create(pt(-2, 1)).setMaxMoves(1).finish(),
            MovementRule.create(pt(2, -1)).setMaxMoves(1).finish(),
            MovementRule.create(pt(-2, -1)).setMaxMoves(1).finish()
    ) {
        @Override
        public String toString() {
            return "N";
        }
    },
    /**
     * the movement rule of a Rook, allowed to move infinitely forward, backward, left and right
     */
    ROOK(
            MovementRule.create(pt(1, 0)).finish(),
            MovementRule.create(pt(0, 1)).finish(),
            MovementRule.create(pt(-1, 0)).finish(),
            MovementRule.create(pt(0, -1)).finish()
    ) {
        @Override
        public String toString() {
            return "R";
        }
    },
    /**
     * the movement rule of a Bishop, allowed to move infinitely diagonally
     */
    BISHOP(
            MovementRule.create(pt(1, 1)).finish(),
            MovementRule.create(pt(-1, 1)).finish(),
            MovementRule.create(pt(1, -1)).finish(),
            MovementRule.create(pt(-1, -1)).finish()
    ) {
        @Override
        public String toString() {
            return "B";
        }
    },
    /**
     * the movement rule of a Queen, with combined rules of bishop and rook.
     */
    QUEEN(
            Stream.concat(BISHOP.getMovementRules(), ROOK.getMovementRules())
    ) {
        @Override
        public String toString() {
            return "Q";
        }
    },


    /**
     * Allowed to attack every second turn forward, back, and diagonally for at most 5 slots while skipping pieces, but can only move one slot
     */
    ARTILLERY (
            MovementRule.create(pt(1, 0)).allowSkipping(true).precondition((pieces, point, piece) -> piece.PLAYER.getTurnCount() % 2 == 0).disallowMove().setMaxMoves(5).finish(),
            MovementRule.create(pt(0, 1)).allowSkipping(true).precondition((pieces, point, piece) -> piece.PLAYER.getTurnCount() % 2 == 0).disallowMove().setMaxMoves(5).finish(),
            MovementRule.create(pt(-1, 0)).allowSkipping(true).precondition((pieces, point, piece) -> piece.PLAYER.getTurnCount() % 2 == 0).disallowMove().setMaxMoves(5).finish(),
            MovementRule.create(pt(0, -1)).allowSkipping(true).precondition((pieces, point, piece) -> piece.PLAYER.getTurnCount() % 2 == 0).disallowMove().setMaxMoves(5).finish(),
            MovementRule.create(pt(1, 1)).allowSkipping(true).precondition((pieces, point, piece) -> piece.PLAYER.getTurnCount() % 2 == 0).disallowMove().setMaxMoves(5).finish(),
            MovementRule.create(pt(-1, 1)).allowSkipping(true).precondition((pieces, point, piece) -> piece.PLAYER.getTurnCount() % 2 == 0).disallowMove().setMaxMoves(5).finish(),
            MovementRule.create(pt(1, -1)).allowSkipping(true).precondition((pieces, point, piece) -> piece.PLAYER.getTurnCount() % 2 == 0).disallowMove().setMaxMoves(5).finish(),
            MovementRule.create(pt(-1, -1)).allowSkipping(true).precondition((pieces, point, piece) -> piece.PLAYER.getTurnCount() % 2 == 0).disallowMove().setMaxMoves(5).finish(),
            MovementRule.create(pt(1, 0)).disallowAttack().setMaxMoves(1).finish(),
            MovementRule.create(pt(0, 1)).disallowAttack().setMaxMoves(1).finish(),
            MovementRule.create(pt(-1, 0)).disallowAttack().setMaxMoves(1).finish(),
            MovementRule.create(pt(0, -1)).disallowAttack().setMaxMoves(1).finish(),
            MovementRule.create(pt(1, 1)).disallowAttack().setMaxMoves(1).finish(),
            MovementRule.create(pt(-1, 1)).disallowAttack().setMaxMoves(1).finish(),
            MovementRule.create(pt(1, -1)).disallowAttack().setMaxMoves(1).finish(),
            MovementRule.create(pt(-1, -1)).disallowAttack().setMaxMoves(1).finish()
    ) {
        @Override
        public String toString() {
            return "C";
        }
    },

    /**
     * Allowed to attack and move up to 2 slots forward, forward-left, forward right
     */
    ARCHER (
            MovementRule.create(pt(0, 1)).allowSkipping(true).setMaxMoves(2).finish(),
            MovementRule.create(pt(1, 1)).allowSkipping(true).setMaxMoves(2).finish(),
            MovementRule.create(pt(-1, 1)).allowSkipping(true).setMaxMoves(2).finish()
    ) {
        @Override
        public String toString() {
            return "A";
        }
    },
    ;

    private final Array<MovementRule> moves;

    PieceTypes(MovementRule... rules) {
        this.moves = new Array<>(rules);
    }

    PieceTypes(Stream<MovementRule> rules) {
        this.moves = new Array<>(rules.toArray(MovementRule[]::new));
    }

    /**
     * @return a stream of MovementRules this type of piece
     */
    @Override
    public Stream<MovementRule> getMovementRules() {
        return moves.stream();
    }
}
