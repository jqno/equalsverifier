package nl.jqno.equalsverifier.internal.reflection.instantiation;

import java.util.Objects;

final class Key {

    private final Class<?> type;
    final String label;

    Key(Class<?> type, String label) {
        this.type = type;
        this.label = label;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Key)) {
            return false;
        }
        Key other = (Key) obj;
        return Objects.equals(type, other.type) && Objects.equals(label, other.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, label);
    }

    @Override
    public String toString() {
        return "Key: [" + type + "/" + label + "]";
    }
}
