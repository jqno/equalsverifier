package nl.jqno.equalsverifier.internal.instantiation;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.BiFunction;

import nl.jqno.equalsverifier.internal.exceptions.ModuleException;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

public final class InstantiationUtil {
    private static final TypeTag OBJECT = new TypeTag(Object.class);

    private InstantiationUtil() {}

    public static Tuple<Object> valuesFor(Field f, TypeTag enclosingType, ValueProvider vp, Attributes attributes) {
        try {
            TypeTag fieldTag = TypeTag.of(f, enclosingType);
            return vp.provideOrThrow(fieldTag, attributes);
        }
        catch (ModuleException e) {
            throw new ModuleException("Field " + f.getName() + " of type " + f.getType().getName()
                    + " is not accessible via the Java Module System.\nConsider opening the module that contains it, or add prefab values for type "
                    + f.getType().getName() + ".", e);
        }
    }

    public static TypeTag determineGenericType(TypeTag tag, int index) {
        if (tag.genericTypes().size() <= index) {
            return OBJECT;
        }
        return tag.genericTypes().get(index);
    }

    public static <A, B, R> Optional<R> zip(
            Optional<A> a,
            Optional<B> b,
            BiFunction<? super A, ? super B, ? extends R> mapper) {
        return a.flatMap(av -> b.map(bv -> mapper.apply(av, bv)));
    }
}
