package util;

import javafx.collections.ModifiableObservableListBase;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class ObservableArray<E> extends ModifiableObservableListBase<E> {

    private final Array<E> delegate;

    public ObservableArray(int size) {
        delegate = new Array<>(size);
    }

    public ObservableArray(E[] array) {
        delegate = new Array<>(array);
    }

    public ObservableArray(ObservableArray<E> arr) {
        delegate = new Array<E>(arr.delegate);
    }

    @Override
    public E get(int index) {
        return delegate.get(index);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    protected void doAdd(int index, E element) {
        delegate.add(index, element);
    }

    @Override
    protected E doSet(int index, E element) {
        return delegate.set(index, element);
    }

    @Override
    protected E doRemove(int index) {
        return delegate.remove(index);
    }

    public Optional<E> getOptional(int index) {
        return delegate.getOptional(index);
    }
}
