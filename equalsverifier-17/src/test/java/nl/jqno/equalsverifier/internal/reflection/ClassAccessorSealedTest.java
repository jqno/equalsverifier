package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import nl.jqno.equalsverifier.integration.extended_contract.SealedClasses.SealedChild;
import nl.jqno.equalsverifier.integration.extended_contract.SealedClasses.SealedParent;
import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;
import nl.jqno.equalsverifier.internal.prefabvalues.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClassAccessorSealedTest {

    private PrefabValues prefabValues;

    @BeforeEach
    public void setup() {
        FactoryCache factoryCache = JavaApiPrefabValues.build();
        prefabValues = new PrefabValues(factoryCache);
    }

    @Test
    public void isNotSealed() {
        var accessor = ClassAccessor.of(SealedChild.class, prefabValues);
        assertFalse(accessor.isSealed());
    }

    @Test
    public void isSealed() {
        var accessor = ClassAccessor.of(SealedParent.class, prefabValues);
        assertTrue(accessor.isSealed());
    }
}
