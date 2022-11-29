package nl.jqno.equalsverifier.integration.extra_features;

import jakarta.persistence.Basic;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.Objects;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

// CHECKSTYLE OFF: HiddenField

public class JakartaLazyEntityTest {

    @Test
    public void gettersAreUsed() {
        EqualsVerifier
            .forClass(CorrectJakartaLazyFieldContainer.class)
            .suppress(Warning.NONFINAL_FIELDS)
            .verify();
    }

    @Test
    public void basicGetterNotUsed_givenEagerLoading() {
        EqualsVerifier
            .forClass(IncorrectBasicJakartaEagerFieldContainer.class)
            .suppress(Warning.NONFINAL_FIELDS)
            .verify();
    }

    @Test
    public void basicGetterNotUsed_givenCorrespondingFieldIgnored() {
        EqualsVerifier
            .forClass(IncorrectBasicJakartaIgnoredLazyFieldContainer.class)
            .withIgnoredFields("basic")
            .suppress(Warning.NONFINAL_FIELDS)
            .verify();
    }

    @Test
    public void basicGetterUsed_givenAnnotationIsOnGetter() {
        getterNotUsed(CorrectBasicJakartaLazyGetterContainer.class, "equals");
    }

    @Test
    public void basicGetterNotUsedInHashCode() {
        getterNotUsed(IncorrectBasicJakartaLazyFieldContainerHashCode.class, "hashCode");
    }

    @Test
    public void basicGetterNotUsed() {
        getterNotUsed(IncorrectBasicJakartaLazyFieldContainer.class, "equals");
    }

    @Test
    public void oneToOneGetterNotUsed() {
        getterNotUsed(IncorrectOneToOneJakartaLazyFieldContainer.class, "equals");
    }

    @Test
    public void oneToManyGetterNotUsed() {
        getterNotUsed(IncorrectOneToManyJakartaLazyFieldContainer.class, "equals");
    }

    @Test
    public void manyToOneGetterNotUsed() {
        getterNotUsed(IncorrectManyToOneJakartaLazyFieldContainer.class, "equals");
    }

    @Test
    public void manyToManyGetterNotUsed() {
        getterNotUsed(IncorrectManyToManyJakartaLazyFieldContainer.class, "equals");
    }

    @Test
    public void elementCollectionGetterNotUsed() {
        getterNotUsed(IncorrectElementCollectionJakartaLazyFieldContainer.class, "equals");
    }

    private void getterNotUsed(Class<?> type, String method) {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(type).suppress(Warning.NONFINAL_FIELDS).verify())
            .assertFailure()
            .assertMessageContains("JPA Entity", method, "direct reference");
    }

    @Entity
    static class CorrectJakartaLazyFieldContainer {

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
            if (!(obj instanceof CorrectJakartaLazyFieldContainer)) {
                return false;
            }
            CorrectJakartaLazyFieldContainer other = (CorrectJakartaLazyFieldContainer) obj;
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
    static class IncorrectBasicJakartaEagerFieldContainer {

        @Basic
        private String basic;

        public String getBasic() {
            return basic;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IncorrectBasicJakartaEagerFieldContainer)) {
                return false;
            }
            IncorrectBasicJakartaEagerFieldContainer other = (IncorrectBasicJakartaEagerFieldContainer) obj;
            return Objects.equals(basic, other.basic);
        }

        @Override
        public int hashCode() {
            return Objects.hash(getBasic());
        }
    }

    @Entity
    static class IncorrectBasicJakartaIgnoredLazyFieldContainer {

        private String somethingElse;

        @Basic(fetch = FetchType.LAZY)
        private String basic;

        public String getBasic() {
            return basic;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IncorrectBasicJakartaIgnoredLazyFieldContainer)) {
                return false;
            }
            IncorrectBasicJakartaIgnoredLazyFieldContainer other = (IncorrectBasicJakartaIgnoredLazyFieldContainer) obj;
            return Objects.equals(somethingElse, other.somethingElse);
        }

        @Override
        public int hashCode() {
            return Objects.hash(somethingElse);
        }
    }

    @Entity
    static class CorrectBasicJakartaLazyGetterContainer {

        private String basic;

        @Basic(fetch = FetchType.LAZY)
        public String getBasic() {
            return basic;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CorrectBasicJakartaLazyGetterContainer)) {
                return false;
            }
            CorrectBasicJakartaLazyGetterContainer other = (CorrectBasicJakartaLazyGetterContainer) obj;
            return Objects.equals(basic, other.basic);
        }

        @Override
        public int hashCode() {
            return Objects.hash(getBasic());
        }
    }

    @Entity
    static class IncorrectBasicJakartaLazyFieldContainerHashCode {

        @Basic(fetch = FetchType.LAZY)
        private String basic;

        public String getBasic() {
            return basic;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IncorrectBasicJakartaLazyFieldContainerHashCode)) {
                return false;
            }
            IncorrectBasicJakartaLazyFieldContainerHashCode other = (IncorrectBasicJakartaLazyFieldContainerHashCode) obj;
            return Objects.equals(getBasic(), other.getBasic());
        }

        @Override
        public int hashCode() {
            return Objects.hash(basic);
        }
    }

    @Entity
    static class IncorrectBasicJakartaLazyFieldContainer {

        @Basic(fetch = FetchType.LAZY)
        private String basic;

        public String getBasic() {
            return basic;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IncorrectBasicJakartaLazyFieldContainer)) {
                return false;
            }
            IncorrectBasicJakartaLazyFieldContainer other = (IncorrectBasicJakartaLazyFieldContainer) obj;
            return Objects.equals(basic, other.basic);
        }

        @Override
        public int hashCode() {
            return Objects.hash(getBasic());
        }
    }

    @Entity
    static class IncorrectOneToOneJakartaLazyFieldContainer {

        @OneToOne(fetch = FetchType.LAZY)
        private String oneToOne;

        public String getOneToOne() {
            return oneToOne;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IncorrectOneToOneJakartaLazyFieldContainer)) {
                return false;
            }
            IncorrectOneToOneJakartaLazyFieldContainer other = (IncorrectOneToOneJakartaLazyFieldContainer) obj;
            return Objects.equals(oneToOne, other.oneToOne);
        }

        @Override
        public int hashCode() {
            return Objects.hash(getOneToOne());
        }
    }

    @Entity
    static class IncorrectOneToManyJakartaLazyFieldContainer {

        @OneToMany(fetch = FetchType.LAZY)
        private String oneToMany;

        public String getOneToMany() {
            return oneToMany;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IncorrectOneToManyJakartaLazyFieldContainer)) {
                return false;
            }
            IncorrectOneToManyJakartaLazyFieldContainer other = (IncorrectOneToManyJakartaLazyFieldContainer) obj;
            return Objects.equals(oneToMany, other.oneToMany);
        }

        @Override
        public int hashCode() {
            return Objects.hash(getOneToMany());
        }
    }

    @Entity
    static class IncorrectManyToOneJakartaLazyFieldContainer {

        @ManyToOne(fetch = FetchType.LAZY)
        private String manyToOne;

        public String getManyToOne() {
            return manyToOne;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IncorrectManyToOneJakartaLazyFieldContainer)) {
                return false;
            }
            IncorrectManyToOneJakartaLazyFieldContainer other = (IncorrectManyToOneJakartaLazyFieldContainer) obj;
            return Objects.equals(manyToOne, other.manyToOne);
        }

        @Override
        public int hashCode() {
            return Objects.hash(getManyToOne());
        }
    }

    @Entity
    static class IncorrectManyToManyJakartaLazyFieldContainer {

        @ManyToMany(fetch = FetchType.LAZY)
        private String manyToMany;

        public String getManyToMany() {
            return manyToMany;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IncorrectManyToManyJakartaLazyFieldContainer)) {
                return false;
            }
            IncorrectManyToManyJakartaLazyFieldContainer other = (IncorrectManyToManyJakartaLazyFieldContainer) obj;
            return Objects.equals(manyToMany, other.manyToMany);
        }

        @Override
        public int hashCode() {
            return Objects.hash(getManyToMany());
        }
    }

    @Entity
    static class IncorrectElementCollectionJakartaLazyFieldContainer {

        @ElementCollection(fetch = FetchType.LAZY)
        private String elementCollection;

        public String getElementCollection() {
            return elementCollection;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IncorrectElementCollectionJakartaLazyFieldContainer)) {
                return false;
            }
            IncorrectElementCollectionJakartaLazyFieldContainer other = (IncorrectElementCollectionJakartaLazyFieldContainer) obj;
            return Objects.equals(elementCollection, other.elementCollection);
        }

        @Override
        public int hashCode() {
            return Objects.hash(getElementCollection());
        }
    }
}
