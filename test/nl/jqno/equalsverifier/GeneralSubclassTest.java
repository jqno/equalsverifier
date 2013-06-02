/*
 * Copyright 2009-2010 Jan Ouwens
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
package nl.jqno.equalsverifier;

import static nl.jqno.equalsverifier.testhelpers.Util.assertFailure;
import nl.jqno.equalsverifier.testhelpers.points.Color;
import nl.jqno.equalsverifier.testhelpers.points.FinalPoint;

import org.junit.Test;

public class GeneralSubclassTest {
	@Test
	public void isFinal() {
		EqualsVerifier.forClass(FinalPoint.class).verify();
	}
	
	@Test
	public void fieldsCheckerAlsoWorksForSubclasses() {
		EqualsVerifier<ColorContainerBrokenSubclass> ev = EqualsVerifier.forClass(ColorContainerBrokenSubclass.class);
		assertFailure(ev, NullPointerException.class, "Non-nullity: equals throws NullPointerException");
	}

	@Test
	public void liskovSubstitutionPrinciple() {
		EqualsVerifier<LiskovSubstitutionPrincipleBroken> ev =
				EqualsVerifier.forClass(LiskovSubstitutionPrincipleBroken.class);
		assertFailure(ev, "Subclass", "object is not equal to an instance of a trivial subclass with equal fields",	"Consider making the class final.");
	}
	
	static class ColorContainer {
		public final Color color;

		public ColorContainer(Color color) {
			this.color = color;
		}
	}
	
	static final class ColorContainerBrokenSubclass extends ColorContainer {
		public ColorContainerBrokenSubclass(Color color) {
			super(color);
		}
		
		@Override
		public final boolean equals(Object obj) {
			if (!(obj instanceof ColorContainerBrokenSubclass)) {
				return false;
			}
			ColorContainerBrokenSubclass other = (ColorContainerBrokenSubclass)obj;
			return color.equals(other.color);
		}
	}
	
	static class LiskovSubstitutionPrincipleBroken {
		private final int x;
		
		LiskovSubstitutionPrincipleBroken(int x) {
			this.x = x;
		}
		
		@Override
		public final boolean equals(Object obj) {
			if (obj == null || getClass() != obj.getClass()) {
				return false;
			}
			return x == ((LiskovSubstitutionPrincipleBroken)obj).x;
		}
		
		@Override
		public final int hashCode() {
			return x;
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + ":" + x;
		}
	}
}
