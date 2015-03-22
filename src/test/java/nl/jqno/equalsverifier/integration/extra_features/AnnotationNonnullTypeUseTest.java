package nl.jqno.equalsverifier.integration.extra_features;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.Java8IntegrationTestBase;

import org.junit.Test;

public class AnnotationNonnullTypeUseTest extends Java8IntegrationTestBase {
	private static final String NONNULL_MANUAL_NAME = "NonnullManual";
	private static final String NONNULL_MANUAL =
			"\nimport java.util.Objects;" +
			"\nimport org.eclipse.jdt.annotation.NonNull;" +
			"\n" +
			"\nclass NonnullManual {" +
			"\n    private final @NonNull Object o;" +
			"\n" +
			"\n    public NonnullManual(Object o) { this.o = o; }" +
			"\n" +
			"\n    @Override" +
			"\n    public final boolean equals(Object obj) {" +
			"\n        if (!(obj instanceof NonnullManual)) {" +
			"\n            return false;" +
			"\n        }" +
			"\n        NonnullManual other = (NonnullManual)obj;" +
			"\n        return o.equals(other.o);" +
			"\n    }" +
			"\n" +
			"\n    @Override" +
			"\n    public final int hashCode() {" +
			"\n        return Objects.hash(o);" +
			"\n    }" +
			"\n}";
	
	@Test
	public void successfullyInstantiatesAJava8ClassWithStreams_whenJava8IsAvailable() throws Exception {
		if (!isJava8Available()) {
			return;
		}
		
		Class<?> java8Class = compile(NONNULL_MANUAL_NAME, NONNULL_MANUAL);
		EqualsVerifier.forClass(java8Class)
				.verify();
	}
}
