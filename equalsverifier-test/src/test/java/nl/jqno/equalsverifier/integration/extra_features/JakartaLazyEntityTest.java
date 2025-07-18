package nl.jqno.equalsverifier.integration.extra_features;

import java.util.Arrays;
import java.util.Objects;

import jakarta.persistence.*;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

// CHECKSTYLE OFF: HiddenField

class JakartaLazyEntityTest {

    @Test
    void gettersAreUsed() {
        EqualsVerifier.forClass(CorrectJakartaLazyFieldContainer.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }

    @Test
    void basicGetterNotUsed_givenEagerLoading() {
        EqualsVerifier
                .forClass(CorrectBasicJakartaEagerFieldContainer.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }

    @Test
    void basicGetterNotUsed_givenCorrespondingFieldIgnored() {
        EqualsVerifier
                .forClass(CorrectBasicJakartaIgnoredLazyFieldContainer.class)
                .withIgnoredFields("basic")
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }

    @Test
    void basicGetterNotUsed_givenWarningSuppressed() {
        EqualsVerifier
                .forClass(CorrectBasicJakartaIgnoredLazyFieldContainer.class)
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    void basicGetterNotUsed_givenAnnotationIsOnGetter() {
        getterNotUsed(IncorrectBasicJakartaLazyGetterContainer.class, "equals");
        getterNotUsed_warningSuppressed(IncorrectBasicJakartaLazyGetterContainer.class);
    }

    @Test
    void basicGetterNotUsedInHashCode() {
        getterNotUsed(IncorrectBasicJakartaLazyFieldContainerHashCode.class, "hashCode");
        getterNotUsed_warningSuppressed(IncorrectBasicJakartaLazyFieldContainerHashCode.class);
    }

    @Test
    void basicGetterNotUsed() {
        getterNotUsed(IncorrectBasicJakartaLazyFieldContainer.class, "equals");
        getterNotUsed_warningSuppressed(IncorrectBasicJakartaLazyFieldContainer.class);
    }

    @Test
    void oneToOneGetterNotUsed() {
        getterNotUsed(IncorrectOneToOneJakartaLazyFieldContainer.class, "equals");
        getterNotUsed_warningSuppressed(IncorrectOneToOneJakartaLazyFieldContainer.class);
    }

    @Test
    void oneToManyGetterNotUsed() {
        getterNotUsed(IncorrectOneToManyJakartaLazyFieldContainer.class, "equals");
        getterNotUsed_warningSuppressed(IncorrectOneToManyJakartaLazyFieldContainer.class);
    }

    @Test
    void manyToOneGetterNotUsed() {
        getterNotUsed(IncorrectManyToOneJakartaLazyFieldContainer.class, "equals");
        getterNotUsed_warningSuppressed(IncorrectManyToOneJakartaLazyFieldContainer.class);
    }

    @Test
    void manyToManyGetterNotUsed() {
        getterNotUsed(IncorrectManyToManyJakartaLazyFieldContainer.class, "equals");
        getterNotUsed_warningSuppressed(IncorrectManyToManyJakartaLazyFieldContainer.class);
    }

    @Test
    void elementCollectionGetterNotUsed() {
        getterNotUsed(IncorrectElementCollectionJakartaLazyFieldContainer.class, "equals");
        getterNotUsed_warningSuppressed(IncorrectElementCollectionJakartaLazyFieldContainer.class);
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
                .suppress(Warning.NONFINAL_FIELDS)
                .withFieldnameToGetterConverter(fn -> "get" + Character.toUpperCase(fn.charAt(2)) + fn.substring(3))
                .verify();
    }

    @Test
    void differentCodingStyle_configured() {
        EqualsVerifier
                .configure()
                .suppress(Warning.NONFINAL_FIELDS)
                .withFieldnameToGetterConverter(fn -> "get" + Character.toUpperCase(fn.charAt(2)) + fn.substring(3))
                .forClass(DifferentCodingStyleContainer.class)
                .verify();
    }

    @Test
    void differentCodingStyle_multiple() {
        EqualsVerifier
                .forClasses(Arrays.asList(DifferentCodingStyleContainer.class))
                .suppress(Warning.NONFINAL_FIELDS)
                .withFieldnameToGetterConverter(fn -> "get" + Character.toUpperCase(fn.charAt(2)) + fn.substring(3))
                .verify();
    }

    @Test
    void getterUsedForGeneratedId() {
        EqualsVerifier.forClass(CorrectGeneratedJakartaIdContainer.class).suppress(Warning.SURROGATE_KEY).verify();
        EqualsVerifier
                .forClass(CorrectGeneratedJakartaIdContainer.class)
                .suppress(Warning.SURROGATE_OR_BUSINESS_KEY)
                .verify();
    }

    @Test
    void getterNotUsedForGeneratedId() {
        getterNotUsed(IncorrectGeneratedJakartaIdContainer.class, "equals", Warning.SURROGATE_KEY);
        getterNotUsed_warningSuppressed(IncorrectGeneratedJakartaIdContainer.class, Warning.SURROGATE_KEY);
        getterNotUsed(IncorrectGeneratedJakartaIdContainer.class, "equals", Warning.SURROGATE_OR_BUSINESS_KEY);
        getterNotUsed_warningSuppressed(IncorrectGeneratedJakartaIdContainer.class, Warning.SURROGATE_OR_BUSINESS_KEY);
    }

    @Test
    void gettersAreUsedAndProtected() {
        EqualsVerifier.forClass(ProtectedJakartaLazyFieldContainer.class).verify();
    }

    @Test
    void finalEntitiesArentLazy() {
        EqualsVerifier.forClass(FinalEntity.class).verify();
    }

    @Test
    void unmetPreconditionThrowsError() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(PreconditionJakartaEntity.class)
                            .suppress(Warning.NULL_FIELDS)
                            .verify())
                .assertFailure()
                .assertMessageContains("Unmet precondition");
    }

    @Test
    void metPreconditionThrowsNoError() {
        EqualsVerifier
                .forClass(PreconditionJakartaEntity.class)
                .withPrefabValuesForField("basic", "precondition:red", "precondition:blue")
                .verify();
    }

    @Test
    void finalGetterThrowsError() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(CorrectButFinalLazyMethodJakartaFieldContainer.class).verify())
                .assertFailure()
                .assertMessageContains("Getter method getGenerated", "is final");
    }

    @Test
    void finalGetterSucceedsIfWarningSuppressed() {
        EqualsVerifier
                .forClass(CorrectButFinalLazyMethodJakartaFieldContainer.class)
                .suppress(Warning.JPA_GETTER)
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
            CorrectBasicJakartaEagerFieldContainer other = (CorrectBasicJakartaEagerFieldContainer) obj;
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
            CorrectBasicJakartaIgnoredLazyFieldContainer other = (CorrectBasicJakartaIgnoredLazyFieldContainer) obj;
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
            IncorrectBasicJakartaLazyGetterContainer other = (IncorrectBasicJakartaLazyGetterContainer) obj;
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
            return getId() == other.getId();
        }

        @Override
        public int hashCode() {
            return Objects.hash(getId());
        }
    }

    @Entity
    static class PreconditionJakartaEntity {

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
            if (!(obj instanceof PreconditionJakartaEntity)) {
                return false;
            }
            PreconditionJakartaEntity other = (PreconditionJakartaEntity) obj;
            return Objects.equals(getBasic(), other.getBasic());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getBasic());
        }
    }

    @Entity
    static class CorrectButFinalLazyMethodJakartaFieldContainer {

        @GeneratedValue
        private Long generated;

        public final Long getGenerated() {
            return generated;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CorrectButFinalLazyMethodJakartaFieldContainer)) {
                return false;
            }
            CorrectButFinalLazyMethodJakartaFieldContainer other = (CorrectButFinalLazyMethodJakartaFieldContainer) obj;
            return Objects.equals(getGenerated(), other.getGenerated());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getGenerated());
        }
    }
}
