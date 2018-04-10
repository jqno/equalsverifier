package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.TwoElementEnum;
import org.junit.Before;
import org.junit.Test;

import java.util.EnumSet;
import java.util.LinkedHashSet;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("rawtypes")
public class ReflectiveEnumSetFactoryTest {
    private final ReflectiveEnumSetFactory factory = new ReflectiveEnumSetFactory();
    private final PrefabValues prefabValues = new PrefabValues();
    private final LinkedHashSet<TypeTag> emptyTypeStack = new LinkedHashSet<>();

    @Before
    public void setUp() {
        prefabValues.addFactory(Enum.class, TwoElementEnum.ONE, TwoElementEnum.TWO, TwoElementEnum.ONE);
    }

    @Test
    public void createSpecificEnumSet() {
        TypeTag tag = new TypeTag(EnumSet.class, new TypeTag(TwoElementEnum.class));
        Tuple<EnumSet> tuple = factory.createValues(tag, prefabValues, emptyTypeStack);

        assertEquals(EnumSet.of(TwoElementEnum.ONE), tuple.getRed());
        assertEquals(EnumSet.of(TwoElementEnum.TWO), tuple.getBlack());
    }

    @Test
    public void createWildcardEnumSet() {
        TypeTag tag = new TypeTag(EnumSet.class, new TypeTag(Object.class));
        Tuple<EnumSet> tuple = factory.createValues(tag, prefabValues, emptyTypeStack);

        assertEquals(EnumSet.of(TwoElementEnum.ONE), tuple.getRed());
        assertEquals(EnumSet.of(TwoElementEnum.TWO), tuple.getBlack());
    }

    @Test
    public void createRawEnumSet() {
        TypeTag tag = new TypeTag(EnumSet.class);
        Tuple<EnumSet> tuple = factory.createValues(tag, prefabValues, emptyTypeStack);

        assertEquals(EnumSet.of(TwoElementEnum.ONE), tuple.getRed());
        assertEquals(EnumSet.of(TwoElementEnum.TWO), tuple.getBlack());
    }
}
