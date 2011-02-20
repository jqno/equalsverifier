/*
 * Copyright 2010-2011 Jan Ouwens
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

import static nl.jqno.equalsverifier.util.Assert.fail;

import java.lang.reflect.Field;
import java.util.EnumSet;

import nl.jqno.equalsverifier.FieldInspector.FieldCheck;
import nl.jqno.equalsverifier.util.ClassAccessor;
import nl.jqno.equalsverifier.util.FieldAccessor;
import nl.jqno.equalsverifier.util.SupportedAnnotations;

class NullChecker<T> implements Checker {
	private ClassAccessor<T> classAccessor;
	private EnumSet<Warning> warningsToSuppress;

	public NullChecker(ClassAccessor<T> classAccessor, EnumSet<Warning> warningsToSuppress) {
		this.classAccessor = classAccessor;
		this.warningsToSuppress = EnumSet.copyOf(warningsToSuppress);
	}

	@Override
	public void check() {
		if (warningsToSuppress.contains(Warning.NULL_FIELDS)) {
			return;
		}
		
		FieldInspector<T> inspector = new FieldInspector<T>(classAccessor);
		inspector.check(new NullPointerExceptionFieldCheck());
	}
	
	private class NullPointerExceptionFieldCheck implements FieldCheck {
		@Override
		public void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
			Field field = referenceAccessor.getField();
			if (field.getType().isPrimitive()) {
				return;
			}
			if (classAccessor.fieldHasAnnotation(field, SupportedAnnotations.NONNULL)) {
				return;
			}
			final Object reference = referenceAccessor.getObject();
			final Object changed = changedAccessor.getObject();
			
			changedAccessor.nullField();
			
			handle(field, "equals", new Runnable() {
				@Override
				public void run() {
					reference.equals(changed);
				}
			});
			
			handle(field, "equals", new Runnable() {
				@Override
				public void run() {
					changed.equals(reference);
				}
			});
			
			handle(field, "hashCode", new Runnable() {
				@Override
				public void run() {
					changed.hashCode();
				}
			});
			
			handle(field, "toString", new Runnable() {
				@Override
				public void run() {
					changed.toString();
				}
			});
			
			referenceAccessor.nullField();
		}
		
		private void handle(Field field, String testedMethodName, Runnable r) {
			try {
				r.run();
			}
			catch (NullPointerException e) {
				npeThrown(testedMethodName);
			}
			catch (Exception e) {
				exceptionThrown(testedMethodName, field, e);
			}
		}

		private void npeThrown(String method) {
			fail("Non-nullity: " + method + " throws NullPointerException.");
		}
		
		private void exceptionThrown(String method, Field field, Exception e) {
			fail(method + " throws " + e.getClass().getSimpleName() + " when field " + field.getName() + " is null.");
		}
	}
}
