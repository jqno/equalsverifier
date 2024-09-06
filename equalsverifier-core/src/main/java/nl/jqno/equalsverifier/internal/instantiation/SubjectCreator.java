package nl.jqno.equalsverifier.internal.instantiation;

import java.lang.reflect.Field;

public interface SubjectCreator<T> {
    T plain();
    T withFieldDefaulted(Field field);
    T withAllFieldsDefaulted();
    T withAllFieldsDefaultedExcept(Field field);
    T withFieldSetTo(Field field, Object value);
    T withFieldChanged(Field field);
    T withAllFieldsChanged();
}
