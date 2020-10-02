package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.prefabvalues.factories.Factories.values;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

import java.lang.reflect.Constructor;
import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;
import nl.jqno.equalsverifier.internal.prefabvalues.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.testhelpers.StringCompilerTestBase;
import org.junit.Before;
import org.junit.Test;

public class RecordObjectAccessorScramblingTest extends StringCompilerTestBase {
    private PrefabValues prefabValues;
    private Constructor<?> constructor;

    @Before
    public void setup() throws Exception {
        assumeTrue(isRecordsAvailable());
        Class<?> type = compile(POINT_RECORD_CLASS_NAME, POINT_RECORD_CLASS);
        constructor = type.getDeclaredConstructor(int.class, int.class);
        FactoryCache factoryCache = JavaApiPrefabValues.build();
        factoryCache.put(
                type,
                values(
                        constructor.newInstance(1, 2),
                        constructor.newInstance(2, 3),
                        constructor.newInstance(1, 2)));
        prefabValues = new PrefabValues(factoryCache);
    }

    @Test
    public void scramble() throws Exception {
        Object original = constructor.newInstance(1, 2);

        Object scrambled = doScramble(original);
        assertFalse(original.equals(scrambled));
    }

    @SuppressWarnings("unchecked")
    private <T> RecordObjectAccessor<T> create(T object) {
        return new RecordObjectAccessor<T>(object, (Class<T>) object.getClass());
    }

    private ObjectAccessor<Object> doScramble(Object object) {
        return create(object).scramble(prefabValues, TypeTag.NULL);
    }

    private static final String POINT_RECORD_CLASS_NAME = "Point";
    private static final String POINT_RECORD_CLASS = "public record Point(int x, int y) {}";
}
