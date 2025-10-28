package nl.jqno.equalsverifier.kotlin

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.kotlin.delegates.*
import nl.jqno.equalsverifier_testhelpers.ExpectedException
import org.junit.jupiter.api.Test

class KotlinDelegationTest {

  /*
   * No delegation
   */

  @Test
  fun `succeed when class is normal (sanity test)`() {
    EqualsVerifier.forClass(Normal::class.java).verify()
  }


  /*
   * Interface delegation
   */

  @Test
  fun `succeed when class uses interface delegation`() {
    EqualsVerifier.forClass(InterfaceDelegation::class.java).verify()
  }

  @Test
  fun `succeed when class uses interface delegation with two explicit fields`() {
    EqualsVerifier.forClass(TwoFoosDelegationWithExplicitFields::class.java).verify()
  }

  @Test
  fun `succeed when class uses interface delegation with two fields supplied as a parameter to a class`() {
    EqualsVerifier.forClass(TwoFoosDelegationWithParamToClass::class.java).verify()
  }

  @Test
  fun `succeed when class uses interface delegation with two fields supplied as a field to a class`() {
    EqualsVerifier.forClass(TwoFoosDelegationWithFieldToClass::class.java).verify()
  }

  @Test
  fun `fail when class uses interface delegation with two fields supplied as a parameter to an interface`() {
    ExpectedException
      .`when` { EqualsVerifier.forClass(TwoFoosDelegationWithParamToInterface::class.java).verify() }
      .assertFailure()
      .assertMessageContains("Abstract delegation", "Add prefab values for ${TwoFoos::class.java.getName()}")
  }

  @Test
  fun `fail when class uses interface delegation with two fields supplied as a field to an interface`() {
    ExpectedException
      .`when` { EqualsVerifier.forClass(TwoFoosDelegationWithFieldToInterface::class.java).verify() }
      .assertFailure()
      .assertMessageContains("Abstract delegation", "Add prefab values for ${TwoFoos::class.java.getName()}")
  }

  @Test
  fun `succeed when class uses interface delegation with two fields supplied as a parameter to an interface with prefab values`() {
    EqualsVerifier.forClass(TwoFoosDelegationWithParamToInterface::class.java)
      .withPrefabValues(TwoFoos::class.java, TwoFoosImpl(1, "a"), TwoFoosImpl(2, "b"))
      .verify()
  }

  @Test
  fun `succeed when class uses interface delegation with two fields supplied as a field to an interface with prefab values`() {
    EqualsVerifier.forClass(TwoFoosDelegationWithFieldToInterface::class.java)
      .withPrefabValues(TwoFoos::class.java, TwoFoosImpl(1, "a"), TwoFoosImpl(2, "b"))
      .verify()
  }

  @Test
  fun `use cryptic variable name when class using interface delegation fails`() {
    ExpectedException
      .`when` { EqualsVerifier.forClass(IncorrectInterfaceDelegation::class.java).verify() }
      .assertFailure()
      .assertMessageContains("\$\$delegate_0")
  }


  /*
   * Lazy delegation
   */

  @Test
  fun `succeed when class uses lazy delegate`() {
    EqualsVerifier.forClass(LazyDelegation::class.java).verify()
  }

  @Test
  fun `succeed when class uses lazy delegate and has prefab values`() {
    EqualsVerifier.forClass(LazyDelegation::class.java).withPrefabValues(Lazy::class.java, lazy { 1 }, lazy { 2 })
      .verify()
  }

  @Test
  fun `succeed when class uses lazy delegate with generic value`() {
    EqualsVerifier.forClass(LazyDelegationWithGenericValue::class.java).verify()
  }

  @Test
  fun `succeed when class uses lazy delegate with nested lazy value`() {
    EqualsVerifier.forClass(LazyDelegationWithNestedGenericValue::class.java).verify()
  }

  @Test
  fun `succeed when class is generic and uses that generic in its value`() {
    EqualsVerifier.forClass(LazyDelegationWithGenericClass::class.java).verify()
  }

  @Test
  fun `succeed when class is generic with bound and uses that generic in its value`() {
    EqualsVerifier.forClass(LazyDelegationWithBoundedGenericClass::class.java).verify()
  }

  @Test
  fun `succeed when class uses two lazy delegates`() {
    EqualsVerifier.forClass(TwoLazyDelegations::class.java).verify()
  }

  @Test
  fun `succeed when class uses two lazy delegates and has generic prefab values`() {
    EqualsVerifier.forClass(TwoLazyDelegations::class.java).withGenericPrefabValues(Lazy::class.java, { lazy { it } })
      .verify()
  }


  /*
   * Other kinds of delegation
   */

  @Test
  fun `succeed when class uses top level delegation`() {
    EqualsVerifier.forClass(TopLevelDelegation::class.java).verify()
  }

  @Test
  fun `succeed when class uses member delegation`() {
    EqualsVerifier.forClass(MemberDelegation::class.java).verify()
  }

  @Test
  fun `succeed when class uses delegation from another class`() {
    EqualsVerifier.forClass(AnotherClassDelegation::class.java).verify()
  }

  @Test
  fun `succeed when class uses delegation for 2 fields from another class`() {
    EqualsVerifier.forClass(DoubleOtherClassDelegation::class.java).verify()
  }

  @Test
  fun `succeed when class uses map delegation`() {
    EqualsVerifier.forClass(MapDelegation::class.java)
      .withPrefabValues(Map::class.java, mapOf("foo" to "a"), mapOf("foo" to "b")).verify()
  }

  @Test
  fun `succeed when class uses reflection delegation`() {
    EqualsVerifier.forClass(ReflectionDelegation::class.java).verify()
  }
}
