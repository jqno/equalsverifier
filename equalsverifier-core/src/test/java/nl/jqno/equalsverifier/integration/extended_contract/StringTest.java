package nl.jqno.equalsverifier.integration.extended_contract;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.internal.checkers.fieldchecks.StringFieldCheck;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

public class StringTest {

    @Test
    public void fail_whenStringIsComparedUsingEqualsIgnoreCaseAndHashCodeIsCaseSensitive() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(IncorrectIgnoreCaseStringEquals.class).verify())
                .assertFailure()
                .assertMessageContains(
                    StringFieldCheck.ERROR_DOC_TITLE,
                    "equalsIgnoreCase",
                    "hashCode",
                    "toUpperCase()",
                    "String field caseInsensitiveString");
    }

    @Test
    public void succeed_whenStringIsComparedUsingEqualsIgnoreCaseAndHashCodeIsAlsoCaseInsensitive() {
        EqualsVerifier.forClass(CorrectIgnoreCaseStringEquals.class).verify();
    }

    @Test
    public void fail_whenStringIsComparedUsingEqualsIgnoreCaseAndHashCodeIsCaseSensitive_givenHashCodeIsCached() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(IncorrectCachedIgnoreCaseStringEquals.class)
                            .withCachedHashCode(
                                "cachedHashCode",
                                "calcHashCode",
                                new IncorrectCachedIgnoreCaseStringEquals("a"))
                            .verify())
                .assertFailure()
                .assertMessageContains(StringFieldCheck.ERROR_DOC_TITLE, "String field caseInsensitiveString");
    }

    private static final class IncorrectIgnoreCaseStringEquals {

        private final String caseInsensitiveString;

        public IncorrectIgnoreCaseStringEquals(String caseInsensitiveString) {
            this.caseInsensitiveString = caseInsensitiveString;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IncorrectIgnoreCaseStringEquals)) {
                return false;
            }
            IncorrectIgnoreCaseStringEquals other = (IncorrectIgnoreCaseStringEquals) obj;
            return caseInsensitiveString == null
                    ? other.caseInsensitiveString == null
                    : caseInsensitiveString.equalsIgnoreCase(other.caseInsensitiveString);
        }

        @Override
        public int hashCode() {
            return Objects.hash(caseInsensitiveString);
        }
    }

    private static final class CorrectIgnoreCaseStringEquals {

        private final String caseInsensitiveString;

        public CorrectIgnoreCaseStringEquals(String caseInsensitiveString) {
            this.caseInsensitiveString = caseInsensitiveString;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CorrectIgnoreCaseStringEquals)) {
                return false;
            }
            CorrectIgnoreCaseStringEquals other = (CorrectIgnoreCaseStringEquals) obj;
            return caseInsensitiveString == null
                    ? other.caseInsensitiveString == null
                    : caseInsensitiveString.equalsIgnoreCase(other.caseInsensitiveString);
        }

        @Override
        public int hashCode() {
            return Objects.hash(caseInsensitiveString == null ? "" : caseInsensitiveString.toUpperCase());
        }
    }

    private static final class IncorrectCachedIgnoreCaseStringEquals {

        private final String caseInsensitiveString;
        private final int cachedHashCode;

        public IncorrectCachedIgnoreCaseStringEquals(String caseInsensitiveString) {
            this.caseInsensitiveString = caseInsensitiveString;
            this.cachedHashCode = calcHashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IncorrectCachedIgnoreCaseStringEquals)) {
                return false;
            }
            IncorrectCachedIgnoreCaseStringEquals other = (IncorrectCachedIgnoreCaseStringEquals) obj;
            return caseInsensitiveString == null
                    ? other.caseInsensitiveString == null
                    : caseInsensitiveString.equalsIgnoreCase(other.caseInsensitiveString);
        }

        @Override
        public int hashCode() {
            return cachedHashCode;
        }

        private int calcHashCode() {
            return Objects.hash(caseInsensitiveString);
        }
    }
}
