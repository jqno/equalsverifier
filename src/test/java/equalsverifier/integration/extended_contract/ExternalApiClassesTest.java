package equalsverifier.integration.extended_contract;

import com.google.common.base.Optional;
import com.google.common.collect.*;
import equalsverifier.EqualsVerifier;
import org.joda.time.*;
import org.junit.Test;

import javax.naming.Reference;
import java.math.BigDecimal;

import static equalsverifier.testhelpers.Util.defaultEquals;
import static equalsverifier.testhelpers.Util.defaultHashCode;

public class ExternalApiClassesTest {
    @Test
    public void succeed_whenClassUsesJavaxClasses() {
        EqualsVerifier.forClass(JavaxContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenClassUsesGoogleGuavaMultiset() {
        EqualsVerifier.forClass(GuavaMultisetContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenClassUsesGoogleGuavaMultimap() {
        EqualsVerifier.forClass(GuavaMultimapContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenClassUsesGoogleGuavaBiMap() {
        EqualsVerifier.forClass(GuavaBiMapContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenClassUsesGoogleGuavaTable() {
        EqualsVerifier.forClass(GuavaTableContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenClassUsesGoogleGuavaImmutableCollection() {
        EqualsVerifier.forClass(GuavaImmutableContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenClassUsesGoogleGuavaRegularCollection() {
        EqualsVerifier.forClass(GuavaRegularCollectionsContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenClassUsesOtherGoogleGuavaClass() {
        EqualsVerifier.forClass(GuavaOtherContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenClassUsesJodaTimeClass() {
        EqualsVerifier.forClass(JodaTimeContainer.class)
                .verify();
    }

    @SuppressWarnings("unused")  //because of the use of defaultEquals and defaultHashCode
    static final class JavaxContainer {
        private final Reference ref;

        public JavaxContainer(Reference ref) {
            this.ref = ref;
        }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }

    @SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
    static final class GuavaMultisetContainer {
        private final Multiset<?> multiset;
        private final SortedMultiset<?> sortedMultiset;
        private final HashMultiset<?> hashMultiset;
        private final TreeMultiset<?> treeMultiset;
        private final LinkedHashMultiset<?> linkedHashMultiset;
        private final ConcurrentHashMultiset<?> concurrentHashMultiset;
        private final EnumMultiset<?> enumMultiset;
        private final ImmutableMultiset<?> immutableMultiset;
        private final ImmutableSortedMultiset<?> immutableSortedMultiset;

     //    CHECKSTYLE: ignore ParameterNumber for 1 line.
        public GuavaMultisetContainer(Multiset<?> multiset, SortedMultiset<?> sortedMultiset, HashMultiset<?> hashMultiset,
                TreeMultiset<?> treeMultiset, LinkedHashMultiset<?> linkedHashMultiset, ConcurrentHashMultiset<?> concurrentHashMultiset,
                EnumMultiset<?> enumMultiset, ImmutableMultiset<?> immutableMultiset, ImmutableSortedMultiset<?> immutableSortedMultiset) {
            this.multiset = multiset; this.sortedMultiset = sortedMultiset; this.hashMultiset = hashMultiset;
            this.treeMultiset = treeMultiset; this.linkedHashMultiset = linkedHashMultiset; this.concurrentHashMultiset = concurrentHashMultiset;
            this.enumMultiset = enumMultiset; this.immutableMultiset = immutableMultiset; this.immutableSortedMultiset = immutableSortedMultiset;
        }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }

    @SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
    static final class GuavaMultimapContainer {
        private final Multimap<?, ?> multimap;
        private final ListMultimap<?, ?> listMultimap;
        private final SetMultimap<?, ?> setMultimap;
        private final SortedSetMultimap<?, ?> sortedSetMultimap;
        private final ArrayListMultimap<?, ?> arrayListMultimap;
        private final HashMultimap<?, ?> hashMultimap;
        private final LinkedListMultimap<?, ?> linkedListMultimap;
        private final LinkedHashMultimap<?, ?> linkedHashMultimap;
        private final TreeMultimap<?, ?> treeMultimap;
        private final ImmutableMultimap<?, ?> immutableMultimap;
        private final ImmutableListMultimap<?, ?> immutableListMultimap;
        private final ImmutableSetMultimap<?, ?> immutableSetMultimap;

        // CHECKSTYLE: ignore ParameterNumber for 1 line.
        public GuavaMultimapContainer(Multimap<?, ?> multimap, ListMultimap<?, ?> listMultimap, SetMultimap<?, ?> setMultimap,
                SortedSetMultimap<?, ?> sortedSetMultimap, ArrayListMultimap<?, ?> arrayListMultimap, HashMultimap<?, ?> hashMultimap,
                LinkedListMultimap<?, ?> linkedListMultimap, LinkedHashMultimap<?, ?> linkedHashMultimap, TreeMultimap<?, ?> treeMultimap,
                ImmutableMultimap<?, ?> immutableMultimap, ImmutableListMultimap<?, ?> immutableListMultimap,
                ImmutableSetMultimap<?, ?> immutableSetMultimap) {
            this.multimap = multimap; this.listMultimap = listMultimap; this.setMultimap = setMultimap;
            this.sortedSetMultimap = sortedSetMultimap; this.arrayListMultimap = arrayListMultimap; this.hashMultimap = hashMultimap;
            this.linkedListMultimap = linkedListMultimap; this.linkedHashMultimap = linkedHashMultimap; this.treeMultimap = treeMultimap;
            this.immutableMultimap = immutableMultimap; this.immutableListMultimap = immutableListMultimap;
            this.immutableSetMultimap = immutableSetMultimap;
        }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }

    @SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
    static final class GuavaBiMapContainer {
        private final BiMap<?, ?> biMap;
        private final HashBiMap<?, ?> hashBiMap;
        private final EnumBiMap<?, ?> enumBiMap;
        private final EnumHashBiMap<?, ?> enumHashBiMap;
        private final ImmutableBiMap<?, ?> immutableBiMap;

        public GuavaBiMapContainer(BiMap<?, ?> biMap, HashBiMap<?, ?> hashBiMap, EnumBiMap<?, ?> enumBiMap,
                EnumHashBiMap<?, ?> enumHashBiMap, ImmutableBiMap<?, ?> immutableBiMap) {
            this.biMap = biMap; this.hashBiMap = hashBiMap; this.enumBiMap = enumBiMap;
            this.enumHashBiMap = enumHashBiMap; this.immutableBiMap = immutableBiMap;
        }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }

    @SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
    static final class GuavaTableContainer {
        private final Table<?, ?, ?> table;
        private final HashBasedTable<?, ?, ?> hashBasedTable;
        private final TreeBasedTable<?, ?, ?> treeBasedTable;
        private final ArrayTable<?, ?, ?> arrayTable;
        private final ImmutableTable<?, ?, ?> immutableTable;

        public GuavaTableContainer(Table<?, ?, ?> table, HashBasedTable<?, ?, ?> hashBasedTable,
                TreeBasedTable<?, ?, ?> treeBasedTable, ArrayTable<?, ?, ?> arrayTable,
                ImmutableTable<?, ?, ?> immutableTable) {
            this.table = table;
            this.hashBasedTable = hashBasedTable;
            this.treeBasedTable = treeBasedTable;
            this.arrayTable = arrayTable;
            this.immutableTable = immutableTable;
        }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }

    @SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
    static final class GuavaRegularCollectionsContainer {
        private final EvictingQueue<?> evictingQueue;
        private final MinMaxPriorityQueue<?> minMaxPriorityQueue;
        private final RangeSet<Instant> rangeSet;
        private final ImmutableRangeSet<String> immutableRangeSet;
        private final TreeRangeSet<BigDecimal> treeRangeSet;

     //    CHECKSTYLE: ignore ParameterNumber for 1 line.
        public GuavaRegularCollectionsContainer(EvictingQueue<?> evictingQueue, MinMaxPriorityQueue<?> minMaxPriorityQueue,
                RangeSet<Instant> rangeSet, ImmutableRangeSet<String> immutableRangeSet, TreeRangeSet<BigDecimal> treeRangeSet) {
            this.evictingQueue = evictingQueue; this.minMaxPriorityQueue = minMaxPriorityQueue;
            this.rangeSet = rangeSet; this.immutableRangeSet = immutableRangeSet; this.treeRangeSet = treeRangeSet;
        }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }

    @SuppressWarnings("unused")  //because of the use of defaultEquals and defaultHashCode
    static final class GuavaImmutableContainer {
        private final ImmutableCollection<?> iCollection;
        private final ImmutableList<?> iList;
        private final ImmutableMap<?, ?> iMap;
        private final ImmutableSet<?> iSet;
        private final ImmutableSortedMap<?, ?> iSortedMap;
        private final ImmutableSortedSet<?> iSortedSet;

       //  CHECKSTYLE: ignore ParameterNumber for 1 line.
        public GuavaImmutableContainer(ImmutableCollection<?> immutableCollection, ImmutableList<?> immutableList,
                ImmutableMap<?, ?> immutableMap, ImmutableSet<?> immutableSet, ImmutableSortedMap<?, ?> iSortedMap,
                ImmutableSortedSet<?> iSortedSet) {
            this.iCollection = immutableCollection; this.iList = immutableList;
            this.iMap = immutableMap; this.iSet = immutableSet; this.iSortedMap = iSortedMap;
            this.iSortedSet = iSortedSet;
        }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }

    @SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
    static final class GuavaOtherContainer {
        private final Range<Integer> range;
        private final Optional<?> optional;

       //  CHECKSTYLE: ignore ParameterNumber for 1 line.
        public GuavaOtherContainer(Range<Integer> range, Optional<?> optional) {
            this.range = range; this.optional = optional;
        }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }

    @SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
    static final class JodaTimeContainer {
        private final LocalDate localDate;
        private final LocalTime localTime;
        private final LocalDateTime localDateTime;
        private final Chronology chronology;
        private final DateTimeZone dateTimeZone;
        private final Partial partial;
        private final PeriodType periodType;
        private final Period period;
        private final YearMonth yearMonth;
        private final MonthDay monthDay;

        // CHECKSTYLE: ignore ParameterNumber for 1 line.
        public JodaTimeContainer(LocalDate localDate, LocalTime localTime, LocalDateTime localDateTime, Chronology chronology,
                DateTimeZone dateTimeZone, Partial partial, PeriodType periodType, Period period, YearMonth yearMonth, MonthDay monthDay) {
            this.localDate = localDate; this.localTime = localTime; this.localDateTime = localDateTime; this.chronology = chronology;
            this.dateTimeZone = dateTimeZone; this.partial = partial; this.periodType = periodType; this.period = period;
            this.yearMonth = yearMonth; this.monthDay = monthDay;
        }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }
}
