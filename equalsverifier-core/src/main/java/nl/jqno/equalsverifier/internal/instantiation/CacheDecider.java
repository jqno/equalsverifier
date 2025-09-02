package nl.jqno.equalsverifier.internal.instantiation;

/**
 * Decides whether {@link CachingValueProvider} should cache instances of the given type.
 */
@FunctionalInterface
public interface CacheDecider {
    /**
     * Determines whether instances of {@code type} should be cached.
     *
     * @param type The type whose instances should or should not be cached.
     * @return Whether instances of the given type should be cached.
     */
    boolean canBeCached(Class<?> type);
}
