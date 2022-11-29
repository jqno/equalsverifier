package nl.jqno.equalsverifier.integration.extra_features;

import jakarta.persistence.Entity;
import java.util.Objects;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import nl.jqno.equalsverifier.testhelpers.annotations.javax.persistence.Basic;
import nl.jqno.equalsverifier.testhelpers.annotations.javax.persistence.FetchType;
import org.junit.jupiter.api.Test;

// CHECKSTYLE OFF: HiddenField

public class JpaLazyEntityTest {

    @Test
    public void gettersAreUsed() {
        EqualsVerifier
            .forClass(CorrectJpaLazyFieldContainer.class)
            .suppress(Warning.NONFINAL_FIELDS)
            .verify();
    }

    @Test
    public void basicGetterNotUsed() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(IncorrectBasicJpaLazyFieldContainer.class)
                    .suppress(Warning.NONFINAL_FIELDS)
                    .verify()
            )
            .assertFailure()
            .assertMessageContains("JPA Entity", "direct reference");
    }

    @Test
    public void basicGetterNotUsed_givenEagerLoading() {
        EqualsVerifier
            .forClass(IncorrectBasicJpaEagerFieldContainer.class)
            .suppress(Warning.NONFINAL_FIELDS)
            .verify();
    }

    @Entity
    static class CorrectJpaLazyFieldContainer {

        @Basic(fetch = FetchType.LAZY)
        private String basic;

        public CorrectJpaLazyFieldContainer(String basic) {
            this.basic = basic;
        }

        public String getBasic() {
            return basic;
        }

        public void setBasic(String basic) {
            this.basic = basic;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CorrectJpaLazyFieldContainer)) {
                return false;
            }
            CorrectJpaLazyFieldContainer other = (CorrectJpaLazyFieldContainer) obj;
            return Objects.equals(getBasic(), other.getBasic());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getBasic());
        }
    }

    @Entity
    static class IncorrectBasicJpaLazyFieldContainer {

        @Basic(fetch = FetchType.LAZY)
        private String basic;

        public IncorrectBasicJpaLazyFieldContainer(String basic) {
            this.basic = basic;
        }

        public String getBasic() {
            return basic;
        }

        public void setBasic(String basic) {
            this.basic = basic;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IncorrectBasicJpaLazyFieldContainer)) {
                return false;
            }
            IncorrectBasicJpaLazyFieldContainer other = (IncorrectBasicJpaLazyFieldContainer) obj;
            return Objects.equals(basic, other.basic);
        }

        @Override
        public int hashCode() {
            return Objects.hash(getBasic());
        }
    }

    @Entity
    static class IncorrectBasicJpaEagerFieldContainer {

        @Basic
        private String basic;

        public IncorrectBasicJpaEagerFieldContainer(String basic) {
            this.basic = basic;
        }

        public String getBasic() {
            return basic;
        }

        public void setBasic(String basic) {
            this.basic = basic;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IncorrectBasicJpaEagerFieldContainer)) {
                return false;
            }
            IncorrectBasicJpaEagerFieldContainer other = (IncorrectBasicJpaEagerFieldContainer) obj;
            return Objects.equals(basic, other.basic);
        }

        @Override
        public int hashCode() {
            return Objects.hash(getBasic());
        }
    }
}
