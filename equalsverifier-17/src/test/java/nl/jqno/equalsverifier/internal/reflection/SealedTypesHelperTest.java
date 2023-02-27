package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import org.junit.jupiter.api.Test;

public class SealedTypesHelperTest {

    @Test
    void twoLevels() {
        var actual = SealedTypesHelper.findInstantiableSubclass(TwoLevelParent.class);
        assertEquals(TwoLevelChild.class, actual.get());
    }

    sealed interface TwoLevelParent {}

    final class TwoLevelChild implements TwoLevelParent {}

    @Test
    void fourLevels() {
        var actual = SealedTypesHelper.findInstantiableSubclass(FourLevelParent.class);
        assertEquals(FourLevelChild.class, actual.get());
    }

    sealed interface FourLevelParent {}

    sealed interface FourLevelMiddle1 extends FourLevelParent {}

    sealed interface FourLevelMiddle2 extends FourLevelMiddle1 {}

    final class FourLevelChild implements FourLevelMiddle2 {}

    @Test
    void allConcrete() {
        var actual = SealedTypesHelper.findInstantiableSubclass(AllConcreteParent.class);
        assertEquals(AllConcreteMiddle.class, actual.get());
    }

    sealed class AllConcreteParent {}

    sealed class AllConcreteMiddle extends AllConcreteParent {}

    final class AllConcreteChild extends AllConcreteMiddle {}

    @Test
    void nonSealedAtTheBottom() {
        var actual = SealedTypesHelper.findInstantiableSubclass(NonSealedAtTheBottomParent.class);
        assertEquals(NonSealedAtTheBottomChild.class, actual.get());
    }

    sealed interface NonSealedAtTheBottomParent {}

    non-sealed interface NonSealedAtTheBottomChild extends NonSealedAtTheBottomParent {}

    @Test
    void notSealed() {
        var actual = SealedTypesHelper.findInstantiableSubclass(Object.class);
        assertEquals(Optional.empty(), actual);
    }
}
