package nl.jqno.equalsverifier.internal.reflection.annotations;

import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.reflection.SuperclassIterable;
import org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Provides access to the annotations that are defined on a class
 * and its fields.
 */
public class AnnotationAccessor {
    private static final int OPCODES = Opcodes.ASM6;

    private final Annotation[] supportedAnnotations;
    private final Class<?> type;
    private final Set<String> ignoredAnnotations;
    private final boolean ignoreFailure;
    private final AnnotationCache cache = new AnnotationCache();

    private boolean shortCircuit = false;

    /**
     * Constructor.
     *
     * @param supportedAnnotations Collection of annotations to query.
     * @param type The class whose annotations need to be queried.
     * @param ignoredAnnotations A collection of type descriptors for
     *          annotations to ignore.
     * @param ignoreFailure Ignore when processing annotations fails when the
     *          class file cannot be read.
     */
    public AnnotationAccessor(Annotation[] supportedAnnotations, Class<?> type, Set<String> ignoredAnnotations, boolean ignoreFailure) {
        this.supportedAnnotations = Arrays.copyOf(supportedAnnotations, supportedAnnotations.length);
        this.type = type;
        this.ignoredAnnotations = ignoredAnnotations;
        this.ignoreFailure = ignoreFailure;
    }

    /**
     * Determines whether {@link #type} has a particular annotation.
     *
     * @param annotation The annotation we want to find.
     * @return True if {@link #type} has an annotation with the supplied name.
     */
    public boolean typeHas(Annotation annotation) {
        if (shortCircuit) {
            return false;
        }
        process();
        return cache.hasClassAnnotation(type, annotation);
    }

    /**
     * Determines whether {@link #type} has a particular annotation on a
     * particular field.
     *
     * @param fieldName The name of the field for which we want to know if it
     *          has the annotation.
     * @param annotation The annotation we want to find.
     * @return True if the specified field in {@link #type} has the specified
     *          annotation.
     * @throws ReflectionException if {@link #type} does not have the specified
     *          field.
     */
    public boolean fieldHas(String fieldName, Annotation annotation) {
        if (shortCircuit) {
            return false;
        }
        process();
        if (!cache.hasField(type, fieldName) && !ignoreFailure) {
            throw new ReflectionException("Class " + type.getName() + " does not have field " + fieldName);
        }
        return cache.hasFieldAnnotation(type, fieldName, annotation);
    }

    private void process() {
        if (cache.hasResolved(type)) {
            return;
        }

        visit();
    }

    private void visit() {
        visitType(type, false);
        for (Class<?> c : SuperclassIterable.of(type)) {
            visitType(c, true);
        }
    }

    private void visitType(Class<?> c, boolean inheriting) {
        ClassLoader classLoader = getClassLoaderFor(c);
        Type asmType = Type.getType(c);
        String url = asmType.getInternalName() + ".class";

        try (InputStream is = classLoader.getResourceAsStream(url)) {
            Visitor v = new Visitor(inheriting);
            ClassReader cr = new ClassReader(is);
            cr.accept(v, 0);
        }
        catch (IOException | UnsupportedOperationException e) {
            if (ignoreFailure) {
                shortCircuit = true;
            }
            else {
                throw new ReflectionException("Cannot read class file for " + c.getSimpleName() +
                        ".\nSuppress Warning.ANNOTATION to skip annotation processing phase.");
            }
        }
    }

    private ClassLoader getClassLoaderFor(Class<?> c) {
        ClassLoader result = c.getClassLoader();
        if (result == null) {
            result = ClassLoader.getSystemClassLoader();
        }
        return result;
    }

    private class Visitor extends ClassVisitor {
        private final boolean inheriting;

        public Visitor(boolean inheriting) {
            super(OPCODES);
            this.inheriting = inheriting;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            return new MyAnnotationVisitor(descriptor, cache, Optional.empty(), inheriting);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            cache.addField(type, name);
            return new MyFieldVisitor(cache, name, inheriting);
        }
    }

    private class MyFieldVisitor extends FieldVisitor {
        private final AnnotationCache cache;
        private final String fieldName;
        private final boolean inheriting;

        public MyFieldVisitor(AnnotationCache cache, String fieldName, boolean inheriting) {
            super(OPCODES);
            this.cache = cache;
            this.fieldName = fieldName;
            this.inheriting = inheriting;
        }

        @Override
        public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
            return new MyAnnotationVisitor(descriptor, cache, Optional.of(fieldName), inheriting);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            return new MyAnnotationVisitor(descriptor, cache, Optional.of(fieldName), inheriting);
        }
    }

    private class MyAnnotationVisitor extends AnnotationVisitor {
        private final String annotationDescriptor;
        private final AnnotationCache cache;
        private final Optional<String> fieldName;
        private final boolean inheriting;

        private final AnnotationProperties properties;

        public MyAnnotationVisitor(String annotationDescriptor, AnnotationCache cache, Optional<String> fieldName, boolean inheriting) {
            super(OPCODES);
            this.annotationDescriptor = annotationDescriptor;
            this.cache = cache;
            this.fieldName = fieldName;
            this.inheriting = inheriting;
            properties = new AnnotationProperties(annotationDescriptor);
        }

        @Override
        public AnnotationVisitor visitArray(String name) {
            Set<Object> foundAnnotations = new HashSet<>();
            properties.putArrayValues(name, foundAnnotations);
            return new AnnotationArrayValueVisitor(foundAnnotations);
        }

        @Override
        public void visitEnd() {
            if (ignoredAnnotations.contains(annotationDescriptor)) {
                return;
            }
            for (Annotation annotation : supportedAnnotations) {
                if (!inheriting || annotation.inherits()) {
                    matchAnnotation(annotation);
                }
            }
        }

        private void matchAnnotation(Annotation annotation) {
            for (String descriptor : annotation.descriptors()) {
                String asBytecodeIdentifier = descriptor.replaceAll("\\.", "/") + ";";
                if (annotationDescriptor.endsWith(asBytecodeIdentifier) && annotation.validate(properties, ignoredAnnotations)) {
                    if (fieldName.isPresent()) {
                        cache.addFieldAnnotation(type, fieldName.get(), annotation);
                    }
                    else {
                        cache.addClassAnnotation(type, annotation);
                    }
                }
            }
        }
    }

    private static class AnnotationArrayValueVisitor extends AnnotationVisitor {
        private final Set<Object> foundAnnotations;

        public AnnotationArrayValueVisitor(Set<Object> foundAnnotations) {
            super(OPCODES);
            this.foundAnnotations = foundAnnotations;
        }

        @Override
        public void visit(String name, Object value) {
            foundAnnotations.add(value);
        }

        @Override
        public void visitEnum(String name, String desc, String value) {
            foundAnnotations.add(value);
        }
    }
}
