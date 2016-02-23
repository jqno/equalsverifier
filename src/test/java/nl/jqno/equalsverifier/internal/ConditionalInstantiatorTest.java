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
package nl.jqno.equalsverifier.internal;

import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.GregorianCalendar;

import static nl.jqno.equalsverifier.internal.Util.classes;
import static nl.jqno.equalsverifier.internal.Util.objects;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

public class ConditionalInstantiatorTest {
    private static final String THIS_TYPE_DOES_NOT_EXIST = "this.type.does.not.Exist";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ConditionalInstantiator ci;

    @Test
    public void resolveReturnsClass_whenTypeExists() {
        ci = new ConditionalInstantiator("java.util.GregorianCalendar");

        Class<?> actual = ci.resolve();
        assertEquals(actual, GregorianCalendar.class);
    }

    @Test
    public void resolveReturnsNull_whenTypeDoesntExist() {
        ci = new ConditionalInstantiator(THIS_TYPE_DOES_NOT_EXIST);

        Class<?> actual = ci.resolve();
        assertNull(actual);
    }

    @Test
    public void objectIsInstantiatedCorrectly_whenValidConstructorParametersAreProvided() {
        ci = new ConditionalInstantiator("java.util.GregorianCalendar");
        Object expected = new GregorianCalendar(1999, 11, 31);

        Object actual = ci.instantiate(classes(int.class, int.class, int.class), objects(1999, 11, 31));
        assertThat(actual, is(expected));
    }

    @Test
    public void nullIsReturned_whenInstantiateIsCalled_givenTypeDoesNotExist() {
        ci = new ConditionalInstantiator(THIS_TYPE_DOES_NOT_EXIST);
        Object actual = ci.instantiate(classes(String.class), objects("nope"));
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void throwsIse_whenInvalidConstructorParametersAreProvided() {
        ci = new ConditionalInstantiator("java.util.GregorianCalendar");

        thrown.expect(ReflectionException.class);
        ci.instantiate(classes(int.class, int.class, int.class), objects(1999, 31, "hello"));
    }

    @Test
    public void objectIsInstantiatedCorrectly_whenValidFactoryMethodAndParametersAreProvided() {
        ci = new ConditionalInstantiator("java.lang.Integer");
        Object expected = Integer.valueOf(42);

        Object actual = ci.callFactory("valueOf", classes(int.class), objects(42));
        assertThat(actual, is(expected));
    }

    @Test
    public void nullIsReturned_whenFactoryIsCalled_givenTypeDoesNotExist() {
        ci = new ConditionalInstantiator(THIS_TYPE_DOES_NOT_EXIST);
        Object actual = ci.callFactory("factory", classes(String.class), objects("nope"));
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void throwsIse_whenInvalidMethodNameIsProvided() {
        ci = new ConditionalInstantiator("java.lang.Integer");

        thrown.expect(ReflectionException.class);
        ci.callFactory("thisMethodDoesntExist", classes(int.class), objects(42));
    }

    @Test
    public void throwsIse_whenInvalidFactoryMethodParametersAreProvided() {
        ci = new ConditionalInstantiator("java.lang.Integer");

        thrown.expect(ReflectionException.class);
        ci.callFactory("valueOf", classes(int.class, int.class), objects(42));
    }

    @Test
    public void objectIsInstantiatedCorrectly_whenValidExternalFactoryMethodAndParametersAreProvided() {
        ci = new ConditionalInstantiator("java.util.List");
        Object expected = Collections.emptyList();

        Object actual = ci.callFactory("java.util.Collections", "emptyList", classes(), objects());
        assertThat(actual, is(expected));
    }

    @Test
    public void nullIsReturned_whenExternalFactoryIsCalled_givenTypeDoesNotExist() {
        ci = new ConditionalInstantiator(THIS_TYPE_DOES_NOT_EXIST);
        Object actual = ci.callFactory("java.util.Collections", "emptyList", classes(), objects());
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void nullIsReturned_whenExternalFactoryIsCalled_givenFactoryTypeDoesNotExist() {
        ci = new ConditionalInstantiator("java.util.List");

        thrown.expect(ReflectionException.class);
        ci.callFactory("java.util.ThisTypeDoesNotExist", "emptyList", classes(), objects());
    }

    @Test
    public void throwsIse_whenInvalidExternalFactoryMethodNameIsProvided() {
        ci = new ConditionalInstantiator("java.util.List");

        thrown.expect(ReflectionException.class);
        ci.callFactory("java.util.Collections", "thisMethodDoesntExist", classes(), objects());
    }

    @Test
    public void throwsIse_whenInvalidExternalFactoryMethodParametersAreProvided() {
        ci = new ConditionalInstantiator("java.util.List");

        thrown.expect(ReflectionException.class);
        ci.callFactory("java.util.Collections", "emptyList", classes(int.class), objects(42));
    }

    @Test
    public void nullIsReturned_whenReturnConstantIsCalled_givenTypeDoesNotExist() {
        ci = new ConditionalInstantiator(THIS_TYPE_DOES_NOT_EXIST);
        Object actual = ci.returnConstant("NOPE");
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void objectIsReturned_whenValidConstantIsProvided() {
        ci = new ConditionalInstantiator("java.math.BigDecimal");
        Object expected = BigDecimal.TEN;

        Object actual = ci.returnConstant("TEN");
        assertThat(actual, is(expected));
    }

    @Test
    public void throwsIse_whenConstantDoesNotExist() {
        ci = new ConditionalInstantiator("java.math.BigDecimal");

        thrown.expect(ReflectionException.class);
        ci.returnConstant("FORTY-TWO");
    }
}
