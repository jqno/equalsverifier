package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.junit.jupiter.api.Test;

public class ExperimentalInstantiatorTest {

    @Test
    void guava1() {
        Class<?> s1 = Multiset.class;
        ExperimentalInstantiator i = new ExperimentalInstantiator(getClass().getClassLoader());
        Class<?> s2 = i.lobotomize(s1);
        System.out.println(s1 + " ~ " + s1.getClassLoader());
        System.out.println(s2 + " ~ " + s2.getClassLoader());
        assertNotEquals(s1.getClassLoader(), s2.getClassLoader());
        assertNotEquals(s1, s2);
    }

    @Test
    void guava2() {
        Class<?> s1 = HashMultiset.class;
        ExperimentalInstantiator i = new ExperimentalInstantiator(getClass().getClassLoader());
        Class<?> s2 = i.lobotomize(s1);
        System.out.println(s1 + " ~ " + s1.getClassLoader());
        System.out.println(s2 + " ~ " + s2.getClassLoader());
        assertNotEquals(s1.getClassLoader(), s2.getClassLoader());
        assertNotEquals(s1, s2);
    }
}
