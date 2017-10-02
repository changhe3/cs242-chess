package util;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * A List wrapper for a array
 *
 * @param <E> the element type
 */
//modified from Arrays.java:3806
public class Array<E> extends AbstractList<E>
        implements RandomAccess, java.io.Serializable {
    private static final long serialVersionUID = -2764017481108945198L;
    private final E[] a;

    @SuppressWarnings("unchecked")
    public Array(int size) {
        this((E[]) new Object[size]);
    }

    public Array(E[] array) {
        a = Objects.requireNonNull(array);
    }

    public Array(Array<E> arr) {
        a = Arrays.copyOf(arr.a, arr.a.length);
    }

    @Override
    public int size() {
        return a.length;
    }

    @Override
    public Object[] toArray() {
        return a.clone();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        int size = size();
        if (a.length < size) {
            return Arrays.copyOf(this.a, size,
                    (Class<? extends T[]>) a.getClass());
        }
        System.arraycopy(this.a, 0, a, 0, size);
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    @Override
    public E get(int index) {
        return a[index];
    }

    public Optional<E> getOptional(int index) {
        return Optional.ofNullable(a[index]);
    }

    @Override
    public E set(int index, E element) {
        E oldValue = a[index];
        a[index] = element;
        return oldValue;
    }

    @Override
    public int indexOf(Object o) {
        E[] a = this.a;
        if (o == null) {
            for (int i = 0; i < a.length; i++) {
                if (a[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < a.length; i++) {
                if (o.equals(a[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    @Override
    public Spliterator<E> spliterator() {
        return Spliterators.spliterator(a, Spliterator.ORDERED);
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        for (E e : a) {
            action.accept(e);
        }
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        Objects.requireNonNull(operator);
        E[] a = this.a;
        for (int i = 0; i < a.length; i++) {
            a[i] = operator.apply(a[i]);
        }
    }

    @Override
    public void sort(Comparator<? super E> c) {
        Arrays.sort(a, c);
    }

    @Override
    public E remove(int index) {
        return set(index, null);
    }

    @Override
    public boolean remove(Object o) {
        Objects.requireNonNull(o);
        for (int i = 0; i < a.length; i++) {
            if (a[i].equals(o)) {
                remove(i);
                return true;
            }
        }
        return false;
    }
}
