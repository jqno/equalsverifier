package nl.jqno.equalsverifier.internal.instantiation;

import java.util.LinkedHashSet;

import nl.jqno.equalsverifier.internal.reflection.TypeTag;

@SuppressWarnings("NonApiType")
public final class Attributes {
    private final String fieldName;
    private final TypeTag preTypeStack;
    private final LinkedHashSet<TypeTag> typeStack;

    private Attributes(String fieldName, TypeTag preTypeStack, LinkedHashSet<TypeTag> typeStack) {
        this.fieldName = fieldName;
        this.preTypeStack = preTypeStack;
        this.typeStack = typeStack;
    }

    public static Attributes empty() {
        return new Attributes(null, null, new LinkedHashSet<>());
    }

    public static Attributes named(String fieldName) {
        return new Attributes(fieldName, null, new LinkedHashSet<>());
    }

    public String fieldName() {
        return fieldName;
    }

    public Attributes clearName() {
        return new Attributes(null, preTypeStack, typeStack());
    }

    public Attributes addToStack(TypeTag tag) {
        // In order to work with VintageValueProvider, we can only add the latest typeTag to the stack
        // _after_ it has been processed. In order to achieve that, we keep it in a separate variable
        // and push it onto the stack only when a new tag is added.
        var newStack = copyTypeStack();
        if (preTypeStack != null) {
            newStack.add(preTypeStack);
        }
        return new Attributes(fieldName, tag, newStack);
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
