package util;

import java.util.Objects;

public class Pair<First, Second> {

    public final First first;
    public final Second second;

    private Pair(First first, Second second) {
        this.first = first;
        this.second = second;
    }

    public static <First, Second> Pair<First, Second> of(First fst, Second snd) {
        return new Pair<>(fst, snd);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) &&
                Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "Pair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
