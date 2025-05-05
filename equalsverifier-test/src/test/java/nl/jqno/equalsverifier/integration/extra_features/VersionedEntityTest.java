package nl.jqno.equalsverifier.integration.extra_features;

import java.util.Objects;
import java.util.UUID;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import nl.jqno.equalsverifier_testhelpers.annotations.javax.persistence.Entity;
import nl.jqno.equalsverifier_testhelpers.annotations.javax.persistence.Id;
import org.junit.jupiter.api.Test;

// CHECKSTYLE OFF: LineLength

class VersionedEntityTest {

    @Test
    void fail_whenInstanceWithAZeroIdDoesNotEqualItself() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(OtherwiseStatelessVersionedEntity.class).verify())
                .assertFailure()
                .assertMessageContains(
                    "entity does not equal an identical copy of itself",
                    Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY.toString());
    }

    @Test
    void fail_whenInstanceWithANonzeroIdEqualsItself_givenIdenticalCopyWarningIsSuppressed() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(OtherwiseStatelessVersionedEntity.class)
                            .suppress(Warning.IDENTICAL_COPY)
                            .verify())
                .assertFailure()
                .assertMessageContains("Unnecessary suppression", Warning.IDENTICAL_COPY.toString());
    }

    @Test
    void succeed_whenInstanceWithAZeroIdDoesNotEqualItselfAndInstanceWithANonzeroIdDoes_givenVersionedEntityWarningIsSuppressed() {
        EqualsVerifier
                .forClass(OtherwiseStatelessVersionedEntity.class)
                .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY, Warning.SURROGATE_KEY)
                .verify();
    }

    @Test
    void fail_whenInstanceWithAZeroIdDoesNotEqualItself_givenAVersionedEntityWithState() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(LongIdStringFieldVersionedEntity.class).verify())
                .assertFailure()
                .assertMessageContains(
                    "entity does not equal an identical copy of itself",
                    Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY.toString());
    }

    @Test
    void fail_whenInstanceWithANonzeroIdEqualsItself_givenAVersionedEntityWithStateAndIdenticalCopyWarningIsSuppressed() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(LongIdStringFieldVersionedEntity.class)
                            .suppress(Warning.IDENTICAL_COPY)
                            .verify())
                .assertFailure()
                .assertMessageContains("Unnecessary suppression", Warning.IDENTICAL_COPY.toString());
    }

    @Test
    void succeed_whenInstanceWithAZeroIdDoesNotEqualItselfAndInstanceWithANonzeroIdDoes_givenAVersionedEntityWithStateAndVersionedEntityWarningIsSuppressed() {
        EqualsVerifier
                .forClass(LongIdStringFieldVersionedEntity.class)
                .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY, Warning.SURROGATE_KEY)
                .verify();
    }

    @Test
    void fail_whenInstanceWithANullIdDoesNotEqualItself_givenAVersionedEntityWithState() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(UuidIdStringFieldVersionedEntity.class).verify())
                .assertFailure()
                .assertMessageContains(
                    "entity does not equal an identical copy of itself",
                    Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY.toString());
    }

    @Test
    void fail_whenInstanceWithANonnullIdEqualsItself_givenAVersionedEntityWithStateAndIdenticalCopyWarningIsSuppressed() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(UuidIdStringFieldVersionedEntity.class)
                            .suppress(Warning.IDENTICAL_COPY)
                            .verify())
                .assertFailure()
                .assertMessageContains("Unnecessary suppression", Warning.IDENTICAL_COPY.toString());
    }

    @Test
    void succeed_whenInstanceWithANullIdDoesNotEqualItselfAndInstanceWithANonnullIdDoes_givenAVersionedEntityWithStateAndVersionedEntityWarningIsSuppressed() {
        EqualsVerifier
                .forClass(UuidIdStringFieldVersionedEntity.class)
                .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY, Warning.SURROGATE_KEY)
                .verify();
    }

    @Test
    void fail_whenInstanceWithAZeroIdCanEqualItselfAndInstanceWithANonzeroIdAlso_givenAVersionedEntityWithState() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(WeakStringVersionedEntity.class).verify())
                .assertFailure()
                .assertMessageContains("Significant fields");
    }

    @Test
    void succeed_whenInstanceWithAZeroIdCanEqualItselfAndInstanceWithANonzeroIdAlso_givenAVersionedEntityWithStateAndVersionedEntityWarningIsSuppressed() {
        EqualsVerifier
                .forClass(WeakStringVersionedEntity.class)
                .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY, Warning.SURROGATE_KEY)
                .verify();
    }

    @Test
    void succeed_whenInstanceWithAZeroIdCanEqualItselfAndInstanceWithANonzeroIdAlso_givenAVersionedEntityWithStateAndAllFieldsWarningIsSuppressed() {
        EqualsVerifier.forClass(WeakStringVersionedEntity.class).suppress(Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
    }

    @Test
    void succeed_whenInstanceWithANullableIdDoesNullCheck_givenAVersionedEntityWithStateAndVersionedEntityWarningIsSuppressed() {
        EqualsVerifier
                .forClass(NullCheckStringVersionedEntity.class)
                .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY, Warning.SURROGATE_KEY)
                .verify();
    }

    @Test
    void succeed_whenEntityWithABusinessKey_givenAVersionedEntityWithStateAndVersionedEntityWarningIsSuppressed() {
        EqualsVerifier
                .forClass(BusinessKeyStringVersionedEntity.class)
                .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)
                .verify();
    }

    @Test
    void succeed_whenVersionedEntityHasAParentEntityThatHandlesEquals() {
        EqualsVerifier
                .forClass(InheritedEntity.class)
                .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY, Warning.SURROGATE_KEY)
                .verify();
    }

    @Test
    void succeed_whenTheParentOfTheVersionedEntityIsCheckedForSanity() {
        EqualsVerifier.forClass(CanEqualVersionedEntity.class).verify();
    }

    @Test
    void fail_whenAnExceptionIsThrownInADifficultToReachPartOfTheSubclassOfAVersionedEntity() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(NonReflexiveCanEqualVersionedEntity.class).verify())
                .assertFailure()
                .assertMessageContains("catch me if you can");
    }

    @Test
    void fail_whenTheExceptionIsThrownInADifficultToReachPartOfTheSubclassOfAVersionedEntity_givenVersionedEntityWarningIsSuppressed() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(NonReflexiveCanEqualVersionedEntity.class)
                            .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)
                            .verify())
                .assertFailure()
                .assertMessageContains("catch me if you can");
    }

    @Entity
    public static final class OtherwiseStatelessVersionedEntity {

        @Id
        private final long id;

        public OtherwiseStatelessVersionedEntity(long id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof OtherwiseStatelessVersionedEntity)) {
                return false;
            }
            OtherwiseStatelessVersionedEntity other = (OtherwiseStatelessVersionedEntity) obj;
            if (id == 0L && other.id == 0L) {
                return super.equals(obj);
            }
            return id == other.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    @Entity
    public static final class LongIdStringFieldVersionedEntity {

        @Id
        private final long id;

        @SuppressWarnings("unused")
        private final String s;

        public LongIdStringFieldVersionedEntity(long id, String s) {
            this.id = id;
            this.s = s;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof LongIdStringFieldVersionedEntity)) {
                return false;
            }
            LongIdStringFieldVersionedEntity other = (LongIdStringFieldVersionedEntity) obj;
            if (id == 0L && other.id == 0L) {
                return false;
            }
            return id == other.id;
        }

        @Override
        public int hashCode() {
            return Float.floatToIntBits(id);
        }
    }

    @Entity
    public static final class UuidIdStringFieldVersionedEntity {

        @Id
        private final UUID id;

        @SuppressWarnings("unused")
        private final String s;

        public UuidIdStringFieldVersionedEntity(UUID id, String s) {
            this.id = id;
            this.s = s;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof UuidIdStringFieldVersionedEntity)) {
                return false;
            }
            UuidIdStringFieldVersionedEntity other = (UuidIdStringFieldVersionedEntity) obj;
            if (id == null && other.id == null) {
                return false;
            }
            return Objects.equals(id, other.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    @Entity
    public static final class WeakStringVersionedEntity {

        @Id
        private final long id;

        private final String s;

        public WeakStringVersionedEntity(long id, String s) {
            this.id = id;
            this.s = s;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof WeakStringVersionedEntity)) {
                return false;
            }
            WeakStringVersionedEntity other = (WeakStringVersionedEntity) obj;
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

    @Entity
    public static final class NullCheckStringVersionedEntity {

        @Id
        private final Long id;

        @SuppressWarnings("unused")
        private final String s;

        public NullCheckStringVersionedEntity(long id, String s) {
            this.id = id;
            this.s = s;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NullCheckStringVersionedEntity)) {
                return false;
            }
            NullCheckStringVersionedEntity other = (NullCheckStringVersionedEntity) obj;
            return id != null && id.equals(other.id);
        }

        @Override
        public int hashCode() {
            return id == null ? 0 : Float.floatToIntBits(id);
        }
    }

    @Entity
    public static final class BusinessKeyStringVersionedEntity {

        @Id
        private final Long id;

        private final String s;

        public BusinessKeyStringVersionedEntity(long id, String s) {
            this.id = id;
            this.s = s;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof BusinessKeyStringVersionedEntity)) {
                return false;
            }
            BusinessKeyStringVersionedEntity other = (BusinessKeyStringVersionedEntity) obj;
            return Objects.equals(s, other.s);
        }

        @Override
        public int hashCode() {
            return Objects.hash(s);
        }
    }

    private static class BaseEntity {

        @Id
        private final Long id;

        public BaseEntity(Long id) {
            this.id = id;
        }

        public boolean isNew() {
            return id == null;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof BaseEntity)) {
                return false;
            }
            if (isNew()) {
                return false;
            }
            BaseEntity other = (BaseEntity) obj;
            return Objects.equals(id, other.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    @Entity
    private static class InheritedEntity extends BaseEntity {

        public InheritedEntity(Long id) {
            super(id);
        }
    }

    @Entity
    private static class CanEqualVersionedEntity {

        private final Long id;

        public CanEqualVersionedEntity(Long id) {
            this.id = id;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof CanEqualVersionedEntity)) {
                return false;
            }
            CanEqualVersionedEntity other = (CanEqualVersionedEntity) obj;
            if (id != null) {
                return id.equals(other.id);
            }
            else if (other.id == null) {
                return other.canEqual(this);
            }
            return false;
        }

        public boolean canEqual(Object obj) {
            return obj instanceof CanEqualVersionedEntity;
        }

        @Override
        public final int hashCode() {
            return Objects.hash(id);
        }
    }

    @Entity
    private static class NonReflexiveCanEqualVersionedEntity extends CanEqualVersionedEntity {

        public NonReflexiveCanEqualVersionedEntity(Long id) {
            super(id);
        }

        @Override
        public boolean canEqual(Object obj) {
            throw new IllegalStateException("catch me if you can");
        }
    }
}
