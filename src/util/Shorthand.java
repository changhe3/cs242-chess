package util;

import java.awt.*;

public class Shorthand {

    public static Point pos(String at) {
        assert at.length() == 2;
        return new Point(at.charAt(0) - 'a', at.charAt(1) - '1');
    }

    public static Point pt(int x, int y) {
        return new Point(x, y);
    }

    /**
     * Represent a point using chess coordinate notation. For larger coordinates on x axis, a-z are used as 26-radix number
     */
    public static String toChessNotation(Point pt) {
        // TODO: implement transformations for larger coordinates
        return String.format("%s%s", ((char) ('a' + pt.x)), ((char) ('1' + pt.y)));
    }

    //https://stackoverflow.com/a/6083688/4261254
    @SafeVarargs
    public static <A> A[] arr(A... elements) {
        return elements;
    }
}
