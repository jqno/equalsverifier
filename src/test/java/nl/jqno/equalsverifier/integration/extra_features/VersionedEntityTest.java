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

import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

public class VersionedEntityTest extends IntegrationTestBase {
	@Test
	public void fail_whenInstanceWithAZeroIdDoesNotEqualItself() {
		expectFailure("object does not equal an identical copy of itself", Warning.IDENTICAL_COPY.toString());
		EqualsVerifier.forClass(VersionedEntity.class)
				.verify();
	}
	
	@Test
	public void fail_whenInstanceWithANonzeroIdEqualsItself_givenIdenticalCopyWarningIsSuppressed() {
		expectFailure("Unnecessary suppression", Warning.IDENTICAL_COPY.toString());
		EqualsVerifier.forClass(VersionedEntity.class)
				.suppress(Warning.IDENTICAL_COPY)
				.verify();
	}
	
	@Test
	public void succeed_whenInstanceWithAZeroIdDoesNotEqualItselfAndInstanceWithANonzeroIdDoes_givenIdenticalCopyForVersionedEntityWarningIsSuppressed() {
		EqualsVerifier.forClass(VersionedEntity.class)
				.suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)
				.verify();
	}
	
	@Test
	public void fail_whenAnExceptionIsThrownInADifficultToReachPartOfTheSubclassOfAVersionedEntity() {
		expectFailure("catch me if you can");
		EqualsVerifier.forClass(NonReflexiveCanEqualVersionedEntity.class)
				.verify();
	}
	
	@Test
	public void succeed_whenTheExceptionIsThrownInADifficultToReachPartOfTheSubclassOfAVersionedEntity_givenIdenticalCopyForVersionedEntityWarningIsSuppressed() {
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
	
	public static final class VersionedEntity {
		private final long id;
		
		public VersionedEntity(long id) { this.id = id; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof VersionedEntity)) {
				return false;
			}
			VersionedEntity other = (VersionedEntity) obj;
			if (id == 0L && other.id == 0L) {
				return super.equals(obj);
			}
			return id == other.id;
		}
		
		@Override public int hashCode() { return defaultHashCode(this); }
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
