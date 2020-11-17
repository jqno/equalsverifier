package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.reflection.Util.classes;
import static nl.jqno.equalsverifier.internal.reflection.Util.objects;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import org.junit.jupiter.api.Test;

public class ConditionalInstantiatorTest {
    private static final String THIS_TYPE_DOES_NOT_EXIST = "this.type.does.not.Exist";

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
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void objectIsInstantiatedCorrectly_whenValidConstructorParametersAreProvided() {
        ci = new ConditionalInstantiator("java.util.GregorianCalendar");
        Calendar expected = new GregorianCalendar(1999, 11, 31);

        Calendar actual =
                ci.instantiate(classes(int.class, int.class, int.class), objects(1999, 11, 31));
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

        assertThrows(
                ReflectionException.class,
                () ->
                        ci.instantiate(
                                classes(int.class, int.class, int.class),
                                objects(1999, 31, "hello")));
    }

    @Test
    public void nullIsReturned_whenInvalidConstructorParametersAreProvided_givenFalse() {
        ci = new ConditionalInstantiator("java.util.GregorianCalendar", false);

        Object actual =
                ci.instantiate(
                        classes(int.class, int.class, int.class), objects(1999, 31, "hello"));
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void objectIsInstantiatedCorrectly_whenValidFactoryMethodAndParametersAreProvided() {
        ci = new ConditionalInstantiator("java.lang.Integer");
        int expected = Integer.valueOf(42);

        int actual = ci.callFactory("valueOf", classes(int.class), objects(42));
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

        assertThrows(
                ReflectionException.class,
                () -> ci.callFactory("thisMethodDoesntExist", classes(int.class), objects(42)));
    }

    @Test
    public void nullIsReturned_whenInvalidMethodNameIsProvided_givenFalse() {
        ci = new ConditionalInstantiator("java.lang.Integer", false);

        Object actual = ci.callFactory("thisMethodDoesntExist", classes(int.class), objects(42));
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void throwsIse_whenInvalidFactoryMethodParametersAreProvided() {
        ci = new ConditionalInstantiator("java.lang.Integer");

        assertThrows(
                ReflectionException.class,
                () -> ci.callFactory("valueOf", classes(int.class, int.class), objects(42)));
    }

    @Test
    public void nullIsReturned_whenInvalidFactoryMethodParametersAreProvided_givenFalse() {
        ci = new ConditionalInstantiator("java.lang.Integer", false);

        Object actual = ci.callFactory("valueOf", classes(int.class, int.class), objects(42));
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void
            objectIsInstantiatedCorrectly_whenValidExternalFactoryMethodAndParametersAreProvided() {
        ci = new ConditionalInstantiator("java.util.List");
        List<?> expected = Collections.emptyList();

        List<?> actual = ci.callFactory("java.util.Collections", "emptyList", classes(), objects());
        assertThat(actual, is(expected));
    }

    @Test
    public void nullIsReturned_whenExternalFactoryIsCalled_givenTypeDoesNotExist() {
        ci = new ConditionalInstantiator(THIS_TYPE_DOES_NOT_EXIST);
        Object actual = ci.callFactory("java.util.Collections", "emptyList", classes(), objects());
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void throwsIse_whenExternalFactoryIsCalled_givenFactoryTypeDoesNotExist() {
        ci = new ConditionalInstantiator("java.util.List");

        assertThrows(
                ReflectionException.class,
                () ->
                        ci.callFactory(
                                "java.util.ThisTypeDoesNotExist",
                                "emptyList",
                                classes(),
                                objects()));
    }

    @Test
    public void
            nullIsReturned_whenExternalFactoryIsCalled_givenFactoryTypeDoesNotExist_givenFalse() {
        ci = new ConditionalInstantiator("java.util.List", false);

        Object actual =
                ci.callFactory("java.util.ThisTypeDoesNotExist", "emptyList", classes(), objects());
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void throwsIse_whenInvalidExternalFactoryMethodNameIsProvided() {
        ci = new ConditionalInstantiator("java.util.List");

        assertThrows(
                ReflectionException.class,
                () ->
                        ci.callFactory(
                                "java.util.Collections",
                                "thisMethodDoesntExist",
                                classes(),
                                objects()));
    }

    @Test
    public void nullIsReturned_whenInvalidExternalFactoryMethodNameIsProvided_givenFalse() {
        ci = new ConditionalInstantiator("java.util.List", false);

        Object actual =
                ci.callFactory(
                        "java.util.Collections", "thisMethodDoesntExist", classes(), objects());
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void throwsIse_whenInvalidExternalFactoryMethodParametersAreProvided() {
        ci = new ConditionalInstantiator("java.util.List");

        assertThrows(
                ReflectionException.class,
                () ->
                        ci.callFactory(
                                "java.util.Collections",
                                "emptyList",
                                classes(int.class),
                                objects(42)));
    }

    @Test
    public void nullIsReturned_whenInvalidExternalFactoryMethodParametersAreProvided_givenFalse() {
        ci = new ConditionalInstantiator("java.util.List", false);

        Object actual =
                ci.callFactory(
                        "java.util.Collections", "emptyList", classes(int.class), objects(42));
        assertThat(actual, is(nullValue()));
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
        BigDecimal expected = BigDecimal.TEN;

        BigDecimal actual = ci.returnConstant("TEN");
        assertThat(actual, is(expected));
    }

    @Test
    public void throwsIse_whenConstantDoesNotExist() {
        ci = new ConditionalInstantiator("java.math.BigDecimal");

        assertThrows(ReflectionException.class, () -> ci.returnConstant("FORTY-TWO"));
    }

    @Test
    public void nullIsReturned_whenConstantDoesNotExist_givenFalse() {
        ci = new ConditionalInstantiator("java.math.BigDecimal", false);

        Object actual = ci.returnConstant("FORTY-TWO");
        assertThat(actual, is(nullValue()));
    }
}
