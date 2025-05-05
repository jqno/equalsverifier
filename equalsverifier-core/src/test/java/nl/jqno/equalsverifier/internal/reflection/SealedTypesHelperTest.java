package nl.jqno.equalsverifier.internal.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class SealedTypesHelperTest {

    @Test
    void twoLevels() {
        var actual = SealedTypesFinder.findInstantiableSubclass(TwoLevelParent.class);
        assertThat(actual.get()).isEqualTo(TwoLevelChild.class);
    }

    sealed interface TwoLevelParent {}

    final class TwoLevelChild implements TwoLevelParent {}

    @Test
    void fourLevels() {
        var actual = SealedTypesFinder.findInstantiableSubclass(FourLevelParent.class);
        assertThat(actual.get()).isEqualTo(FourLevelChild.class);
    }

    sealed interface FourLevelParent {}

    sealed interface FourLevelMiddle1 extends FourLevelParent {}

    sealed interface FourLevelMiddle2 extends FourLevelMiddle1 {}

    final class FourLevelChild implements FourLevelMiddle2 {}

    @Test
    void allConcrete() {
        var actual = SealedTypesFinder.findInstantiableSubclass(AllConcreteParent.class);
        assertThat(actual.get()).isEqualTo(AllConcreteMiddle.class);
    }

    sealed class AllConcreteParent {}

    sealed class AllConcreteMiddle extends AllConcreteParent {}

    final class AllConcreteChild extends AllConcreteMiddle {}

    @Test
    void nonSealedAtTheBottom() {
        var actual = SealedTypesFinder.findInstantiableSubclass(NonSealedAtTheBottomParent.class);
        assertThat(actual.get()).isEqualTo(NonSealedAtTheBottomChild.class);
    }

    sealed interface NonSealedAtTheBottomParent {}

    non-sealed interface NonSealedAtTheBottomChild extends NonSealedAtTheBottomParent {}

    @Test
    void notSealed() {
        var actual = SealedTypesFinder.findInstantiableSubclass(Object.class);
        assertThat(actual).isEqualTo(Optional.empty());
    }
}
