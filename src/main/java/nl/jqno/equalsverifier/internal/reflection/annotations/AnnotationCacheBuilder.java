package nl.jqno.equalsverifier.internal.reflection.annotations;

import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.pool.TypePool;
import nl.jqno.equalsverifier.internal.reflection.SuperclassIterable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

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

        try {
            TypePool pool = TypePool.Default.of(type.getClassLoader());
            TypeDescription typeDescription = pool.describe(type.getName()).resolve();

            visitType(type, cache, typeDescription, false);
            visitSuperclasses(type, cache, pool);
            visitOuterClasses(type, cache, pool);
            visitPackage(type, cache, pool);
        }
        catch (IllegalStateException ignored) {
            // Just ignore this class if it can't be processed.
        }
    }

    private void visitType(Class<?> type, AnnotationCache cache, TypeDescription typeDescription, boolean inheriting) {
        visitClass(type, cache, typeDescription, inheriting);
        visitFields(type, cache, typeDescription, inheriting);
    }

    private void visitSuperclasses(Class<?> type, AnnotationCache cache, TypePool pool) {
        SuperclassIterable.of(type).forEach(c -> {
            TypeDescription typeDescription = pool.describe(c.getName()).resolve();
            visitType(type, cache, typeDescription, true);
        });
    }

    private void visitOuterClasses(Class<?> type, AnnotationCache cache, TypePool pool) {
        Class<?> outer = type.getDeclaringClass();
        while (outer != null) {
            TypeDescription typeDescription = pool.describe(outer.getName()).resolve();
            visitType(type, cache, typeDescription, false);

            outer = outer.getDeclaringClass();
        }
    }

    private void visitPackage(Class<?> type, AnnotationCache cache, TypePool pool) {
        Package pkg = type.getPackage();
        if (pkg == null) {
            return;
        }

        String className = pkg.getName() + ".package-info";

        try {
            TypeDescription typeDescription = pool.describe(className).resolve();
            visitType(type, cache, typeDescription, false);
        }
        catch (IllegalStateException e) {
            // No package object; do nothing.
        }
    }

    private void visitClass(Class<?> type, AnnotationCache cache, TypeDescription typeDescription, boolean inheriting) {
        Consumer<Annotation> addToCache = a -> cache.addClassAnnotation(type, a);
        typeDescription.getDeclaredAnnotations()
            .forEach(a -> cacheSupportedAnnotations(a, cache, addToCache, inheriting));
    }

    private void visitFields(Class<?> type, AnnotationCache cache, TypeDescription typeDescription, boolean inheriting) {
        typeDescription.getDeclaredFields().forEach(f -> {
            Consumer<Annotation> addToCache = a -> cache.addFieldAnnotation(type, f.getName(), a);

            // Regular field annotations
            f.getDeclaredAnnotations()
                .forEach(a -> cacheSupportedAnnotations(a, cache, addToCache, inheriting));

            // Type-use annotations
            f.getType().getDeclaredAnnotations()
                .forEach(a -> cacheSupportedAnnotations(a, cache, addToCache, inheriting));
        });
    }

    private void cacheSupportedAnnotations(AnnotationDescription annotation, AnnotationCache cache, Consumer<Annotation> addToCache, boolean inheriting) {
        AnnotationProperties props = buildAnnotationProperties(annotation);
        supportedAnnotations
            .stream()
            .filter(sa -> matches(annotation, sa))
            .filter(sa -> !inheriting || sa.inherits())
            .filter(sa -> sa.validate(props, cache, ignoredAnnotations))
            .forEach(addToCache);
    }

    private AnnotationProperties buildAnnotationProperties(AnnotationDescription annotation) {
        AnnotationProperties props = new AnnotationProperties(annotation.getAnnotationType().getDescriptor());
        annotation.getAnnotationType().getDeclaredMethods().forEach(m -> {
            Object val = annotation.getValue(m).resolve();
            if (val.getClass().isArray()) {
                Object[] array = (Object[])val;
                Set<Object> values = new HashSet<>(Arrays.asList(array));
                props.putArrayValues(m.getName(), values);
            }
        });
        return props;
    }

    private boolean matches(AnnotationDescription foundAnnotation, Annotation supportedAnnotation) {
        String canonicalName = foundAnnotation.getAnnotationType().getCanonicalName();
        return supportedAnnotation.descriptors()
            .stream()
            .anyMatch(canonicalName::endsWith);
    }
}
