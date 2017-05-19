/*
 * Copyright 2017 Jan Ouwens
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
package nl.jqno.equalsverifier.integration.operational;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.signedjar.SignedJarPoint;
import org.junit.Test;

public class SignedJarTest {
    @Test
    public void succeed_whenTestingAClassFromASignedJar() {
        EqualsVerifier.forClass(SignedJarPoint.class)
                .verify();
    }

    @Test
    public void succeed_whenTestingAClassThatExtendsFromAClassFromASignedJar() {
        EqualsVerifier.forClass(SubclassOfSignedJarPoint.class)
                .verify();
    }

    static final class SubclassOfSignedJarPoint extends SignedJarPoint {
        public SubclassOfSignedJarPoint(int x, int y) {
            super(x, y);
        }
    }
}
