package nl.jqno.equalsverifier.internal.instantiation;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.reflection.*;

/**
 * A ValueProvider that can instantiate abstract types, including (sealed) interfaces.
 */
public class AbstractValueProvider implements ValueProvider {

    private final ValueProvider vp;

    public AbstractValueProvider(ValueProvider vp) {
        this.vp = vp;
    }

    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes) {
        ClassProbe<T> probe = ClassProbe.of(tag.getType());
        if (!probe.isAbstract()) {
            return Optional.empty();
        }

        Class<T> concrete = SubtypeManager.findInstantiableSubclass(probe).get();
        var concreteTag = new TypeTag(concrete);
        return vp.provide(concreteTag, attributes);
    }
}
