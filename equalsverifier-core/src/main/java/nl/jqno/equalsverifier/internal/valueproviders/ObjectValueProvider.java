package nl.jqno.equalsverifier.internal.valueproviders;

import static nl.jqno.equalsverifier.internal.valueproviders.InstantiationUtil.valuesFor;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import nl.jqno.equalsverifier.internal.instantiators.InstantiatorFactory;
import nl.jqno.equalsverifier.internal.reflection.*;
import nl.jqno.equalsverifier.internal.util.Rethrow;
import org.objenesis.Objenesis;

/**
 * A ValueProvider that can instantiate regular objects.
 */
public class ObjectValueProvider implements ValueProvider {

    private final ValueProvider vp;
    private final Objenesis objenesis;
    private final boolean forceFinalMeansFinal;

    public ObjectValueProvider(ValueProvider vp, Objenesis objenesis, boolean forceFinalMeansFinal) {
        this.vp = vp;
        this.objenesis = objenesis;
        this.forceFinalMeansFinal = forceFinalMeansFinal;
    }

    /** {@inheritDoc}} */
    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes) {
        var instantiator = InstantiatorFactory.<T>of(ClassProbe.of(tag.getType()), objenesis, forceFinalMeansFinal);
        var values = determineValues(tag.getType(), tag, attributes);
        var tuple = Rethrow.rethrow(() -> values.map(instantiator::instantiate));
        return Optional.of(tuple);
    }

    private <T> Tuple<Map<Field, Object>> determineValues(Class<T> actualType, TypeTag tag, Attributes attributes) {
        Map<Field, Object> red = new HashMap<>();
        Map<Field, Object> blue = new HashMap<>();
        Map<Field, Object> redCopy = new HashMap<>();

        for (var p : FieldIterable.ofIgnoringStatic(actualType)) {
            var field = p.getField();
            var value = valuesFor(field, tag, vp, attributes);

            red.put(field, value.red());
            blue.put(field, value.blue());
            redCopy.put(field, value.red());
        }

        return new Tuple<>(red, blue, redCopy);
    }
}
