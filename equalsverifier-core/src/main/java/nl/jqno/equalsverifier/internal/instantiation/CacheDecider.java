package nl.jqno.equalsverifier.internal.instantiation;

@FunctionalInterface
public interface CacheDecider {
    boolean canBeCached(Class<?> type);
}
