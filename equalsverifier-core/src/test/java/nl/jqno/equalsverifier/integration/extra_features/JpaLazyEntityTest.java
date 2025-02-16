package nl.jqno.equalsverifier.integration.extra_features;

import java.util.Arrays;
import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import nl.jqno.equalsverifier.testhelpers.annotations.javax.persistence.*;
import org.junit.jupiter.api.Test;

// CHECKSTYLE OFF: HiddenField

class JpaLazyEntityTest {

    @Test
    void gettersAreUsed() {
        EqualsVerifier.forClass(CorrectJpaLazyFieldContainer.class).verify();
    }

    @Test
    void basicGetterAbsent() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(LazyFieldWithoutGetterContainer.class).verify())
                .assertFailure()
                .assertMessageContains("doesn't contain getter getBasic() for field basic");
    }

    @Test
    void basicGetterNotUsed_givenEagerLoading() {
        EqualsVerifier.forClass(CorrectBasicJpaEagerFieldContainer.class).verify();
    }

    @Test
    void basicGetterNotUsed_givenCorrespondingFieldIgnored() {
        EqualsVerifier.forClass(CorrectBasicJpaIgnoredLazyFieldContainer.class).withIgnoredFields("basic").verify();
    }

    @Test
    void basicGetterNotUsed_givenWarningSuppressed() {
        EqualsVerifier
                .forClass(CorrectBasicJpaIgnoredLazyFieldContainer.class)
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    void basicGetterNotUsed_givenAnnotationIsOnGetter() {
        getterNotUsed(IncorrectBasicJpaLazyGetterContainer.class, "equals");
        getterNotUsed_warningSuppressed(IncorrectBasicJpaLazyGetterContainer.class);
    }

    @Test
    void basicGetterNotUsedInHashCode() {
        getterNotUsed(IncorrectBasicJpaLazyFieldContainerHashCode.class, "hashCode");
        getterNotUsed_warningSuppressed(IncorrectBasicJpaLazyFieldContainerHashCode.class);
    }

    @Test
    void basicGetterNotUsed() {
        getterNotUsed(IncorrectBasicJpaLazyFieldContainer.class, "equals");
        getterNotUsed_warningSuppressed(IncorrectBasicJpaLazyFieldContainer.class);
    }

    @Test
    void oneToOneGetterNotUsed() {
        getterNotUsed(IncorrectOneToOneJpaLazyFieldContainer.class, "equals");
        getterNotUsed_warningSuppressed(IncorrectOneToOneJpaLazyFieldContainer.class);
    }

    @Test
    void oneToManyGetterNotUsed() {
        getterNotUsed(IncorrectOneToManyJpaLazyFieldContainer.class, "equals");
        getterNotUsed_warningSuppressed(IncorrectOneToManyJpaLazyFieldContainer.class);
    }

    @Test
    void manyToOneGetterNotUsed() {
        getterNotUsed(IncorrectManyToOneJpaLazyFieldContainer.class, "equals");
        getterNotUsed_warningSuppressed(IncorrectManyToOneJpaLazyFieldContainer.class);
    }

    @Test
    void manyToManyGetterNotUsed() {
        getterNotUsed(IncorrectManyToManyJpaLazyFieldContainer.class, "equals");
        getterNotUsed_warningSuppressed(IncorrectManyToManyJpaLazyFieldContainer.class);
    }

    @Test
    void elementCollectionGetterNotUsed() {
        getterNotUsed(IncorrectElementCollectionJpaLazyFieldContainer.class, "equals");
        getterNotUsed_warningSuppressed(IncorrectElementCollectionJpaLazyFieldContainer.class);
    }

    @Test
    void lazyGettersPickedUpInSuper() {
        EqualsVerifier.forClass(LazyGetterContainer.class).usingGetClass().verify();
        EqualsVerifier.forClass(ChildOfLazyGetterContainer.class).usingGetClass().verify();
    }

    @Test
    void constantHashCode_givenStrictHashCodeSuppressed() {
        EqualsVerifier.forClass(ConstantHashCodeContainer.class).suppress(Warning.STRICT_HASHCODE).verify();
    }

    @Test
    void differentCodingStyle_single() {
        EqualsVerifier
                .forClass(DifferentCodingStyleContainer.class)
                .withFieldnameToGetterConverter(fn -> "get" + Character.toUpperCase(fn.charAt(2)) + fn.substring(3))
                .verify();
    }

    @Test
    void differentCodingStyle_configured() {
        EqualsVerifier
                .configure()
                .withFieldnameToGetterConverter(fn -> "get" + Character.toUpperCase(fn.charAt(2)) + fn.substring(3))
                .forClass(DifferentCodingStyleContainer.class)
                .verify();
    }

    @Test
    void differentCodingStyle_multiple() {
        EqualsVerifier
                .forClasses(Arrays.asList(DifferentCodingStyleContainer.class))
                .withFieldnameToGetterConverter(fn -> "get" + Character.toUpperCase(fn.charAt(2)) + fn.substring(3))
                .verify();
    }

    @Test
    void getterUsedForGeneratedId() {
        EqualsVerifier.forClass(CorrectGeneratedJpaIdContainer.class).suppress(Warning.SURROGATE_KEY).verify();
        EqualsVerifier
                .forClass(CorrectGeneratedJpaIdContainer.class)
                .suppress(Warning.SURROGATE_OR_BUSINESS_KEY)
                .verify();
    }

    @Test
    void getterNotUsedForGeneratedId() {
        getterNotUsed(IncorrectGeneratedJpaIdContainer.class, "equals", Warning.SURROGATE_KEY);
        getterNotUsed_warningSuppressed(IncorrectGeneratedJpaIdContainer.class, Warning.SURROGATE_KEY);
        getterNotUsed(IncorrectGeneratedJpaIdContainer.class, "equals", Warning.SURROGATE_OR_BUSINESS_KEY);
        getterNotUsed_warningSuppressed(IncorrectGeneratedJpaIdContainer.class, Warning.SURROGATE_OR_BUSINESS_KEY);
    }

    @Test
    void gettersAreUsedAndProtected() {
        EqualsVerifier.forClass(ProtectedJpaLazyFieldContainer.class).verify();
    }

    @Test
    void finalEntitiesArentLazy() {
        EqualsVerifier.forClass(FinalEntity.class).verify();
    }

    @Test
    void unmetPreconditionThrowsError() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(PreconditionJpaEntity.class).suppress(Warning.NULL_FIELDS).verify())
                .assertFailure()
                .assertMessageContains("Unmet precondition");
    }

    @Test
    void metPreconditionThrowsNoError() {
        EqualsVerifier
                .forClass(PreconditionJpaEntity.class)
                .withPrefabValuesForField("basic", "precondition:red", "precondition:blue")
                .verify();
    }

    private void getterNotUsed(Class<?> type, String method, Warning... additionalWarnings) {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(type).suppress(additionalWarnings).verify())
                .assertFailure()
                .assertMessageContains("JPA Entity", method, "direct reference");
    }

    private void getterNotUsed_warningSuppressed(Class<?> type, Warning... additionalWarnings) {
        EqualsVerifier.forClass(type).suppress(Warning.JPA_GETTER).suppress(additionalWarnings).verify();
    }

    @Entity
    static class CorrectJpaLazyFieldContainer {

        @Basic(fetch = FetchType.LAZY)
        private String basic;

        @OneToOne
        private String oneToOne;

        @OneToMany
        private String oneToMany;

        @ManyToOne
        private String manyToOne;

        @ManyToMany
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
            return Objects.equals(getBasic(), other.getBasic())
                    && Objects.equals(getOneToOne(), other.getOneToOne())
                    && Objects.equals(getOneToMany(), other.getOneToMany())
                    && Objects.equals(getManyToOne(), other.getManyToOne())
                    && Objects.equals(getManyToMany(), other.getManyToMany())
                    && Objects.equals(getElementCollection(), other.getElementCollection());
        }

        @Override
        public int hashCode() {
            return Objects
                    .hash(
                        getBasic(),
                        getOneToOne(),
                        getOneToMany(),
                        getManyToOne(),
                        getManyToMany(),
                        getElementCollection());
        }
    }

    @Entity
    static class LazyFieldWithoutGetterContainer {

        @OneToMany
        private String basic;

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof LazyFieldWithoutGetterContainer)) {
                return false;
            }
            LazyFieldWithoutGetterContainer other = (LazyFieldWithoutGetterContainer) obj;
            return Objects.equals(basic, other.basic);
        }

        @Override
        public int hashCode() {
            return Objects.hash(basic);
        }
    }

    @Entity
    static class CorrectBasicJpaEagerFieldContainer {

        @Basic
        private String basic;

        public String getBasic() {
            return basic;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CorrectBasicJpaEagerFieldContainer)) {
                return false;
            }
            CorrectBasicJpaEagerFieldContainer other = (CorrectBasicJpaEagerFieldContainer) obj;
            return Objects.equals(basic, other.basic);
        }

        @Override
        public int hashCode() {
            return Objects.hash(getBasic());
        }
    }

    @Entity
    static class CorrectBasicJpaIgnoredLazyFieldContainer {

        private String somethingElse;

        @Basic(fetch = FetchType.LAZY)
        private String basic;

        public String getBasic() {
            return basic;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CorrectBasicJpaIgnoredLazyFieldContainer)) {
                return false;
            }
            CorrectBasicJpaIgnoredLazyFieldContainer other = (CorrectBasicJpaIgnoredLazyFieldContainer) obj;
            return Objects.equals(somethingElse, other.somethingElse);
        }

        @Override
        public int hashCode() {
            return Objects.hash(somethingElse);
        }
    }

    @Entity
    static class IncorrectBasicJpaLazyGetterContainer {

        private String basic;

        @Basic(fetch = FetchType.LAZY)
        public String getBasic() {
            return basic;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IncorrectBasicJpaLazyGetterContainer)) {
                return false;
            }
            IncorrectBasicJpaLazyGetterContainer other = (IncorrectBasicJpaLazyGetterContainer) obj;
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

        @OneToOne
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

        @OneToMany
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

        @ManyToOne
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

        @ManyToMany
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

        @ElementCollection
        private String elementCollection;

        public String getElementCollection() {
            return elementCollection;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IncorrectElementCollectionJpaLazyFieldContainer)) {
                return false;
            }
            IncorrectElementCollectionJpaLazyFieldContainer other =
                    (IncorrectElementCollectionJpaLazyFieldContainer) obj;
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
            return Objects.equals(getOneToMany(), other.getOneToMany())
                    && Objects.equals(getManyToOne(), other.getManyToOne());
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
            return Objects.equals(getOneToMany(), other.getOneToMany())
                    && Objects.equals(getManyToOne(), other.getManyToOne());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getOneToMany(), getManyToOne());
        }
    }

    @Entity
    static class CorrectGeneratedJpaIdContainer {

        @Id
        @GeneratedValue
        private String id;

        public String getId() {
            return id;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CorrectGeneratedJpaIdContainer)) {
                return false;
            }
            CorrectGeneratedJpaIdContainer other = (CorrectGeneratedJpaIdContainer) obj;
            return Objects.equals(getId(), other.getId());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getId());
        }
    }

    @Entity
    static class IncorrectGeneratedJpaIdContainer {

        @Id
        @GeneratedValue
        private String id;

        public String getId() {
            return id;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IncorrectGeneratedJpaIdContainer)) {
                return false;
            }
            IncorrectGeneratedJpaIdContainer other = (IncorrectGeneratedJpaIdContainer) obj;
            return Objects.equals(id, other.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(getId());
        }
    }

    @Entity
    static class ProtectedJpaLazyFieldContainer {

        @Basic(fetch = FetchType.LAZY)
        private String basic;

        protected String getBasic() {
            return basic;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ProtectedJpaLazyFieldContainer)) {
                return false;
            }
            ProtectedJpaLazyFieldContainer other = (ProtectedJpaLazyFieldContainer) obj;
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

    @Entity
    static class PreconditionJpaEntity {

        @Basic(fetch = FetchType.LAZY)
        private String basic;

        public String getBasic() {
            return basic;
        }

        @Override
        public boolean equals(Object obj) {
            if (getBasic() != null && !getBasic().startsWith("precondition:")) {
                throw new IllegalStateException("Unmet precondition: " + getBasic());
            }
            if (!(obj instanceof PreconditionJpaEntity)) {
                return false;
            }
            PreconditionJpaEntity other = (PreconditionJpaEntity) obj;
            return Objects.equals(getBasic(), other.getBasic());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getBasic());
        }
    }
}
