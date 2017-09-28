package util;

import java.util.Objects;

@FunctionalInterface
public interface TriPredicate<T1, T2, T3> {

    boolean test(T1 t1, T2 t2, T3 t3);

    default TriPredicate<T1, T2, T3> and(TriPredicate<? super T1, ? super T2, ? super T3> other) {
        Objects.requireNonNull(other);
        return (T1 t1, T2 t2, T3 t3) -> test(t1, t2, t3) && other.test(t1, t2, t3);
    }

    default TriPredicate<T1, T2, T3> negate() {
        return (t1, t2, t3) -> !test(t1, t2, t3);
    }

    default TriPredicate<T1, T2, T3> or(TriPredicate<? super T1, ? super T2, ? super T3> other) {
        Objects.requireNonNull(other);
        return (T1 t1, T2 t2, T3 t3) -> test(t1, t2, t3) || other.test(t1, t2, t3);
    }
}
