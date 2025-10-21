package nl.jqno.equalsverifier.internal.instantiation;

import static nl.jqno.equalsverifier.internal.instantiation.vintage.factories.Factories.collection;
import static nl.jqno.equalsverifier.internal.instantiation.vintage.factories.Factories.simple;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.*;
import java.util.function.Supplier;

import nl.jqno.equalsverifier.internal.instantiation.vintage.FactoryCache;
import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.instantiation.vintage.factories.EnumMapFactory;
import nl.jqno.equalsverifier.internal.instantiation.vintage.factories.EnumSetFactory;
import nl.jqno.equalsverifier.internal.instantiation.vintage.factories.PrefabValueFactory;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.kotlin.KotlinLazy;
import nl.jqno.equalsverifier.internal.reflection.kotlin.KotlinScreen;
import nl.jqno.equalsverifier.internal.versionspecific.ScopedValuesHelper;
import nl.jqno.equalsverifier.internal.versionspecific.SequencedCollectionsHelper;

/**
 * Creates instances of classes for use in a {@link VintageValueProvider} object.
 *
 * <p>
 * Contains hand-made instances of well-known Java API classes that cannot be instantiated dynamically because of an
 * internal infinite recursion of types, or other issues.
 */
@SuppressWarnings("JdkObsolete")
public final class JavaApiPrefabValues {

    private final FactoryCache factoryCache;

    /** Private constructor. Use {@link #build()}. */
    private JavaApiPrefabValues(FactoryCache factoryCache) {
        this.factoryCache = factoryCache;
    }

    /**
     * Creates a FactoryCache pre-populated with instances of Java API classes that cannot be instantiated dynamically.
     *
     * @return A pre-populated {@link FactoryCache}.
     */
    public static FactoryCache build() {
        FactoryCache result = new FactoryCache();
        new JavaApiPrefabValues(result).addJavaClasses();
        return result;
    }

    private void addJavaClasses() {
        addNonCollectionClasses();
        addLists();
        addMaps();
        addSets();
        SequencedCollectionsHelper.add(factoryCache);
        ScopedValuesHelper.add(factoryCache);
        addAtomicClasses();
        addKotlinClasses();
    }

    private void addNonCollectionClasses() {
        addFactory(CompletableFuture.class, simple(ignored -> new CompletableFuture<>(), CompletableFuture::new));
        addFactory(Optional.class, simple(Optional::of, Optional::empty));
        addFactory(Supplier.class, simple(a -> () -> a, () -> () -> null));
        addFactory(ThreadLocal.class, simple(a -> ThreadLocal.withInitial(() -> a), null));
    }

    @SuppressWarnings("unchecked")
    private void addLists() {
        addFactory(Vector.class, collection(Vector::new)); // Keep this line until FallbackFactory no longer produces Arrays
    }

    @SuppressWarnings("unchecked")
    private void addMaps() {
        addFactory(EnumMap.class, new EnumMapFactory<>(EnumMap::new));
    }

    @SuppressWarnings("unchecked")
    private void addSets() {
        addFactory(EnumSet.class, new EnumSetFactory<>(c -> EnumSet.copyOf(c)));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void addAtomicClasses() {
        addFactory(AtomicMarkableReference.class, simple(r -> new AtomicMarkableReference(r, true), null));
        addFactory(AtomicReference.class, simple(AtomicReference::new, null));
        addFactory(AtomicStampedReference.class, simple(r -> new AtomicStampedReference(r, 0), null));
        addFactory(AtomicReferenceArray.class, (tag, pv, stack) -> {
            TypeTag genericTag = tag.genericTypes().get(0);
            Tuple<?> tup = pv.provideOrThrow(genericTag, Attributes.empty());
            return tup.map(v -> new AtomicReferenceArray<>(new Object[] { v }));
        });
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void addKotlinClasses() {
        if (KotlinScreen.LAZY != null) {
            addFactory(KotlinScreen.LAZY, (tag, pv, stack) -> {
                TypeTag genericTag = tag.genericTypes().get(0);
                Tuple tup = pv.provideOrThrow(genericTag, Attributes.empty());
                return tup.map(v -> KotlinLazy.lazy(v));
            });
        }
    }

    private <T> void addFactory(Class<T> type, PrefabValueFactory<T> factory) {
        factoryCache.put(type, factory);
    }
}
