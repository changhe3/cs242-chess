package models;

import util.TriPredicate;
import util.Vector2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * An interface representing a type, aka. its behaviour, of a chess piece,
 */
public interface PieceType {

    /**
     * Generate all available moves for a piece excluding those that coudl put the player of self in check
     *
     * @param board   the board self is on
     * @param currLoc the current location of the piece
     * @param self    the piece itself
     * @return all such moves
     */
    default Stream<Board.Operation> generateMoves(Board board, Point currLoc, Piece self) {
        return generateMoves(board, currLoc, self, true);
    }

    /**
     * Generate all available moves
     *
     * @param board       the board self is on
     * @param currLoc     the current location of the piece
     * @param self        the piece itself
     * @param checkChecks whether or not to check check conditions
     * @return all available moves if checkChecks is false, otherwise, excluding those that could put the player of self in check
     */
    default Stream<Board.Operation> generateMoves(Board board, Point currLoc, Piece self, boolean checkChecks) {
        final Predicate<Board.Operation> checkFilter = checkChecks ? op -> {
            op.accept(board);
            Player enemy = board.theOther(self.PLAYER);
            boolean underCheck = board.generateMoves(enemy, false)
                    .anyMatch(o -> o.getClass() == Board.Operation.Attack.class && self.PLAYER.getKing().getLocation().equals(o.TO));
            op.reverse(board);
            return !underCheck;
        } : op -> true;
        return getMovementRules().flatMap(movementRule -> movementRule.generateMoves(board, currLoc, self)).distinct().filter(checkFilter);
    }

    /**
     * @return a stream of MovementRules
     */
    Stream<MovementRule> getMovementRules();

    /**
     * A class representing how a piece should move or attack
     */
    final class MovementRule {

        /**
         * the minimum unit of movement and its direction for a type of piece
         */
        public final Point UNIT_VEC;
        /**
         * the max amount of movements a type of piece can make in one turn
         */
        public final int MAX_MOVES;
        /**
         * whether skipping other pieces along the path is allowed
         */
        public final boolean SKIPPING;
        /**
         * prescribing whether a piece is allowed to attack and whether it is allowed move
         */
        public final Mode MODE;
        /**
         * Under what condition is this movementRule active
         */
        public final Precondition PRECONDITION;

        private MovementRule(Point UNIT_VEC, int MAX_MOVES, Mode MODE, boolean SKIPPING,
                             Precondition PRECONDITION) {
            this.UNIT_VEC = UNIT_VEC;
            this.MAX_MOVES = MAX_MOVES;
            this.SKIPPING = SKIPPING;
            this.MODE = MODE;
            this.PRECONDITION = PRECONDITION;
        }

        /**
         * Create a movementRule using the provided builder class
         *
         * @param unit_movement specify the minimum movement toward a particular direction
         * @return a builder for movementRule
         */
        public static Builder create(Point unit_movement) {
            return new Builder(unit_movement);
        }

        /**
         * @param board   the board self is on
         * @param currLoc the location of self
         * @param self    the piece itself
         * @return all available moves this rule can generate for a piece ignoring check conditions
         */
        public Stream<Board.Operation> generateMoves(Board board, Point currLoc, Piece self) {
            if (!PRECONDITION.test(board, currLoc, self)) {
                return Stream.empty();
            }
            List<Point> locations = new ArrayList<>();
            for (int i = 1; MAX_MOVES == -1 || i <= MAX_MOVES; i++) {
                Point unit_vec = self.PLAYER.apply(UNIT_VEC);
                Point loc = Vector2D.add(currLoc, Vector2D.scalarMult(unit_vec, i));
                if (!board.inBound(loc)) break;
                locations.add(loc);
                if (!SKIPPING && board.get(loc) != null) {
                    break;
                }
            }
            return locations.stream().flatMap(point -> {
                Piece res = board.get(point);
                // if the location is empty and if moving is allowed, move to that location.
                if (res == null) {
                    return MODE == Mode.ATTACK_ONLY ? Stream.empty() : Stream.of(Board.Operation.move(currLoc, point));
                }
                // if the location is occupied by an enemy's piece, attack
                else if (res.PLAYER != self.PLAYER) {
                    return MODE == Mode.MOVE_ONLY ? Stream.empty() : Stream.of(Board.Operation.attack(currLoc, point));
                }
                // if the location is occupied by a friendly piece, do nothing
                else {
                    return Stream.empty();
                }
            }).distinct();
        }

        /**
         * Specify whether a piece is allow to attack or move in a particular MovementRule
         */
        public enum Mode {
            /**
             * Allowed to either attack or move
             */
            UNRESTRICTED,
            /**
             * Allowed to move only
             */
            MOVE_ONLY,
            /**
             * Allowed to attack only
             */
            ATTACK_ONLY
        }

        /**
         * A alias for a predicate to determine whether this rule is active
         * given the board, the piece, and the current location of the piece
         */
        @FunctionalInterface
        public interface Precondition extends TriPredicate<Board, Point, Piece> {
        }

        /**
         * The builder class for MovementRule
         */
        public static class Builder {
            private final Point unit_vec;
            private int max_moves = -1;
            private Mode mode = Mode.UNRESTRICTED;
            private boolean skipping = false;
            private Precondition active_if = (board, point, piece) -> true;

            private Builder(Point unit_vec) {
                this.unit_vec = unit_vec;
            }

            /**
             * Set the maximum amount of MovementRule.UNIT_VEC this piece can move in a turn specified by this rule
             *
             * @param max_moves the maximum amount of unit movement a piece can have specified by this rule
             * @return the configured builder instance
             */
            public Builder setMaxMoves(int max_moves) {
                this.max_moves = max_moves;
                return this;
            }

            /**
             * Set the mode of this piece specified by this rule, see PieceType.Mode
             *
             * @param mode the mode of this piece specified by this rule
             * @return the configured builder instance
             */
            public Builder setMode(Mode mode) {
                this.mode = mode;
                return this;
            }

            /**
             * Set the piece to be allowed to move past an occupied location under this rule
             * @param skipping if skipping is allowed
             * @return the configured builder instance
             */
            public Builder allowSkipping(boolean skipping) {
                this.skipping = skipping;
                return this;
            }

            /**
             * Set the activation condition for this rule
             * @param active_if the callback that return true if this rule is applicable
             * @return the configured builder instance
             */
            public Builder precondition(Precondition active_if) {
                this.active_if = active_if;
                return this;
            }

            /**
             * Disallow the piece to attack under this rule
             * @return the configured builder instance
             */
            public Builder disallowAttack() {
                this.mode = Mode.MOVE_ONLY;
                return this;
            }

            /**
             * Disallow the piece to move with attacking under this rule
             * @return the configured builder instance
             */
            public Builder disallowMove() {
                this.mode = Mode.ATTACK_ONLY;
                return this;
            }

            /**
             * Finish off the building process
             * @return the finished rule instance
             */
            public MovementRule finish() {
                return new MovementRule(unit_vec, max_moves, mode, skipping, active_if);
            }
        }
    }
}
