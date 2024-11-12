package nl.jqno.equalsverifier.internal.reflection.instantiation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.AttributedString;
import java.util.Map;
import nl.jqno.equalsverifier.internal.exceptions.ModuleException;
import nl.jqno.equalsverifier.internal.exceptions.RecursionException;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.GenericPrefabValueProvider.GenericFactories;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider.Attributes;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import nl.jqno.equalsverifier.testhelpers.types.RecursiveTypeHelper.NodeArray;
import nl.jqno.equalsverifier.testhelpers.types.RecursiveTypeHelper.TwoStepNodeArrayA;
import nl.jqno.equalsverifier.testhelpers.types.RecursiveTypeHelper.TwoStepNodeArrayB;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.AllArrayTypesContainer;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.AllTypesContainer;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.OneElementEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

public class DifficultClassValueProviderTest {

    private static final Attributes EMPTY_ATTRIBUTES = Attributes.unlabeled();
    private final Objenesis objenesis = new ObjenesisStd();
    private final PrefabValueProvider prefabs = new PrefabValueProvider();
    private final ValueProvider valueProvider = DefaultValueProviders.withVintage(
        prefabs,
        new GenericFactories(),
        objenesis
    );

    private TypeTag tag;

    @Test
    public void allTypes() {
        tag = new TypeTag(AllTypesContainer.class);
        check();
    }

    @Test
    public void allArrayTypes() {
        tag = new TypeTag(AllArrayTypesContainer.class);
        check();
    }

    @Test
    public void mapWithSingleElementEnumKey() {
        tag = new TypeTag(Map.class, new TypeTag(OneElementEnum.class), new TypeTag(Object.class));
        check();
    }

    @Test
    @DisabledForJreRange(max = JRE.JAVA_11)
    public void moduleException_for_inaccessibleClass() {
        tag = new TypeTag(InaccessibleContainer.class);
        checkForFailure(ModuleException.class);
    }

    @Test
    public void oneStepRecursiveArray() {
        prefabs.register(
            NodeArray.class,
            null,
            Tuple.of(new NodeArray(), new NodeArray(), new NodeArray())
        );
        tag = new TypeTag(NodeArray.class);
        check();
    }

    @Test
    public void recursionException_for_oneStepRecursiveArray() {
        tag = new TypeTag(NodeArray.class);
        checkForFailure(RecursionException.class);
    }

    @Test
    public void twoStepRecursiveArray() {
        prefabs.register(
            TwoStepNodeArrayB.class,
            null,
            Tuple.of(new TwoStepNodeArrayB(), new TwoStepNodeArrayB(), new TwoStepNodeArrayB())
        );
        tag = new TypeTag(TwoStepNodeArrayA.class);
        check();
    }

    @Test
    public void recursionException_for_twoStepRecursiveArray() {
        tag = new TypeTag(TwoStepNodeArrayA.class);
        checkForFailure(RecursionException.class);
    }

    private void check() {
        Tuple<?> value = valueProvider.provideOrThrow(tag, EMPTY_ATTRIBUTES);

        Class<?> expected = tag.getType();
        Class<?> actual = value.getRed().getClass();
        assertTrue(expected.isAssignableFrom(actual));
    }

    private void checkForFailure(Class<? extends Throwable> expectedException) {
        ExpectedException
            .when(() -> valueProvider.provideOrThrow(tag, EMPTY_ATTRIBUTES))
            .assertThrows(expectedException);
    }

    @SuppressWarnings("unused")
    static final class InaccessibleContainer {

        private AttributedString as;

        public InaccessibleContainer(AttributedString as) {
            this.as = as;
        }
    }
}
