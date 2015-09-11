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
package nl.jqno.equalsverifier.testhelpers.annotations.org.eclipse.jdt.annotation;

import java.lang.annotation.*;

/**
 * This annotation serves as a placeholder for the real
 * {@link org.eclipse.jdt.annotation.NonNullByDefault} annotation. However,
 * since that annotation is compiled for Java 8, and this code base must support
 * Java 6, we use this copy instead.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.PACKAGE, ElementType.TYPE })
public @interface NonNullByDefault {
}