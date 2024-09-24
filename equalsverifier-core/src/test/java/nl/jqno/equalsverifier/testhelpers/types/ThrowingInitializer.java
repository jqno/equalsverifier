package nl.jqno.equalsverifier.testhelpers.types;

public final class ThrowingInitializer {
    {
        // Throwing something that will immediately be thrown when the class is constructed.
        if (true) {
            throw new IllegalStateException("initializing");
        }
    }

    private ThrowingInitializer() {}

    public static final ThrowingInitializer X = new ThrowingInitializer();
    public static final ThrowingInitializer Y = new ThrowingInitializer();
}
