package nl.jqno.equalsverifier.internal.valueproviders.prefab;

import static nl.jqno.equalsverifier.internal.valueproviders.InstantiationUtil.determineGenericType;
import static nl.jqno.equalsverifier.internal.valueproviders.InstantiationUtil.zip;

import java.util.*;

import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.valueproviders.Attributes;
import nl.jqno.equalsverifier.internal.valueproviders.ValueProvider;

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

    @SuppressWarnings({ "rawtypes", "unchecked" })
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
            var keys = vp.provide(determineGenericType(tag, 0), attributes).map(tup -> tup.map(e -> (Enum) e));
            var values = vp.provide(determineGenericType(tag, 1), attributes);

            return zip(keys, values, (k, v) -> k.map(e -> {
                // Use red for key and blue for value in the Red map to avoid having identical keys and values.
                // But don't do it in the Blue map, or they may cancel each other out again.
                var map = new EnumMap(k.red().getDeclaringClass());
                map.put(e, v.blue());
                return (T) map;
            }).swapBlueIfEqualToRed(() -> (T) new EnumMap(k.red().getDeclaringClass())));
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
            return vp.provide(determineGenericType(tag, 0), attributes).map(tup -> {
                var t = tup.map(e -> (Enum) e);
                return t
                        .map(e -> (T) EnumSet.of(e))
                        .swapBlueIfEqualToRed(() -> (T) EnumSet.noneOf(t.blue().getDeclaringClass()));
            });
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
