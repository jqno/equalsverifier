package nl.jqno.equalsverifier.internal.reflection.instantiation.builtin;

import static nl.jqno.equalsverifier.internal.reflection.instantiation.builtin.BuiltinHelper.tuple;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Optional;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider;

// CHECKSTYLE OFF: CyclomaticComplexity
// CHECKSTYLE OFF: NPathComplexity

public class BuiltinJavaLangValueProvider implements ValueProvider {

    @SuppressFBWarnings(
        value = "DM_STRING_CTOR",
        justification = "We want to make an actual copy of a String"
    )
    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes) {
        // Primitives
        if (tag.matches(boolean.class)) {
            return tuple(true, false, true);
        }
        if (tag.matches(byte.class)) {
            return tuple((byte) 1, (byte) 2, (byte) 3);
        }
        if (tag.matches(char.class)) {
            return tuple('a', 'b', 'a');
        }
        if (tag.matches(double.class)) {
            return tuple(0.5D, 1.0D, 0.5D);
        }
        if (tag.matches(float.class)) {
            return tuple(0.5F, 1.0F, 0.5F);
        }
        if (tag.matches(int.class)) {
            return tuple(1, 2, 1);
        }
        if (tag.matches(long.class)) {
            return tuple(1L, 2L, 1L);
        }
        if (tag.matches(short.class)) {
            return tuple((short) 1, (short) 2, (short) 1);
        }

        // Boxed types
        if (tag.matches(Boolean.class)) {
            return tuple(true, false, true);
        }
        if (tag.matches(Byte.class)) {
            return tuple((byte) 1, (byte) 2, (byte) 1);
        }
        if (tag.matches(Character.class)) {
            return tuple('α', 'ω', Character.valueOf('α'));
        }
        if (tag.matches(Double.class)) {
            return tuple(0.5D, 1.0D, Double.valueOf(0.5D));
        }
        if (tag.matches(Float.class)) {
            return tuple(0.5F, 1.0F, Float.valueOf(0.5F));
        }
        if (tag.matches(Integer.class)) {
            return tuple(1000, 2000, Integer.valueOf(1000));
        }
        if (tag.matches(Long.class)) {
            return tuple(1000L, 2000L, Long.valueOf(1000L));
        }
        if (tag.matches(Short.class)) {
            return tuple((short) 1000, (short) 2000, Short.valueOf((short) 1000));
        }

        // Special classes
        if (tag.matches(Object.class)) {
            Object red = new Object();
            return tuple(red, new Object(), red);
        }
        if (tag.matches(String.class)) {
            return tuple("one", "two", new String("one"));
        }
        if (tag.matches(Enum.class)) {
            return tuple(Dummy.RED, Dummy.BLUE, Dummy.RED);
        }

        return Optional.empty();
    }

    private enum Dummy {
        RED,
        BLUE
    }
}
