package util;

import java.lang.reflect.Array;
import java.util.Collection;

public class Utilities {

    public static <T> void swap(final T[] array, final int i, final int j) {
        final T atI = array[i];
        array[i] = array[j];
        array[j] = atI;
    }


    /**
     * from https://stackoverflow.com/a/45449889/4261254
     *
     * @param array
     * @param i
     * @param j
     */
    public static void swap_ref(final Object array, final int i, final int j) {
        final Object atI = Array.get(array, i);
        Array.set(array, i, Array.get(array, j));
        Array.set(array, j, atI);
    }

    public static <E> void toggle(final Collection<E> col, E elem) {
        if (!col.remove(elem)) {
            col.add(elem);
        }
    }
}
