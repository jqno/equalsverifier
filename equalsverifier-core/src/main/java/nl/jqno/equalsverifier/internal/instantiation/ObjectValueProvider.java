package nl.jqno.equalsverifier.internal.instantiation;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import nl.jqno.equalsverifier.internal.exceptions.ModuleException;
import nl.jqno.equalsverifier.internal.reflection.*;
import nl.jqno.equalsverifier.internal.util.Rethrow;
import org.objenesis.Objenesis;

public class ObjectValueProvider implements ValueProvider {

    private final ValueProvider vp;
    private final Objenesis objenesis;

    public ObjectValueProvider(ValueProvider vp, Objenesis objenesis) {
        this.vp = vp;
        this.objenesis = objenesis;
    }

    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes) {
        var values = determineValues(tag, attributes);
        var instanceCreator = new InstanceCreator<T>(ClassProbe.of(tag.getType()), objenesis);
        var tuple = Rethrow.rethrow(() -> values.map(instanceCreator::instantiate));
        return Optional.of(tuple);
    }

    private <T> Tuple<Map<Field, Object>> determineValues(TypeTag tag, Attributes attributes) {
        Class<T> type = tag.getType();
        Map<Field, Object> red = new HashMap<>();
        Map<Field, Object> blue = new HashMap<>();
        Map<Field, Object> redCopy = new HashMap<>();

        for (var p : FieldIterable.ofIgnoringStatic(type)) {
            var field = p.getField();
            var value = valuesFor(field, tag, attributes.clearName());

            red.put(field, value.red());
            blue.put(field, value.blue());
            redCopy.put(field, value.redCopy());
        }

        return new Tuple<>(red, blue, redCopy);
    }

    private Tuple<Object> valuesFor(Field f, TypeTag tag, Attributes attributes) {
        try {
            TypeTag fieldTag = TypeTag.of(f, tag);
            return vp.provideOrThrow(fieldTag, attributes);
        }
        catch (ModuleException e) {
            throw new ModuleException("Field " + f.getName() + " of type " + f.getType().getName()
                    + " is not accessible via the Java Module System.\nConsider opening the module that contains it, or add prefab values for type "
                    + f.getType().getName() + ".", e);
        }
    }
}
