package nl.jqno.equalsverifier.internal.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import nl.jqno.equalsverifier.internal.reflection.Instantiator;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

class FormatterTest {

    private final Objenesis objenesis = new ObjenesisStd();

    @Test
    void noParameters() {
        Formatter f = Formatter.of("No parameters");
        assertThat(f.format()).isEqualTo("No parameters");
    }

    @Test
    void oneSimpleParameter() {
        Formatter f = Formatter.of("One simple parameter: %%", new Simple(42));
        assertThat(f.format()).isEqualTo("One simple parameter: Simple: 42");
    }

    @Test
    void multipleSimpleParameters() {
        Formatter f = Formatter
                .of("Multiple simple parameters: %% and %% and also %%", new Simple(0), new Simple(1), new Simple(2));
        assertThat(f.format()).isEqualTo("Multiple simple parameters: Simple: 0 and Simple: 1 and also Simple: 2");
    }

    @Test
    void oneThrowingParameter() {
        Formatter f = Formatter.of("One throwing parameter: %%", new Throwing(1337, "string"));
        assertThat(f.format())
                .isEqualTo("One throwing parameter: [Throwing i=1337 s=string]-throws IllegalStateException(msg)");
    }

    @Test
    void oneThrowingParameterWithNullSubparameter() {
        Formatter f = Formatter.of("One throwing parameter: %%", new Throwing(1337, null));
        assertThat(f.format())
                .isEqualTo("One throwing parameter: [Throwing i=1337 s=null]-throws IllegalStateException(msg)");
    }

    @Test
    void oneParameterWithNoFieldsAndThrowsWithNullMessage() {
        Formatter f = Formatter.of("No fields, null message: %%", new NoFieldsAndThrowsNullMessage());
        assertThat(f.format())
                .isEqualTo(
                    "No fields, null message: [NoFieldsAndThrowsNullMessage (no fields)]-throws NullPointerException(null)");
    }

    @Test
    void oneAbstractParameter() {
        Instantiator<Abstract> i = Instantiator.of(Abstract.class, objenesis);
        Formatter f = Formatter.of("Abstract: %%", i.instantiate());
        assertThat(f.format(), containsString("Abstract: [Abstract x=0]"));
    }

    @Test
    void oneConcreteSubclassParameter() {
        Instantiator<AbstractImpl> i = Instantiator.of(AbstractImpl.class, objenesis);
        Formatter f = Formatter.of("Concrete: %%", i.instantiate());
        assertThat(f.format(), containsString("Concrete: something concrete"));
    }

    @Test
    void oneDelegatedAbstractParameter() {
        Instantiator<AbstractDelegation> i = Instantiator.of(AbstractDelegation.class, objenesis);
        Formatter f = Formatter.of("Abstract: %%", i.instantiate());
        assertThat(f.format(), containsString("Abstract: [AbstractDelegation y=0]"));
    }

    @Test
    void oneDelegatedConcreteSubclassParameter() {
        Instantiator<AbstractDelegationImpl> i = Instantiator.of(AbstractDelegationImpl.class, objenesis);
        Formatter f = Formatter.of("Concrete: %%", i.instantiate());
        assertThat(f.format(), containsString("Concrete: something concrete"));
    }

    @Test
    void oneThrowingContainerParameter() {
        Instantiator<Throwing> i = Instantiator.of(Throwing.class, objenesis);
        ThrowingContainer tc = new ThrowingContainer(i.instantiate());
        Formatter f = Formatter.of("TC: %%", tc);
        String expected =
                "TC: [ThrowingContainer t=[Throwing i=0 s=null]-throws IllegalStateException(msg)]-throws IllegalStateException(msg)";
        assertThat(f.format(), containsString(expected));
    }

    @Test
    void oneAbstractContainerParameter() {
        Instantiator<AbstractDelegation> i = Instantiator.of(AbstractDelegation.class, objenesis);
        AbstractContainer ac = new AbstractContainer(i.instantiate());

        Formatter f = Formatter.of("AC: %%", ac);
        assertThat(f.format(), containsString("AC: [AbstractContainer ad=[AbstractDelegation y=0]]"));
    }

    @Test
    void parameterWithMixOfVariousFields() {
        Mix mix = new Mix();
        mix.throwing = new Throwing(42, "empty");

        Formatter f = Formatter.of("%%", mix);
        String expected =
                "[Mix i=42 s=null t=not null throwing=[Throwing i=42 s=empty]-throws IllegalStateException(msg)]"
                        + "-throws UnsupportedOperationException(null)";
        assertThat(f.format(), containsString(expected));
    }

    @Test
    void connectedParameters() {
        Formatter f = Formatter.of("%%%%", 1, 2);
        assertThat(f.format(), containsString("12"));
    }

    @Test
    void nullParameter() {
        Formatter f = Formatter.of("This parameter is null: %%", (Object) null);
        assertThat(f.format()).isEqualTo("This parameter is null: null");
    }

    @Test
    void nullMessage() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> Formatter.of(null));
    }

    @Test
    void notEnoughParameters() {
        Formatter f = Formatter.of("Not enough: %% and %%");

        ExpectedException
                .when(() -> f.format())
                .assertThrows(IllegalStateException.class)
                .assertMessageContains("Not enough parameters");
    }

    @Test
    void tooManyParameters() {
        Formatter f = Formatter.of("Too many!", new Simple(0));

        ExpectedException
                .when(() -> f.format())
                .assertThrows(IllegalStateException.class)
                .assertMessageContains("Too many parameters");
    }

    static class Simple {

        private final int i;

        public Simple(int i) {
            this.i = i;
        }

        @Override
        public String toString() {
            return "Simple: " + i;
        }
    }

    static class Throwing {

        @SuppressWarnings("unused")
        private final int i;

        @SuppressWarnings("unused")
        private final String s;

        public Throwing(int i, String s) {
            this.i = i;
            this.s = s;
        }

        @Override
        public String toString() {
            throw new IllegalStateException("msg");
        }
    }

    static class NoFieldsAndThrowsNullMessage {

        @Override
        public String toString() {
            throw new NullPointerException();
        }
    }

    abstract static class Abstract {

        @SuppressWarnings("unused")
        private final int x = 10;

        @Override
        public abstract String toString();
    }

    static class AbstractImpl extends Abstract {

        @Override
        public String toString() {
            return "something concrete";
        }
    }

    abstract static class AbstractDelegation {

        @SuppressWarnings("unused")
        private final int y = 20;

        public abstract String somethingAbstract();

        @Override
        public String toString() {
            return somethingAbstract();
        }
    }

    static class AbstractDelegationImpl extends AbstractDelegation {

        @Override
        public String somethingAbstract() {
            return "something concrete";
        }
    }

    static class ThrowingContainer {

        private final Throwing t;

        public ThrowingContainer(Throwing t) {
            this.t = t;
        }

        @Override
        public String toString() {
            return "ThrowingContainer " + t.toString();
        }
    }

    static class AbstractContainer {

        public final AbstractDelegation ad;

        public AbstractContainer(AbstractDelegation ad) {
            this.ad = ad;
        }

        @Override
        public String toString() {
            return "AbstractContainer: " + ad.toString();
        }
    }

    static class Mix {

        public final int i = 42;
        public final String s = null;
        public final String t = "not null";
        public Throwing throwing;

        @Override
        public String toString() {
            throw new UnsupportedOperationException();
        }
    }
}
