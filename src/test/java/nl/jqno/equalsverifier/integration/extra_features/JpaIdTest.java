package nl.jqno.equalsverifier.integration.extra_features;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;
import nl.jqno.equalsverifier.testhelpers.annotations.javax.persistence.Id;
import nl.jqno.equalsverifier.testhelpers.annotations.org.hibernate.annotations.NaturalId;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("unused")
public class JpaIdTest extends ExpectedExceptionTestBase {

    @Test
    public void succeed_whenIdFieldIsNotUsed_givenIdIsAnnotatedWithId() {
        EqualsVerifier.forClass(JpaIdBusinessKeyPerson.class)
                .verify();
    }

    @Test
    public void fail_whenOnlyIdFieldIsUsed_givenIdIsAnnotatedWithId() {
        expectFailure("Significant fields", "id is marked @Id", "equals should not use it", "Suppress Warning.SURROGATE_KEY if");
        EqualsVerifier.forClass(JpaIdSurrogateKeyPerson.class)
                .verify();
    }

    @Test
    public void succeed_whenOnlyIdFieldIsUsed_givenIdIsAnnotatedWithIdAndSurrogateKeyWarningIsSuppressed() {
        EqualsVerifier.forClass(JpaIdSurrogateKeyPerson.class)
                .suppress(Warning.SURROGATE_KEY)
                .verify();
    }

    @Test
    public void fail_whenIdFieldIsNotUsed_givenIdIsAnnotatedWithIdAndSurrogateKeyWarningIsSuppressed() {
        expectFailure("Significant fields", "id is marked @Id", "Warning.SURROGATE_KEY", "equals does not use");
        EqualsVerifier.forClass(JpaIdBusinessKeyPerson.class)
                .suppress(Warning.SURROGATE_KEY)
                .verify();
    }

    @Test
    public void succeed_whenOnlySocialSecurityIsUsed_givenSocialSecurityIsAnnotatedWithNaturalId() {
        EqualsVerifier.forClass(NaturalIdBusinessKeyPerson.class)
                .verify();
    }

    @Test@Ignore
    public void fail_whenOnlySocialSecurityIsUsed_givenSocialSecurityIsAnnotatedWithNaturalIdButSurrogateKeyWarningIsSuppressed() {
        expectFailure("<<placeholder>>");
        EqualsVerifier.forClass(NaturalIdBusinessKeyPerson.class)
                .suppress(Warning.SURROGATE_KEY)
                .verify();
    }

    static final class JpaIdBusinessKeyPerson {
        @Id
        private final UUID id;
        private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;

        public JpaIdBusinessKeyPerson(UUID id, String socialSecurity, String name, LocalDate birthdate) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof JpaIdBusinessKeyPerson)) {
                return false;
            }
            JpaIdBusinessKeyPerson other = (JpaIdBusinessKeyPerson)obj;
            return Objects.equals(socialSecurity, other.socialSecurity) &&
                Objects.equals(name, other.name) &&
                Objects.equals(birthdate, other.birthdate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(socialSecurity, name, birthdate);
        }
    }

    static final class JpaIdSurrogateKeyPerson {
        @Id
        private final UUID id;
        private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;

        public JpaIdSurrogateKeyPerson(UUID id, String socialSecurity, String name, LocalDate birthdate) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof JpaIdSurrogateKeyPerson)) {
                return false;
            }
            JpaIdSurrogateKeyPerson other = (JpaIdSurrogateKeyPerson)obj;
            return Objects.equals(id, other.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    static final class NaturalIdBusinessKeyPerson {
        @Id
        private final UUID id;
        @NaturalId
        private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;

        public NaturalIdBusinessKeyPerson(UUID id, String socialSecurity, String name, LocalDate birthdate) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NaturalIdBusinessKeyPerson)) {
                return false;
            }
            NaturalIdBusinessKeyPerson other = (NaturalIdBusinessKeyPerson)obj;
            return Objects.equals(socialSecurity, other.socialSecurity);
        }

        @Override
        public int hashCode() {
            return Objects.hash(socialSecurity);
        }
    }
}
