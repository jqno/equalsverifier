package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.reflection.Util.classes;
import static nl.jqno.equalsverifier.internal.reflection.Util.objects;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import org.junit.jupiter.api.Test;

class ConditionalInstantiatorTest {

    private static final String THIS_TYPE_DOES_NOT_EXIST = "this.type.does.not.Exist";

    private ConditionalInstantiator ci;

    @Test
    void resolveReturnsClass_whenTypeExists() {
        ci = new ConditionalInstantiator("java.util.GregorianCalendar");

        Class<?> actual = ci.resolve();
        assertThat(actual).isEqualTo(GregorianCalendar.class);
    }

    @Test
    void resolveReturnsNull_whenTypeDoesntExist() {
        ci = new ConditionalInstantiator(THIS_TYPE_DOES_NOT_EXIST);

        Class<?> actual = ci.resolve();
        assertThat(actual, is(nullValue()));
    }

    @Test
    void objectIsInstantiatedCorrectly_whenValidConstructorParametersAreProvided() {
        ci = new ConditionalInstantiator("java.util.GregorianCalendar");
        Calendar expected = new GregorianCalendar(1999, 11, 31);

        Calendar actual = ci.instantiate(classes(int.class, int.class, int.class), objects(1999, 11, 31));
        assertThat(actual, is(expected));
    }

    @Test
    void nullIsReturned_whenInstantiateIsCalled_givenTypeDoesNotExist() {
        ci = new ConditionalInstantiator(THIS_TYPE_DOES_NOT_EXIST);
        Object actual = ci.instantiate(classes(String.class), objects("nope"));
        assertThat(actual, is(nullValue()));
    }

    @Test
    void throwsIse_whenInvalidConstructorParametersAreProvided() {
        ci = new ConditionalInstantiator("java.util.GregorianCalendar");

        assertThatExceptionOfType(ReflectionException.class)
                .isThrownBy(() -> ci.instantiate(classes(int.class, int.class, int.class), objects(1999, 31, "hello")));
    }

    @Test
    void nullIsReturned_whenInvalidConstructorParametersAreProvided_givenFalse() {
        ci = new ConditionalInstantiator("java.util.GregorianCalendar", false);

        Object actual = ci.instantiate(classes(int.class, int.class, int.class), objects(1999, 31, "hello"));
        assertThat(actual, is(nullValue()));
    }

    @Test
    void objectIsInstantiatedCorrectly_whenValidFactoryMethodAndParametersAreProvided() {
        ci = new ConditionalInstantiator("java.lang.Integer");
        int expected = Integer.valueOf(42);

        int actual = ci.callFactory("valueOf", classes(int.class), objects(42));
        assertThat(actual, is(expected));
    }

    @Test
    void nullIsReturned_whenFactoryIsCalled_givenTypeDoesNotExist() {
        ci = new ConditionalInstantiator(THIS_TYPE_DOES_NOT_EXIST);
        Object actual = ci.callFactory("factory", classes(String.class), objects("nope"));
        assertThat(actual, is(nullValue()));
    }

    @Test
    void throwsIse_whenInvalidMethodNameIsProvided() {
        ci = new ConditionalInstantiator("java.lang.Integer");

        assertThatExceptionOfType(ReflectionException.class)
                .isThrownBy(() -> ci.callFactory("thisMethodDoesntExist", classes(int.class), objects(42)));
    }

    @Test
    void nullIsReturned_whenInvalidMethodNameIsProvided_givenFalse() {
        ci = new ConditionalInstantiator("java.lang.Integer", false);

        Object actual = ci.callFactory("thisMethodDoesntExist", classes(int.class), objects(42));
        assertThat(actual, is(nullValue()));
    }

    @Test
    void throwsIse_whenInvalidFactoryMethodParametersAreProvided() {
        ci = new ConditionalInstantiator("java.lang.Integer");

        assertThatExceptionOfType(ReflectionException.class)
                .isThrownBy(() -> ci.callFactory("valueOf", classes(int.class, int.class), objects(42)));
    }

    @Test
    void nullIsReturned_whenInvalidFactoryMethodParametersAreProvided_givenFalse() {
        ci = new ConditionalInstantiator("java.lang.Integer", false);

        Object actual = ci.callFactory("valueOf", classes(int.class, int.class), objects(42));
        assertThat(actual, is(nullValue()));
    }

    @Test
    void objectIsInstantiatedCorrectly_whenValidExternalFactoryMethodAndParametersAreProvided() {
        ci = new ConditionalInstantiator("java.util.List");
        List<?> expected = Collections.emptyList();

        List<?> actual = ci.callFactory("java.util.Collections", "emptyList", classes(), objects());
        assertThat(actual, is(expected));
    }

    @Test
    void nullIsReturned_whenExternalFactoryIsCalled_givenTypeDoesNotExist() {
        ci = new ConditionalInstantiator(THIS_TYPE_DOES_NOT_EXIST);
        Object actual = ci.callFactory("java.util.Collections", "emptyList", classes(), objects());
        assertThat(actual, is(nullValue()));
    }

    @Test
    void throwsIse_whenExternalFactoryIsCalled_givenFactoryTypeDoesNotExist() {
        ci = new ConditionalInstantiator("java.util.List");

        assertThatExceptionOfType(ReflectionException.class)
                .isThrownBy(() -> ci.callFactory("java.util.ThisTypeDoesNotExist", "emptyList", classes(), objects()));
    }

    @Test
    void nullIsReturned_whenExternalFactoryIsCalled_givenFactoryTypeDoesNotExist_givenFalse() {
        ci = new ConditionalInstantiator("java.util.List", false);

        Object actual = ci.callFactory("java.util.ThisTypeDoesNotExist", "emptyList", classes(), objects());
        assertThat(actual, is(nullValue()));
    }

    @Test
    void throwsIse_whenInvalidExternalFactoryMethodNameIsProvided() {
        ci = new ConditionalInstantiator("java.util.List");

        assertThatExceptionOfType(ReflectionException.class)
                .isThrownBy(
                    () -> ci.callFactory("java.util.Collections", "thisMethodDoesntExist", classes(), objects()));
    }

    @Test
    void nullIsReturned_whenInvalidExternalFactoryMethodNameIsProvided_givenFalse() {
        ci = new ConditionalInstantiator("java.util.List", false);

        Object actual = ci.callFactory("java.util.Collections", "thisMethodDoesntExist", classes(), objects());
        assertThat(actual, is(nullValue()));
    }

    @Test
    void throwsIse_whenInvalidExternalFactoryMethodParametersAreProvided() {
        ci = new ConditionalInstantiator("java.util.List");

        assertThatExceptionOfType(ReflectionException.class)
                .isThrownBy(
                    () -> ci.callFactory("java.util.Collections", "emptyList", classes(int.class), objects(42)));
    }

    @Test
    void nullIsReturned_whenInvalidExternalFactoryMethodParametersAreProvided_givenFalse() {
        ci = new ConditionalInstantiator("java.util.List", false);

        Object actual = ci.callFactory("java.util.Collections", "emptyList", classes(int.class), objects(42));
        assertThat(actual, is(nullValue()));
    }

    @Test
    void nullIsReturned_whenReturnConstantIsCalled_givenTypeDoesNotExist() {
        ci = new ConditionalInstantiator(THIS_TYPE_DOES_NOT_EXIST);
        Object actual = ci.returnConstant("NOPE");
        assertThat(actual, is(nullValue()));
    }

    @Test
    void objectIsReturned_whenValidConstantIsProvided() {
        ci = new ConditionalInstantiator("java.math.BigDecimal");
        BigDecimal expected = BigDecimal.TEN;

        BigDecimal actual = ci.returnConstant("TEN");
        assertThat(actual, is(expected));
    }

    @Test
    void throwsIse_whenConstantDoesNotExist() {
        ci = new ConditionalInstantiator("java.math.BigDecimal");

        assertThatExceptionOfType(ReflectionException.class).isThrownBy(() -> ci.returnConstant("FORTY-TWO"));
    }

    @Test
    void nullIsReturned_whenConstantDoesNotExist_givenFalse() {
        ci = new ConditionalInstantiator("java.math.BigDecimal", false);

        Object actual = ci.returnConstant("FORTY-TWO");
        assertThat(actual, is(nullValue()));
    }
}
