package nl.jqno.equalsverifier.internal.valueproviders;

import java.util.ArrayList;
import java.util.Optional;

import nl.jqno.equalsverifier.Func;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

/**
 * A ValueProvider for generic classes for which the user has provided a factory.
 */
public class UserGenericPrefabValueProvider implements ValueProvider {

    private final UserPrefabValueCaches prefabs;
    private final ValueProvider vp;

    public UserGenericPrefabValueProvider(UserPrefabValueCaches prefabs, ValueProvider vp) {
        this.prefabs = prefabs;
        this.vp = vp;
    }

    /** {@inheritDoc}} */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes) {
        Class<T> type = tag.getType();
        Func<T> genericFactory = prefabs.getGeneric(type);
        if (genericFactory == null) {
            return Optional.empty();
        }

        var reds = new ArrayList<Object>();
        var blues = new ArrayList<Object>();
        int n = tag.getType().getTypeParameters().length;
        for (int i = 0; i < n; i++) {
            TypeTag paramTag = InstantiationUtil.determineGenericType(tag, i);
            Tuple<Object> tuple = vp.provideOrThrow(paramTag, attributes);

            reds.add(tuple.red());
            blues.add(tuple.blue());
        }

        T red = genericFactory.apply(reds);
        T blue = genericFactory.apply(blues);
        T redCopy = genericFactory.apply(reds);

        return Optional.of(new Tuple<>(red, blue, redCopy));
    }
}
