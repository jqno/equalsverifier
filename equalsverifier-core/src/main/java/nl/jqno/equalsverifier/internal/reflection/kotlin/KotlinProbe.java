package nl.jqno.equalsverifier.internal.reflection.kotlin;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import kotlin.LazyKt;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.*;
import kotlin.reflect.full.KClasses;
import kotlin.reflect.jvm.ReflectJvmMapping;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

public final class KotlinProbe {
    private KotlinProbe() {}

    public static <T> kotlin.Lazy<T> lazy(T value) {
        return LazyKt.lazyOf(value);
    }

    public static String getKotlinPropertyNameFor(Field field) {
        Class<?> declaringClass = field.getDeclaringClass();
        KClass<?> kClass = JvmClassMappingKt.getKotlinClass(declaringClass);
        for (KProperty<?> prop : KClasses.getMemberProperties(kClass)) {
            Field backing = ReflectJvmMapping.getJavaField(prop);
            if (backing != null && backing.equals(field)) {
                return prop.getName();
            }
        }
        return null;
    }

    public static TypeTag determineGenerics(Class<?> container, Field field) {
        KClass<?> kType = JvmClassMappingKt.getKotlinClass(container);
        String kFieldName = getKotlinPropertyNameFor(field);
        KCallable<?> kField = kType.getMembers().stream().filter(m -> kFieldName.equals(m.getName())).findAny().get();
        KType kReturnType = kField.getReturnType();

        TypeTag tag = createTypeTag(kReturnType);
        return field.getType().equals(KotlinScreen.LAZY) ? new TypeTag(KotlinScreen.LAZY, tag) : tag;
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
