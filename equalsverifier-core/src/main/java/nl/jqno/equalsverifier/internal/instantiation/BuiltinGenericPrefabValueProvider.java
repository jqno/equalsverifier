package nl.jqno.equalsverifier.internal.instantiation;

import java.util.*;

import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

public class BuiltinGenericPrefabValueProvider implements ValueProvider {
    private final ValueProvider vp;

    public BuiltinGenericPrefabValueProvider(ValueProvider vp) {
        this.vp = vp;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes) {
        if (tag.getType().equals(List.class)
                || tag.getType().equals(Iterable.class)
                || tag.getType().equals(Collection.class)) {
            Optional<Tuple<T>> element = vp.provide(tag.genericTypes().get(0), attributes.clearName());
            if (element.isEmpty()) {
                return Optional.empty();
            }
            Tuple result = element.get().map(val -> {
                var r = new ArrayList();
                r.add(val);
                return r;
            });
            return Optional.of(result);
        }
        return Optional.empty();
    }
}
