package nl.jqno.equalsverifier.internal.instantiation.prefab;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

public class RecursingValueProvider implements ValueProvider {

    private ValueProvider recurse;

    public void setRecurse(ValueProvider newRecurse) {
        this.recurse = newRecurse;
    }

    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, String fieldName) {
        return recurse.provide(tag, fieldName);
    }

}
