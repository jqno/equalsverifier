package nl.jqno.equalsverifier.internal.prefab;

import java.nio.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import nl.jqno.equalsverifier.internal.reflection.Tuple;

// CHECKSTYLE OFF: CyclomaticComplexity
// CHECKSTYLE OFF: NPathComplexity

public class JavaNioValueSupplier<T> extends ValueSupplier<T> {
    public JavaNioValueSupplier(Class<T> type) {
        super(type);
    }

    @Override
    public Optional<Tuple<T>> get() {
        // java.nio
        if (is(Buffer.class)) {
            return val(
                ByteBuffer.wrap(new byte[] { 0 }),
                ByteBuffer.wrap(new byte[] { 1 }),
                ByteBuffer.wrap(new byte[] { 0 }));
        }
        if (is(ByteBuffer.class)) {
            return val(
                ByteBuffer.wrap(new byte[] { 0 }),
                ByteBuffer.wrap(new byte[] { 1 }),
                ByteBuffer.wrap(new byte[] { 0 }));
        }
        if (is(CharBuffer.class)) {
            return val(CharBuffer.wrap("a"), CharBuffer.wrap("b"), CharBuffer.wrap("a"));
        }
        if (is(DoubleBuffer.class)) {
            return val(
                DoubleBuffer.wrap(new double[] { 0.0 }),
                DoubleBuffer.wrap(new double[] { 1.0 }),
                DoubleBuffer.wrap(new double[] { 0.0 }));
        }
        if (is(FloatBuffer.class)) {
            return val(
                FloatBuffer.wrap(new float[] { 0.0f }),
                FloatBuffer.wrap(new float[] { 1.0f }),
                FloatBuffer.wrap(new float[] { 0.0f }));
        }
        if (is(IntBuffer.class)) {
            return val(
                IntBuffer.wrap(new int[] { 0 }),
                IntBuffer.wrap(new int[] { 1 }),
                IntBuffer.wrap(new int[] { 0 }));
        }
        if (is(LongBuffer.class)) {
            return val(
                LongBuffer.wrap(new long[] { 0 }),
                LongBuffer.wrap(new long[] { 1 }),
                LongBuffer.wrap(new long[] { 0 }));
        }
        if (is(ShortBuffer.class)) {
            return val(
                ShortBuffer.wrap(new short[] { 0 }),
                ShortBuffer.wrap(new short[] { 1 }),
                ShortBuffer.wrap(new short[] { 0 }));
        }

        // java.nio.charset
        if (is(Charset.class)) {
            return val(StandardCharsets.UTF_8, StandardCharsets.US_ASCII, StandardCharsets.UTF_8);
        }

        return Optional.empty();
    }
}
