package nl.jqno.equalsverifier.internal.instantiation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.text.AttributedString;
import java.util.Objects;
import java.util.Set;

import nl.jqno.equalsverifier.Mode;
import nl.jqno.equalsverifier.internal.exceptions.MessagingException;
import nl.jqno.equalsverifier.internal.exceptions.ModuleException;
import nl.jqno.equalsverifier.internal.exceptions.RecursionException;
import nl.jqno.equalsverifier.internal.instantiation.vintage.FactoryCache;
import nl.jqno.equalsverifier.internal.reflection.FieldCache;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.util.ValueProviderBuilder;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import nl.jqno.equalsverifier_testhelpers.types.Point;
import nl.jqno.equalsverifier_testhelpers.types.RecursiveTypeHelper.*;
import nl.jqno.equalsverifier_testhelpers.types.TypeHelper.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.objenesis.ObjenesisStd;

public class IntegratedValueProvidersTest {

    private static final Set<Mode> SKIP_MOCKITO = Set.of(Mode.skipMockito());
    private static final Attributes SOME_ATTRIBUTES = Attributes.named("someFieldName");

    private UserPrefabValueCaches prefabs = new UserPrefabValueCaches();
    private ValueProvider sut =
            ValueProviderBuilder.build(SKIP_MOCKITO, prefabs, new FactoryCache(), new FieldCache(), new ObjenesisStd());

    @Test
    void instantiateAllTypes() {
        var actual = provide(AllTypesContainer.class).red();

        assertThat(actual._boolean).isTrue();
        assertThat(actual._byte).isEqualTo((byte) 1);
        assertThat(actual._char).isEqualTo('a');
        assertThat(actual._double).isEqualTo(0.5);
        assertThat(actual._float).isEqualTo(0.5F);
        assertThat(actual._int).isEqualTo(1);
        assertThat(actual._long).isEqualTo(1);
        assertThat(actual._short).isEqualTo((short) 1);

        assertThat(actual._Boolean).isTrue();
        assertThat(actual._Byte).isEqualTo((byte) 1);
        assertThat(actual._Char).isEqualTo('α');
        assertThat(actual._Double).isEqualTo(0.5);
        assertThat(actual._Float).isEqualTo(0.5F);
        assertThat(actual._Int).isEqualTo(1000);
        assertThat(actual._Long).isEqualTo(1000);
        assertThat(actual._Short).isEqualTo((short) 1000);

        assertThat(actual._enum).isEqualTo(SimpleEnum.FIRST);
        assertThat(actual._array).isEqualTo(new int[] { 1 });
        assertThat(actual._object).isNotNull();
        assertThat(actual._string).isEqualTo("one");
    }

    @Test
    void instantiateArrayTypes() {
        var actual = provide(AllArrayTypesContainer.class).red();

        assertThat(actual.booleans).isEqualTo(new boolean[] { true });
        assertThat(actual.bytes).isEqualTo(new byte[] { 1 });
        assertThat(actual.chars).isEqualTo(new char[] { 'a' });
        assertThat(actual.doubles).isEqualTo(new double[] { 0.5 });
        assertThat(actual.floats).isEqualTo(new float[] { 0.5F });
        assertThat(actual.ints).isEqualTo(new int[] { 1 });
        assertThat(actual.longs).isEqualTo(new long[] { 1L });
        assertThat(actual.shorts).isEqualTo(new short[] { 1 });

        assertThat(actual.Booleans).isEqualTo(new Boolean[] { true });
        assertThat(actual.Bytes).isEqualTo(new Byte[] { 1 });
        assertThat(actual.Characters).isEqualTo(new Character[] { 'α' });
        assertThat(actual.Doubles).isEqualTo(new Double[] { 0.5 });
        assertThat(actual.Floats).isEqualTo(new Float[] { 0.5F });
        assertThat(actual.Integers).isEqualTo(new Integer[] { 1000 });
        assertThat(actual.Longs).isEqualTo(new Long[] { 1000L });
        assertThat(actual.Shorts).isEqualTo(new Short[] { 1000 });

        assertThat(actual.enums).isEqualTo(new SimpleEnum[] { SimpleEnum.FIRST });
        assertThat(actual.arrays).isEqualTo(new int[][] { { 1 } });
        assertThat(actual.objects).hasSize(1).doesNotContainNull();
        assertThat(actual.strings).isEqualTo(new String[] { "one" });
    }

    @Test
    void redIsDifferentFromBlue() {
        var tuple = provide(Point.class);
        assertThat(tuple.red()).isNotEqualTo(tuple.blue());
    }

    @Test
    void redHasSameValueButIsNotSameObjectAsRedCopy() {
        var tuple = provide(Point.class);
        assertThat(tuple.red()).isEqualTo(tuple.redCopy()).isNotSameAs(tuple.redCopy());
    }

    @Test
    void doesntTouchStaticFields() {
        var tuple = provide(IntContainer.class);
        assertThat(tuple)
                .isEqualTo(new Tuple<>(new IntContainer(1, 1), new IntContainer(2, 2), new IntContainer(1, 1)));
        // Assert that static fields are untouched
        assertThat(IntContainer.staticI).isEqualTo(-100);
        assertThat(IntContainer.STATIC_FINAL_I).isEqualTo(-10);
    }

    @Test
    void instantiateFromCache() {
        prefabs.register(String.class, "x", "y", "x");
        var actual = provide(String.class);
        assertThat(actual).isEqualTo(new Tuple<>("x", "y", "x"));
    }

    @Test
    void instantiateFromGenericCache_withArity1() {
        prefabs.registerGeneric(Generic1.class, Generic1::new);
        var actual = sut
                .<Generic1<String>>provideOrThrow(
                    new TypeTag(Generic1.class, new TypeTag(String.class)),
                    SOME_ATTRIBUTES);
        assertThat(actual.red().t).isEqualTo("one");
    }

    @Test
    void instantiateFromGenericCache_withArity2() {
        prefabs.registerGeneric(Generic2.class, Generic2::new);
        var actual = sut
                .<Generic2<String, String>>provideOrThrow(
                    new TypeTag(Generic2.class, new TypeTag(String.class), new TypeTag(String.class)),
                    SOME_ATTRIBUTES);
        assertThat(actual.red().t).isEqualTo("one");
        assertThat(actual.red().u).isEqualTo("one");
    }

    @Test
    void instantiateNestedGenerics() {
        var actual = provide(GenericContainerContainerContainer.class).red();

        assertThat(actual.strings.ts.t).isNotNull();
        assertThat(actual.strings.ts.t.getClass()).isEqualTo(String.class);
        assertThat(actual.points.ts.t).isNotNull();
        assertThat(actual.points.ts.t.getClass()).isEqualTo(Point.class);
    }

    @Test
    void instantiateInterfaceField() {
        var actual = provide(InterfaceContainer.class).red();
        assertThat(actual.field).isNotNull();
        assertThat(actual.field.getClass()).isAssignableTo(Interface.class);
    }

    @Test
    void instantiateAbstractClassField() {
        var actual = provide(AbstractClassContainer.class).red();
        assertThat(actual.field).isNotNull();
        assertThat(actual.field.getClass()).isAssignableTo(AbstractClass.class);
    }

    @Test
    void instantiateSameClassTwiceButNoRecursion() {
        provide(NotRecursiveA.class);
    }

    @Test
    void recursiveWithAnotherFieldFirst() {
        assertThatThrownBy(() -> provide(RecursiveWithAnotherFieldFirst.class))
                .isInstanceOf(RecursionException.class)
                .extracting(e -> ((MessagingException) e).getDescription())
                .asString()
                .contains(RecursiveWithAnotherFieldFirst.class.getSimpleName())
                .doesNotContain(RecursiveThisIsTheOtherField.class.getSimpleName());
    }

    @Test
    void instantiateOneStepRecursiveObject() {
        prefabs.register(Node.class, new Node(null), new Node(new Node(null)), new Node(null));
        var actual = provide(Node.class);
        assertThat(actual.blue()).isEqualTo(new Node(new Node(null)));
    }

    @Test
    void throwRecursionException_whenAttemptingToInstantiateOneStepRecursiveObject() {
        ExpectedException.when(() -> provide(Node.class)).assertThrows(RecursionException.class);
    }

    @Test
    void instantiateTwoStepRecursiveObject() {
        prefabs
                .register(
                    TwoStepNodeB.class,
                    new TwoStepNodeB(null),
                    new TwoStepNodeB(new TwoStepNodeA(null)),
                    new TwoStepNodeB(null));
        var actual = provide(TwoStepNodeA.class);
        assertThat(actual.red()).isEqualTo(new TwoStepNodeA(new TwoStepNodeB(null)));
    }

    @Test
    void throwRecursionException_whenAttemptingToInstantiateTwoStepRecursiveObject() {
        ExpectedException.when(() -> provide(TwoStepNodeA.class)).assertThrows(RecursionException.class);
    }

    @Test
    @Disabled("Find out why the second class isn't mentioned")
    void recursionExceptionHasProperErrorMessage() {
        assertThatThrownBy(() -> provide(TwoStepNodeA.class))
                .isInstanceOf(RecursionException.class)
                .extracting(e -> ((MessagingException) e).getDescription())
                .asString()
                .contains(TwoStepNodeA.class.getSimpleName(), TwoStepNodeB.class.getSimpleName());
    }

    @Test
    void instantiateOneStepRecursiveArray() {
        prefabs.register(NodeArray.class, new NodeArray(null), new NodeArray(new NodeArray[] {}), new NodeArray(null));
        var actual = provide(NodeArray.class);
        assertThat(actual.blue()).isEqualTo(new NodeArray(new NodeArray[] {}));
    }

    @Test
    void throwRecursionException_whenAttemptingToInstantiateOneStepRecursiveArray() {
        ExpectedException.when(() -> provide(NodeArray.class)).assertThrows(RecursionException.class);
    }

    @Test
    void instantiateTwoStepRecursiveArray() {
        prefabs
                .register(
                    TwoStepNodeArrayB.class,
                    new TwoStepNodeArrayB(null),
                    new TwoStepNodeArrayB(new TwoStepNodeArrayA[] {}),
                    new TwoStepNodeArrayB(null));
        var actual = provide(TwoStepNodeArrayA.class);
        assertThat(actual.red())
                .isEqualTo(new TwoStepNodeArrayA(new TwoStepNodeArrayB[] { new TwoStepNodeArrayB(null) }));
    }

    @Test
    void throwRecursionException_whenAttemptingToInstantiateTwoStepRecursiveArray() {
        ExpectedException.when(() -> provide(TwoStepNodeArrayA.class)).assertThrows(RecursionException.class);
    }

    @Test
    void throwModuleException_whenAttemptingToInstantiateSomethingOutOfModule() {
        ExpectedException.when(() -> provide(AttributedString.class)).assertThrows(ModuleException.class);
    }

    @Test
    void throwModuleException_whenAttemptingToInstantiateSomethingThatContainsSomethingOutOfModule() {
        ExpectedException.when(() -> provide(InaccessibleContainer.class)).assertThrows(ModuleException.class);
    }

    private <T> Tuple<T> provide(Class<T> type) {
        return sut.provideOrThrow(new TypeTag(type), SOME_ATTRIBUTES);
    }

    private static final class IntContainer {

        @SuppressWarnings("unused")
        private static int staticI = -100;

        private static final int STATIC_FINAL_I = -10;

        @SuppressWarnings("unused")
        private final int finalI;

        @SuppressWarnings("unused")
        private int i;

        private IntContainer(int finalI, int i) {
            this.finalI = finalI;
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof IntContainer other && finalI == other.finalI && i == other.i;
        }

        @Override
        public int hashCode() {
            return Objects.hash(finalI, i);
        }
    }

    static final class GenericContainerContainerContainer {

        private final GenericContainerContainer<String> strings =
                new GenericContainerContainer<>(new GenericContainer<>(null));
        private final GenericContainerContainer<Point> points =
                new GenericContainerContainer<>(new GenericContainer<>(null));
    }

    static final class GenericContainerContainer<T> {

        private GenericContainer<T> ts;

        public GenericContainerContainer(GenericContainer<T> ts) {
            this.ts = ts;
        }
    }

    static final class GenericContainer<T> {
        private T t;

        public GenericContainer(T t) {
            this.t = t;
        }
    }

    @SuppressWarnings("unused")
    static final class InaccessibleContainer {

        private AttributedString as;

        public InaccessibleContainer(AttributedString as) {
            this.as = as;
        }
    }
}
