/*
 * Copyright 2018 Jan Ouwens
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
package nl.jqno.equalsverifier.testhelpers.annotations.javax.annotation;

import javax.annotation.meta.TypeQualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The Java Platform Module System in Java 9 makes it hard access the
 * javax.annotation.* annotations, because they live in a dependency
 * but end up inside a split package through the java.xml.ws.annotation
 * module. The latter is slated to be removed, but it hasn't happened
 * yet in Java 9.
 *
 * Adding javax.annotation.Nonnull to a class works fine, but accessing
 * it with reflection causes NoClassDefFoundErrors.
 *
 * This annotation can be used in tests to replace the regular
 * javax.annotation.Nonnull.
 *
 * For more info, see https://blog.codefx.org/java/jsr-305-java-9/
 */
@TypeQualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface Nonnull {
}
