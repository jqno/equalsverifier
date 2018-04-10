package nl.jqno.equalsverifier.testhelpers.annotations;

import nl.jqno.equalsverifier.internal.reflection.annotations.Annotation;
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationProperties;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public enum TestSupportedAnnotations implements Annotation {
    // Type's closing ; is added by AnnotationAccessor.
    TYPE_RUNTIME_RETENTION(false, "Lnl/jqno/equalsverifier/testhelpers/annotations/TypeAnnotationRuntimeRetention"),
    TYPE_CLASS_RETENTION(false, "Lnl/jqno/equalsverifier/testhelpers/annotations/TypeAnnotationClassRetention"),
    FIELD_RUNTIME_RETENTION(false, "Lnl/jqno/equalsverifier/testhelpers/annotations/FieldAnnotationRuntimeRetention"),
    FIELD_CLASS_RETENTION(false, "Lnl/jqno/equalsverifier/testhelpers/annotations/FieldAnnotationClassRetention"),

    TYPE_RUNTIME_RETENTION_PARTIAL_DESCRIPTOR(false, "TypeAnnotationRuntimeRetention"),
    TYPE_RUNTIME_RETENTION_CANONICAL_DESCRIPTOR(false, TypeAnnotationRuntimeRetention.class.getCanonicalName()),

    FIELD_RUNTIME_RETENTION_PARTIAL_DESCRIPTOR(false, "FieldAnnotationRuntimeRetention"),
    FIELD_RUNTIME_RETENTION_CANONICAL_DESCRIPTOR(false, FieldAnnotationRuntimeRetention.class.getCanonicalName()),

    TYPE_INHERITS(true, "TypeAnnotationInherits"),
    TYPE_DOESNT_INHERIT(false, "TypeAnnotationDoesntInherit"),
    FIELD_INHERITS(true, "FieldAnnotationInherits"),
    FIELD_DOESNT_INHERIT(false, "FieldAnnotationDoesntInherit"),

    PACKAGE_ANNOTATION(false, "PackageAnnotation"),
    INAPPLICABLE(false, "Inapplicable") {
        @Override
        public boolean validate(AnnotationProperties properties, Set<String> ignoredAnnotations) {
            return false;
        }
    };

    private final boolean inherits;
    private final List<String> descriptors;

    private TestSupportedAnnotations(boolean inherits, String... descriptors) {
        this.inherits = inherits;
        this.descriptors = Arrays.asList(descriptors);
    }

    @Override
    public Iterable<String> descriptors() {
        return descriptors;
    }

    @Override
    public boolean inherits() {
        return inherits;
    }

    @Override
    public boolean validate(AnnotationProperties properties, Set<String> ignoredAnnotations) {
        return true;
    }
}
