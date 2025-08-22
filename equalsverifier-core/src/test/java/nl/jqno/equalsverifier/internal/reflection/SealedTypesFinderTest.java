package nl.jqno.equalsverifier.internal.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SealedTypesFinderTest {

    @Test
    void twoLevels() {
        var probe = ClassProbe.of(TwoLevelParent.class);
        var actual = SealedTypesFinder.findInstantiableSubclass(probe);
        assertThat(actual.get()).isEqualTo(TwoLevelChild.class);
    }

    sealed interface TwoLevelParent {}

    static final class TwoLevelChild implements TwoLevelParent {}

    @Test
    void fourLevels() {
        var probe = ClassProbe.of(FourLevelParent.class);
        var actual = SealedTypesFinder.findInstantiableSubclass(probe);
        assertThat(actual.get()).isEqualTo(FourLevelChild.class);
    }

    sealed interface FourLevelParent {}

    sealed interface FourLevelMiddle1 extends FourLevelParent {}

    sealed interface FourLevelMiddle2 extends FourLevelMiddle1 {}

    static final class FourLevelChild implements FourLevelMiddle2 {}

    @Test
    void allConcrete() {
        var probe = ClassProbe.of(AllConcreteParent.class);
        var actual = SealedTypesFinder.findInstantiableSubclass(probe);
        assertThat(actual.get()).isEqualTo(AllConcreteParent.class);
    }

    sealed static class AllConcreteParent {}

    sealed static class AllConcreteMiddle extends AllConcreteParent {}

    static final class AllConcreteChild extends AllConcreteMiddle {}

    @Test
    void abstractTopThreeLevels() {
        var probe = ClassProbe.of(AbstractParent.class);
        var actual = SealedTypesFinder.findInstantiableSubclass(probe);
        assertThat(actual.get()).isEqualTo(AbstractMiddle.class);
    }

    sealed abstract static class AbstractParent {}

    sealed static class AbstractMiddle extends AbstractParent {}

    static final class AbstractChild extends AbstractMiddle {}

    @Test
    void nonSealedAtTheBottom() {
        var probe = ClassProbe.of(NonSealedAtTheBottomParent.class);
        var actual = SealedTypesFinder.findInstantiableSubclass(probe);
        assertThat(actual.get()).isEqualTo(NonSealedAtTheBottomChild.class);
    }

    sealed interface NonSealedAtTheBottomParent {}

    non-sealed interface NonSealedAtTheBottomChild extends NonSealedAtTheBottomParent {}

    @Test
    void notSealed() {
        var probe = ClassProbe.of(Object.class);
        var actual = SealedTypesFinder.findInstantiableSubclass(probe);
        assertThat(actual.get()).isEqualTo(Object.class);
    }
}
