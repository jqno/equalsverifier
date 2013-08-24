package nl.jqno.equalsverifier.coverage;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.points.Color;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class Coverage {
	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{ EclipseInstanceOfPoint.class },
				{ EclipseGetClassPoint.class }
		});
	}
	
	private final Class<?> type;
	
	public Coverage(Class<?> type) {
		this.type = type;
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