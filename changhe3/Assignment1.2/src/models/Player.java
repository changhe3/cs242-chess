package models;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * A class storing states for players. After each game, the these states must be reset by calling Player.resetAll()
 */
public abstract class Player implements UnaryOperator<Point> {

    public final String ID;
    private final List<Piece> pieces;
    private int turnCount;
    private Piece king;

    Player(final String id) {
        turnCount = 1;
        pieces = new ArrayList<>();
        king = null;
        ID = id;
    }

    public static Player black() {
        return new Black();
    }

    public static Player black(String id) {
        return new Black(id);
    }

    public static Player white() {
        return new White();
    }

    public static Player white(String id) {
        return new White(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Player player = (Player) o;
        return Objects.equals(ID, player.ID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }

    /**
     * Increment the turnCount for this player
     */
    public void increment() {
        turnCount++;
    }

    /**
     * @return the turnCount for this player
     */
    public int getTurnCount() {
        return turnCount;
    }

    /**
     * @return the king of this player
     */
    public Piece getKing() {
        return king;
    }

    /**
     * @return a stream of all pieces of this player, excluding those that have been captured
     */
    public Stream<Piece> getPieces() {
        return getPieces(false);
    }

    /**
     * @param includesCaptured whether or not to include the capture pieces
     * @return a stream of all pieces of this player, excluding those that have been captured if includeCaptured is true
     */
    public Stream<Piece> getPieces(boolean includesCaptured) {
        return pieces.stream().filter(piece -> includesCaptured || !piece.isCaptured());
    }

    /**
     * Register a piece under a player. Note that this method is automatically called in the constructor of Piece, so user should not call this method.
     *
     * @param piece a piece
     */
    void registerPiece(Piece piece) {
        if (piece.TYPE == PieceTypes.KING) {
            king = piece;
        }
        pieces.add(piece);
    }

    public abstract int getAvatarId();

    public static final class Black extends Player {

        public Black() {
            this("b");
        }

        public Black(String id) {
            super(id);
        }

        @Override
        public Point apply(Point point) {
            return new Point(point.x, -point.y);
        }

        @Override
        public String toString() {
            return ID;
        }

        @Override
        public int getAvatarId() {
            return 0;
        }
    }

    public static final class White extends Player {

        public White() {
            this("w");
        }

        public White(String id) {
            super(id);
        }

        @Override
        public Point apply(Point point) {
            return point;
        }

        @Override
        public String toString() {
            return ID;
        }

        @Override
        public int getAvatarId() {
            return 1;
        }
    }
}
