package nl.jqno.equalsverifier.integration.extra_features;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

import java.util.Objects;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

public class VersionedEntityTest {
    @Test
    public void fail_whenInstanceWithAZeroIdDoesNotEqualItself() {
        ExpectedException.when(
                        () ->
                                EqualsVerifier.forClass(OtherwiseStatelessVersionedEntity.class)
                                        .verify())
                .assertFailure()
                .assertMessageContains(
                        "object does not equal an identical copy of itself",
                        Warning.IDENTICAL_COPY.toString());
    }

    @Test
    public void
            fail_whenInstanceWithANonzeroIdEqualsItself_givenIdenticalCopyWarningIsSuppressed() {
        ExpectedException.when(
                        () ->
                                EqualsVerifier.forClass(OtherwiseStatelessVersionedEntity.class)
                                        .suppress(Warning.IDENTICAL_COPY)
                                        .verify())
                .assertFailure()
                .assertMessageContains(
                        "Unnecessary suppression", Warning.IDENTICAL_COPY.toString());
    }

    @Test
    public void
            succeed_whenInstanceWithAZeroIdDoesNotEqualItselfAndInstanceWithANonzeroIdDoes_givenVersionedEntityWarningIsSuppressed() {
        EqualsVerifier.forClass(OtherwiseStatelessVersionedEntity.class)
                .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)
                .verify();
    }

    @Test
    public void fail_whenInstanceWithAZeroIdDoesNotEqualItself_givenAVersionedEntityWithState() {
        ExpectedException.when(() -> EqualsVerifier.forClass(StringVersionedEntity.class).verify())
                .assertFailure()
                .assertMessageContains(
                        "object does not equal an identical copy of itself",
                        Warning.IDENTICAL_COPY.toString());
    }

    @Test
    public void
            fail_whenInstanceWithANonzeroIdEqualsItself_givenAVersionedEntityWithStateAndIdenticalCopyWarningIsSuppressed() {
        ExpectedException.when(
                        () ->
                                EqualsVerifier.forClass(StringVersionedEntity.class)
                                        .suppress(Warning.IDENTICAL_COPY)
                                        .verify())
                .assertFailure()
                .assertMessageContains(
                        "Unnecessary suppression", Warning.IDENTICAL_COPY.toString());
    }

    @Test
    // CHECKSTYLE OFF: LineLength.
    public void
            succeed_whenInstanceWithAZeroIdDoesNotEqualItselfAndInstanceWithANonzeroIdDoes_givenAVersionedEntityWithStateAndVersionedEntityWarningIsSuppressed() {
        EqualsVerifier.forClass(StringVersionedEntity.class)
                .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)
                .verify();
    }
    // CHECKSTYLE ON: LineLength.

    @Test
    public void
            fail_whenInstanceWithAZeroIdCanEqualItselfAndInstanceWithANonzeroIdAlso_givenAVersionedEntityWithState() {
        ExpectedException.when(
                        () -> EqualsVerifier.forClass(WeakStringVersionedEntity.class).verify())
                .assertFailure()
                .assertMessageContains("Significant fields");
    }

    @Test
    // CHECKSTYLE OFF: LineLength.
    public void
            succeed_whenInstanceWithAZeroIdCanEqualItselfAndInstanceWithANonzeroIdAlso_givenAVersionedEntityWithStateAndVersionedEntityWarningIsSuppressed() {
        EqualsVerifier.forClass(WeakStringVersionedEntity.class)
                .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)
                .verify();
    }
    // CHECKSTYLE ON: LineLength.

    @Test
    // CHECKSTYLE OFF: LineLength
    public void
            succeed_whenInstanceWithAZeroIdCanEqualItselfAndInstanceWithANonzeroIdAlso_givenAVersionedEntityWithStateAndAllFieldsWarningIsSuppressed() {
        EqualsVerifier.forClass(WeakStringVersionedEntity.class)
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }
    // CHECKSTYLE ON: LineLength.

    @Test
    public void
            fail_whenAnExceptionIsThrownInADifficultToReachPartOfTheSubclassOfAVersionedEntity() {
        ExpectedException.when(
                        () ->
                                EqualsVerifier.forClass(NonReflexiveCanEqualVersionedEntity.class)
                                        .verify())
                .assertFailure()
                .assertMessageContains("catch me if you can");
    }

    @Test
    public void
            fail_whenTheExceptionIsThrownInADifficultToReachPartOfTheSubclassOfAVersionedEntity_givenVersionedEntityWarningIsSuppressed() {
        ExpectedException.when(
                        () ->
                                EqualsVerifier.forClass(NonReflexiveCanEqualVersionedEntity.class)
                                        .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)
                                        .verify())
                .assertFailure()
                .assertMessageContains("catch me if you can");
    }

    @Test
    public void succeed_whenTheParentOfTheVersionedEntityIsCheckedForSanity() {
        EqualsVerifier.forClass(CanEqualVersionedEntity.class).verify();
    }

    public static final class OtherwiseStatelessVersionedEntity {
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
            return defaultHashCode(this);
        }
    }

    public static final class StringVersionedEntity {
        private final long id;

        @SuppressWarnings("unused")
        private final String s;

        public StringVersionedEntity(long id, String s) {
            this.id = id;
            this.s = s;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof StringVersionedEntity)) {
                return false;
            }
            StringVersionedEntity other = (StringVersionedEntity) obj;
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

    public static final class WeakStringVersionedEntity {
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
            } else if (other.id == null) {
                return other.canEqual(this);
            }
            return false;
        }

        public boolean canEqual(Object obj) {
            return obj instanceof CanEqualVersionedEntity;
        }

        @Override
        public final int hashCode() {
            return defaultHashCode(this);
        }
    }

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
