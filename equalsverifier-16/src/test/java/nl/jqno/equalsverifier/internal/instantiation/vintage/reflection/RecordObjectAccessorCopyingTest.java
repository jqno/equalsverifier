package nl.jqno.equalsverifier.internal.instantiation.vintage.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.FieldProbe;
import nl.jqno.equalsverifier.internal.reflection.Instantiator;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

class RecordObjectAccessorCopyingTest {

    private Objenesis objenesis = new ObjenesisStd();

    @Test
    void copyHappyPath() {
        Object original = instantiate(SimpleRecord.class);
        Object copy = copyOf(original);

        assertThat(copy).isNotSameAs(original).isEqualTo(original);
    }

    @Test
    void shallowCopy() {
        Object original = instantiate(RecordContainer.class);
        Object copy = copyOf(original);
        RecordObjectAccessor<?> originalAccessor = create(original);
        RecordObjectAccessor<?> copyAccessor = create(copy);

        assertThat(copy).isNotSameAs(original);
        for (FieldProbe p : FieldIterable.of(original.getClass())) {
            Object a = originalAccessor.getField(p);
            Object b = copyAccessor.getField(p);
            assertThat(b).isSameAs(a);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> RecordObjectAccessor<T> create(T object) {
        return new RecordObjectAccessor<T>(object, (Class<T>) object.getClass());
    }

    private <T> T instantiate(Class<T> type) {
        return Instantiator.of(type, objenesis).instantiate();
    }

    private <T> T copyOf(T from) {
        return create(from).copy(objenesis);
    }

    record SimpleRecord(int i, String s) {}

    record RecordContainer(SimpleRecord r) {}
}
