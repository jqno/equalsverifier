package nl.jqno.equalsverifier.integration;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Uninstantiable {

    private final String s;
    private final String t;

    public Uninstantiable(List<String> s, String t) {
        this.s = s == null ? null : s.stream().collect(Collectors.joining(", "));
        this.t = t;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Uninstantiable other && Objects.equals(s, other.s) && Objects.equals(t, other.t);
    }

    @Override
    public int hashCode() {
        return Objects.hash(s, t);
    }
}
