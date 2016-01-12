/*
 * Copyright 2010-2011, 2013, 2015 Jan Ouwens
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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import nl.jqno.equalsverifier.FieldInspector.FieldCheck;
import nl.jqno.equalsverifier.internal.ClassAccessor;
import nl.jqno.equalsverifier.internal.FieldAccessor;
import nl.jqno.equalsverifier.internal.Formatter;
import nl.jqno.equalsverifier.internal.annotations.NonnullAnnotationChecker;

import java.lang.reflect.Field;

import static nl.jqno.equalsverifier.internal.Assert.fail;

class NullChecker<T> implements Checker {
    private final Configuration<T> config;
    private final ClassAccessor<T> classAccessor;

    public NullChecker(Configuration<T> config) {
        this.config = config;
        this.classAccessor = config.createClassAccessor();
    }

    @Override
    public void check() {
        if (config.getWarningsToSuppress().contains(Warning.NULL_FIELDS)) {
            return;
        }

        FieldInspector<T> inspector = new FieldInspector<T>(classAccessor, config.getTypeTag());
        inspector.check(new NullPointerExceptionFieldCheck());
    }

    private class NullPointerExceptionFieldCheck implements FieldCheck {
        @Override
        public void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
            Field field = referenceAccessor.getField();
            if (field.getType().isPrimitive()) {
                return;
            }
            if (NonnullAnnotationChecker.fieldIsNonnull(classAccessor, field)) {
                return;
            }
            final Object reference = referenceAccessor.getObject();
            final Object changed = changedAccessor.getObject();

            changedAccessor.defaultField();

            handle("equals", field, new Runnable() {
                @Override
                @SuppressFBWarnings(value = "RV_RETURN_VALUE_IGNORED", justification = "We only want to see if it throws an exception.")
                public void run() {
                    reference.equals(changed);
                }
            });

            handle("equals", field, new Runnable() {
                @Override
                @SuppressFBWarnings(value = "RV_RETURN_VALUE_IGNORED", justification = "We only want to see if it throws an exception.")
                public void run() {
                    changed.equals(reference);
                }
            });

            handle("hashCode", field, new Runnable() {
                @Override
                public void run() {
                    config.getCachedHashCodeInitializer().getInitializedHashCode(changed);
                }
            });

            referenceAccessor.defaultField();
        }

        private void handle(String testedMethodName, Field field, Runnable r) {
            try {
                r.run();
            }
            catch (NullPointerException e) {
                npeThrown(testedMethodName, field, e);
            }
            catch (Exception e) {
                exceptionThrown(testedMethodName, field, e);
            }
        }

        private void npeThrown(String method, Field field, Exception e) {
            fail(Formatter.of("Non-nullity: %% throws NullPointerException on field %%.", method, field.getName()), e);
        }

        private void exceptionThrown(String method, Field field, Exception e) {
            fail(Formatter.of("%% throws %% when field %% is null.", method, e.getClass().getSimpleName(), field.getName()), e);
        }
    }
}
