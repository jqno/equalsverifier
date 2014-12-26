/*
 * Copyright 2014 Jan Ouwens
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
package nl.jqno.equalsverifier.integration.extra_features.jsr305_nonnull.inapplicable;

public class Jsr305DefaultNonnullInapplicableTypeQualifierDefaultOnPackage {
	private final Object o;
	
	public Jsr305DefaultNonnullInapplicableTypeQualifierDefaultOnPackage(Object o) { this.o = o; }
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Jsr305DefaultNonnullInapplicableTypeQualifierDefaultOnPackage)) {
			return false;
		}
		Jsr305DefaultNonnullInapplicableTypeQualifierDefaultOnPackage other = (Jsr305DefaultNonnullInapplicableTypeQualifierDefaultOnPackage)obj;
		return o.equals(other.o);
	}
	
	@Override public int hashCode() { return o.hashCode(); }
}