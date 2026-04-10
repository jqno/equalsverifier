package nl.jqno.equalsverifier.signedjar;

/*
 * Abstract, to force EqualsVerifier to create a dynamic subclass.
 */
public abstract class SignedJarPoint {
    private final int x;
    private final int y;

    public SignedJarPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public final boolean equals(Object obj) {
        if (!(obj instanceof SignedJarPoint)) {
            return false;
        }
        SignedJarPoint other = (SignedJarPoint) obj;
        return x == other.x && y == other.y;
    }

    @Override
    public final int hashCode() {
        return x + (31 * y);
    }
}
