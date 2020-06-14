package nl.jqno.equalsverifier.internal.reflection.annotations;

import static nl.jqno.equalsverifier.internal.reflection.Util.setOf;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.pool.TypePool;
import nl.jqno.equalsverifier.internal.reflection.SuperclassIterable;

public class AnnotationCacheBuilder {

    private final List<Annotation> supportedAnnotations;
    private final Set<String> ignoredAnnotations;

    public AnnotationCacheBuilder(
            Annotation[] supportedAnnotations, Set<String> ignoredAnnotations) {
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

            visitType(setOf(type), cache, typeDescription, false);
            visitSuperclasses(type, cache, pool);
            visitOuterClasses(type, cache, pool);
            visitPackage(type, cache, pool);
        } catch (IllegalStateException ignored) {
            // Just ignore this class if it can't be processed.
        }
    }

    private void visitType(
            Set<Class<?>> types,
            AnnotationCache cache,
            TypeDescription typeDescription,
            boolean inheriting) {
        visitClass(types, cache, typeDescription, inheriting);
        visitFields(types, cache, typeDescription, inheriting);
    }

    private void visitSuperclasses(Class<?> type, AnnotationCache cache, TypePool pool) {
        for (Class<?> c : SuperclassIterable.of(type)) {
            TypeDescription typeDescription = pool.describe(c.getName()).resolve();
            visitType(setOf(type, c), cache, typeDescription, true);
        }
    }

    private void visitOuterClasses(Class<?> type, AnnotationCache cache, TypePool pool) {
        Class<?> outer = type.getDeclaringClass();
        while (outer != null) {
            TypeDescription typeDescription = pool.describe(outer.getName()).resolve();
            visitType(setOf(type, outer), cache, typeDescription, false);

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
            visitType(setOf(type), cache, typeDescription, false);
        } catch (IllegalStateException e) {
            // No package object; do nothing.
        }
    }

    private void visitClass(
            Set<Class<?>> types,
            AnnotationCache cache,
            TypeDescription typeDescription,
            boolean inheriting) {
        Consumer<Annotation> addToCache = a -> types.forEach(t -> cache.addClassAnnotation(t, a));
        typeDescription
                .getDeclaredAnnotations()
                .forEach(a -> cacheSupportedAnnotations(a, types, cache, addToCache, inheriting));
    }

    private void visitFields(
            Set<Class<?>> types,
            AnnotationCache cache,
            TypeDescription typeDescription,
            boolean inheriting) {
        for (FieldDescription.InDefinedShape f : typeDescription.getDeclaredFields()) {
            Consumer<Annotation> addToCache =
                    a -> types.forEach(t -> cache.addFieldAnnotation(t, f.getName(), a));

            // Regular field annotations
            for (AnnotationDescription a : f.getDeclaredAnnotations()) {
                cacheSupportedAnnotations(a, types, cache, addToCache, inheriting);
            }

            // Type-use annotations
            for (AnnotationDescription a : f.getType().getDeclaredAnnotations()) {
                cacheSupportedAnnotations(a, types, cache, addToCache, inheriting);
            }
        }

        MethodList<MethodDescription.InDefinedShape> methods =
                typeDescription
                        .getDeclaredMethods()
                        .filter(m -> m.getName().startsWith("get") && m.getName().length() > 3);
        for (MethodDescription.InDefinedShape m : methods) {
            String methodName = m.getName();
            String correspondingFieldName =
                    Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
            Consumer<Annotation> addToCache =
                    a -> types.forEach(t -> cache.addFieldAnnotation(t, correspondingFieldName, a));

            for (AnnotationDescription a : m.getDeclaredAnnotations()) {
                cacheSupportedAnnotations(a, types, cache, addToCache, inheriting);
            }
        }
    }

    private void cacheSupportedAnnotations(
            AnnotationDescription annotation,
            Set<Class<?>> types,
            AnnotationCache cache,
            Consumer<Annotation> addToCache,
            boolean inheriting) {

        if (ignoredAnnotations.contains(annotation.getAnnotationType().getCanonicalName())) {
            return;
        }

        Consumer<Annotation> postProcess = a -> a.postProcess(types, cache);

        AnnotationProperties props = buildAnnotationProperties(annotation);
        supportedAnnotations.stream()
                .filter(sa -> matches(annotation, sa))
                .filter(sa -> !inheriting || sa.inherits())
                .filter(sa -> sa.validate(props, cache, ignoredAnnotations))
                .forEach(addToCache.andThen(postProcess));
    }

    private AnnotationProperties buildAnnotationProperties(AnnotationDescription annotation) {
        AnnotationProperties props =
                new AnnotationProperties(annotation.getAnnotationType().getCanonicalName());
        for (MethodDescription.InDefinedShape m :
                annotation.getAnnotationType().getDeclaredMethods()) {
            Object val = annotation.getValue(m).resolve();
            if (val.getClass().isArray() && !val.getClass().getComponentType().isPrimitive()) {
                Object[] array = (Object[]) val;
                Set<String> values = new HashSet<>();
                for (Object obj : array) {
                    if (obj instanceof TypeDescription) {
                        values.add(((TypeDescription) obj).getName());
                    } else {
                        values.add(obj.toString());
                    }
                }
                props.putArrayValues(m.getName(), values);
            }
        }
        return props;
    }

    private boolean matches(AnnotationDescription foundAnnotation, Annotation supportedAnnotation) {
        String canonicalName = foundAnnotation.getAnnotationType().getCanonicalName();
        return supportedAnnotation.partialClassNames().stream().anyMatch(canonicalName::endsWith);
    }
}
