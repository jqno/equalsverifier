/*
 * Copyright 2010 Jan Ouwens
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

import static nl.jqno.equalsverifier.util.Assert.assertTrue;

import java.util.List;

class PreconditionChecker<T> implements Checker {
	private final Class<T> type;
	private final List<T> equalExamples;
	private final List<T> unequalExamples;

	public PreconditionChecker(Class<T> type, List<T> equalExamples, List<T> unequalExamples) {
		this.type = type;
		this.equalExamples = equalExamples;
		this.unequalExamples = unequalExamples;
	}
	
	@Override
	public void check() {
		assertTrue("Precondition: no examples.", unequalExamples.size() > 0);
		for (T example : equalExamples) {
			assertTrue("Precondition:\n  " + equalExamples.get(0) + "\nand\n  " + example + "\nare of different classes",
					type.isAssignableFrom(example.getClass()));
		}
		for (T example : unequalExamples) {
			assertTrue("Precondition:\n  " + unequalExamples.get(0) + "\nand\n  " + example + "\nare of different classes",
					type.isAssignableFrom(example.getClass()));
		}
	}
}
