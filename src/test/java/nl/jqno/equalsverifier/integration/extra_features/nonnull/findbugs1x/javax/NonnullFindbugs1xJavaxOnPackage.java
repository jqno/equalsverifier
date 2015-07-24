/*
 * Copyright 2014-2015 Jan Ouwens
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
package nl.jqno.equalsverifier.integration.extra_features.nonnull.findbugs1x.javax;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

public final class NonnullFindbugs1xJavaxOnPackage {
    private final Object o;

    public NonnullFindbugs1xJavaxOnPackage(Object o) { this.o = o; }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NonnullFindbugs1xJavaxOnPackage)) {
            return false;
        }
        NonnullFindbugs1xJavaxOnPackage other = (NonnullFindbugs1xJavaxOnPackage)obj;
        return o.equals(other.o);
    }

    @Override public int hashCode() { return defaultHashCode(this); }
}