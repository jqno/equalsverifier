package equalsverifier.reflection;

/**
 * Signals that a reflection call went awry.
 */
@SuppressWarnings("serial")
public class ReflectionException extends RuntimeException {
    public ReflectionException(String message) {
        super(message);
    }

    public ReflectionException(Throwable cause) {
        super(cause);
    }
}
