package nl.jqno.equalsverifier.internal.instantiation.prefab;

import java.util.*;

import nl.jqno.equalsverifier.internal.instantiation.Attributes;
import nl.jqno.equalsverifier.internal.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

// CHECKSTYLE OFF: CyclomaticComplexity
// CHECKSTYLE OFF: NPathComplexity
// CHECKSTYLE OFF: ExecutableStatementCount

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

        // Maps
        if (is(Map.class)) {
            return map(tag, attributes, HashMap::new);
        }
        if (is(LinkedHashMap.class)) {
            return map(tag, attributes, LinkedHashMap::new);
        }
        if (is(HashMap.class)) {
            return map(tag, attributes, HashMap::new);
        }
        if (is(Hashtable.class)) {
            return map(tag, attributes, Hashtable::new);
        }
        if (is(NavigableMap.class)) {
            return map(tag, attributes, () -> new TreeMap<>(OBJECT_COMPARATOR));
        }
        if (is(SortedMap.class)) {
            return map(tag, attributes, () -> new TreeMap<>(OBJECT_COMPARATOR));
        }
        if (is(TreeMap.class)) {
            return map(tag, attributes, () -> new TreeMap<>(OBJECT_COMPARATOR));
        }
        if (is(WeakHashMap.class)) {
            return map(tag, attributes, WeakHashMap::new);
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

        // Queues
        if (is(Queue.class)) {
            return collection(tag, attributes, PriorityQueue::new);
        }
        if (is(Deque.class)) {
            return collection(tag, attributes, () -> new ArrayDeque<>(1));
        }
        if (is(ArrayDeque.class)) {
            return collection(tag, attributes, () -> new ArrayDeque<>(1));
        }
        if (is(PriorityQueue.class)) {
            return collection(tag, attributes, PriorityQueue::new);
        }

        return Optional.empty();
    }
}
