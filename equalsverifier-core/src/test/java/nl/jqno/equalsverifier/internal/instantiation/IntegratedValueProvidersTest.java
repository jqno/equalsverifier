package nl.jqno.equalsverifier.internal.instantiation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import nl.jqno.equalsverifier.Mode;
import nl.jqno.equalsverifier.internal.exceptions.RecursionException;
import nl.jqno.equalsverifier.internal.instantiation.vintage.FactoryCache;
import nl.jqno.equalsverifier.internal.reflection.FieldCache;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.util.ValueProviderCreator;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import nl.jqno.equalsverifier_testhelpers.types.RecursiveTypeHelper.NodeArray;
import nl.jqno.equalsverifier_testhelpers.types.RecursiveTypeHelper.TwoStepNodeArrayA;
import nl.jqno.equalsverifier_testhelpers.types.RecursiveTypeHelper.TwoStepNodeArrayB;
import nl.jqno.equalsverifier_testhelpers.types.TypeHelper.AllArrayTypesContainer;
import nl.jqno.equalsverifier_testhelpers.types.TypeHelper.AllTypesContainer;
import nl.jqno.equalsverifier_testhelpers.types.TypeHelper.SimpleEnum;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.objenesis.ObjenesisStd;

public class IntegratedValueProvidersTest {

    private static final Set<Mode> SKIP_MOCKITO = Set.of(Mode.skipMockito());
    private static final Attributes SOME_ATTRIBUTES = Attributes.named("someFieldName");

    private static final TypeTag NODE_ARRAY_TAG = new TypeTag(NodeArray.class);
    private static final TypeTag TWOSTEP_NODE_ARRAY_A_TAG = new TypeTag(TwoStepNodeArrayA.class);

    private UserPrefabValueProvider userPrefabs = new UserPrefabValueProvider();
    private ValueProvider sut = ValueProviderCreator
            .create(SKIP_MOCKITO, userPrefabs, new FactoryCache(), new FieldCache(), new ObjenesisStd());

    @Test
    void instantiateAllTypes() {
        var actual = sut.<AllTypesContainer>provide(new TypeTag(AllTypesContainer.class), SOME_ATTRIBUTES).get().red();

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
        var actual = sut
                .<AllArrayTypesContainer>provide(new TypeTag(AllArrayTypesContainer.class), SOME_ATTRIBUTES)
                .get()
                .red();

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
    void instantiateOneStepRecursiveArray() {
        userPrefabs
                .register(NodeArray.class, new NodeArray(null), new NodeArray(new NodeArray[] {}), new NodeArray(null));
        var actual = sut.<NodeArray>provideOrThrow(NODE_ARRAY_TAG, SOME_ATTRIBUTES);
        assertThat(actual.blue()).isEqualTo(new NodeArray(new NodeArray[] {}));
    }

    @Test
    void throwRecursionException_whenAttemptingToInstantiateOneStepRecursiveArray() {
        ExpectedException
                .when(() -> sut.provide(NODE_ARRAY_TAG, SOME_ATTRIBUTES))
                .assertThrows(RecursionException.class);
    }

    @Test
    void instantiateTwoStepRecursiveArray() {
        userPrefabs
                .register(
                    TwoStepNodeArrayB.class,
                    new TwoStepNodeArrayB(null),
                    new TwoStepNodeArrayB(new TwoStepNodeArrayA[] {}),
                    new TwoStepNodeArrayB(null));
        var actual = sut.<TwoStepNodeArrayA>provideOrThrow(TWOSTEP_NODE_ARRAY_A_TAG, SOME_ATTRIBUTES);
        assertThat(actual.red())
                .isEqualTo(new TwoStepNodeArrayA(new TwoStepNodeArrayB[] { new TwoStepNodeArrayB(null) }));
    }

    @Test
    @Disabled("Restore when FallbackFactory no longer creates new normal object")
    void throwRecursionException_whenAttemptingToInstantiateTwoStepRecursiveArray() {
        ExpectedException
                .when(() -> sut.provide(TWOSTEP_NODE_ARRAY_A_TAG, SOME_ATTRIBUTES))
                .assertThrows(RecursionException.class);
    }
}
