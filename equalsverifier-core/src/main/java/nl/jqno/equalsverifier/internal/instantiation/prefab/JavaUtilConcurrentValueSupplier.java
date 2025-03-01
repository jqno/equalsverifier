package nl.jqno.equalsverifier.internal.instantiation.prefab;

import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.StampedLock;

import nl.jqno.equalsverifier.internal.reflection.Tuple;

// CHECKSTYLE OFF: CyclomaticComplexity
// CHECKSTYLE OFF: NPathComplexity

class JavaUtilConcurrentValueSupplier<T> extends ValueSupplier<T> {
    public JavaUtilConcurrentValueSupplier(Class<T> type) {
        super(type);
    }

    @Override
    public Optional<Tuple<T>> get() {
        // java.util.concurrent
        if (is(Semaphore.class)) {
            var red = new Semaphore(1);
            return val(red, new Semaphore(1), red);
        }
        if (is(SynchronousQueue.class)) {
            var red = new SynchronousQueue<>();
            return val(red, new SynchronousQueue<>(), red);
        }

        // java.util.concurrent.atomic
        if (is(AtomicBoolean.class)) {
            return val(new AtomicBoolean(true), new AtomicBoolean(false), new AtomicBoolean(true));
        }
        if (is(AtomicInteger.class)) {
            return val(new AtomicInteger(1), new AtomicInteger(2), new AtomicInteger(1));
        }
        if (is(AtomicIntegerArray.class)) {
            return val(
                new AtomicIntegerArray(new int[] { 1 }),
                new AtomicIntegerArray(new int[] { 2 }),
                new AtomicIntegerArray(new int[] { 1 }));
        }
        if (is(AtomicLong.class)) {
            return val(new AtomicLong(1L), new AtomicLong(2L), new AtomicLong(1L));
        }
        if (is(AtomicLongArray.class)) {
            return val(
                new AtomicLongArray(new long[] { 1L }),
                new AtomicLongArray(new long[] { 2L }),
                new AtomicLongArray(new long[] { 1L }));
        }
        if (is(DoubleAdder.class)) {
            var red = new DoubleAdder();
            return val(red, new DoubleAdder(), red);
        }
        if (is(DoubleAccumulator.class)) {
            var red = new DoubleAccumulator((a, b) -> a + b, 0.0);
            var blue = new DoubleAccumulator((a, b) -> a * b, 1.0);
            return val(red, blue, red);
        }
        if (is(LongAdder.class)) {
            var red = new LongAdder();
            return val(red, new LongAdder(), red);
        }
        if (is(LongAccumulator.class)) {
            var red = new LongAccumulator((a, b) -> a + b, 0);
            var blue = new LongAccumulator((a, b) -> a * b, 1);
            return val(red, blue, red);
        }

        // java.util.concurrent.lock
        if (is(StampedLock.class)) {
            var red = new StampedLock();
            return val(red, new StampedLock(), red);
        }
        if (is(ReentrantLock.class)) {
            var red = new ReentrantLock();
            return val(red, new ReentrantLock(), red);
        }

        return Optional.empty();
    }
}
