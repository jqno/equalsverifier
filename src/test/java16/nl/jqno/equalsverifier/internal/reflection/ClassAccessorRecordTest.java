package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.jupiter.api.Assertions.assertTrue;

import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;
import nl.jqno.equalsverifier.internal.prefabvalues.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClassAccessorRecordTest {

    private PrefabValues prefabValues;

    @BeforeEach
    public void setup() {
        FactoryCache factoryCache = JavaApiPrefabValues.build();
        prefabValues = new PrefabValues(factoryCache);
    }

    @Test
    public void isRecord() {
        ClassAccessor<?> accessor = ClassAccessor.of(SimpleRecord.class, prefabValues);
        assertTrue(accessor.isRecord());
    }

    record SimpleRecord(int i) {}
}
