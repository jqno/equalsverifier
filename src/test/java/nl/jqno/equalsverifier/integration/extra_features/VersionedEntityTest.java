/*
 * Copyright 2013-2015 Jan Ouwens
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.jqno.equalsverifier.integration.extra_features;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import org.junit.Test;

import java.util.Objects;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

public class VersionedEntityTest extends IntegrationTestBase {
    @Test
    public void fail_whenInstanceWithAZeroIdDoesNotEqualItself() {
        expectFailure("object does not equal an identical copy of itself", Warning.IDENTICAL_COPY.toString());
        EqualsVerifier.forClass(OtherwiseStatelessVersionedEntity.class)
                .verify();
    }

    @Test
    public void fail_whenInstanceWithANonzeroIdEqualsItself_givenIdenticalCopyWarningIsSuppressed() {
        expectFailure("Unnecessary suppression", Warning.IDENTICAL_COPY.toString());
        EqualsVerifier.forClass(OtherwiseStatelessVersionedEntity.class)
                .suppress(Warning.IDENTICAL_COPY)
                .verify();
    }

    @Test
    public void succeed_whenInstanceWithAZeroIdDoesNotEqualItselfAndInstanceWithANonzeroIdDoes_givenVersionedEntityWarningIsSuppressed() {
        EqualsVerifier.forClass(OtherwiseStatelessVersionedEntity.class)
                .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)
                .verify();
    }

    @Test
    public void fail_whenInstanceWithAZeroIdDoesNotEqualItself_givenAVersionedEntityWithState() {
        expectFailure("object does not equal an identical copy of itself", Warning.IDENTICAL_COPY.toString());
        EqualsVerifier.forClass(StringVersionedEntity.class)
                .verify();
    }

    @Test
    public void fail_whenInstanceWithANonzeroIdEqualsItself_givenAVersionedEntityWithStateAndIdenticalCopyWarningIsSuppressed() {
        expectFailure("Unnecessary suppression", Warning.IDENTICAL_COPY.toString());
        EqualsVerifier.forClass(StringVersionedEntity.class)
                .suppress(Warning.IDENTICAL_COPY)
                .verify();
    }

    @Test
    // CHECKSTYLE: ignore LineLength for 1 line.
    public void succeed_whenInstanceWithAZeroIdDoesNotEqualItselfAndInstanceWithANonzeroIdDoes_givenAVersionedEntityWithStateAndVersionedEntityWarningIsSuppressed() {
        EqualsVerifier.forClass(StringVersionedEntity.class)
                .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)
                .verify();
    }

    @Test
    public void fail_whenInstanceWithAZeroIdCanEqualItselfAndInstanceWithANonzeroIdAlso_givenAVersionedEntityWithState() {
        expectFailure("Significant fields");
        EqualsVerifier.forClass(WeakStringVersionedEntity.class)
                .verify();
    }

    @Test
    // CHECKSTYLE: ignore LineLength for 1 line.
    public void succeed_whenInstanceWithAZeroIdCanEqualItselfAndInstanceWithANonzeroIdAlso_givenAVersionedEntityWithStateAndVersionedEntityWarningIsSuppressed() {
        EqualsVerifier.forClass(WeakStringVersionedEntity.class)
                .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)
                .verify();
    }

    @Test
    // CHECKSTYLE: ignore LineLength for 1 line.
    public void succeed_whenInstanceWithAZeroIdCanEqualItselfAndInstanceWithANonzeroIdAlso_givenAVersionedEntityWithStateAndAllFieldsWarningIsSuppressed() {
        EqualsVerifier.forClass(WeakStringVersionedEntity.class)
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    public void fail_whenAnExceptionIsThrownInADifficultToReachPartOfTheSubclassOfAVersionedEntity() {
        expectFailure("catch me if you can");
        EqualsVerifier.forClass(NonReflexiveCanEqualVersionedEntity.class)
                .verify();
    }

    @Test
    public void fail_whenTheExceptionIsThrownInADifficultToReachPartOfTheSubclassOfAVersionedEntity_givenVersionedEntityWarningIsSuppressed() {
        expectFailure("catch me if you can");
        EqualsVerifier.forClass(NonReflexiveCanEqualVersionedEntity.class)
                .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)
                .verify();
    }

    @Test
    public void succeed_whenTheParentOfTheVersionedEntityIsCheckedForSanity() {
        EqualsVerifier.forClass(CanEqualVersionedEntity.class)
                .verify();
    }

    public static final class OtherwiseStatelessVersionedEntity {
        private final long id;

        public OtherwiseStatelessVersionedEntity(long id) { this.id = id; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof OtherwiseStatelessVersionedEntity)) {
                return false;
            }
            OtherwiseStatelessVersionedEntity other = (OtherwiseStatelessVersionedEntity)obj;
            if (id == 0L && other.id == 0L) {
                return super.equals(obj);
            }
            return id == other.id;
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    public static final class StringVersionedEntity {
        private final long id;
        @SuppressWarnings("unused")
        private final String s;

        public StringVersionedEntity(long id, String s) { this.id = id; this.s = s; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof StringVersionedEntity)) {
                return false;
            }
            StringVersionedEntity other = (StringVersionedEntity)obj;
            if (id == 0L && other.id == 0L) {
                return false;
            }
            return id == other.id;
        }

        @Override public int hashCode() {
            return Float.floatToIntBits(id);
        }
    }

    public static final class WeakStringVersionedEntity {
        private final long id;
        private final String s;

        public WeakStringVersionedEntity(long id, String s) { this.id = id; this.s = s; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof WeakStringVersionedEntity)) {
                return false;
            }
            WeakStringVersionedEntity other = (WeakStringVersionedEntity)obj;
            if (id == 0L && other.id == 0L) {
                return Objects.equals(s, other.s);
            }
            return id == other.id;
        }

        @Override public int hashCode() {
            return Float.floatToIntBits(id);
        }
    }

    private static class CanEqualVersionedEntity {
        private final Long id;

        public CanEqualVersionedEntity(Long id) { this.id = id; }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof CanEqualVersionedEntity)) {
                return false;
            }
            CanEqualVersionedEntity other = (CanEqualVersionedEntity)obj;
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

        @Override public final int hashCode() { return defaultHashCode(this); }
    }

    private static class NonReflexiveCanEqualVersionedEntity extends CanEqualVersionedEntity {
        public NonReflexiveCanEqualVersionedEntity(Long id) { super(id); }

        @Override
        public boolean canEqual(Object obj) {
            throw new IllegalStateException("catch me if you can");
        }
    }
}
