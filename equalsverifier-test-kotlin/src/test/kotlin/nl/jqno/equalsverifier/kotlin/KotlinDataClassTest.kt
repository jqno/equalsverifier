package nl.jqno.equalsverifier.kotlin

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

class KotlinDataClassTest {

  data class DataClassWithComputedProperty(val value: String) {
    val isEmpty = value.isEmpty()
  }

  @Test
  fun `properties not declared in primary constructor of data classes are ignored`() {
    EqualsVerifier.forClass(DataClassWithComputedProperty::class.java)
      .verify()
  }

  data class DataClassWithSecondaryConstructor(val value: String) {
    constructor(prefix: String, isEmpty: Boolean) : this(prefix)
    val isEmpty = value.isEmpty()
  }

  @Test
  fun `properties declared in secondary constructor of data classes are ignored`() {
    EqualsVerifier.forClass(DataClassWithSecondaryConstructor::class.java)
      .verify()
  }
}