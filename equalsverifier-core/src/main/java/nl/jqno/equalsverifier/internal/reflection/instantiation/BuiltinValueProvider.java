package nl.jqno.equalsverifier.internal.reflection.instantiation;

import java.util.Optional;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.builtin.BuiltinJavaLangValueProvider;

public class BuiltinValueProvider implements ValueProvider {

    private final ValueProvider javaLang = new BuiltinJavaLangValueProvider();

    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, String label) {
        if (tag.getType().isPrimitive() || inPackage(tag, "java.lang")) {
            return javaLang.provide(tag, label);
        }
        return Optional.empty();
    }

    private boolean inPackage(TypeTag tag, String packagePrefix) {
        return tag.getType().getName().startsWith(packagePrefix + ".");
    }
}
