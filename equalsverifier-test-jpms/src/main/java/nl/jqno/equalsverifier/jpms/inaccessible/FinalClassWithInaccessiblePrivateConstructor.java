package nl.jqno.equalsverifier.jpms.inaccessible;

@SuppressWarnings("unused")
public final class FinalClassWithInaccessiblePrivateConstructor {
    private final int i;

    private FinalClassWithInaccessiblePrivateConstructor(int i) {
        this.i = i;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FinalClassWithInaccessiblePrivateConstructor other && i == other.i;
    }

    @Override
    public int hashCode() {
        return i;
    }
}
