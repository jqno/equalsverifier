package nl.jqno.equalsverifier.integration.extended_contract;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedMultiset;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Range;

public class ExternalApiClassesTest {
	@Test
	public void succeed_whenClassUsesGoogleGuavaClass() {
		EqualsVerifier.forClass(GuavaContainer.class)
				.verify();
	}
	
	@SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
	static final class GuavaContainer {
		private final ImmutableList<?> iList;
		private final ImmutableMap<?, ?> iMap;
		private final ImmutableSet<?> iSet;
		private final ImmutableSortedMap<?, ?> iSortedMap;
		private final ImmutableSortedSet<?> iSortedSet;
		private final ImmutableMultiset<?> iMultiset;
		private final ImmutableSortedMultiset<?> iSortedMultiset;
		private final ImmutableListMultimap<?, ?> iListMultimap;
		private final ImmutableSetMultimap<?, ?> iSetMultimap;
		private final ImmutableBiMap<?, ?> iBiMap;
		private final ImmutableTable<?, ?, ?> iTable;
		private final Range<?> range;
		private final Optional<?> optional;
		
		public GuavaContainer(ImmutableList<?> immutableList, ImmutableMap<?, ?> immutableMap, ImmutableSet<?> immutableSet, ImmutableSortedMap<?, ?> iSortedMap, ImmutableSortedSet<?> iSortedSet, ImmutableMultiset<?> iMultiset, ImmutableSortedMultiset<?> iSortedMultiset, ImmutableListMultimap<?, ?> iListMultimap, ImmutableSetMultimap<?, ?> iSetMultimap, ImmutableBiMap<?, ?> immutableBiMap, ImmutableTable<?, ?, ?> iTable, Range<?> range, Optional<?> optional)
			{ this.iList = immutableList; this.iMap = immutableMap; this.iSet = immutableSet; this.iSortedMap = iSortedMap; this.iSortedSet = iSortedSet; this.iMultiset = iMultiset; this.iSortedMultiset = iSortedMultiset; this.iListMultimap = iListMultimap; this.iSetMultimap = iSetMultimap; this.iBiMap = immutableBiMap; this.iTable = iTable; this.range = range; this.optional = optional; }
		
		@Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
		@Override public int hashCode() { return defaultHashCode(this); }
	}
}
