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
package nl.jqno.equalsverifier.internal.prefabvalues;

/**
 * Container for two values of the same type: a "red" one and a "black" one.
 */
public final class Tuple<T> {
    private final T red;
    private final T black;

    /**
     * Constructor.
     *
     * @param red The red value.
     * @param black The black value.
     */
    public Tuple(T red, T black) {
        this.red = red;
        this.black = black;
    }

    /**
     * Factory method that turns two untyped values into a typed tuple.
     *
     * @param red The red value.
     * @param black The black value.
     * @param <U> The assumed type of the values.
     */
    @SuppressWarnings("unchecked")
    public static <U> Tuple<U> of(Object red, Object black) {
        return new Tuple<>((U)red, (U)black);
    }

    /**
     * Returns the red value.
     */
    public T getRed() {
        return red;
    }

    /**
     * Returns the black value.
     */
    public T getBlack() {
        return black;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tuple)) {
            return false;
        }
        Tuple<?> other = (Tuple<?>)obj;
        return red.equals(other.red) && black.equals(other.black);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 37;
        result = (59 * result) + red.hashCode();
        result = (59 * result) + black.hashCode();
        return result;
    }
}
