package nl.jqno.equalsverifier.testhelpers.types;

public class Multiple {

    private final int a;
    private final int b;

    public Multiple(int a, int b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public final boolean equals(Object obj) {
        if (!(obj instanceof Multiple)) {
            return false;
        }
        Multiple other = (Multiple) obj;
        return a * b == other.a * other.b;
    }

    @Override
    public final int hashCode() {
        return a * b;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + a + "*" + b + "=" + (a * b);
    }
}
