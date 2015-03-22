/*
 * Copyright 2015 Jan Ouwens
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
package nl.jqno.equalsverifier.integration.extra_features.nonnull.eclipse;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

public final class NonnullEclipseOnPackage {
	private final Object o;
	
	public NonnullEclipseOnPackage(Object o) { this.o = o; }
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof NonnullEclipseOnPackage)) {
			return false;
		}
		NonnullEclipseOnPackage other = (NonnullEclipseOnPackage)obj;
		return o.equals(other.o);
	}
	
	@Override public int hashCode() { return defaultHashCode(this); }
}