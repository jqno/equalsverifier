package nl.jqno.equalsverifier;

import java.util.List;

import nl.jqno.instantiator.Instantiator;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;


public class InboxTest {
	@Test
	public void seen1() {
		EqualsVerifier.forClass(StefanA.class).verify();
	}
	
	@Test
	public void seen2() {
		Instantiator<StefanA> i = Instantiator.forClass(StefanA.class);
		StefanA stefanA = i.instantiate();
		i.scramble(stefanA);
		i.scramble(stefanA);
	}
	
	@Test
	public void immutableList() {
		EqualsVerifier.forClass(ImmutableSetContainer.class).verify();
	}
	
	@Test
	public void immutableList2() {
		Instantiator<ImmutableSetContainer> i = Instantiator.forClass(ImmutableSetContainer.class);
		ImmutableSetContainer c = i.instantiate();
		i.scramble(c);
		i.scramble(c);
	}
	
	@Test
	public void object() {
		EqualsVerifier.forClass(Object.class).verify();
	}
	
	@Test
	public void testEnum() {
		EqualsVerifier.forClass(E.class).verify();
	}
	
	@Test
	public void bennoA() {
		EqualsVerifier.forClass(BennoA.class)
				.with(Feature.FIELDS_ARE_NEVER_NULL)
				.verify();
	}
	
	@Test
	public void bennoB() {
		EqualsVerifier.forClass(BennoB.class)
				.with(Feature.FIELDS_ARE_NEVER_NULL)
				.verify();
	}
	
	enum E { ONE, TWO }
	
	static final class ImmutableSetContainer {
		private final ImmutableSet<String> set = ImmutableSet.of();
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ImmutableSetContainer)) {
				return false;
			}
			ImmutableSetContainer other = (ImmutableSetContainer)obj;
			return set == null ? other.set == null : set.equals(other.set);
		}
		
		@Override
		public int hashCode() {
			return set == null ? 0 : set.hashCode();
		}
	}

	public final class BennoA {
		private final BennoB b;
		
		public BennoA(BennoB b) {
			if (b == null) {
				throw new NullPointerException("b");
			}

			this.b = b;
		}
		
		@Override 
		public int hashCode() {
			return b.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			return (obj instanceof BennoA) && ((BennoA) obj).b.equals(b);
		}
	}

	final class BennoB {
		private final List<List<Integer>> listOfLists;
		
		public BennoB(List<List<Integer>> listOfLists) {
			if (listOfLists == null) {
				throw new NullPointerException("listOfLists");
			}

			this.listOfLists = listOfLists;
		}

		@Override
		public int hashCode() {
			return listOfLists.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return (obj instanceof BennoB) && listOfLists.equals(((BennoB) obj).listOfLists);
		}
	}
	
	static final class StefanA {
		private final StefanB b;
		private final StefanC c;
		
		public StefanA(StefanB b, StefanC c) {
			this.b = b;
			this.c = c;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof StefanA)) {
				return false;
			}
			StefanA other = (StefanA)obj;
			return (b == null ? other.b == null : b.equals(other.b)) &&
					(c == null ? other.c == null : c.equals(other.c));
		}
		
		@Override
		public int hashCode() {
			int result = b == null ? 0 : b.hashCode();
			result += 31 * (c == null ? 0 : c.hashCode());
			return result;
		}
	}
	
	static final class StefanB {
		private final StefanD d;
		
		public StefanB(StefanD d) {
			this.d = d;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof StefanB)) {
				return false;
			}
			StefanB other = (StefanB)obj;
			return d == null ? other.d == null : d.equals(other.d);
		}
		
		@Override
		public int hashCode() {
			return d == null ? 0 : d.hashCode();
		}
	}
	
	static final class StefanC {
		private final StefanD d;
		
		public StefanC(StefanD d) {
			this.d = d;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof StefanC)) {
				return false;
			}
			StefanC other = (StefanC)obj;
			return d == null ? other.d == null : d.equals(other.d);
		}
		
		@Override
		public int hashCode() {
			return d == null ? 0 : d.hashCode();
		}
	}
	
	static final class StefanD {}
}
