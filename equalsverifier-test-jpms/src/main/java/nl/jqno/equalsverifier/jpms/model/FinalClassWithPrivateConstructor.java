package nl.jqno.equalsverifier.jpms.model;

@SuppressWarnings("unused")
public final class FinalClassWithPrivateConstructor {
    private final int i;

    private FinalClassWithPrivateConstructor(int i) {
        this.i = i;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FinalClassWithPrivateConstructor other && i == other.i;
    }

    @Override
    public int hashCode() {
        return i;
    }
}
