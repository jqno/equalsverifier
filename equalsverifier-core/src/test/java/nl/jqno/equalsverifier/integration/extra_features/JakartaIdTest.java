package nl.jqno.equalsverifier.integration.extra_features;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import nl.jqno.equalsverifier.testhelpers.annotations.org.hibernate.annotations.NaturalId;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unused")
public class JakartaIdTest {

    @Test
    public void succeed_whenIdFieldIsNotUsed_givenIdIsAnnotatedWithId() {
        EqualsVerifier.forClass(JakartaIdBusinessKeyPerson.class).verify();
        EqualsVerifier.forClass(JakartaIdBusinessKeyPersonReorderedFields.class).verify();
    }

    @Test
    public void succeed_whenOnlyIdFieldIsUsed_givenIdIsAnnotatedWithIdAndSurrogateKeyWarningIsSuppressed() {
        EqualsVerifier
            .forClass(JakartaIdSurrogateKeyPerson.class)
            .suppress(Warning.SURROGATE_KEY)
            .verify();
        EqualsVerifier
            .forClass(JakartaIdSurrogateKeyPersonReorderedFields.class)
            .suppress(Warning.SURROGATE_KEY)
            .verify();
    }

    @Test
    public void fail_whenIdFieldIsNotUsed_givenIdIsAnnotatedWithIdButIdAnnotationIsIgnored() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(JakartaIdBusinessKeyPerson.class)
                    .withIgnoredAnnotations(jakarta.persistence.Id.class)
                    .verify()
            )
            .assertFailure()
            .assertMessageContains("Significant fields", "equals does not use id");
    }

    @Test
    public void fail_whenOnlyIdFieldIsUsed_givenIdIsAnnotatedWithId() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(JakartaIdSurrogateKeyPerson.class).verify())
            .assertFailure()
            .assertMessageContains(
                "Significant fields",
                "id is marked @Id",
                "equals should not use it",
                "Suppress Warning.SURROGATE_KEY if"
            );
    }

    @Test
    public void fail_whenOnlyIdFieldIsUsed_givenIdIsAnnotatedWithId2() {
        ExpectedException
            .when(() ->
                EqualsVerifier.forClass(JakartaIdSurrogateKeyPersonReorderedFields.class).verify()
            )
            .assertFailure()
            .assertMessageContains(
                "Significant fields",
                "equals does not use socialSecurity",
                "Suppress Warning.SURROGATE_KEY if"
            );
    }

    @Test
    public void fail_whenIdFieldIsNotUsed_givenIdIsAnnotatedWithIdAndSurrogateKeyWarningIsSuppressed() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(JakartaIdBusinessKeyPerson.class)
                    .suppress(Warning.SURROGATE_KEY)
                    .verify()
            )
            .assertFailure()
            .assertMessageContains(
                "Significant fields",
                "id is marked @Id",
                "Warning.SURROGATE_KEY",
                "equals does not use"
            );
    }

    @Test
    public void fail_whenIdFieldIsNotUsed_givenIdIsAnnotatedWithIdAndSurrogateKeyWarningIsSuppressed2() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(JakartaIdBusinessKeyPersonReorderedFields.class)
                    .suppress(Warning.SURROGATE_KEY)
                    .verify()
            )
            .assertFailure()
            .assertMessageContains(
                "Significant fields",
                "equals should not use socialSecurity",
                "Warning.SURROGATE_KEY is suppressed and it is not marked as @Id",
                "but it does"
            );
    }

    @Test
    public void succeed_whenEmbeddedIdIsUsedCorrectly() {
        EqualsVerifier.forClass(JakartaEmbeddedIdBusinessKeyPerson.class).verify();
        EqualsVerifier
            .forClass(JakartaEmbeddedIdSurrogateKeyPerson.class)
            .suppress(Warning.SURROGATE_KEY)
            .verify();
    }

    @Test
    public void fail_whenOnlyEmbeddedIdFieldIsUsed_givenIdIsAnnotatedWithEmbeddedId() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(JakartaEmbeddedIdSurrogateKeyPerson.class).verify())
            .assertFailure()
            .assertMessageContains(
                "Significant fields",
                "id is marked @Id or @EmbeddedId",
                "equals should not use it",
                "Suppress Warning.SURROGATE_KEY if"
            );
    }

    @Test
    public void fail_whenIdFieldIsNotUsed_givenIdIsAnnotatedWithEmbeddedIdAndSurrogateKeyWarningIsSuppressed() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(JakartaEmbeddedIdBusinessKeyPerson.class)
                    .suppress(Warning.SURROGATE_KEY)
                    .verify()
            )
            .assertFailure()
            .assertMessageContains(
                "Significant fields",
                "id is marked @Id or @EmbeddedId",
                "Warning.SURROGATE_KEY",
                "equals does not use"
            );
    }

    @Test
    public void fail_whenEmbeddedIdFieldIsTheOnlyFieldUsed() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(JakartaEmbeddedIdBusinessKeyPerson.class)
                    .withOnlyTheseFields("id")
                    .verify()
            )
            .assertFailure()
            .assertMessageContains(
                "Precondition: you can't use withOnlyTheseFields on a field marked @Id or @EmbeddedId.",
                "Suppress Warning.SURROGATE_KEY if"
            );
    }

    @Test
    public void succeed_whenOnlySocialSecurityIsUsed_givenSocialSecurityIsAnnotatedWithNaturalId() {
        EqualsVerifier.forClass(NaturalIdBusinessKeyPerson.class).verify();
    }

    @Test
    public void fail_whenOnlySocialSecurityIsUsed_givenSocialSecurityIsAnnotatedWithNaturalIdButIdAnnotationIsIgnored() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(NaturalIdBusinessKeyPerson.class)
                    .withIgnoredAnnotations(NaturalId.class)
                    .verify()
            )
            .assertFailure()
            .assertMessageContains("Significant fields", "equals does not use name");
    }

    @Test
    public void succeed_whenIdAndNameFieldsAreNotUsed_givenNameIsIgnored() {
        EqualsVerifier
            .forClass(JakartaIdBusinessKeyPersonDoesntUseName.class)
            .withIgnoredFields("name")
            .verify();
    }

    @Test
    public void succeed_whenIdAndNameFieldsAreNotUsed_givenSocialSecurityAndBirthdateAreOnlyUsed() {
        EqualsVerifier
            .forClass(JakartaIdBusinessKeyPersonDoesntUseName.class)
            .withOnlyTheseFields("socialSecurity", "birthdate")
            .verify();
    }

    @Test
    public void succeed_whenIdIsPartOfAProperJakartaEntity() {
        EqualsVerifier.forClass(JakartaIdBusinessKeyPersonEntity.class).verify();
    }

    @Test
    public void succeed_whenNaturalIdIsPartOfAProperJakartaEntity() {
        EqualsVerifier.forClass(NaturalIdBusinessKeyPersonEntity.class).verify();
    }

    @Test
    public void succeed_whenEqualsBehavesLikeVersionedEntity_givenIdIsMarkedWithIdAndWarningVersionedEntityIsSuppressed() {
        EqualsVerifier
            .forClass(JakartaIdVersionedEntity.class)
            .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)
            .verify();
    }

    @Test
    public void succeed_whenMethodsAreAnnotatedInsteadOfFields() {
        EqualsVerifier.forClass(MethodAnnotatedBusinessKeyPerson.class).verify();
    }

    @Test
    public void fail_whenIdFieldIsTheOnlyFieldUsed() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(JakartaIdBusinessKeyPerson.class)
                    .withOnlyTheseFields("id")
                    .verify()
            )
            .assertFailure()
            .assertMessageContains(
                "Precondition: you can't use withOnlyTheseFields on a field marked @Id or @EmbeddedId.",
                "Suppress Warning.SURROGATE_KEY if"
            );
    }

    @Test
    public void fail_whenOnlySocialSecurityIsUsed_givenSocialSecurityIsAnnotatedWithNaturalIdButSurrogateKeyWarningIsSuppressed() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(NaturalIdBusinessKeyPerson.class)
                    .suppress(Warning.SURROGATE_KEY)
                    .verify()
            )
            .assertFailure()
            .assertMessageContains(
                "Precondition: you can't suppress Warning.SURROGATE_KEY when fields are marked @NaturalId."
            );
    }

    @Test
    public void fail_whenWithOnlyTheseFieldsIsUsed_givenWarningSurrogateKeyIsSuppressed() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(JakartaIdSurrogateKeyPerson.class)
                    .withOnlyTheseFields("socialSecurity")
                    .suppress(Warning.SURROGATE_KEY)
            )
            .assertThrows(IllegalStateException.class)
            .assertMessageContains(
                "Precondition",
                "you can't use withOnlyTheseFields when Warning.SURROGATE_KEY is suppressed."
            );
    }

    @Test
    public void fail_whenWithOnlyTheseFieldsIsUsed_givenWarningSurrogateKeyIsSuppressedInReverse() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(JakartaIdSurrogateKeyPerson.class)
                    .suppress(Warning.SURROGATE_KEY)
                    .withOnlyTheseFields("socialSecurity")
            )
            .assertThrows(IllegalStateException.class)
            .assertMessageContains(
                "Precondition",
                "you can't use withOnlyTheseFields when Warning.SURROGATE_KEY is suppressed."
            );
    }

    @Test
    public void fail_whenFieldsAreIgnored_givenWarningSurrogateKeyIsSuppressed() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(JakartaIdSurrogateKeyPerson.class)
                    .withIgnoredFields("socialSecurity")
                    .suppress(Warning.SURROGATE_KEY)
            )
            .assertThrows(IllegalStateException.class)
            .assertMessageContains(
                "Precondition: you can't use withIgnoredFields when Warning.SURROGATE_KEY is suppressed."
            );
    }

    @Test
    public void fail_whenFieldsAreIgnored_givenWarningSurrogateKeyIsSuppressedInReverse() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(JakartaIdSurrogateKeyPerson.class)
                    .suppress(Warning.SURROGATE_KEY)
                    .withIgnoredFields("socialSecurity")
            )
            .assertThrows(IllegalStateException.class)
            .assertMessageContains(
                "Precondition: you can't use withIgnoredFields when Warning.SURROGATE_KEY is suppressed."
            );
    }

    @Test
    public void fail_whenWithOnlyTheseFieldsIsUsed_givenFieldsAreMarkedWithNaturalId() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(NaturalIdBusinessKeyPerson.class)
                    .withOnlyTheseFields("socialSecurity")
                    .verify()
            )
            .assertFailure()
            .assertMessageContains(
                "Precondition: you can't use withOnlyTheseFields when fields are marked with @NaturalId."
            );
    }

    @Test
    public void fail_whenFieldsAreIgnored_givenFieldsAreMarkedWithNaturalId() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(NaturalIdBusinessKeyPerson.class)
                    .withIgnoredFields("socialSecurity")
                    .verify()
            )
            .assertFailure()
            .assertMessageContains(
                "Precondition: you can't use withIgnoredFields when fields are marked with @NaturalId."
            );
    }

    @Test
    public void fail_whenWarningVersionedEntityIsSuppressed_givenAFieldIsAnnotatedWithNaturalId() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(NaturalIdBusinessKeyPerson.class)
                    .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)
                    .verify()
            )
            .assertFailure()
            .assertMessageContains(
                "Precondition: you can't suppress Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY when fields are marked with @NaturalId."
            );
    }

    @Test
    public void fail_whenWarningVersionedEntityIsSuppressed_givenWarningSurrogateKeyIsAlsoSuppressed() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(JakartaIdBusinessKeyPerson.class)
                    .suppress(Warning.SURROGATE_KEY, Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)
            )
            .assertThrows(IllegalStateException.class)
            .assertMessageContains(
                "Precondition",
                "you can't suppress Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY when Warning.SURROGATE_KEY is also suppressed."
            );
    }

    @Test
    public void fail_whenANaturalIdAnnotationFromAnotherPackageIsUsed() {
        ExpectedException
            .when(() ->
                EqualsVerifier.forClass(NonHibernateNaturalIdBusinessKeyPerson.class).verify()
            )
            .assertFailure()
            .assertMessageContains("Significant fields");
    }

    static class JakartaIdBusinessKeyPerson {

        @jakarta.persistence.Id
        private final UUID id;

        private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;

        public JakartaIdBusinessKeyPerson(
            UUID id,
            String socialSecurity,
            String name,
            LocalDate birthdate
        ) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof JakartaIdBusinessKeyPerson)) {
                return false;
            }
            JakartaIdBusinessKeyPerson other = (JakartaIdBusinessKeyPerson) obj;
            return (
                Objects.equals(socialSecurity, other.socialSecurity) &&
                Objects.equals(name, other.name) &&
                Objects.equals(birthdate, other.birthdate)
            );
        }

        @Override
        public final int hashCode() {
            return Objects.hash(socialSecurity, name, birthdate);
        }
    }

    static class JakartaIdBusinessKeyPersonReorderedFields {

        private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;

        @jakarta.persistence.Id
        private final UUID id;

        public JakartaIdBusinessKeyPersonReorderedFields(
            UUID id,
            String socialSecurity,
            String name,
            LocalDate birthdate
        ) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof JakartaIdBusinessKeyPersonReorderedFields)) {
                return false;
            }
            JakartaIdBusinessKeyPersonReorderedFields other = (JakartaIdBusinessKeyPersonReorderedFields) obj;
            return (
                Objects.equals(socialSecurity, other.socialSecurity) &&
                Objects.equals(name, other.name) &&
                Objects.equals(birthdate, other.birthdate)
            );
        }

        @Override
        public final int hashCode() {
            return Objects.hash(socialSecurity, name, birthdate);
        }
    }

    static class JakartaIdSurrogateKeyPerson {

        @jakarta.persistence.Id
        private final UUID id;

        private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;

        public JakartaIdSurrogateKeyPerson(
            UUID id,
            String socialSecurity,
            String name,
            LocalDate birthdate
        ) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof JakartaIdSurrogateKeyPerson)) {
                return false;
            }
            JakartaIdSurrogateKeyPerson other = (JakartaIdSurrogateKeyPerson) obj;
            return Objects.equals(id, other.id);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(id);
        }
    }

    static class JakartaIdSurrogateKeyPersonReorderedFields {

        private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;

        @jakarta.persistence.Id
        private final UUID id;

        public JakartaIdSurrogateKeyPersonReorderedFields(
            UUID id,
            String socialSecurity,
            String name,
            LocalDate birthdate
        ) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof JakartaIdSurrogateKeyPersonReorderedFields)) {
                return false;
            }
            JakartaIdSurrogateKeyPersonReorderedFields other = (JakartaIdSurrogateKeyPersonReorderedFields) obj;
            return Objects.equals(id, other.id);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(id);
        }
    }

    static class JakartaEmbeddedIdBusinessKeyPerson {

        @jakarta.persistence.EmbeddedId
        private final UUID id;

        private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;

        public JakartaEmbeddedIdBusinessKeyPerson(
            UUID id,
            String socialSecurity,
            String name,
            LocalDate birthdate
        ) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof JakartaEmbeddedIdBusinessKeyPerson)) {
                return false;
            }
            JakartaEmbeddedIdBusinessKeyPerson other = (JakartaEmbeddedIdBusinessKeyPerson) obj;
            return (
                Objects.equals(socialSecurity, other.socialSecurity) &&
                Objects.equals(name, other.name) &&
                Objects.equals(birthdate, other.birthdate)
            );
        }

        @Override
        public final int hashCode() {
            return Objects.hash(socialSecurity, name, birthdate);
        }
    }

    static class JakartaEmbeddedIdSurrogateKeyPerson {

        @jakarta.persistence.EmbeddedId
        private final UUID id;

        private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;

        public JakartaEmbeddedIdSurrogateKeyPerson(
            UUID id,
            String socialSecurity,
            String name,
            LocalDate birthdate
        ) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof JakartaEmbeddedIdSurrogateKeyPerson)) {
                return false;
            }
            JakartaEmbeddedIdSurrogateKeyPerson other = (JakartaEmbeddedIdSurrogateKeyPerson) obj;
            return Objects.equals(id, other.id);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(id);
        }
    }

    static class NaturalIdBusinessKeyPerson {

        @jakarta.persistence.Id
        private final UUID id;

        @NaturalId
        private final String socialSecurity;

        private final String name;
        private final LocalDate birthdate;

        public NaturalIdBusinessKeyPerson(
            UUID id,
            String socialSecurity,
            String name,
            LocalDate birthdate
        ) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof NaturalIdBusinessKeyPerson)) {
                return false;
            }
            NaturalIdBusinessKeyPerson other = (NaturalIdBusinessKeyPerson) obj;
            return Objects.equals(socialSecurity, other.socialSecurity);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(socialSecurity);
        }
    }

    static class JakartaIdBusinessKeyPersonDoesntUseName {

        @jakarta.persistence.Id
        private final UUID id;

        private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;

        public JakartaIdBusinessKeyPersonDoesntUseName(
            UUID id,
            String socialSecurity,
            String name,
            LocalDate birthdate
        ) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof JakartaIdBusinessKeyPersonDoesntUseName)) {
                return false;
            }
            JakartaIdBusinessKeyPersonDoesntUseName other = (JakartaIdBusinessKeyPersonDoesntUseName) obj;
            return (
                Objects.equals(socialSecurity, other.socialSecurity) &&
                Objects.equals(birthdate, other.birthdate)
            );
        }

        @Override
        public final int hashCode() {
            return Objects.hash(socialSecurity, birthdate);
        }
    }

    @jakarta.persistence.Entity
    static class JakartaIdBusinessKeyPersonEntity {

        @jakarta.persistence.Id
        private UUID id;

        private String socialSecurity;
        private String name;
        private LocalDate birthdate;

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof JakartaIdBusinessKeyPersonEntity)) {
                return false;
            }
            JakartaIdBusinessKeyPersonEntity other = (JakartaIdBusinessKeyPersonEntity) obj;
            return (
                Objects.equals(socialSecurity, other.socialSecurity) &&
                Objects.equals(name, other.name) &&
                Objects.equals(birthdate, other.birthdate)
            );
        }

        @Override
        public final int hashCode() {
            return Objects.hash(socialSecurity, name, birthdate);
        }
    }

    @jakarta.persistence.Entity
    static class NaturalIdBusinessKeyPersonEntity {

        @jakarta.persistence.Id
        private UUID id;

        @NaturalId
        private String socialSecurity;

        private String name;
        private LocalDate birthdate;

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof NaturalIdBusinessKeyPersonEntity)) {
                return false;
            }
            NaturalIdBusinessKeyPersonEntity other = (NaturalIdBusinessKeyPersonEntity) obj;
            return Objects.equals(socialSecurity, other.socialSecurity);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(socialSecurity);
        }
    }

    static class NonHibernateNaturalIdBusinessKeyPerson {

        @jakarta.persistence.Id
        private final UUID id;

        @nl.jqno.equalsverifier.testhelpers.annotations.NaturalId
        private final String socialSecurity;

        private final String name;
        private final LocalDate birthdate;

        public NonHibernateNaturalIdBusinessKeyPerson(
            UUID id,
            String socialSecurity,
            String name,
            LocalDate birthdate
        ) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof NonHibernateNaturalIdBusinessKeyPerson)) {
                return false;
            }
            NonHibernateNaturalIdBusinessKeyPerson other = (NonHibernateNaturalIdBusinessKeyPerson) obj;
            return Objects.equals(socialSecurity, other.socialSecurity);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(socialSecurity);
        }
    }

    public static class JakartaIdVersionedEntity {

        @jakarta.persistence.Id
        private final long id;

        private final String s;

        public JakartaIdVersionedEntity(long id, String s) {
            this.id = id;
            this.s = s;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof JakartaIdVersionedEntity)) {
                return false;
            }
            JakartaIdVersionedEntity other = (JakartaIdVersionedEntity) obj;
            if (id == 0L && other.id == 0L) {
                return Objects.equals(s, other.s);
            }
            return id == other.id;
        }

        @Override
        public final int hashCode() {
            return Float.floatToIntBits(id);
        }
    }

    static class MethodAnnotatedBusinessKeyPerson {

        private final UUID id;
        private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;

        public MethodAnnotatedBusinessKeyPerson(
            UUID id,
            String socialSecurity,
            String name,
            LocalDate birthdate
        ) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @jakarta.persistence.Id
        public UUID getId() {
            return id;
        }

        @NaturalId
        public String getSocialSecurity() {
            return socialSecurity;
        }

        public String getName() {
            return name;
        }

        public LocalDate getBirthdate() {
            return birthdate;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof MethodAnnotatedBusinessKeyPerson)) {
                return false;
            }
            MethodAnnotatedBusinessKeyPerson other = (MethodAnnotatedBusinessKeyPerson) obj;
            return Objects.equals(socialSecurity, other.socialSecurity);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(socialSecurity);
        }
    }
}
