package nl.jqno.equalsverifier.internal.reflection.kotlin;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import kotlin.Lazy;
import kotlin.LazyKt;
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

    public static <T> Lazy<T> lazy(T value) {
        return LazyKt.lazyOf(value);
    }

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
        assertHasKotlinReflect();
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
        KClass<?> kclass = (KClass<?>) kType.getClassifier();
        Class<?> rawClass = JvmClassMappingKt.getJavaClass(kclass);

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

    private static void assertHasKotlinReflect() {
        if (!KotlinScreen.canProbe()) {
            var msg = Formatter
                    .of(
                        "Library org.jetbrains.kotlin:kotlin-reflect required to verify this class. Please add it to your project.");
            Assert.fail(msg);
        }
    }
}
