package it;

import java.util.List;
import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.ScanOption;
import nl.jqno.equalsverifier.jpms.model.*;
import nl.jqno.equalsverifier.jpms.model.Records.RecordPoint;
import nl.jqno.equalsverifier.jpms.model.Records.RecordPointContainer;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import nl.jqno.equalsverifier_testhelpers.types.RecursiveTypeHelper.RecursiveTypeContainer;
import org.junit.jupiter.api.Test;

public class MockitoWorksInTheModularWorldTest {

    @Test
    void classCanBeVerified() {
        EqualsVerifier.forClass(ClassPoint.class).verify();
    }

    @Test
    void classContainingClassCanBeVerified() {
        EqualsVerifier.forClass(ClassPointContainer.class).verify();
    }

    @Test
    void recordCanBeVerified() {
        EqualsVerifier.forClass(RecordPoint.class).verify();
    }

    @Test
    void recordContainingRecordCanBeVerified() {
        EqualsVerifier.forClass(RecordPointContainer.class).verify();
    }

    @Test
    void classContainingFieldsFromOtherJdkModulesCanBeVerified() {
        EqualsVerifier.forClass(FieldsFromJdkModulesHaver.class).verify();
    }

    @Test
    void recursiveClassCanBeVerified() {
        EqualsVerifier.forClass(RecursiveTypeContainer.class).verify();
    }

    @Test
    void nonFinalClassCanBeVerified() {
        EqualsVerifier.forClass(NonFinal.class).verify();
    }

    @Test
    void forPackageWorks() {
        EqualsVerifier
                .forPackage("nl.jqno.equalsverifier.jpms.model", ScanOption.except(Records.class::equals))
                .verify();
    }

    @Test
    void showingMessagesWorks() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(IncorrectEquals.class).verify())
                .assertFailure()
                .assertMessageContains("Significant fields");
    }

    @SuppressWarnings("InconsistentHashCode")
    static final class IncorrectEquals {
        final List<Foo> l;
        final int i;

        public IncorrectEquals(List<Foo> l, int i) {
            this.l = l;
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof IncorrectEquals other && Objects.equals(l, other.l);
        }

        @Override
        public int hashCode() {
            return Objects.hash(l, i);
        }

        @Override
        public String toString() {
            return "IncorrectEquals: [" + l + ", " + i + "]";
        }
    }

    static final class Foo {
        final String s;

        public Foo(String s) {
            this.s = s;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Foo other && Objects.equals(s, other.s);
        }

        @Override
        public int hashCode() {
            return Objects.hash(s);
        }

        @Override
        public String toString() {
            return "Foo: [" + s + "]";
        }
    }

}
