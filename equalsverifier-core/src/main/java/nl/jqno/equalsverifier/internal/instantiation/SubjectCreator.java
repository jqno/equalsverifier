package nl.jqno.equalsverifier.internal.instantiation;

import java.lang.reflect.Field;
import nl.jqno.equalsverifier.internal.util.Configuration;

public interface SubjectCreator<T> {
    T plain();
    T withFieldDefaulted(Field field);
    T withAllFieldsDefaulted(Configuration<T> config);
    T withAllFieldsDefaultedExcept(Field field);
    T withFieldSetTo(Field field, Object value);
    T withFieldChanged(Field field);
    T withAllFieldsChanged();
}
