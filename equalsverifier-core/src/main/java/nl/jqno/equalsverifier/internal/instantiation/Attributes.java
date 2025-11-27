package nl.jqno.equalsverifier.internal.instantiation;

import java.util.LinkedHashSet;

import nl.jqno.equalsverifier.internal.reflection.TypeTag;

@SuppressWarnings("NonApiType")
public final class Attributes {
    private final String fieldName;
    private final LinkedHashSet<TypeTag> typeStack;

    private Attributes(String fieldName, LinkedHashSet<TypeTag> typeStack) {
        this.fieldName = fieldName;
        this.typeStack = typeStack;
    }

    public static Attributes empty() {
        return new Attributes(null, new LinkedHashSet<>());
    }

    public static Attributes named(String fieldName) {
        return new Attributes(fieldName, new LinkedHashSet<>());
    }

    public String fieldName() {
        return fieldName;
    }

    public Attributes clearName() {
        return new Attributes(null, typeStack());
    }

    public Attributes addToStack(TypeTag tag) {
        var newStack = copyTypeStack();
        newStack.add(tag);
        return new Attributes(fieldName, newStack);
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
