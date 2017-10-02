package models;

import util.ObservableArray;
import util.Pair;

import java.awt.*;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static util.Shorthand.*;

/**
 * A data structure representing a chess board by storing instances of Piece in an array. The coordinate system
 * used starts with 0 from bottom left and has up as positive-y and right as positive-x
 *
 * @author changhe3
 */
public final class Board extends ObservableArray<Piece> implements AutoCloseable {

    public final Map<PieceType, String[]> pieceResourcePaths;

    /**
     * the number of columns of the chess board
     */
    public final int N_COLS;
    /**
     * the number of rows of the chess board
     */
    public final int N_ROWS;

    public final Player BLACK;
    public final Player WHITE;

    private final Stack<Pair<Operation, Player>> history = new Stack<>();

    /**
     * Construct a Board instance with n_COLS columns and n_ROWS rows
     *
     * @param n_COLS the number of columns of the constructed board
     * @param n_ROWS the number of rows of the constructed board
     */
    public Board(final int n_COLS, final int n_ROWS, final Player black, final Player white) {
        this(n_COLS, n_ROWS, black, white, Map.of(
                PieceTypes.KING, arr("assets/bk.png", "assets/wk.png"),
                PieceTypes.BISHOP, arr("assets/bb.png", "assets/wb.png"),
                PieceTypes.ROOK, arr("assets/br.png", "assets/wr.png"),
                PieceTypes.KNIGHT, arr("assets/bn.png", "assets/wn.png"),
                PieceTypes.PAWN, arr("assets/bp.png", "assets/wp.png"),
                PieceTypes.QUEEN, arr("assets/bq.png", "assets/wq.png")
        ));
    }

    /**
     * Construct a Board instance with n_COLS columns and n_ROWS rows
     *
     * @param n_COLS             the number of columns of the constructed board
     * @param n_ROWS             the number of rows of the constructed board
     * @param pieceResourcePaths the path to the avatars of pieces
     */
    public Board(final int n_COLS, final int n_ROWS, final Player black, final Player white, Map<PieceType, String[]> pieceResourcePaths) {
        super(n_COLS * n_ROWS);
        N_COLS = n_COLS;
        N_ROWS = n_ROWS;
        BLACK = black;
        WHITE = white;
        this.pieceResourcePaths = pieceResourcePaths;
    }

    /**
     * Construct a copy of another Board instance.
     * Note that individual instances of Piece in the parent class is shallow copied, aka. share the same references.
     *
     * @param board the Board instance to be copied
     */
    public Board(Board board) {
        this(board, Map.of(
                PieceTypes.KING, arr("assets/bk.png", "assets/wk.png"),
                PieceTypes.BISHOP, arr("assets/bb.png", "assets/wb.png"),
                PieceTypes.ROOK, arr("assets/br.png", "assets/wr.png"),
                PieceTypes.KNIGHT, arr("assets/bn.png", "assets/wn.png"),
                PieceTypes.PAWN, arr("assets/bp.png", "assets/wp.png"),
                PieceTypes.QUEEN, arr("assets/bq.png", "assets/wq.png")
        ));
    }

    /**
     * Construct a copy of another Board instance.
     * Note that individual instances of Piece in the parent class is shallow copied, aka. share the same references.
     *
     * @param board              the Board instance to be copied
     * @param pieceResourcePaths the path to the avatars of pieces
     */
    public Board(Board board, Map<PieceType, String[]> pieceResourcePaths) {
        super(board);
        N_ROWS = board.N_ROWS;
        N_COLS = board.N_COLS;
        BLACK = board.BLACK;
        WHITE = board.WHITE;
        this.pieceResourcePaths = pieceResourcePaths;
    }

    /**
     * @return a instance of Board with the default configuration of chess
     */
    public static Board defaultBoard(Player black, Player white) {
        Board ret = new Board(8, 8, black, white);
        for (int x = 0; x < 8; x++) {
            ret.addPiece(PieceTypes.PAWN, white, pt(x, 1));
            ret.addPiece(PieceTypes.PAWN, black, pt(x, 6));
        }
        for (int x = 0; x < 8; x++) {
            if (x == 0 || x == 7) {
                ret.addPiece(PieceTypes.ROOK, white, pt(x, 0));
                ret.addPiece(PieceTypes.ROOK, black, pt(x, 7));
            } else if (x == 1 || x == 6) {
                ret.addPiece(PieceTypes.KNIGHT, white, pt(x, 0));
                ret.addPiece(PieceTypes.KNIGHT, black, pt(x, 7));
            } else if (x == 2 || x == 5) {
                ret.addPiece(PieceTypes.BISHOP, white, pt(x, 0));
                ret.addPiece(PieceTypes.BISHOP, black, pt(x, 7));
            } else if (x == 4) {
                ret.addPiece(PieceTypes.KING, white, pt(x, 0));
                ret.addPiece(PieceTypes.KING, black, pt(x, 7));
            } else {
                ret.addPiece(PieceTypes.QUEEN, white, pt(x, 0));
                ret.addPiece(PieceTypes.QUEEN, black, pt(x, 7));
            }
        }

        return ret;
    }

    /**
     * Get the rival of a player
     *
     * @param player a player
     * @return the rival of player
     */
    public Player theOther(Player player) {
        if (player.equals(BLACK)) {
            return WHITE;
        } else {
            return BLACK;
        }
    }

    /**
     * Check whether a point is a valid location
     *
     * @param p the point to be checked
     * @return whether point p is a valid location on this instance of Board
     */
    public boolean inBound(Point p) {
        return 0 <= p.x && p.x < N_COLS && 0 <= p.y && p.y < N_ROWS;
    }

    /**
     * Execute an operation on this board, while increment the turn count for that player
     *
     * @param op     the operation
     * @param player the player from which this operation op executes
     */
    public void execute(Operation op, Player player) {
        player.increment();
        assert getOptional(op.FROM).filter(piece -> piece.PLAYER == player).isPresent();
        op.accept(this);
        history.push(Pair.of(op, player));
    }

    /**
     * Execute a group of operations, used only for testing
     *
     * @param moves a Stream of Pairs of Operations and the Players from which they operates
     */
    public void execute(Stream<Pair<Operation, Player>> moves) {
        moves.forEach(move -> execute(move.first, move.second));
    }

    /**
     * Add a new Piece to the current Board
     *
     * @param TYPE     the type of Piece to be added
     * @param PLAYER   the player where the Piece belongs
     * @param location the initial location of the Piece
     */
    public void addPiece(PieceType TYPE, Player PLAYER, Point location) {
        set(location, new Piece(TYPE, PLAYER, location));
    }

    // unmanaged operations, pieces' coordinates will not be updated

    /**
     * Get the piece at a certain location
     *
     * @param p a location
     * @return the piece at location p, null if there is none
     */
    public Piece get(Point p) {
        return get(coord(p));
    }

    private Piece set(Point p, Piece piece) {
        return set(coord(p), piece);
    }

    /**
     * Get the piece at a certain location
     *
     * @param x the x coordinate of that location
     * @param y the y coordinate of that location
     * @return the piece at location pt(x, y), null if there is none
     */
    public Piece get(int x, int y) {
        return get(coord(x, y));
    }

    private Piece set(int x, int y, Piece piece) {
        return set(coord(x, y), piece);
    }

    /**
     * Same as get(Point) but encapsulate it with an Optional<Piece> instead
     */
    public Optional<Piece> getOptional(Point p) {
        return Optional.ofNullable(get(p));
    }

    /**
     * Same as get(int, int) but encapsulate it with an Optional<Piece> instead
     */
    public Optional<Piece> getOptional(int x, int y) {
        return Optional.ofNullable(get(x, y));
    }

    /**
     * Remove the piece at a location
     *
     * @param p a location
     * @return the removed piece at location p, null if none removed
     */
    public Piece remove(Point p) {
        return super.remove(coord(p));
    }

    /**
     * Convert the coordinate system used in this class to the 0-based index used in the internal array representation
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return a 0-based index
     */
    public int coord(int x, int y) {
        return y * N_COLS + x;
    }

    /**
     * Same as the coord(int, int), using a java.awt.Point as parameter instead
     */
    public int coord(Point p) {
        return coord(p.x, p.y);
    }

    /**
     * Generate all available moves on a piece at location p that will not put the piece's owner in check
     *
     * @param p a location
     * @return all such moves
     */
    public Stream<Operation> generateMoves(Point p) {
        Optional<Piece> piece = getOptional(p);
        return piece.stream().flatMap(piece_ -> piece_.TYPE.generateMoves(this, p, piece_));
    }


    /**
     * Generate all available moves for a piece
     *
     * @param piece       a piece
     * @param checkChecks whether or not to check checks.
     * @return If checkChecks is true, then all moves that will put piece's owner in check are discarded, other wise return all available moves
     */
    public Stream<Operation> generateMoves(Piece piece, boolean checkChecks) {
        return piece.TYPE.generateMoves(this, piece.getLocation(), piece, checkChecks);
    }

    /**
     * Generate all available moves for a player
     *
     * @param player a player
     * @return all available moves except for those that will put player in check at next turn
     */
    public Stream<Operation> generateMoves(Player player) {
        return generateMoves(player, true);
    }

    /**
     * Generate all available moves for a player
     *
     * @param player      a player
     * @param checkChecks whether or not to check checks
     * @return all available moves if checkChecks is false, otherwise, excluding those moves that will put player in check
     */
    public Stream<Operation> generateMoves(Player player, boolean checkChecks) {
        return player.getPieces().flatMap(piece -> this.generateMoves(piece, checkChecks));
    }

    /**
     * @param player a player
     * @return whether player is in check or not
     */
    public boolean inCheck(Player player) {
        Player enemy = theOther(player);
        return generateMoves(enemy, false)
                .anyMatch(op -> op.getClass() == Operation.Attack.class && op.TO.equals(player.getKing().getLocation()));
    }

    public Stack<Pair<Operation, Player>> getHistory() {
        return history;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Board pieces = (Board) o;
        return N_COLS == pieces.N_COLS &&
                N_ROWS == pieces.N_ROWS;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), N_COLS, N_ROWS);
    }

    @Override
    public void close() {

    }

    /**
     * @return a string representation for the board
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x < 8; x++) {
                Optional<Piece> pieceOptional = getOptional(x, y);
                builder.append(pieceOptional.map(piece -> String.format("%s%s%s\t", piece.PLAYER, piece.TYPE, toChessNotation(piece.getLocation()))).orElse("----\t"));
            }
            builder.append("\n\n");
        }
        return builder.toString();
    }

    /**
     * A callback representing an operation on a board
     */
    public abstract static class Operation implements Consumer<Board> {

        /**
         * the source of the operation
         */
        public final Point FROM;
        /**
         * the destination of the operation
         */
        public final Point TO;


        /**
         * Construct an instance of Operation
         */
        protected Operation(Point from, Point to) {
            this.FROM = from;
            this.TO = to;
        }

        /**
         * Create an instance of Operation.Move
         *
         * @param from the original location
         * @param to   the location to be moved to
         * @return an operation of moving a piece at location from to location to
         */
        public static Move move(Point from, Point to) {
            return new Move(from, to);
        }

        /**
         * Create an instance of Operation.Attack
         *
         * @param from the original location
         * @param to   the location to be attacked to
         * @return an operation of attack a piece at location to with a piece at location from
         */
        public static Attack attack(Point from, Point to) {
            return new Attack(from, to);
        }

        /**
         * Reverse an operation on a board. Note that this should only be called immediately after executing the operation.
         *
         * @param board the board
         */
        public abstract void reverse(Board board);

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Operation operation = (Operation) o;
            return Objects.equals(FROM, operation.FROM) &&
                    Objects.equals(TO, operation.TO);
        }

        @Override
        public int hashCode() {
            return Objects.hash(FROM, TO);
        }

        public static class Move extends Operation {

            private final long Uid = 12341323332L;

            private Move(Point from, Point to) {
                super(from, to);
            }

            /**
             * Move the piece at FROM to TO on a board
             *
             * @param board the board
             */
            @Override
            public void accept(Board board) {
                final Piece piece = board.remove(FROM);
                board.set(TO, piece);
                piece.setLocation(TO);
                piece.incrementNumOperations();
            }

            /**
             * Undo this movement on a board
             *
             * @param board the board
             */
            @Override
            public void reverse(Board board) {
                final Piece piece = board.remove(TO);
                board.set(FROM, piece);
                piece.setLocation(FROM);
                piece.decrementNumOperations();
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }
                if (!super.equals(o)) {
                    return false;
                }
                Move move = (Move) o;
                return Uid == move.Uid;
            }

            @Override
            public int hashCode() {
                return Objects.hash(super.hashCode(), Uid);
            }

            /**
             * @return A string representation of a movement using chess coordinate notation, see Shorthand.toChessNotation(Point)
             */
            @Override
            public String toString() {
                return String.format("%s -> %s", toChessNotation(FROM), toChessNotation(TO));
            }
        }

        public static class Attack extends Operation {

            private final long Uid = 324132345L;
            private Piece captured = null;

            private Attack(Point from, Point to) {
                super(from, to);
            }

            /**
             * Attack a piece at TO using a piece from FROM on a board
             *
             * @param board a board
             */
            @Override
            public void accept(Board board) {
                final Piece attacking = board.remove(FROM);
                attacking.setLocation(TO);
                captured = board.set(TO, attacking);
                captured.capture();
                attacking.incrementNumOperations();
            }

            /**
             * Undo this attack on a board
             *
             * @param board the board
             */
            @Override
            public void reverse(Board board) {
                final Piece attacking = board.remove(TO);
                board.set(FROM, attacking);
                attacking.setLocation(FROM);
                board.set(TO, captured);
                captured.uncapture(TO);
                attacking.decrementNumOperations();
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }
                if (!super.equals(o)) {
                    return false;
                }
                Attack attack = (Attack) o;
                return Uid == attack.Uid;
            }

            @Override
            public int hashCode() {
                return Objects.hash(super.hashCode(), Uid);
            }

            /**
             * @return A string representation of a movement using chess coordinate notation, see Shorthand.toChessNotation(Point)
             */
            @Override
            public String toString() {
                return String.format("%s -x %s", toChessNotation(FROM), toChessNotation(TO));
            }
        }
    }
}
