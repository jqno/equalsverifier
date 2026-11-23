package nl.jqno.equalsverifier.integration.valueobjects;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class ValueClassInheritanceTest {

    @Test
    void succeed_whenValueClassExtendsAbstractValueClass() {
        EqualsVerifier.forClass(Circle.class).verify();
    }

    @Test
    void succeed_whenValueClassExtendsAbstractValueClassWithFields() {
        EqualsVerifier.forClass(Dog.class).verify();
    }

    @Test
    void succeed_forEmailID() {
        EqualsVerifier.forClass(EmailID.class).verify();
    }

    @Test
    void succeed_forPhoneID() {
        EqualsVerifier.forClass(PhoneID.class).verify();
    }

    @Test
    void succeed_forUsernameID() {
        EqualsVerifier.forClass(UsernameID.class).verify();
    }

    @Test
    void succeed_whenValueClassImplementsInterface() {
        EqualsVerifier.forClass(Person.class).verify();
    }

    @Test
    void fail_whenValueClassInHierarchyIgnoresSuperclassField() {
        assertThatThrownBy(() -> EqualsVerifier.forClass(BrokenCat.class).verify())
                .isInstanceOf(AssertionError.class);
    }

    static abstract value class Shape {
        public abstract double area();
    }

    static value class Circle extends Shape {
        private final double radius;

        public Circle(double radius) {
            this.radius = radius;
        }

        @Override
        public double area() {
            return Math.PI * radius * radius;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Circle other && Double.compare(radius, other.radius) == 0;
        }

        @Override
        public int hashCode() {
            return Double.hashCode(radius);
        }
    }

    static abstract value class Animal {
        private final String species;

        public Animal(String species) {
            this.species = species;
        }

        public String species() {
            return species;
        }
    }

    static value class Dog extends Animal {
        private final String name;

        public Dog(String species, String name) {
            this.name = name;
            super(species);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Dog other
                    && Objects.equals(species(), other.species())
                    && Objects.equals(name, other.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(species(), name);
        }
    }

    sealed static abstract value class UserID permits EmailID, PhoneID, UsernameID {}

    static value class EmailID extends UserID {
        private final String name;
        private final String domain;

        public EmailID(String name, String domain) {
            this.name = name;
            this.domain = domain;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof EmailID other
                    && Objects.equals(name, other.name)
                    && Objects.equals(domain, other.domain);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, domain);
        }
    }

    static value class PhoneID extends UserID {
        private final String digits;

        public PhoneID(String digits) {
            this.digits = digits;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof PhoneID other && Objects.equals(digits, other.digits);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(digits);
        }
    }

    static value class UsernameID extends UserID {
        private final String name;

        public UsernameID(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof UsernameID other && Objects.equals(name, other.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }
    }

    interface Named {
        String name();
    }

    static value class Person implements Named {
        private final String name;

        public Person(String name) {
            this.name = name;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Person other && Objects.equals(name, other.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }
    }

    static value class BrokenCat extends Animal {
        private final String name;

        public BrokenCat(String species, String name) {
            this.name = name;
            super(species);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof BrokenCat other && Objects.equals(name, other.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }
    }
}
