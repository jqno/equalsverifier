package nl.jqno.equalsverifier.internal.instantiation.prefab;

import java.util.Optional;
import java.util.concurrent.*;

import nl.jqno.equalsverifier.internal.instantiation.Attributes;
import nl.jqno.equalsverifier.internal.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

// CHECKSTYLE OFF: CyclomaticComplexity
// CHECKSTYLE OFF: NPathComplexity

@SuppressWarnings("JdkObsolete")
public class GenericJavaUtilConcurrentValueSupplier<T> extends GenericValueSupplier<T> {

    public GenericJavaUtilConcurrentValueSupplier(Class<T> type, ValueProvider vp) {
        super(type, vp);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<Tuple<T>> get(TypeTag tag, Attributes attributes) {

        // Traditional collections
        if (is(CopyOnWriteArrayList.class)) {
            return collection(tag, attributes, CopyOnWriteArrayList::new);
        }
        if (is(CopyOnWriteArraySet.class)) {
            return collection(tag, attributes, CopyOnWriteArraySet::new);
        }
        if (is(ConcurrentHashMap.class)) {
            return map(tag, attributes, ConcurrentHashMap::new);
        }
        if (is(ConcurrentNavigableMap.class)) {
            return map(tag, attributes, () -> new ConcurrentSkipListMap<>());
        }
        if (is(ConcurrentSkipListMap.class)) {
            return map(tag, attributes, () -> new ConcurrentSkipListMap<>());
        }

        // Queues
        if (is(ArrayBlockingQueue.class)) {
            return collection(tag, attributes, () -> new ArrayBlockingQueue<>(1));
        }
        if (is(BlockingDeque.class)) {
            return collection(tag, attributes, () -> new LinkedBlockingDeque<>(1));
        }
        if (is(BlockingQueue.class)) {
            return collection(tag, attributes, () -> new ArrayBlockingQueue<>(1));
        }
        if (is(ConcurrentLinkedDeque.class)) {
            return collection(tag, attributes, ConcurrentLinkedDeque::new);
        }
        if (is(ConcurrentLinkedQueue.class)) {
            return collection(tag, attributes, ConcurrentLinkedQueue::new);
        }
        if (is(DelayQueue.class)) {
            return collection(tag, attributes, DelayQueue::new);
        }
        if (is(LinkedBlockingQueue.class)) {
            return collection(tag, attributes, LinkedBlockingQueue::new);
        }
        if (is(LinkedTransferQueue.class)) {
            return collection(tag, attributes, LinkedTransferQueue::new);
        }
        if (is(PriorityBlockingQueue.class)) {
            return collection(tag, attributes, PriorityBlockingQueue::new);
        }
        if (is(SynchronousQueue.class)) {
            return collection(tag, attributes, SynchronousQueue::new);
        }

        return Optional.empty();
    }
}
