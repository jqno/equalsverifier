package nl.jqno.equalsverifier.internal.instantiation.prefab;

import java.util.*;

import nl.jqno.equalsverifier.internal.instantiation.Attributes;
import nl.jqno.equalsverifier.internal.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

// CHECKSTYLE OFF: CyclomaticComplexity
// CHECKSTYLE OFF: NPathComplexity
// CHECKSTYLE OFF: ExecutableStatementCount
// CHECKSTYLE OFF: JavaNCSS

@SuppressWarnings("JdkObsolete")
public class GenericJavaUtilValueSupplier<T> extends GenericValueSupplier<T> {

    private static final Comparator<Object> OBJECT_COMPARATOR = Comparator.comparingInt(Object::hashCode);

    public GenericJavaUtilValueSupplier(TypeTag tag, ValueProvider vp, Attributes attributes) {
        super(tag, vp, attributes);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<Tuple<T>> get() {
        if (is(Optional.class)) {
            return generic(val -> Optional.of(val), () -> Optional.empty());
        }
        if (is(Collection.class)) {
            return collection(ArrayList::new);
        }

        // Lists
        if (is(List.class)) {
            return collection(ArrayList::new);
        }
        if (is(ArrayList.class)) {
            return collection(ArrayList::new);
        }
        if (is(LinkedList.class)) {
            return collection(LinkedList::new);
        }
        if (is(Stack.class)) {
            return collection(Stack::new);
        }
        if (is(Vector.class)) {
            return collection(Vector::new);
        }

        // Maps
        if (is(Map.class)) {
            return map(HashMap::new);
        }
        if (is(LinkedHashMap.class)) {
            return map(LinkedHashMap::new);
        }
        if (is(HashMap.class)) {
            return map(HashMap::new);
        }
        if (is(Hashtable.class)) {
            return map(Hashtable::new);
        }
        if (is(NavigableMap.class)) {
            return map(() -> new TreeMap<>(OBJECT_COMPARATOR));
        }
        if (is(SortedMap.class)) {
            return map(() -> new TreeMap<>(OBJECT_COMPARATOR));
        }
        if (is(TreeMap.class)) {
            return map(() -> new TreeMap<>(OBJECT_COMPARATOR));
        }
        if (is(WeakHashMap.class)) {
            return map(WeakHashMap::new);
        }
        if (is(EnumMap.class)) {
            var asHashMap = map(HashMap::new);
            return asHashMap.map(t -> t.map(e -> (T) new EnumMap<>((Map) e)));
        }

        // Sets
        if (is(Set.class)) {
            return collection(HashSet::new);
        }
        if (is(SortedSet.class)) {
            return collection(() -> new TreeSet<>(OBJECT_COMPARATOR));
        }
        if (is(NavigableSet.class)) {
            return collection(() -> new TreeSet<>(OBJECT_COMPARATOR));
        }
        if (is(HashSet.class)) {
            return collection(HashSet::new);
        }
        if (is(TreeSet.class)) {
            return collection(() -> new TreeSet<>(OBJECT_COMPARATOR));
        }
        if (is(EnumSet.class)) {
            var asHashSet = collection(HashSet::new);
            return asHashSet.map(t -> t.map(e -> (T) EnumSet.copyOf((Set) e)));
        }

        // Queues
        if (is(Queue.class)) {
            return collection(PriorityQueue::new);
        }
        if (is(Deque.class)) {
            return collection(() -> new ArrayDeque<>(1));
        }
        if (is(ArrayDeque.class)) {
            return collection(() -> new ArrayDeque<>(1));
        }
        if (is(PriorityQueue.class)) {
            return collection(PriorityQueue::new);
        }

        return Optional.empty();
    }
}
