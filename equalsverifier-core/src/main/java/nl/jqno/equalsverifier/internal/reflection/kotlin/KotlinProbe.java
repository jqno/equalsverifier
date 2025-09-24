package nl.jqno.equalsverifier.internal.reflection.kotlin;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.*;
import kotlin.reflect.full.KClasses;
import kotlin.reflect.jvm.ReflectJvmMapping;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

public final class KotlinProbe {
    private KotlinProbe() {}

    public static <T> Lazy<T> lazy(T value) {
        return LazyKt.lazyOf(value);
    }

    public static Optional<String> getKotlinPropertyNameFor(Field field) {
        return getKotlinPropertyFor(field).map(p -> p.getName());
    }

    public static Optional<KProperty<?>> getKotlinPropertyFor(Field field) {
        Class<?> declaringClass = field.getDeclaringClass();
        KClass<?> kClass = JvmClassMappingKt.getKotlinClass(declaringClass);
        for (KProperty<?> prop : KClasses.getMemberProperties(kClass)) {
            Field backing = ReflectJvmMapping.getJavaField(prop);
            if (backing != null && backing.equals(field)) {
                return Optional.of(prop);
            }
        }
        return Optional.empty();
    }

    public static Optional<TypeTag> determineType(Class<?> container, Field field) {
        KClass<?> kType = JvmClassMappingKt.getKotlinClass(container);
        Optional<String> optKFieldName = getKotlinPropertyNameFor(field);
        if (optKFieldName.isEmpty()) {
            return Optional.empty();
        }
        String kFieldName = optKFieldName.get();
        KCallable<?> kField = kType.getMembers().stream().filter(m -> kFieldName.equals(m.getName())).findAny().get();
        KType kReturnType = kField.getReturnType();

        TypeTag tag = createTypeTag(kReturnType);
        TypeTag result = field.getType().equals(KotlinScreen.LAZY) ? new TypeTag(KotlinScreen.LAZY, tag) : tag;
        return Optional.of(result);
    }

    private static TypeTag createTypeTag(KType kType) {
        Class<?> rawClass = kTypeToClass(kType);

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

    private static Class<?> kTypeToClass(KType ktype) {
        KClass<?> kclass = (KClass<?>) ktype.getClassifier();
        return JvmClassMappingKt.getJavaClass(kclass);
    }
}
