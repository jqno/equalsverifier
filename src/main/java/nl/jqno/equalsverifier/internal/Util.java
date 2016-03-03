/*
 * Copyright 2016 Jan Ouwens
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
package nl.jqno.equalsverifier.internal;

public final class Util {
    private Util() {
        // Do not instantiate
    }

    /**
     * Helper method to resolve a Class of a given name.
     *
     * @param className The fully qualified name of the class to resolve.
     * @return The corresponding class if it exists, null otherwise.
     */
    public static Class<?> classForName(String className) {
        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Helper method to create an array of Classes.
     *
     * @param classes The classes to construct an array out of.
     * @return An array with the given classes.
     */
    public static Class<?>[] classes(Class<?>... classes) {
        return classes;
    }

    /**
     * Helper method to create an array of Objects.
     *
     * @param objects The objects to construct an array out of.
     * @return An array with the given objects.
     */
    public static Object[] objects(Object... objects) {
        return objects;
    }
}
