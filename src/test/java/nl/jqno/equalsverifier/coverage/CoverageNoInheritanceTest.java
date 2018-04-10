package nl.jqno.equalsverifier.coverage;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.types.Color;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class CoverageNoInheritanceTest {
    private final Class<?> type;

    public CoverageNoInheritanceTest(Class<?> type) {
        this.type = type;
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { EclipseGetClassPoint.class },
                { EclipseInstanceOfPoint.class },
                { HandwrittenGetClassPoint.class },
                { HandwrittenInstanceOfPoint.class },
                { IntelliJGetClassPoint.class },
                { IntelliJInstanceOfPoint.class },
                { LombokInstanceOfPoint.class },
                { NetBeansGetClassPoint.class }
        });
    }

    @Test
    public void testCoverage() {
        EqualsVerifier.forClass(type).verify();
    }

    @Test
    public void callTheConstructor() throws Exception {
        Constructor<?> constructor = type.getConstructor(int.class, int.class, Color.class);
        constructor.newInstance(0, 0, Color.INDIGO);
    }
}
