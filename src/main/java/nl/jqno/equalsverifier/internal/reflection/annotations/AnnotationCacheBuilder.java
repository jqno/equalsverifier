package nl.jqno.equalsverifier.internal.reflection.annotations;

import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.SuperclassIterable;
import org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;

public class AnnotationCacheBuilder {
    private final List<Annotation> supportedAnnotations;
    private final Set<String> ignoredAnnotations;

    public AnnotationCacheBuilder(Annotation[] supportedAnnotations, Set<String> ignoredAnnotations) {
        this.supportedAnnotations = Arrays.asList(supportedAnnotations);
        this.ignoredAnnotations = ignoredAnnotations;
    }

    public void build(Class<?> type, AnnotationCache cache) {
        if (cache.hasResolved(type)) {
            return;
        }

        visitClass(type, cache);
        visitFields(type, cache);
    }

    private void visitFields(Class<?> type, AnnotationCache cache) {
        for (Field f : FieldIterable.of(type)) {
            build(f.getClass(), cache);
        }
    }

    private void visitClass(Class<?> type, AnnotationCache cache) {
        visitType(type, type, cache, false);
        visitSuperclasses(type, cache);
        visitOuterClasses(type, cache);
        visitPackage(type, cache);
    }

    private void visitSuperclasses(Class<?> type, AnnotationCache cache) {
        for (Class<?> c : SuperclassIterable.of(type)) {
            visitType(c, type, cache, true);
        }
    }

    private void visitOuterClasses(Class<?> type, AnnotationCache cache) {
        Class<?> outer = type.getDeclaringClass();
        while (outer != null) {
            visitType(outer, type, cache, false);
            outer = outer.getDeclaringClass();
        }
    }

    private void visitPackage(Class<?> type, AnnotationCache cache) {
        try {
            Package pkg = type.getPackage();
            if (pkg == null) {
                return;
            }

            String className = pkg.getName() + ".package-info";
            Class<?> packageType = Class.forName(className);
            visitType(packageType, type, cache, false);
        }
        catch (ClassNotFoundException e) {
            // No package object; do nothing.
        }
    }

    private void visitType(Class<?> type, Class<?> cacheInto, AnnotationCache cache, boolean inheriting) {
        ClassLoader classLoader = getClassLoaderFor(type);
        Type asmType = Type.getType(type);
        String url = asmType.getInternalName() + ".class";

        try (InputStream is = classLoader.getResourceAsStream(url)) {
            Visitor v = new Visitor(cacheInto, cache, inheriting);
            ClassReader cr = new ClassReader(is);
            cr.accept(v, 0);
        }
        catch (IOException e) {
            // Just ignore this class if it can't be processed.
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
        private final Class<?> type;
        private final AnnotationCache cache;
        private final boolean inheriting;

        public Visitor(Class<?> type, AnnotationCache cache, boolean inheriting) {
            super(Opcodes.ASM6);
            this.type = type;
            this.cache = cache;
            this.inheriting = inheriting;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            return new MyAnnotationVisitor(type, descriptor, cache, Optional.empty(), inheriting);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            cache.addField(type, name);
            return new MyFieldVisitor(type, cache, name, inheriting);
        }
    }

    private class MyFieldVisitor extends FieldVisitor {
        private final Class<?> type;
        private final AnnotationCache cache;
        private final String fieldName;
        private final boolean inheriting;

        public MyFieldVisitor(Class<?> type, AnnotationCache cache, String fieldName, boolean inheriting) {
            super(Opcodes.ASM6);
            this.type = type;
            this.cache = cache;
            this.fieldName = fieldName;
            this.inheriting = inheriting;
        }

        @Override
        public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
            return new MyAnnotationVisitor(type, descriptor, cache, Optional.of(fieldName), inheriting);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            return new MyAnnotationVisitor(type, descriptor, cache, Optional.of(fieldName), inheriting);
        }
    }

    private class MyAnnotationVisitor extends AnnotationVisitor {
        private final Class<?> type;
        private final String annotationDescriptor;
        private final AnnotationCache cache;
        private final Optional<String> fieldName;
        private final boolean inheriting;

        private final AnnotationProperties properties;

        public MyAnnotationVisitor(Class<?> type, String annotationDescriptor, AnnotationCache cache,
                Optional<String> fieldName, boolean inheriting) {
            super(Opcodes.ASM6);
            this.type = type;
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
            super(Opcodes.ASM6);
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
