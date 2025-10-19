package nl.jqno.equalsverifier.internal.instantiation.prefab;

import java.util.*;

import nl.jqno.equalsverifier.internal.instantiation.Attributes;
import nl.jqno.equalsverifier.internal.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

// CHECKSTYLE OFF: CyclomaticComplexity
// CHECKSTYLE OFF: NPathComplexity

@SuppressWarnings("JdkObsolete")
public class GenericJavaUtilValueSupplier<T> extends GenericValueSupplier<T> {

    private static final Comparator<Object> OBJECT_COMPARATOR = Comparator.comparingInt(Object::hashCode);

    public GenericJavaUtilValueSupplier(Class<T> type, ValueProvider vp) {
        super(type, vp);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<Tuple<T>> get(TypeTag tag, Attributes attributes) {
        if (is(Collection.class)) {
            return collection(tag, attributes, ArrayList::new);
        }

        // Lists
        if (is(List.class)) {
            return collection(tag, attributes, ArrayList::new);
        }
        if (is(ArrayList.class)) {
            return collection(tag, attributes, ArrayList::new);
        }
        if (is(LinkedList.class)) {
            return collection(tag, attributes, LinkedList::new);
        }
        if (is(Stack.class)) {
            return collection(tag, attributes, Stack::new);
        }
        if (is(Vector.class)) {
            return collection(tag, attributes, Vector::new);
        }

        // Sets
        if (is(Set.class)) {
            return collection(tag, attributes, HashSet::new);
        }
        if (is(SortedSet.class)) {
            return collection(tag, attributes, () -> new TreeSet<>(OBJECT_COMPARATOR));
        }
        if (is(NavigableSet.class)) {
            return collection(tag, attributes, () -> new TreeSet<>(OBJECT_COMPARATOR));
        }
        if (is(HashSet.class)) {
            return collection(tag, attributes, HashSet::new);
        }
        if (is(TreeSet.class)) {
            return collection(tag, attributes, () -> new TreeSet<>(OBJECT_COMPARATOR));
        }

        return Optional.empty();
    }
}
