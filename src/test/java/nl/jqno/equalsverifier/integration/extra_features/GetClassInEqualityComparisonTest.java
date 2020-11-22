package nl.jqno.equalsverifier.integration.extra_features;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class GetClassInEqualityComparisonTest {
    @Test
    public void
            succeed_whenGetClassIsPartOfEqualityComparison_givenAnAbstractSuperclassAndUsingGetClassIsUsed() {
        EqualsVerifier.forClass(Identifiable.class).usingGetClass().verify();
    }

    @Test
    public void
            succeed_whenGetClassIsPartOfEqualityComparison_givenAConcreteImplementationAndUsingGetClassIsUsed() {
        EqualsVerifier.forClass(Person.class).usingGetClass().verify();
    }

    @Test
    public void
            succeed_whenGetClassIsPartOfEqualityComparison_givenAnotherConcreteImplementationAndUsingGetClassIsUsed() {
        EqualsVerifier.forClass(Account.class).usingGetClass().verify();
    }

    abstract static class Identifiable {
        private final int id;

        public Identifiable(int id) {
            this.id = id;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof Identifiable)) {
                return false;
            }
            Identifiable other = (Identifiable) obj;
            return id == other.id && getClass() == other.getClass();
        }

        @Override
        public final int hashCode() {
            return id;
        }
    }

    static class Person extends Identifiable {
        public Person(int id) {
            super(id);
        }
    }

    static class Account extends Identifiable {
        public Account(int id) {
            super(id);
        }
    }
}
