package nl.jqno.equalsverifier.internal.valueproviders;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.exceptions.NoValueException;
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

        try {
            var concrete = SubtypeManager.findInstantiableSubclass(probe, vp, attributes);
            var concreteTag = new TypeTag(concrete);
            return vp.provide(concreteTag, attributes);
        }
        catch (NoValueException e) {
            throw new NoValueException("Could not construct a value for " + tag.getType().getSimpleName()
                    + ": it is sealed and no non-recursive subclass could be found. Please add prefab values for this type.",
                    e);
        }
    }
}
