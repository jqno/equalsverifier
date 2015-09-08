/*
 * Copyright 2010, 2014-2015 Jan Ouwens
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
package nl.jqno.equalsverifier.integration.extended_contract;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import org.junit.Ignore;
import org.junit.Test;

public class DontOverrideEqualsTest extends IntegrationTestBase {
    @Test
    public void fail_whenEqualsIsInheritedDirectlyFromObject() {
        expectFailure("Equals is inherited directly from Object");
        EqualsVerifier.forClass(NoEqualsNoHashCodeMethod.class)
                .verify();
    }

    @Test
    public void succeed_whenEqualsIsInheritedDirectlyFromObject_givenDirectlyInheritedWarningIsSuppressed() {
        EqualsVerifier.forClass(NoEqualsNoHashCodeMethod.class)
                .suppress(Warning.INHERITED_DIRECTLY_FROM_OBJECT)
                .verify();
    }

    @Test
    public void succeed_whenEqualsIsInheritedButNotFromObject() {
        EqualsVerifier.forClass(InheritedEqualsAndHashCodeMethod.class)
                .verify();
    }

    @Test
    public void succeed_whenClassIsAPojoAndEqualsIsInheritedDirectlyFromObject_givenVariousWarningsAreSuppressed() {
        EqualsVerifier.forClass(Pojo.class)
                .suppress(Warning.INHERITED_DIRECTLY_FROM_OBJECT)
                .suppress(Warning.NONFINAL_FIELDS, Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    static final class NoEqualsNoHashCodeMethod {}

    static final class InheritedEqualsAndHashCodeMethod extends Point {
        InheritedEqualsAndHashCodeMethod(int x, int y) { super(x, y); }
    }

    public final class Pojo {
        private String value;

        public void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return getClass().getName() + " " + value;
        }
    }
}
