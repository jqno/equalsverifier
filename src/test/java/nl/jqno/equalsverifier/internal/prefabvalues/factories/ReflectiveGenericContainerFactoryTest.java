package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import com.google.common.base.Optional;
import nl.jqno.equalsverifier.internal.prefabvalues.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashSet;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("rawtypes")
public class ReflectiveGenericContainerFactoryTest {
    private static final TypeTag STRING_TYPETAG = new TypeTag(String.class);
    private static final TypeTag STRINGOPTIONAL_TYPETAG = new TypeTag(Optional.class, STRING_TYPETAG);
    private static final TypeTag OBJECT_TYPETAG = new TypeTag(Object.class);
    private static final TypeTag WILDCARDOPTIONAL_TYPETAG = new TypeTag(Optional.class, OBJECT_TYPETAG);
    private static final TypeTag RAWOPTIONAL_TYPETAG = new TypeTag(Optional.class);

    private static final ReflectiveGenericContainerFactory<Optional> OPTIONAL_FACTORY =
            new ReflectiveGenericContainerFactory<>("com.google.common.base.Optional", "of", Object.class);

    private final PrefabValues prefabValues = new PrefabValues();
    private final LinkedHashSet<TypeTag> typeStack = new LinkedHashSet<>();
    private String red;
    private String black;
    private Object redObject;
    private Object blackObject;

    @Before
    public void setUp() {
        JavaApiPrefabValues.addTo(prefabValues);
        red = prefabValues.giveRed(STRING_TYPETAG);
        black = prefabValues.giveBlack(STRING_TYPETAG);
        redObject = prefabValues.giveRed(OBJECT_TYPETAG);
        blackObject = prefabValues.giveBlack(OBJECT_TYPETAG);
    }

    @Test
    public void createOptionalsOfString() {
        Tuple<Optional> tuple = OPTIONAL_FACTORY.createValues(STRINGOPTIONAL_TYPETAG, prefabValues, typeStack);
        assertEquals(Optional.of(red), tuple.getRed());
        assertEquals(Optional.of(black), tuple.getBlack());
    }

    @Test
    public void createOptionalsOfWildcard() {
        Tuple<Optional> tuple = OPTIONAL_FACTORY.createValues(WILDCARDOPTIONAL_TYPETAG, prefabValues, typeStack);
        assertEquals(Optional.of(redObject), tuple.getRed());
        assertEquals(Optional.of(blackObject), tuple.getBlack());
    }

    @Test
    public void createRawOptionals() {
        Tuple<Optional> tuple = OPTIONAL_FACTORY.createValues(RAWOPTIONAL_TYPETAG, prefabValues, typeStack);
        assertEquals(Optional.of(redObject), tuple.getRed());
        assertEquals(Optional.of(blackObject), tuple.getBlack());
    }
}
