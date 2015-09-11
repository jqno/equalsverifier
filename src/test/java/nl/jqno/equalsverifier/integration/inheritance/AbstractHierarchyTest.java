/*
 * Copyright 2009-2010, 2014 Jan Ouwens
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
package nl.jqno.equalsverifier.integration.inheritance;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import nl.jqno.equalsverifier.testhelpers.types.Color;
import org.junit.Test;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

public class AbstractHierarchyTest extends IntegrationTestBase {
    @Test
    public void succeed_whenEqualsAndHashCodeAreFinal_givenClassIsAbstract() {
        EqualsVerifier.forClass(AbstractFinalMethodsPoint.class)
                .verify();
    }

    @Test
    public void succeed_whenAnImplementingClassWithCorrectlyImplementedEquals_givenClassIsAbstract() {
        EqualsVerifier.forClass(AbstractRedefinablePoint.class)
                .withRedefinedSubclass(FinalRedefinedPoint.class)
                .verify();
    }

    @Test
    public void fail_whenEqualsThrowsNull_givenClassIsAbstract() {
        expectFailureWithCause(NullPointerException.class, "Non-nullity: equals throws NullPointerException");
        EqualsVerifier.forClass(NullThrowingColorContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenEqualsThrowsNull_givenClassIsAbstractAndWarningIsSuppressed() {
        EqualsVerifier.forClass(NullThrowingColorContainer.class)
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    static abstract class AbstractFinalMethodsPoint {
        private final int x;
        private final int y;

        public AbstractFinalMethodsPoint(int x, int y) { this.x = x; this.y = y; }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof AbstractFinalMethodsPoint)) {
                return false;
            }
            AbstractFinalMethodsPoint p = (AbstractFinalMethodsPoint)obj;
            return x == p.x && y == p.y;
        }

        @Override public final int hashCode() { return defaultHashCode(this); }
    }

    static abstract class AbstractRedefinablePoint {
        private final int x;
        private final int y;

        public AbstractRedefinablePoint(int x, int y) { this.x = x; this.y = y; }

        public boolean canEqual(Object obj) {
            return obj instanceof AbstractRedefinablePoint;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof AbstractRedefinablePoint)) {
                return false;
            }
            AbstractRedefinablePoint p = (AbstractRedefinablePoint)obj;
            return p.canEqual(this) && x == p.x && y == p.y;
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class FinalRedefinedPoint extends AbstractRedefinablePoint {
        private final Color color;

        public FinalRedefinedPoint(int x, int y, Color color) { super(x, y); this.color = color; }

        @Override
        public boolean canEqual(Object obj) {
            return obj instanceof FinalRedefinedPoint;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof FinalRedefinedPoint)) {
                return false;
            }
            FinalRedefinedPoint p = (FinalRedefinedPoint)obj;
            return p.canEqual(this) && super.equals(p) && color == p.color;
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static abstract class NullThrowingColorContainer {
        private final Color color;

        public NullThrowingColorContainer(Color color) { this.color = color; }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof NullThrowingColorContainer)) {
                return false;
            }
            return color.equals(((NullThrowingColorContainer)obj).color);
        }

        @Override public final int hashCode() { return defaultHashCode(this); }
    }
}
