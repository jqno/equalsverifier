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
package nl.jqno.equalsverifier.testhelpers.annotations.edu.umd.cs.findbugs.annotations;

import java.lang.annotation.*;

/**
 * This annotation serves as a paceholder for the real
 * {@link edu.umd.cs.findbugs.annotations.DefaultAnnotation}, which is
 * deprecated. We use this annotation to avoid warnings in places where we can't
 * suppress the warning.
 */
@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.CLASS)
public @interface DefaultAnnotation {
    Class<? extends Annotation>[] value();
}
