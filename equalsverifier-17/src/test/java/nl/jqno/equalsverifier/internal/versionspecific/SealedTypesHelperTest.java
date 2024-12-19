package nl.jqno.equalsverifier.internal.versionspecific;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class SealedTypesHelperTest {

    @Test
    void twoLevels() {
        var actual = SealedTypesHelper.findInstantiableSubclass(TwoLevelParent.class);
        assertThat(actual.get()).isEqualTo(TwoLevelChild.class);
    }

    sealed interface TwoLevelParent {}

    final class TwoLevelChild implements TwoLevelParent {}

    @Test
    void fourLevels() {
        var actual = SealedTypesHelper.findInstantiableSubclass(FourLevelParent.class);
        assertThat(actual.get()).isEqualTo(FourLevelChild.class);
    }

    sealed interface FourLevelParent {}

    sealed interface FourLevelMiddle1 extends FourLevelParent {}

    sealed interface FourLevelMiddle2 extends FourLevelMiddle1 {}

    final class FourLevelChild implements FourLevelMiddle2 {}

    @Test
    void allConcrete() {
        var actual = SealedTypesHelper.findInstantiableSubclass(AllConcreteParent.class);
        assertThat(actual.get()).isEqualTo(AllConcreteMiddle.class);
    }

    sealed class AllConcreteParent {}

    sealed class AllConcreteMiddle extends AllConcreteParent {}

    final class AllConcreteChild extends AllConcreteMiddle {}

    @Test
    void nonSealedAtTheBottom() {
        var actual = SealedTypesHelper.findInstantiableSubclass(NonSealedAtTheBottomParent.class);
        assertThat(actual.get()).isEqualTo(NonSealedAtTheBottomChild.class);
    }

    sealed interface NonSealedAtTheBottomParent {}

    non-sealed interface NonSealedAtTheBottomChild extends NonSealedAtTheBottomParent {}

    @Test
    void notSealed() {
        var actual = SealedTypesHelper.findInstantiableSubclass(Object.class);
        assertThat(actual).isEqualTo(Optional.empty());
    }
}
