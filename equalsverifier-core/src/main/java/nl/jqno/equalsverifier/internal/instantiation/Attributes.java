package nl.jqno.equalsverifier.internal.instantiation;

import java.util.LinkedHashSet;

import nl.jqno.equalsverifier.internal.reflection.TypeTag;

@SuppressWarnings("NonApiType")
public final class Attributes {
    private final String cacheKey;
    private final String fieldName;
    private final LinkedHashSet<TypeTag> typeStack;

    private Attributes(String cacheKey, String fieldName, LinkedHashSet<TypeTag> typeStack) {
        this.cacheKey = cacheKey;
        this.fieldName = fieldName;
        this.typeStack = typeStack;
    }

    public static Attributes empty() {
        return new Attributes(null, null, new LinkedHashSet<>());
    }

    public static Attributes named(String fieldName) {
        return new Attributes(fieldName, fieldName, new LinkedHashSet<>());
    }

    public String cacheKey() {
        return cacheKey;
    }

    public Attributes clearCacheKey() {
        return new Attributes(null, fieldName, typeStack());
    }

    public String fieldName() {
        return fieldName;
    }

    public Attributes addToStack(TypeTag tag) {
        var newStack = copyTypeStack();
        newStack.add(tag);
        return new Attributes(cacheKey, fieldName, newStack);
    }

    public boolean typeStackContains(TypeTag tag) {
        return typeStack.contains(tag);
    }

    public LinkedHashSet<TypeTag> typeStack() {
        return copyTypeStack();
    }

    private LinkedHashSet<TypeTag> copyTypeStack() {
        return new LinkedHashSet<>(typeStack);
    }
}
