package nl.jqno.equalsverifier.integration.extra_features;

import java.util.Objects;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import nl.jqno.equalsverifier.testhelpers.annotations.javax.persistence.Basic;
import nl.jqno.equalsverifier.testhelpers.annotations.javax.persistence.ElementCollection;
import nl.jqno.equalsverifier.testhelpers.annotations.javax.persistence.Entity;
import nl.jqno.equalsverifier.testhelpers.annotations.javax.persistence.FetchType;
import nl.jqno.equalsverifier.testhelpers.annotations.javax.persistence.ManyToMany;
import nl.jqno.equalsverifier.testhelpers.annotations.javax.persistence.ManyToOne;
import nl.jqno.equalsverifier.testhelpers.annotations.javax.persistence.OneToMany;
import nl.jqno.equalsverifier.testhelpers.annotations.javax.persistence.OneToOne;
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
    public void basicGetterNotUsed_givenEagerLoading() {
        EqualsVerifier
            .forClass(IncorrectBasicJpaEagerFieldContainer.class)
            .suppress(Warning.NONFINAL_FIELDS)
            .verify();
    }

    @Test
    public void basicGetterNotUsedInHashCode() {
        getterNotUsed(IncorrectBasicJpaLazyFieldContainerHashCode.class, "hashCode");
    }

    @Test
    public void basicGetterNotUsed() {
        getterNotUsed(IncorrectBasicJpaLazyFieldContainer.class, "equals");
    }

    @Test
    public void oneToOneGetterNotUsed() {
        getterNotUsed(IncorrectOneToOneJpaLazyFieldContainer.class, "equals");
    }

    @Test
    public void oneToManyGetterNotUsed() {
        getterNotUsed(IncorrectOneToManyJpaLazyFieldContainer.class, "equals");
    }

    @Test
    public void manyToOneGetterNotUsed() {
        getterNotUsed(IncorrectManyToOneJpaLazyFieldContainer.class, "equals");
    }

    @Test
    public void manyToManyGetterNotUsed() {
        getterNotUsed(IncorrectManyToManyJpaLazyFieldContainer.class, "equals");
    }

    @Test
    public void elementCollectionGetterNotUsed() {
        getterNotUsed(IncorrectElementCollectionJpaLazyFieldContainer.class, "equals");
    }

    private void getterNotUsed(Class<?> type, String method) {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(type).suppress(Warning.NONFINAL_FIELDS).verify())
            .assertFailure()
            .assertMessageContains("JPA Entity", method, "direct reference");
    }

    @Entity
    static class CorrectJpaLazyFieldContainer {

        @Basic(fetch = FetchType.LAZY)
        private String basic;

        @OneToOne(fetch = FetchType.LAZY)
        private String oneToOne;

        @OneToMany(fetch = FetchType.LAZY)
        private String oneToMany;

        @ManyToOne(fetch = FetchType.LAZY)
        private String manyToOne;

        @ManyToMany(fetch = FetchType.LAZY)
        private String manyToMany;

        @ElementCollection(fetch = FetchType.LAZY)
        private String elementCollection;

        public String getBasic() {
            return basic;
        }

        public String getOneToOne() {
            return oneToOne;
        }

        public String getOneToMany() {
            return oneToMany;
        }

        public String getManyToOne() {
            return manyToOne;
        }

        public String getManyToMany() {
            return manyToMany;
        }

        public String getElementCollection() {
            return elementCollection;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CorrectJpaLazyFieldContainer)) {
                return false;
            }
            CorrectJpaLazyFieldContainer other = (CorrectJpaLazyFieldContainer) obj;
            return (
                Objects.equals(getBasic(), other.getBasic()) &&
                Objects.equals(getOneToOne(), other.getOneToOne()) &&
                Objects.equals(getOneToMany(), other.getOneToMany()) &&
                Objects.equals(getManyToOne(), other.getManyToOne()) &&
                Objects.equals(getManyToMany(), other.getManyToMany()) &&
                Objects.equals(getElementCollection(), other.getElementCollection())
            );
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                getBasic(),
                getOneToOne(),
                getOneToMany(),
                getManyToOne(),
                getManyToMany(),
                getElementCollection()
            );
        }
    }

    @Entity
    static class IncorrectBasicJpaEagerFieldContainer {

        @Basic
        private String basic;

        public String getBasic() {
            return basic;
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

    @Entity
    static class IncorrectBasicJpaLazyFieldContainerHashCode {

        @Basic(fetch = FetchType.LAZY)
        private String basic;

        public String getBasic() {
            return basic;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IncorrectBasicJpaLazyFieldContainerHashCode)) {
                return false;
            }
            IncorrectBasicJpaLazyFieldContainerHashCode other = (IncorrectBasicJpaLazyFieldContainerHashCode) obj;
            return Objects.equals(getBasic(), other.getBasic());
        }

        @Override
        public int hashCode() {
            return Objects.hash(basic);
        }
    }

    @Entity
    static class IncorrectBasicJpaLazyFieldContainer {

        @Basic(fetch = FetchType.LAZY)
        private String basic;

        public String getBasic() {
            return basic;
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
    static class IncorrectOneToOneJpaLazyFieldContainer {

        @OneToOne(fetch = FetchType.LAZY)
        private String oneToOne;

        public String getOneToOne() {
            return oneToOne;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IncorrectOneToOneJpaLazyFieldContainer)) {
                return false;
            }
            IncorrectOneToOneJpaLazyFieldContainer other = (IncorrectOneToOneJpaLazyFieldContainer) obj;
            return Objects.equals(oneToOne, other.oneToOne);
        }

        @Override
        public int hashCode() {
            return Objects.hash(getOneToOne());
        }
    }

    @Entity
    static class IncorrectOneToManyJpaLazyFieldContainer {

        @OneToMany(fetch = FetchType.LAZY)
        private String oneToMany;

        public String getOneToMany() {
            return oneToMany;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IncorrectOneToManyJpaLazyFieldContainer)) {
                return false;
            }
            IncorrectOneToManyJpaLazyFieldContainer other = (IncorrectOneToManyJpaLazyFieldContainer) obj;
            return Objects.equals(oneToMany, other.oneToMany);
        }

        @Override
        public int hashCode() {
            return Objects.hash(getOneToMany());
        }
    }

    @Entity
    static class IncorrectManyToOneJpaLazyFieldContainer {

        @ManyToOne(fetch = FetchType.LAZY)
        private String manyToOne;

        public String getManyToOne() {
            return manyToOne;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IncorrectManyToOneJpaLazyFieldContainer)) {
                return false;
            }
            IncorrectManyToOneJpaLazyFieldContainer other = (IncorrectManyToOneJpaLazyFieldContainer) obj;
            return Objects.equals(manyToOne, other.manyToOne);
        }

        @Override
        public int hashCode() {
            return Objects.hash(getManyToOne());
        }
    }

    @Entity
    static class IncorrectManyToManyJpaLazyFieldContainer {

        @ManyToMany(fetch = FetchType.LAZY)
        private String manyToMany;

        public String getManyToMany() {
            return manyToMany;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IncorrectManyToManyJpaLazyFieldContainer)) {
                return false;
            }
            IncorrectManyToManyJpaLazyFieldContainer other = (IncorrectManyToManyJpaLazyFieldContainer) obj;
            return Objects.equals(manyToMany, other.manyToMany);
        }

        @Override
        public int hashCode() {
            return Objects.hash(getManyToMany());
        }
    }

    @Entity
    static class IncorrectElementCollectionJpaLazyFieldContainer {

        @ElementCollection(fetch = FetchType.LAZY)
        private String elementCollection;

        public String getElementCollection() {
            return elementCollection;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IncorrectElementCollectionJpaLazyFieldContainer)) {
                return false;
            }
            IncorrectElementCollectionJpaLazyFieldContainer other = (IncorrectElementCollectionJpaLazyFieldContainer) obj;
            return Objects.equals(elementCollection, other.elementCollection);
        }

        @Override
        public int hashCode() {
            return Objects.hash(getElementCollection());
        }
    }
}
