package nl.jqno.equalsverifier.internal.instantiation.prefab;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;

import nl.jqno.equalsverifier.internal.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

public class BuiltinGenericPrefabValueProvider implements ValueProvider {
    private final ValueProvider recursive;

    public BuiltinGenericPrefabValueProvider(ValueProvider recursive) {
        this.recursive = recursive;
    }

    @SuppressWarnings({ "rawtypes", "unchecked", "JdkObsolete" })
    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, String fieldName) {
        Class<T> type = tag.getType();
        if (type.equals(ArrayList.class)) {
            Tuple<T> element = recursive.provideOrThrow(tag.genericTypes().get(0), null);
            Tuple result = element.map(v -> {
                var r = new ArrayList();
                r.add(v);
                return r;
            });
            return Optional.of(result);
        }
        if (type.equals(LinkedList.class)) {
            Tuple<T> element = recursive.provideOrThrow(tag.genericTypes().get(0), null);
            Tuple result = element.map(v -> {
                var r = new LinkedList();
                r.add(v);
                return r;
            });
            return Optional.of(result);
        }
        return Optional.empty();
    }
}
