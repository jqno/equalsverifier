package nl.jqno.equalsverifier.coverage;

import java.util.Arrays;
import java.util.Collection;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.types.Color;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class CoverageWithInheritanceTest<T, U extends T, V extends U> {
    private final Class<?> containerType;
    private final Class<T> superType;
    private final Class<U> subType;
    private final Class<V> endpointType;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public CoverageWithInheritanceTest(Class<?> containerType) {
        this.containerType = containerType;
        Class[] containingTypes = containerType.getClasses();
        this.superType = find(containingTypes, "Point");
        this.subType = find(containingTypes, "ColorPoint");
        this.endpointType = find(containingTypes, "EndPoint");
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {{HandwrittenCanEqual.class}, {LombokCanEqual.class}});
    }

    @SuppressWarnings("rawtypes")
    private Class find(Class[] types, String name) {
        Class result = null;
        for (Class type : types) {
            if (type.getSimpleName().equals(name)) {
                result = type;
            }
        }
        return result;
    }

    @Test
    public void testSuperCoverage() {
        EqualsVerifier.forClass(superType).withRedefinedSubclass(subType).verify();
    }

    @Test
    public void testSubCoverage() {
        EqualsVerifier.forClass(subType)
                .withRedefinedSuperclass()
                .withRedefinedSubclass(endpointType)
                .verify();
    }

    @Test
    public void callTheConstructors() throws Exception {
        containerType.getConstructor().newInstance();
        superType.getConstructor(int.class, int.class).newInstance(0, 0);
        subType.getConstructor(int.class, int.class, Color.class).newInstance(0, 0, Color.INDIGO);
        endpointType
                .getConstructor(int.class, int.class, Color.class)
                .newInstance(0, 0, Color.INDIGO);
    }
}
