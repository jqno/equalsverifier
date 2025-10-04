package nl.jqno.equalsverifier.internal.instantiation.prefab;

import java.util.*;

import nl.jqno.equalsverifier.internal.exceptions.RecursionException;
import nl.jqno.equalsverifier.internal.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

public class RecursingValueProvider implements ValueProvider {

    private ValueProvider recurse;
    private final Set<Occurrence> occurrences = new HashSet<>();

    public void setRecurse(ValueProvider newRecurse) {
        this.recurse = newRecurse;
    }

    public void clear() {
        occurrences.clear();
    }

    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, String fieldName) {
        var occ = new Occurrence(tag, fieldName);
        if (occurrences.contains(occ)) {
            var s = new LinkedHashSet<TypeTag>();
            s.add(tag);
            throw new RecursionException(s);
        }
        occurrences.add(occ);
        return recurse.provide(tag, fieldName);
    }

    record Occurrence(TypeTag tag, String fieldName) {}
}
