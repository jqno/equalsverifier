package nl.jqno.equalsverifier.integration.extra_features;

import jakarta.persistence.*;
import java.util.Arrays;
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
            .forClass(CorrectBasicJakartaEagerFieldContainer.class)
            .suppress(Warning.NONFINAL_FIELDS)
            .verify();
    }

    @Test
    public void basicGetterNotUsed_givenCorrespondingFieldIgnored() {
        EqualsVerifier
            .forClass(CorrectBasicJakartaIgnoredLazyFieldContainer.class)
            .withIgnoredFields("basic")
            .suppress(Warning.NONFINAL_FIELDS)
            .verify();
    }

    @Test
    public void basicGetterNotUsed_givenWarningSuppressed() {
        EqualsVerifier
            .forClass(CorrectBasicJakartaIgnoredLazyFieldContainer.class)
            .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
            .verify();
    }

    @Test
    public void basicGetterNotUsed_givenAnnotationIsOnGetter() {
        getterNotUsed(IncorrectBasicJakartaLazyGetterContainer.class, "equals");
        getterNotUsed_warningSuppressed(IncorrectBasicJakartaLazyGetterContainer.class);
    }

    @Test
    public void basicGetterNotUsedInHashCode() {
        getterNotUsed(IncorrectBasicJakartaLazyFieldContainerHashCode.class, "hashCode");
        getterNotUsed_warningSuppressed(IncorrectBasicJakartaLazyFieldContainerHashCode.class);
    }

    @Test
    public void basicGetterNotUsed() {
        getterNotUsed(IncorrectBasicJakartaLazyFieldContainer.class, "equals");
        getterNotUsed_warningSuppressed(IncorrectBasicJakartaLazyFieldContainer.class);
    }

    @Test
    public void oneToOneGetterNotUsed() {
        getterNotUsed(IncorrectOneToOneJakartaLazyFieldContainer.class, "equals");
        getterNotUsed_warningSuppressed(IncorrectOneToOneJakartaLazyFieldContainer.class);
    }

    @Test
    public void oneToManyGetterNotUsed() {
        getterNotUsed(IncorrectOneToManyJakartaLazyFieldContainer.class, "equals");
        getterNotUsed_warningSuppressed(IncorrectOneToManyJakartaLazyFieldContainer.class);
    }

    @Test
    public void manyToOneGetterNotUsed() {
        getterNotUsed(IncorrectManyToOneJakartaLazyFieldContainer.class, "equals");
        getterNotUsed_warningSuppressed(IncorrectManyToOneJakartaLazyFieldContainer.class);
    }

    @Test
    public void manyToManyGetterNotUsed() {
        getterNotUsed(IncorrectManyToManyJakartaLazyFieldContainer.class, "equals");
        getterNotUsed_warningSuppressed(IncorrectManyToManyJakartaLazyFieldContainer.class);
    }

    @Test
    public void elementCollectionGetterNotUsed() {
        getterNotUsed(IncorrectElementCollectionJakartaLazyFieldContainer.class, "equals");
        getterNotUsed_warningSuppressed(IncorrectElementCollectionJakartaLazyFieldContainer.class);
    }

    @Test
    public void lazyGettersPickedUpInSuper() {
        EqualsVerifier.forClass(LazyGetterContainer.class).usingGetClass().verify();
        EqualsVerifier.forClass(ChildOfLazyGetterContainer.class).usingGetClass().verify();
    }

    @Test
    public void constantHashCode_givenStrictHashCodeSuppressed() {
        EqualsVerifier
            .forClass(ConstantHashCodeContainer.class)
            .suppress(Warning.STRICT_HASHCODE)
            .verify();
    }

    @Test
    public void differentCodingStyle_single() {
        EqualsVerifier
            .forClass(DifferentCodingStyleContainer.class)
            .suppress(Warning.NONFINAL_FIELDS)
            .withFieldnameToGetterConverter(fn ->
                "get" + Character.toUpperCase(fn.charAt(2)) + fn.substring(3)
            )
            .verify();
    }

    @Test
    public void differentCodingStyle_configured() {
        EqualsVerifier
            .configure()
            .suppress(Warning.NONFINAL_FIELDS)
            .withFieldnameToGetterConverter(fn ->
                "get" + Character.toUpperCase(fn.charAt(2)) + fn.substring(3)
            )
            .forClass(DifferentCodingStyleContainer.class)
            .verify();
    }

    @Test
    public void differentCodingStyle_multiple() {
        EqualsVerifier
            .forClasses(Arrays.asList(DifferentCodingStyleContainer.class))
            .suppress(Warning.NONFINAL_FIELDS)
            .withFieldnameToGetterConverter(fn ->
                "get" + Character.toUpperCase(fn.charAt(2)) + fn.substring(3)
            )
            .verify();
    }

    @Test
    public void getterUsedForGeneratedId() {
        EqualsVerifier
            .forClass(CorrectGeneratedJakartaIdContainer.class)
            .suppress(Warning.SURROGATE_KEY)
            .verify();
        EqualsVerifier
            .forClass(CorrectGeneratedJakartaIdContainer.class)
            .suppress(Warning.SURROGATE_OR_BUSINESS_KEY)
            .verify();
    }

    @Test
    public void getterNotUsedForGeneratedId() {
        getterNotUsed(IncorrectGeneratedJakartaIdContainer.class, "equals", Warning.SURROGATE_KEY);
        getterNotUsed_warningSuppressed(
            IncorrectGeneratedJakartaIdContainer.class,
            Warning.SURROGATE_KEY
        );
        getterNotUsed(
            IncorrectGeneratedJakartaIdContainer.class,
            "equals",
            Warning.SURROGATE_OR_BUSINESS_KEY
        );
        getterNotUsed_warningSuppressed(
            IncorrectGeneratedJakartaIdContainer.class,
            Warning.SURROGATE_OR_BUSINESS_KEY
        );
    }

    @Test
    public void gettersAreUsedAndProtected() {
        EqualsVerifier.forClass(ProtectedJakartaLazyFieldContainer.class).verify();
    }

    @Test
    public void finalEntitiesArentLazy() {
        EqualsVerifier.forClass(FinalEntity.class).verify();
    }

    private void getterNotUsed(Class<?> type, String method, Warning... additionalWarnings) {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(type).suppress(additionalWarnings).verify())
            .assertFailure()
            .assertMessageContains("JPA Entity", method, "direct reference");
    }

    private void getterNotUsed_warningSuppressed(Class<?> type, Warning... additionalWarnings) {
        EqualsVerifier
            .forClass(type)
            .suppress(Warning.JPA_GETTER)
            .suppress(additionalWarnings)
            .verify();
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
    static class CorrectBasicJakartaEagerFieldContainer {

        @Basic
        private String basic;

        public String getBasic() {
            return basic;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CorrectBasicJakartaEagerFieldContainer)) {
                return false;
            }
            CorrectBasicJakartaEagerFieldContainer other =
                (CorrectBasicJakartaEagerFieldContainer) obj;
            return Objects.equals(basic, other.basic);
        }

        @Override
        public int hashCode() {
            return Objects.hash(getBasic());
        }
    }

    @Entity
    static class CorrectBasicJakartaIgnoredLazyFieldContainer {

        private String somethingElse;

        @Basic(fetch = FetchType.LAZY)
        private String basic;

        public String getBasic() {
            return basic;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CorrectBasicJakartaIgnoredLazyFieldContainer)) {
                return false;
            }
            CorrectBasicJakartaIgnoredLazyFieldContainer other =
                (CorrectBasicJakartaIgnoredLazyFieldContainer) obj;
            return Objects.equals(somethingElse, other.somethingElse);
        }

        @Override
        public int hashCode() {
            return Objects.hash(somethingElse);
        }
    }

    @Entity
    static class IncorrectBasicJakartaLazyGetterContainer {

        private String basic;

        @Basic(fetch = FetchType.LAZY)
        public String getBasic() {
            return basic;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IncorrectBasicJakartaLazyGetterContainer)) {
                return false;
            }
            IncorrectBasicJakartaLazyGetterContainer other =
                (IncorrectBasicJakartaLazyGetterContainer) obj;
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
            IncorrectBasicJakartaLazyFieldContainerHashCode other =
                (IncorrectBasicJakartaLazyFieldContainerHashCode) obj;
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
            IncorrectBasicJakartaLazyFieldContainer other =
                (IncorrectBasicJakartaLazyFieldContainer) obj;
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
            IncorrectOneToOneJakartaLazyFieldContainer other =
                (IncorrectOneToOneJakartaLazyFieldContainer) obj;
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
            IncorrectOneToManyJakartaLazyFieldContainer other =
                (IncorrectOneToManyJakartaLazyFieldContainer) obj;
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
            IncorrectManyToOneJakartaLazyFieldContainer other =
                (IncorrectManyToOneJakartaLazyFieldContainer) obj;
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
            IncorrectManyToManyJakartaLazyFieldContainer other =
                (IncorrectManyToManyJakartaLazyFieldContainer) obj;
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
            IncorrectElementCollectionJakartaLazyFieldContainer other =
                (IncorrectElementCollectionJakartaLazyFieldContainer) obj;
            return Objects.equals(elementCollection, other.elementCollection);
        }

        @Override
        public int hashCode() {
            return Objects.hash(getElementCollection());
        }
    }

    @Entity
    static class LazyGetterContainer {

        @Basic(fetch = FetchType.LAZY)
        private String s;

        public String getS() {
            return s;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            return Objects.equals(getS(), ((LazyGetterContainer) obj).getS());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getS());
        }
    }

    @Entity
    static class ChildOfLazyGetterContainer extends LazyGetterContainer {

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            return super.equals(obj);
        }
    }

    @Entity
    static class ConstantHashCodeContainer {

        @OneToMany
        private String oneToMany;

        @ManyToOne
        private String manyToOne;

        public String getOneToMany() {
            return oneToMany;
        }

        public String getManyToOne() {
            return manyToOne;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ConstantHashCodeContainer)) {
                return false;
            }
            ConstantHashCodeContainer other = (ConstantHashCodeContainer) obj;
            return (
                Objects.equals(getOneToMany(), other.getOneToMany()) &&
                Objects.equals(getManyToOne(), other.getManyToOne())
            );
        }

        @Override
        public int hashCode() {
            return 42;
        }
    }

    @Entity
    static class DifferentCodingStyleContainer {

        // CHECKSTYLE OFF: MemberName
        @OneToMany(fetch = FetchType.LAZY)
        private String m_oneToMany;

        @ManyToOne(fetch = FetchType.LAZY)
        private String m_manyToOne;

        // CHECKSTYLE ON: MemberName

        public String getOneToMany() {
            return m_oneToMany;
        }

        public String getManyToOne() {
            return m_manyToOne;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof DifferentCodingStyleContainer)) {
                return false;
            }
            DifferentCodingStyleContainer other = (DifferentCodingStyleContainer) obj;
            return (
                Objects.equals(getOneToMany(), other.getOneToMany()) &&
                Objects.equals(getManyToOne(), other.getManyToOne())
            );
        }

        @Override
        public int hashCode() {
            return Objects.hash(getOneToMany(), getManyToOne());
        }
    }

    @Entity
    static class CorrectGeneratedJakartaIdContainer {

        @Id
        @GeneratedValue
        private String id;

        public String getId() {
            return id;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CorrectGeneratedJakartaIdContainer)) {
                return false;
            }
            CorrectGeneratedJakartaIdContainer other = (CorrectGeneratedJakartaIdContainer) obj;
            return Objects.equals(getId(), other.getId());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getId());
        }
    }

    @Entity
    static class IncorrectGeneratedJakartaIdContainer {

        @Id
        @GeneratedValue
        private String id;

        public String getId() {
            return id;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IncorrectGeneratedJakartaIdContainer)) {
                return false;
            }
            IncorrectGeneratedJakartaIdContainer other = (IncorrectGeneratedJakartaIdContainer) obj;
            return Objects.equals(id, other.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(getId());
        }
    }

    @Entity
    static class ProtectedJakartaLazyFieldContainer {

        @Basic(fetch = FetchType.LAZY)
        private String basic;

        protected String getBasic() {
            return basic;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ProtectedJakartaLazyFieldContainer)) {
                return false;
            }
            ProtectedJakartaLazyFieldContainer other = (ProtectedJakartaLazyFieldContainer) obj;
            return Objects.equals(getBasic(), other.getBasic());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getBasic());
        }
    }

    @Entity
    static final class FinalEntity {

        @GeneratedValue // which is considered lazy
        private int id;

        public int getId() {
            return id;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof FinalEntity)) {
                return false;
            }
            FinalEntity other = (FinalEntity) obj;
            return Objects.equals(getId(), other.getId());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getId());
        }
    }
}
