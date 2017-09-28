package models;

import util.Shorthand;

import java.awt.*;
import java.util.Objects;

/**
 * A class representing states of a piece
 */
public class Piece {

    /**
     * The type of this piece
     */
    public final PieceType TYPE;
    /**
     * The owner of this piece
     */
    public final Player PLAYER;


    private Point location;
    private boolean captured;
    private int nOps = 0;

    /**
     * Construct a piece
     *
     * @param TYPE     The type of the constructed piece
     * @param PLAYER   The owner of the constructed piece
     * @param location The initial location of the constructed piece
     */
    public Piece(PieceType TYPE, Player PLAYER, Point location) {
        Objects.requireNonNull(location);
        Objects.requireNonNull(PLAYER);
        Objects.requireNonNull(TYPE);
        this.TYPE = TYPE;
        this.PLAYER = PLAYER;
        this.location = location;
        captured = false;
        PLAYER.registerPiece(this);
    }

    /**
     * Return the current location of this piece
     *
     * @return the current location of this piece
     */
    public Point getLocation() {
        return location;
    }

    /**
     * Set the current location of this piece. Note that this should be avoided because calling this method does update
     * its location on the board. To do so, use board.execute(Operation, Player)
     * @param location the location this piece will be moved to
     */
    public void setLocation(Point location) {
        this.location = location;
    }

    /**
     * Set the state of this piece to be captured
     */
    public void capture() {
        captured = true;
        location = null;
    }

    /**
     * Undo the capture and reset this piece to location
     *
     * @param location a location this piece will be reset to
     */
    public void uncapture(Point location) {
        this.location = location;
        captured = false;
    }

    /**
     * Check whether the piece is captured
     * @return true if the piece is captured, false otherwise
     */
    public boolean isCaptured() {
        return captured;
    }

    /**
     * @return the number of operations that have been applied to this piece, aka. its movements and attacks to other pieces
     */
    public int getNumOperations() {
        return nOps;
    }

    /**
     * Increment the number of operations that have been applied to this piece.
     * See Piece.getNumOperations()
     */
    public void incrementNumOperations() {
        nOps++;
    }

    /**
     * Decrement the number of operations that have been applied to this piece.
     * See Piece.getNumOperations()
     */
    public void decrementNumOperations() {
        nOps--;
    }

    /**
     * Return a string representation of this piece.
     * @return a string representation of this piece, including its owner, type, and location
     */
    @Override
    public String toString() {
        return String.format("%s's %s at %s", PLAYER, TYPE, Shorthand.toChessNotation(location));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return captured == piece.captured &&
                Objects.equals(TYPE, piece.TYPE) &&
                PLAYER == piece.PLAYER &&
                Objects.equals(location, piece.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(TYPE, PLAYER, location, captured);
    }
}
