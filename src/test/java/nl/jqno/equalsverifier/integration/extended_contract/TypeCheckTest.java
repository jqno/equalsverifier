/*
 * Copyright 2015 Jan Ouwens
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.jqno.equalsverifier.integration.extended_contract;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import org.junit.Test;

public class TypeCheckTest extends IntegrationTestBase {
	@Test
	public void fail_whenEqualsReturnsTrueForACompletelyUnrelatedType() {
		expectFailure("Type-check: equals returns true for an unrelated type.");
		EqualsVerifier.forClass(WrongTypeCheck.class)
				.verify();
	}
	
	@Test
	public void fail_whenEqualsDoesNotTypeCheck() {
		expectFailureWithCause(ClassCastException.class, "Type-check: equals throws ClassCastException");
		EqualsVerifier.forClass(NoTypeCheck.class)
				.verify();
	}
	
	@Test
	public void fail_whenEqualsDoesNotTypeCheckAndThrowsAnExceptionOtherThanClassCastException() {
		expectFailureWithCause(IllegalStateException.class, "Type-check: equals throws IllegalStateException");
		EqualsVerifier.forClass(NoTypeCheckButNoClassCastExceptionEither.class)
				.verify();
	}
	
	static final class WrongTypeCheck {
		private final int i;
		
		public WrongTypeCheck(int i) { this.i = i; }
		
		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof WrongTypeCheck)) {
				return true;
			}
			return i == ((WrongTypeCheck)obj).i;
		}
	}
	
	static final class NoTypeCheck {
		private final int i;
		
		public NoTypeCheck(int i) { this.i = i; }
		
		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			return i == ((NoTypeCheck)obj).i;
		}
	}
	
	static final class NoTypeCheckButNoClassCastExceptionEither {
		private final int i;
		
		public NoTypeCheckButNoClassCastExceptionEither(int i) { this.i = i; }
		
		@Override
		public boolean equals(Object obj) {
			try {
				if (obj == null) {
					return false;
				}
				return i == ((NoTypeCheckButNoClassCastExceptionEither)obj).i;
			}
			catch (ClassCastException e) {
				throw new IllegalStateException(e);
			}
		}
	}
}
