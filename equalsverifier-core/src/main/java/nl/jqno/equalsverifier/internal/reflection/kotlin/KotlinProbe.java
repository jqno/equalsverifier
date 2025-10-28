package nl.jqno.equalsverifier.internal.reflection.kotlin;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.*;
import kotlin.reflect.full.KClasses;
import kotlin.reflect.jvm.ReflectJvmMapping;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.util.Assert;
import nl.jqno.equalsverifier.internal.util.Formatter;

/**
 * Collection of 'dangerous' reflection utilities for Kotlin. Can only be used when we know for sure that the
 * `kotlin-reflect` library is available on the classpath.
 */
public final class KotlinProbe {
    private KotlinProbe() {}

    public static List<String> translateKotlinToBytecodeFieldNames(Class<?> container, List<String> fieldNames) {
        return fieldNames.stream().map(fn -> translateKotlinToBytecodeFieldName(container, fn)).toList();
    }

    public static String translateKotlinToBytecodeFieldName(Class<?> container, String fieldName) {
        KClass<?> kClass = JvmClassMappingKt.getKotlinClass(container);
        for (KProperty<?> prop : KClasses.getMemberProperties(kClass)) {
            if (prop.getName().equals(fieldName)) {
                Field backing = ReflectJvmMapping.getJavaField(prop);
                return backing == null ? null : backing.getName();
            }
        }
        return fieldName;
    }

    public static Optional<String> getKotlinPropertyNameFor(Field field) {
        Class<?> declaringClass = field.getDeclaringClass();
        KClass<?> kClass = JvmClassMappingKt.getKotlinClass(declaringClass);
        for (KProperty<?> prop : KClasses.getMemberProperties(kClass)) {
            Field backing = ReflectJvmMapping.getJavaField(prop);
            if (backing != null && backing.equals(field)) {
                return Optional.of(prop.getName());
            }
        }
        return Optional.empty();
    }

    public static Optional<TypeTag> determineLazyType(Class<?> container, Field field) {
        assertHasKotlinReflect(field);
        if (!field.getType().equals(KotlinScreen.LAZY)) {
            return Optional.empty();
        }
        KClass<?> kType = JvmClassMappingKt.getKotlinClass(container);
        Optional<String> optKFieldName = getKotlinPropertyNameFor(field);
        if (optKFieldName.isEmpty()) {
            return Optional.empty();
        }
        String kFieldName = optKFieldName.get();
        KCallable<?> kField = kType.getMembers().stream().filter(m -> kFieldName.equals(m.getName())).findAny().get();
        KType kReturnType = kField.getReturnType();

        TypeTag tag = createTypeTag(kReturnType);
        return Optional.of(new TypeTag(KotlinScreen.LAZY, tag));
    }

    private static TypeTag createTypeTag(KType kType) {
        Class<?> rawClass = determineClass(kType);

        if (kType.getArguments().isEmpty()) {
            return new TypeTag(rawClass);
        }

        List<TypeTag> genericTypeTags = kType
                .getArguments()
                .stream()
                .map(a -> a.getType())
                .filter(Objects::nonNull)
                .map(KotlinProbe::createTypeTag)
                .collect(Collectors.toList());

        return new TypeTag(rawClass, genericTypeTags);
    }

    private static Class<?> determineClass(KType kType) {
        // The logic in this method follows that of TypeTag's inner methods
        var classifier = kType.getClassifier();

        if (classifier instanceof KClass<?>) {
            var kclass = (KClass<?>) classifier;
            return JvmClassMappingKt.getJavaClass(kclass);
        }

        if (classifier instanceof KTypeParameter) {
            var kTypeParameter = (KTypeParameter) classifier;
            for (KType b : kTypeParameter.getUpperBounds()) {
                var upper = determineClass(b);
                if (!Object.class.equals(upper)) {
                    return upper;
                }
            }
        }

        return Object.class;
    }

    private static void assertHasKotlinReflect(Field f) {
        if (!KotlinScreen.canProbe()) {
            var msg = "Cannot read Kotlin field " + f.getName() + ". " + KotlinScreen.ERROR_MESSAGE;
            Assert.fail(Formatter.of(msg));
        }
    }
}
