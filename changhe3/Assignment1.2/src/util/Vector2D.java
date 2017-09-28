package util;

import java.awt.*;
import java.util.Objects;

public class Vector2D {

    public static Point negative(Point a) {
        return new Point(-a.x, -a.y);
    }

    public static Point add(Point a, Point b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return new Point(a.x + b.x, a.y + b.y);
    }

    public static Point subtract(Point a, Point b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return add(a, negative(b));
    }

    public static Point scalarMult(Point a, int s) {
        Objects.requireNonNull(a);
        return new Point(a.x * s, a.y * s);
    }

    public static Point scalarDiv(Point a, int s) {
        Objects.requireNonNull(a);
        return new Point(a.x / s, a.y / s);
    }
}
