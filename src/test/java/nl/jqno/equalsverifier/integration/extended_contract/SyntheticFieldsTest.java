package nl.jqno.equalsverifier.integration.extended_contract;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import java.util.Objects;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

@SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
public class SyntheticFieldsTest {
    @Test
    public void succeed_whenClassHasASyntheticFieldBecauseItsInsideAUnitTestClass() {
        EqualsVerifier.forClass(Outer.class)
                .verify();
    }

    @Test
    public void succeed_whenClassHasASyntheticFieldBecauseItsAnInnerClass() {
        EqualsVerifier.forClass(Outer.Inner.class)
                .verify();
    }

    @Test
    public void succeed_whenClassHasAFieldThatHasASyntheticField() {
        EqualsVerifier.forClass(OuterContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenClassIsInstrumentedByCobertura_givenCoberturaDoesntMarkItsFieldsSynthetic() {
        EqualsVerifier.forClass(CoberturaContainer.class)
                .verify();
    }

    /* non-static */ final class Outer {
        private final Inner inner;

        private /* non-static */ final class Inner {
            private final int foo;

            public Inner(int foo) { this.foo = foo; }

            @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
            @Override public int hashCode() { return defaultHashCode(this); }
        }

        public Outer() { inner = null; }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }

    /* non-static */ final class OuterContainer {
        private final Outer outer;

        public OuterContainer() {
            outer = null;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof OuterContainer)) {
                return false;
            }
            OuterContainer other = (OuterContainer)obj;
            return Objects.equals(outer, other.outer);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(outer);
        }
    }

    public static final class CoberturaContainer {
        // CHECKSTYLE: ignore StaticVariableName for 1 line.
        public static transient int[] __cobertura_counters;
        private final int i;

        public CoberturaContainer(int i) {
            this.i = i;
        }

        static {
            __cobertura_counters = new int[1];
        }

        @Override
        public boolean equals(Object obj) {
            __cobertura_counters[0] += 1;
            if (!(obj instanceof CoberturaContainer)) {
                return false;
            }
            CoberturaContainer p = (CoberturaContainer)obj;
            return p.i == i;
        }

        @Override
        public int hashCode() {
            __cobertura_counters[0] += 1;
            return defaultHashCode(this);
        }
    }
}
