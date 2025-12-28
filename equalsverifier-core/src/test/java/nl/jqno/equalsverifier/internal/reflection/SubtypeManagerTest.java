package nl.jqno.equalsverifier.internal.reflection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import nl.jqno.equalsverifier.internal.exceptions.NoValueException;
import nl.jqno.equalsverifier.internal.instantiation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.ObjenesisStd;

class SubtypeManagerTest {

    private RecursionDetectingValueProvider vp;
    private final Attributes attributes = Attributes.empty();

    @BeforeEach
    void setUp() {
        vp = new RecursionDetectingValueProvider();
        var avp = new AbstractValueProvider(new BuiltinPrefabValueProvider());
        var ovp = new ObjectValueProvider(vp, new ObjenesisStd());
        var chain = new ChainedValueProvider(avp, ovp);
        vp.setValueProvider(chain);
    }

    @Test
    void giveDynamicSubclass() throws Exception {
        class Super {}
        Class<?> sub = SubtypeManager
                .giveDynamicSubclass(
                    Super.class,
                    "dynamicField",
                    b -> b.defineField("dynamicField", int.class, Visibility.PRIVATE));
        Field f = sub.getDeclaredField("dynamicField");
        assertThat(f).isNotNull();
    }

    @Test
    void giveDynamicSubclassForClassWithNoPackage() {
        Class<?> type = new ByteBuddy()
                .with(TypeValidation.DISABLED)
                .subclass(Object.class)
                .name("NoPackage")
                .make()
                .load(getClass().getClassLoader())
                .getLoaded();
        SubtypeManager.giveDynamicSubclass(type, "X", b -> b);
    }

    @Test
    void twoLevels() {
        var probe = ClassProbe.of(TwoLevelParent.class);
        var actual = SubtypeManager.findInstantiableSubclass(probe, vp, attributes);
        assertThat(actual).isEqualTo(TwoLevelChild.class);
    }

    sealed interface TwoLevelParent {}

    static final class TwoLevelChild implements TwoLevelParent {}

    @Test
    void fourLevels() {
        var probe = ClassProbe.of(FourLevelParent.class);
        var actual = SubtypeManager.findInstantiableSubclass(probe, vp, attributes);
        assertThat(actual).isEqualTo(FourLevelChild.class);
    }

    sealed interface FourLevelParent {}

    sealed interface FourLevelMiddle1 extends FourLevelParent {}

    sealed interface FourLevelMiddle2 extends FourLevelMiddle1 {}

    static final class FourLevelChild implements FourLevelMiddle2 {}

    @Test
    void allConcrete() {
        var probe = ClassProbe.of(AllConcreteParent.class);
        var actual = SubtypeManager.findInstantiableSubclass(probe, vp, attributes);
        assertThat(actual).isEqualTo(AllConcreteParent.class);
    }

    sealed static class AllConcreteParent {}

    sealed static class AllConcreteMiddle extends AllConcreteParent {}

    static final class AllConcreteChild extends AllConcreteMiddle {}

    @Test
    void abstractTopThreeLevels() {
        var probe = ClassProbe.of(AbstractParent.class);
        var actual = SubtypeManager.findInstantiableSubclass(probe, vp, attributes);
        assertThat(actual).isEqualTo(AbstractMiddle.class);
    }

    sealed abstract static class AbstractParent {}

    sealed static class AbstractMiddle extends AbstractParent {}

    static final class AbstractChild extends AbstractMiddle {}

    @Test
    void nonSealedInterfaceAtTheBottom() {
        var probe = ClassProbe.of(NonSealedAtTheBottomParent.class);
        var actual = SubtypeManager.findInstantiableSubclass(probe, vp, attributes);
        assertThat(actual)
                .isNotEqualTo(NonSealedAtTheBottomChild.class)
                .isAssignableTo(NonSealedAtTheBottomChild.class);
    }

    sealed interface NonSealedAtTheBottomParent {}

    non-sealed interface NonSealedAtTheBottomChild extends NonSealedAtTheBottomParent {}

    @Test
    void findSeveral() {
        var probe = ClassProbe.of(Hierarchy1.class);
        var actuals = SubtypeManager.findAllInstantiablePermittedSubclasses(probe);
        var hierarchy3a = SubtypeManager.giveDynamicSubclass(Hierarchy3a.class);
        assertThat(actuals).containsExactly(hierarchy3a, Hierarchy3b.class, Hierarchy2b.class);
    }

    sealed interface Hierarchy1 {}

    sealed interface Hierarchy2a extends Hierarchy1 {}

    non-sealed interface Hierarchy3a extends Hierarchy2a {}

    static final class Hierarchy3b implements Hierarchy2a {}

    static non-sealed class Hierarchy2b implements Hierarchy1 {}

    @Test
    void findNothing() {
        var probe = ClassProbe.of(OnlyRecursiveSubs.class);
        assertThatThrownBy(() -> SubtypeManager.findInstantiableSubclass(probe, vp, attributes))
                .isInstanceOf(NoValueException.class);
    }

    static record RecursiveSubContainer(OnlyRecursiveSubs x) {}

    sealed interface OnlyRecursiveSubs permits RecursiveSub1, RecursiveSub2 {}

    static record RecursiveSub1(RecursiveSubContainer x) implements OnlyRecursiveSubs {}

    static record RecursiveSub2(RecursiveSubContainer x) implements OnlyRecursiveSubs {}

    @Test
    void notSealed() {
        var probe = ClassProbe.of(Object.class);
        var actual = SubtypeManager.findInstantiableSubclass(probe, vp, attributes);
        assertThat(actual).isEqualTo(Object.class);
    }
}
