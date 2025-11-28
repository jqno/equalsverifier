package nl.jqno.equalsverifier.internal.instantiation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.Set;

import nl.jqno.equalsverifier.Mode;
import nl.jqno.equalsverifier.internal.reflection.FieldCache;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.util.ValueProviderBuilder;
import org.junit.jupiter.api.Test;
import org.objenesis.ObjenesisStd;

public class IntegratedValueProviderModernFeaturesTest {

    private static final Set<Mode> SKIP_MOCKITO = Set.of(Mode.skipMockito());
    private static final Attributes SOME_ATTRIBUTES = Attributes.named("someFieldName");

    private UserPrefabValueCaches prefabs = new UserPrefabValueCaches();
    private ValueProvider sut = ValueProviderBuilder.build(SKIP_MOCKITO, prefabs, new FieldCache(), new ObjenesisStd());

    @Test
    void redCopyHasTheSameValuesAsRed_whenSutContainsGenericValueThatNeedsToBeIdenticalInRedAndRedCopy() {
        var tuple =
                sut.<GenericRecordContainer>provideOrThrow(new TypeTag(GenericRecordContainer.class), SOME_ATTRIBUTES);

        assertThat(tuple.redCopy()).isEqualTo(tuple.red());
        assertThat(tuple.redCopy()).isNotSameAs(tuple.red());
    }

    @Test
    void valuesAreASubtypeOfSealedType() {
        var tuple = sut
                .<SealedParentWithFinalChild>provideOrThrow(
                    new TypeTag(SealedParentWithFinalChild.class),
                    SOME_ATTRIBUTES);

        assertThat(tuple.red().getClass())
                .isAssignableTo(SealedParentWithFinalChild.class)
                .isNotEqualTo(SealedParentWithFinalChild.class);
    }

    @Test
    void redCopyHasTheSameValuesAsRed_whenSutIsAbstractSealedAndPermittedTypeAddsField() {
        var tuple = sut
                .<SealedParentWithFinalChild>provideOrThrow(
                    new TypeTag(SealedParentWithFinalChild.class),
                    SOME_ATTRIBUTES);

        assertThat(tuple.redCopy()).isEqualTo(tuple.red());
        assertThat(tuple.redCopy()).isNotSameAs(tuple.red());
    }

    record GenericRecord<T>(T t) {}

    record GenericRecordContainer(GenericRecord<?> bgr) {}

    public abstract static sealed class SealedParentWithFinalChild permits FinalSealedChild {

        private final int i;

        public SealedParentWithFinalChild(int i) {
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof SealedParentWithFinalChild other && i == other.i;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i);
        }
    }

    public static final class FinalSealedChild extends SealedParentWithFinalChild {

        private final int j;

        public FinalSealedChild(int i, int j) {
            super(i);
            this.j = j;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof FinalSealedChild other && super.equals(obj) && j == other.j;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), j);
        }
    }
}
