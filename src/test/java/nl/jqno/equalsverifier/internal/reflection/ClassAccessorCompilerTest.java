package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;
import nl.jqno.equalsverifier.internal.prefabvalues.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.testhelpers.StringCompilerTestBase;
import org.junit.Before;
import org.junit.Test;

public class ClassAccessorCompilerTest extends StringCompilerTestBase {

    private PrefabValues prefabValues;

    @Before
    public void setup() {
        FactoryCache factoryCache = JavaApiPrefabValues.build();
        prefabValues = new PrefabValues(factoryCache);
    }

    @Test
    public void isRecord() {
        assumeTrue(isRecordsAvailable());
        Class<?> record = compileSimpleRecord();
        ClassAccessor<?> accessor = ClassAccessor.of(record, prefabValues);
        assertTrue(accessor.isRecord());
    }
}
