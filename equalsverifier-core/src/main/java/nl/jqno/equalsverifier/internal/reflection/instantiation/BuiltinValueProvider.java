package nl.jqno.equalsverifier.internal.reflection.instantiation;

import java.util.LinkedHashSet;
import java.util.Optional;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.builtin.BuiltinJavaLangValueProvider;
import nl.jqno.equalsverifier.internal.reflection.instantiation.builtin.BuiltinJavaUtilValueProvider;

public class BuiltinValueProvider implements ValueProvider {

    private final ValueProvider javaLang;
    private final ValueProvider javaUtil;

    public BuiltinValueProvider(ValueProvider valueProvider) {
        javaLang = new BuiltinJavaLangValueProvider();
        javaUtil = new BuiltinJavaUtilValueProvider(valueProvider);
    }

    @Override
    public <T> Optional<Tuple<T>> provide(
        TypeTag tag,
        String label,
        LinkedHashSet<TypeTag> typeStack
    ) {
        if (tag.getType().isPrimitive() || inPackage(tag, "java.lang")) {
            return javaLang.provide(tag, label, typeStack);
        }
        if (inPackage(tag, "java.util")) {
            return javaUtil.provide(tag, label, typeStack);
        }
        return Optional.empty();
    }

    private boolean inPackage(TypeTag tag, String packagePrefix) {
        return tag.getType().getName().startsWith(packagePrefix + ".");
    }
}
