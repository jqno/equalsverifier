package nl.jqno.equalsverifier.integration.extra_features;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;
import nl.jqno.equalsverifier.testhelpers.annotations.javax.persistence.EmbeddedId;
import nl.jqno.equalsverifier.testhelpers.annotations.javax.persistence.Entity;
import nl.jqno.equalsverifier.testhelpers.annotations.javax.persistence.Id;
import nl.jqno.equalsverifier.testhelpers.annotations.org.hibernate.annotations.NaturalId;
import org.junit.Test;

@SuppressWarnings("unused")
public class JpaIdTest extends ExpectedExceptionTestBase {

    @Test
    public void succeed_whenIdFieldIsNotUsed_givenIdIsAnnotatedWithId() {
        EqualsVerifier.forClass(JpaIdBusinessKeyPerson.class).verify();
        EqualsVerifier.forClass(JpaIdBusinessKeyPersonReorderedFields.class).verify();
    }

    @Test
    public void
            succeed_whenOnlyIdFieldIsUsed_givenIdIsAnnotatedWithIdAndSurrogateKeyWarningIsSuppressed() {
        EqualsVerifier.forClass(JpaIdSurrogateKeyPerson.class)
                .suppress(Warning.SURROGATE_KEY)
                .verify();
        EqualsVerifier.forClass(JpaIdSurrogateKeyPersonReorderedFields.class)
                .suppress(Warning.SURROGATE_KEY)
                .verify();
    }

    @Test
    public void fail_whenIdFieldIsNotUsed_givenIdIsAnnotatedWithIdButIdAnnotationIsIgnored() {
        expectFailure("Significant fields", "equals does not use id");
        EqualsVerifier.forClass(JpaIdBusinessKeyPerson.class)
                .withIgnoredAnnotations(Id.class)
                .verify();
    }

    @Test
    public void fail_whenOnlyIdFieldIsUsed_givenIdIsAnnotatedWithId() {
        expectFailure(
                "Significant fields",
                "id is marked @Id",
                "equals should not use it",
                "Suppress Warning.SURROGATE_KEY if");
        EqualsVerifier.forClass(JpaIdSurrogateKeyPerson.class).verify();
    }

    @Test
    public void fail_whenOnlyIdFieldIsUsed_givenIdIsAnnotatedWithId2() {
        expectFailure(
                "Significant fields",
                "equals does not use socialSecurity",
                "Suppress Warning.SURROGATE_KEY if");
        EqualsVerifier.forClass(JpaIdSurrogateKeyPersonReorderedFields.class).verify();
    }

    @Test
    public void
            fail_whenIdFieldIsNotUsed_givenIdIsAnnotatedWithIdAndSurrogateKeyWarningIsSuppressed() {
        expectFailure(
                "Significant fields",
                "id is marked @Id",
                "Warning.SURROGATE_KEY",
                "equals does not use");
        EqualsVerifier.forClass(JpaIdBusinessKeyPerson.class)
                .suppress(Warning.SURROGATE_KEY)
                .verify();
    }

    @Test
    public void
            fail_whenIdFieldIsNotUsed_givenIdIsAnnotatedWithIdAndSurrogateKeyWarningIsSuppressed2() {
        expectFailure(
                "Significant fields",
                "equals should not use socialSecurity",
                "Warning.SURROGATE_KEY is suppressed and it is not marked as @Id",
                "but it does");
        EqualsVerifier.forClass(JpaIdBusinessKeyPersonReorderedFields.class)
                .suppress(Warning.SURROGATE_KEY)
                .verify();
    }

    @Test
    public void succeed_whenEmbeddedIdIsUsedCorrectly() {
        EqualsVerifier.forClass(JpaEmbeddedIdBusinessKeyPerson.class).verify();
        EqualsVerifier.forClass(JpaEmbeddedIdSurrogateKeyPerson.class)
                .suppress(Warning.SURROGATE_KEY)
                .verify();
    }

    @Test
    public void fail_whenOnlyEmbeddedIdFieldIsUsed_givenIdIsAnnotatedWithEmbeddedId() {
        expectFailure(
                "Significant fields",
                "id is marked @Id or @EmbeddedId",
                "equals should not use it",
                "Suppress Warning.SURROGATE_KEY if");
        EqualsVerifier.forClass(JpaEmbeddedIdSurrogateKeyPerson.class).verify();
    }

    @Test
    public void
            fail_whenIdFieldIsNotUsed_givenIdIsAnnotatedWithEmbeddedIdAndSurrogateKeyWarningIsSuppressed() {
        expectFailure(
                "Significant fields",
                "id is marked @Id or @EmbeddedId",
                "Warning.SURROGATE_KEY",
                "equals does not use");
        EqualsVerifier.forClass(JpaEmbeddedIdBusinessKeyPerson.class)
                .suppress(Warning.SURROGATE_KEY)
                .verify();
    }

    @Test
    public void fail_whenEmbeddedIdFieldIsTheOnlyFieldUsed() {
        expectFailure(
                "Precondition: you can't use withOnlyTheseFields on a field marked @Id or @EmbeddedId.",
                "Suppress Warning.SURROGATE_KEY if");
        EqualsVerifier.forClass(JpaEmbeddedIdBusinessKeyPerson.class)
                .withOnlyTheseFields("id")
                .verify();
    }

    @Test
    public void succeed_whenOnlySocialSecurityIsUsed_givenSocialSecurityIsAnnotatedWithNaturalId() {
        EqualsVerifier.forClass(NaturalIdBusinessKeyPerson.class).verify();
    }

    @Test
    public void
            succeed_whenOnlySocialSecurityIsUsed_givenSocialSecurityIsAnnotatedWithNaturalIdAndNothingIsAnnotatedWithJpaId() {
        EqualsVerifier.forClass(NaturalIdWithoutJpaIdBusinessKeyPerson.class).verify();
    }

    @Test
    public void
            fail_whenOnlySocialSecurityIsUsed_givenSocialSecurityIsAnnotatedWithNaturalIdButIdAnnotationIsIgnored() {
        expectFailure("Significant fields", "equals does not use name");
        EqualsVerifier.forClass(NaturalIdBusinessKeyPerson.class)
                .withIgnoredAnnotations(NaturalId.class)
                .verify();
    }

    @Test
    public void succeed_whenIdAndNameFieldsAreNotUsed_givenNameIsIgnored() {
        EqualsVerifier.forClass(JpaIdBusinessKeyPersonDoesntUseName.class)
                .withIgnoredFields("name")
                .verify();
    }

    @Test
    public void succeed_whenIdAndNameFieldsAreNotUsed_givenSocialSecurityAndBirthdateAreOnlyUsed() {
        EqualsVerifier.forClass(JpaIdBusinessKeyPersonDoesntUseName.class)
                .withOnlyTheseFields("socialSecurity", "birthdate")
                .verify();
    }

    @Test
    public void succeed_whenIdIsPartOfAProperJpaEntity() {
        EqualsVerifier.forClass(JpaIdBusinessKeyPersonEntity.class).verify();
    }

    @Test
    public void succeed_whenNaturalIdIsPartOfAProperJpaEntity() {
        EqualsVerifier.forClass(NaturalIdBusinessKeyPersonEntity.class).verify();
    }

    @Test
    public void
            succeed_whenEqualsBehavesLikeVersionedEntity_givenIdIsMarkedWithIdAndWarningVersionedEntityIsSuppressed() {
        EqualsVerifier.forClass(JpaIdVersionedEntity.class)
                .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)
                .verify();
    }

    @Test
    public void succeed_whenMethodsAreAnnotatedInsteadOfFields() {
        EqualsVerifier.forClass(MethodAnnotatedBusinessKeyPerson.class).verify();
    }

    @Test
    public void fail_whenIdFieldIsTheOnlyFieldUsed() {
        expectFailure(
                "Precondition: you can't use withOnlyTheseFields on a field marked @Id or @EmbeddedId.",
                "Suppress Warning.SURROGATE_KEY if");
        EqualsVerifier.forClass(JpaIdBusinessKeyPerson.class).withOnlyTheseFields("id").verify();
    }

    @Test
    public void
            fail_whenOnlySocialSecurityIsUsed_givenSocialSecurityIsAnnotatedWithNaturalIdButSurrogateKeyWarningIsSuppressed() {
        expectFailure(
                "Precondition: you can't suppress Warning.SURROGATE_KEY when fields are marked @NaturalId.");
        EqualsVerifier.forClass(NaturalIdBusinessKeyPerson.class)
                .suppress(Warning.SURROGATE_KEY)
                .verify();
    }

    @Test
    public void fail_whenWithOnlyTheseFieldsIsUsed_givenWarningSurrogateKeyIsSuppressed() {
        expectException(
                IllegalStateException.class,
                "Precondition",
                "you can't use withOnlyTheseFields when Warning.SURROGATE_KEY is suppressed.");
        EqualsVerifier.forClass(JpaIdSurrogateKeyPerson.class)
                .withOnlyTheseFields("socialSecurity")
                .suppress(Warning.SURROGATE_KEY);
    }

    @Test
    public void fail_whenWithOnlyTheseFieldsIsUsed_givenWarningSurrogateKeyIsSuppressedInReverse() {
        expectException(
                IllegalStateException.class,
                "Precondition",
                "you can't use withOnlyTheseFields when Warning.SURROGATE_KEY is suppressed.");
        EqualsVerifier.forClass(JpaIdSurrogateKeyPerson.class)
                .suppress(Warning.SURROGATE_KEY)
                .withOnlyTheseFields("socialSecurity");
    }

    @Test
    public void fail_whenFieldsAreIgnored_givenWarningSurrogateKeyIsSuppressed() {
        expectException(
                IllegalStateException.class,
                "Precondition: you can't use withIgnoredFields when Warning.SURROGATE_KEY is suppressed.");
        EqualsVerifier.forClass(JpaIdSurrogateKeyPerson.class)
                .withIgnoredFields("socialSecurity")
                .suppress(Warning.SURROGATE_KEY);
    }

    @Test
    public void fail_whenFieldsAreIgnored_givenWarningSurrogateKeyIsSuppressedInReverse() {
        expectException(
                IllegalStateException.class,
                "Precondition: you can't use withIgnoredFields when Warning.SURROGATE_KEY is suppressed.");
        EqualsVerifier.forClass(JpaIdSurrogateKeyPerson.class)
                .suppress(Warning.SURROGATE_KEY)
                .withIgnoredFields("socialSecurity");
    }

    @Test
    public void fail_whenWithOnlyTheseFieldsIsUsed_givenFieldsAreMarkedWithNaturalId() {
        expectFailure(
                "Precondition: you can't use withOnlyTheseFields when fields are marked with @NaturalId.");
        EqualsVerifier.forClass(NaturalIdBusinessKeyPerson.class)
                .withOnlyTheseFields("socialSecurity")
                .verify();
    }

    @Test
    public void fail_whenFieldsAreIgnored_givenFieldsAreMarkedWithNaturalId() {
        expectFailure(
                "Precondition: you can't use withIgnoredFields when fields are marked with @NaturalId.");
        EqualsVerifier.forClass(NaturalIdBusinessKeyPerson.class)
                .withIgnoredFields("socialSecurity")
                .verify();
    }

    @Test
    public void fail_whenWarningVersionedEntityIsSuppressed_givenAFieldIsAnnotatedWithNaturalId() {
        expectFailure(
                "Precondition: you can't suppress Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY when fields are marked with @NaturalId.");
        EqualsVerifier.forClass(NaturalIdBusinessKeyPerson.class)
                .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)
                .verify();
    }

    @Test
    public void
            fail_whenWarningVersionedEntityIsSuppressed_givenWarningSurrogateKeyIsAlsoSuppressed() {
        expectException(
                IllegalStateException.class,
                "Precondition",
                "you can't suppress Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY when Warning.SURROGATE_KEY is also suppressed.");
        EqualsVerifier.forClass(JpaIdBusinessKeyPerson.class)
                .suppress(Warning.SURROGATE_KEY, Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY);
    }

    @Test
    public void fail_whenAnIdAnnotationFromAnotherPackageIsUsed() {
        expectFailure("Significant fields");
        EqualsVerifier.forClass(NonJpaIdBusinessKeyPerson.class).verify();
    }

    @Test
    public void fail_whenANaturalIdAnnotationFromAnotherPackageIsUsed() {
        expectFailure("Significant fields");
        EqualsVerifier.forClass(NonHibernateNaturalIdBusinessKeyPerson.class).verify();
    }

    static final class JpaIdBusinessKeyPerson {
        @Id private final UUID id;
        private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;

        public JpaIdBusinessKeyPerson(
                UUID id, String socialSecurity, String name, LocalDate birthdate) {
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
            JpaIdBusinessKeyPerson other = (JpaIdBusinessKeyPerson) obj;
            return Objects.equals(socialSecurity, other.socialSecurity)
                    && Objects.equals(name, other.name)
                    && Objects.equals(birthdate, other.birthdate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(socialSecurity, name, birthdate);
        }
    }

    static final class JpaIdBusinessKeyPersonReorderedFields {
        private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;
        @Id private final UUID id;

        public JpaIdBusinessKeyPersonReorderedFields(
                UUID id, String socialSecurity, String name, LocalDate birthdate) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof JpaIdBusinessKeyPersonReorderedFields)) {
                return false;
            }
            JpaIdBusinessKeyPersonReorderedFields other =
                    (JpaIdBusinessKeyPersonReorderedFields) obj;
            return Objects.equals(socialSecurity, other.socialSecurity)
                    && Objects.equals(name, other.name)
                    && Objects.equals(birthdate, other.birthdate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(socialSecurity, name, birthdate);
        }
    }

    static final class JpaIdSurrogateKeyPerson {
        @Id private final UUID id;
        private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;

        public JpaIdSurrogateKeyPerson(
                UUID id, String socialSecurity, String name, LocalDate birthdate) {
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
            JpaIdSurrogateKeyPerson other = (JpaIdSurrogateKeyPerson) obj;
            return Objects.equals(id, other.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    static final class JpaIdSurrogateKeyPersonReorderedFields {
        private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;
        @Id private final UUID id;

        public JpaIdSurrogateKeyPersonReorderedFields(
                UUID id, String socialSecurity, String name, LocalDate birthdate) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof JpaIdSurrogateKeyPersonReorderedFields)) {
                return false;
            }
            JpaIdSurrogateKeyPersonReorderedFields other =
                    (JpaIdSurrogateKeyPersonReorderedFields) obj;
            return Objects.equals(id, other.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    static final class JpaEmbeddedIdBusinessKeyPerson {
        @EmbeddedId private final UUID id;
        private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;

        public JpaEmbeddedIdBusinessKeyPerson(
                UUID id, String socialSecurity, String name, LocalDate birthdate) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof JpaEmbeddedIdBusinessKeyPerson)) {
                return false;
            }
            JpaEmbeddedIdBusinessKeyPerson other = (JpaEmbeddedIdBusinessKeyPerson) obj;
            return Objects.equals(socialSecurity, other.socialSecurity)
                    && Objects.equals(name, other.name)
                    && Objects.equals(birthdate, other.birthdate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(socialSecurity, name, birthdate);
        }
    }

    static final class JpaEmbeddedIdSurrogateKeyPerson {
        @EmbeddedId private final UUID id;
        private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;

        public JpaEmbeddedIdSurrogateKeyPerson(
                UUID id, String socialSecurity, String name, LocalDate birthdate) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof JpaEmbeddedIdSurrogateKeyPerson)) {
                return false;
            }
            JpaEmbeddedIdSurrogateKeyPerson other = (JpaEmbeddedIdSurrogateKeyPerson) obj;
            return Objects.equals(id, other.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    static final class NaturalIdBusinessKeyPerson {
        @Id private final UUID id;
        @NaturalId private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;

        public NaturalIdBusinessKeyPerson(
                UUID id, String socialSecurity, String name, LocalDate birthdate) {
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
            NaturalIdBusinessKeyPerson other = (NaturalIdBusinessKeyPerson) obj;
            return Objects.equals(socialSecurity, other.socialSecurity);
        }

        @Override
        public int hashCode() {
            return Objects.hash(socialSecurity);
        }
    }

    static final class NaturalIdWithoutJpaIdBusinessKeyPerson {
        private final UUID id;
        @NaturalId private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;

        public NaturalIdWithoutJpaIdBusinessKeyPerson(
                UUID id, String socialSecurity, String name, LocalDate birthdate) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NaturalIdWithoutJpaIdBusinessKeyPerson)) {
                return false;
            }
            NaturalIdWithoutJpaIdBusinessKeyPerson other =
                    (NaturalIdWithoutJpaIdBusinessKeyPerson) obj;
            return Objects.equals(socialSecurity, other.socialSecurity);
        }

        @Override
        public int hashCode() {
            return Objects.hash(socialSecurity);
        }
    }

    static final class JpaIdBusinessKeyPersonDoesntUseName {
        @Id private final UUID id;
        private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;

        public JpaIdBusinessKeyPersonDoesntUseName(
                UUID id, String socialSecurity, String name, LocalDate birthdate) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof JpaIdBusinessKeyPersonDoesntUseName)) {
                return false;
            }
            JpaIdBusinessKeyPersonDoesntUseName other = (JpaIdBusinessKeyPersonDoesntUseName) obj;
            return Objects.equals(socialSecurity, other.socialSecurity)
                    && Objects.equals(birthdate, other.birthdate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(socialSecurity, birthdate);
        }
    }

    @Entity
    static final class JpaIdBusinessKeyPersonEntity {
        @Id private UUID id;
        private String socialSecurity;
        private String name;
        private LocalDate birthdate;

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof JpaIdBusinessKeyPersonEntity)) {
                return false;
            }
            JpaIdBusinessKeyPersonEntity other = (JpaIdBusinessKeyPersonEntity) obj;
            return Objects.equals(socialSecurity, other.socialSecurity)
                    && Objects.equals(name, other.name)
                    && Objects.equals(birthdate, other.birthdate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(socialSecurity, name, birthdate);
        }
    }

    @Entity
    static final class NaturalIdBusinessKeyPersonEntity {
        @Id private UUID id;
        @NaturalId private String socialSecurity;
        private String name;
        private LocalDate birthdate;

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NaturalIdBusinessKeyPersonEntity)) {
                return false;
            }
            NaturalIdBusinessKeyPersonEntity other = (NaturalIdBusinessKeyPersonEntity) obj;
            return Objects.equals(socialSecurity, other.socialSecurity);
        }

        @Override
        public int hashCode() {
            return Objects.hash(socialSecurity);
        }
    }

    static final class NonJpaIdBusinessKeyPerson {
        @nl.jqno.equalsverifier.testhelpers.annotations.Id private final UUID id;
        private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;

        public NonJpaIdBusinessKeyPerson(
                UUID id, String socialSecurity, String name, LocalDate birthdate) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NonJpaIdBusinessKeyPerson)) {
                return false;
            }
            NonJpaIdBusinessKeyPerson other = (NonJpaIdBusinessKeyPerson) obj;
            return Objects.equals(socialSecurity, other.socialSecurity)
                    && Objects.equals(name, other.name)
                    && Objects.equals(birthdate, other.birthdate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(socialSecurity, name, birthdate);
        }
    }

    static final class NonHibernateNaturalIdBusinessKeyPerson {
        @Id private final UUID id;

        @nl.jqno.equalsverifier.testhelpers.annotations.NaturalId
        private final String socialSecurity;

        private final String name;
        private final LocalDate birthdate;

        public NonHibernateNaturalIdBusinessKeyPerson(
                UUID id, String socialSecurity, String name, LocalDate birthdate) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NonHibernateNaturalIdBusinessKeyPerson)) {
                return false;
            }
            NonHibernateNaturalIdBusinessKeyPerson other =
                    (NonHibernateNaturalIdBusinessKeyPerson) obj;
            return Objects.equals(socialSecurity, other.socialSecurity);
        }

        @Override
        public int hashCode() {
            return Objects.hash(socialSecurity);
        }
    }

    public static final class JpaIdVersionedEntity {
        @Id private final long id;
        private final String s;

        public JpaIdVersionedEntity(long id, String s) {
            this.id = id;
            this.s = s;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof JpaIdVersionedEntity)) {
                return false;
            }
            JpaIdVersionedEntity other = (JpaIdVersionedEntity) obj;
            if (id == 0L && other.id == 0L) {
                return Objects.equals(s, other.s);
            }
            return id == other.id;
        }

        @Override
        public int hashCode() {
            return Float.floatToIntBits(id);
        }
    }

    static final class MethodAnnotatedBusinessKeyPerson {
        private final UUID id;
        private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;

        public MethodAnnotatedBusinessKeyPerson(
                UUID id, String socialSecurity, String name, LocalDate birthdate) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Id
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
        public boolean equals(Object obj) {
            if (!(obj instanceof MethodAnnotatedBusinessKeyPerson)) {
                return false;
            }
            MethodAnnotatedBusinessKeyPerson other = (MethodAnnotatedBusinessKeyPerson) obj;
            return Objects.equals(socialSecurity, other.socialSecurity);
        }

        @Override
        public int hashCode() {
            return Objects.hash(socialSecurity);
        }
    }
}
