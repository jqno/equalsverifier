package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class RecordProbe<T> {

    private final Class<T> type;

    public RecordProbe(Class<T> type) {
        this.type = type;
    }

    public Stream<FieldProbe> fields() {
        return StreamSupport.stream(FieldIterable.ofIgnoringStatic(type).spliterator(), false);
    }

    public T callRecordConstructor(List<?> params) {
        Constructor<T> constructor = getRecordConstructor();
        return rethrow(() -> constructor.newInstance(params.toArray(new Object[0])), e -> buildMessage(e, params));
    }

    private Constructor<T> getRecordConstructor() {
        return rethrow(() -> {
            List<Class<?>> constructorTypes = fields().map(FieldProbe::getType).collect(Collectors.toList());
            Constructor<T> result = type.getDeclaredConstructor(constructorTypes.toArray(new Class<?>[0]));
            result.setAccessible(true);
            return result;
        });
    }

    private String buildMessage(Throwable e, List<?> params) {
        String msg = "Record: failed to run constructor for record type " + type.getName()
                + "\n   These were the values passed to the constructor: " + params;

        if (e.getCause() instanceof NullPointerException) {
            return msg + "\n   If the record does not accept null values for its constructor parameters,"
                    + " consider suppressing Warning.NULL_FIELDS.";
        }

        msg += """
               \n   If the record does not accept any of the given values for its constructor parameters, \
               consider providing prefab values for the types of those fields.""";
        return msg;
    }
}
