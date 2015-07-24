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
package nl.jqno.equalsverifier.testhelpers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

public class IntegrationTestBase {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public void expectFailureWithCause(Class<? extends Throwable> cause, String... fragments) {
        thrown.expect(new CauseMatcher(cause));
        expectException(AssertionError.class, fragments);
    }

    public void expectFailure(String... fragments) {
        expectException(AssertionError.class, fragments);
    }

    public void expectException(Class<? extends Throwable> e, String... fragments) {
        thrown.expect(e);
        for (String messageFragment : fragments) {
            thrown.expectMessage(messageFragment);
        }
    }

    private static final class CauseMatcher extends BaseMatcher<Object> {
        private final Class<? extends Throwable> cause;

        public CauseMatcher(Class<? extends Throwable> cause) {
            this.cause = cause;
        }

        @Override
        public boolean matches(Object item) {
            Throwable actual = ((Throwable)item).getCause();
            if (cause == null) {
                return actual == null;
            }
            return cause.isInstance(actual);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("unexpected exception");
        }
    }
}
