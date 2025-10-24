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

    public GenericJavaUtilConcurrentValueSupplier(TypeTag tag, ValueProvider vp, Attributes attributes) {
        super(tag, vp, attributes);
    }

    @Override
    public Optional<Tuple<T>> get() {

        if (is(CompletableFuture.class)) {
            return generic(ignored -> new CompletableFuture<>());
        }

        // Traditional collections
        if (is(CopyOnWriteArrayList.class)) {
            return collection(CopyOnWriteArrayList::new);
        }
        if (is(CopyOnWriteArraySet.class)) {
            return collection(CopyOnWriteArraySet::new);
        }
        if (is(ConcurrentHashMap.class)) {
            return map(ConcurrentHashMap::new);
        }
        if (is(ConcurrentNavigableMap.class)) {
            return map(() -> new ConcurrentSkipListMap<>());
        }
        if (is(ConcurrentSkipListMap.class)) {
            return map(() -> new ConcurrentSkipListMap<>());
        }

        // Queues
        if (is(ArrayBlockingQueue.class)) {
            return collection(() -> new ArrayBlockingQueue<>(1));
        }
        if (is(BlockingDeque.class)) {
            return collection(() -> new LinkedBlockingDeque<>(1));
        }
        if (is(BlockingQueue.class)) {
            return collection(() -> new ArrayBlockingQueue<>(1));
        }
        if (is(ConcurrentLinkedDeque.class)) {
            return collection(ConcurrentLinkedDeque::new);
        }
        if (is(ConcurrentLinkedQueue.class)) {
            return collection(ConcurrentLinkedQueue::new);
        }
        if (is(DelayQueue.class)) {
            return collection(DelayQueue::new);
        }
        if (is(LinkedBlockingQueue.class)) {
            return collection(LinkedBlockingQueue::new);
        }
        if (is(LinkedTransferQueue.class)) {
            return collection(LinkedTransferQueue::new);
        }
        if (is(PriorityBlockingQueue.class)) {
            return collection(PriorityBlockingQueue::new);
        }
        if (is(SynchronousQueue.class)) {
            return collection(SynchronousQueue::new);
        }

        return Optional.empty();
    }
}
