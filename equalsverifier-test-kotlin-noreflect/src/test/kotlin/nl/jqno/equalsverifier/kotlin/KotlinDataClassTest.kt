package nl.jqno.equalsverifier.kotlin

import nl.jqno.equalsverifier.EqualsVerifier
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class KotlinDataClassTest {

  data class DataClassWithComputedProperty(val value: String) {
    val isEmpty = value.isEmpty()
  }

  @Test
  fun `properties not declared in primary constructor of data classes are ignored`() {
    assertThatThrownBy {
        EqualsVerifier.forClass(DataClassWithComputedProperty::class.java)
          .verify()
      }.isInstanceOf(AssertionError::class.java)
      .hasMessageContaining("Significant fields: equals does not use isEmpty, or it is stateless.")
      .hasMessageContaining("Note: This is a Kotlin class. Import kotlin-reflect in the classpath to use the adapted EqualsVerifier implementation.")
  }

  data class DataClassWithSecondaryConstructor(val value: String) {
    constructor(prefix: String, isEmpty: Boolean) : this(prefix)
    val isEmpty = value.isEmpty()
  }

  @Test
  fun `properties declared in secondary constructor of data classes are ignored`() {
    assertThatThrownBy {
      EqualsVerifier.forClass(DataClassWithSecondaryConstructor::class.java)
        .verify()
    }.isInstanceOf(AssertionError::class.java)
      .hasMessageContaining("Significant fields: equals does not use isEmpty, or it is stateless.")
      .hasMessageContaining("Note: This is a Kotlin class. Import kotlin-reflect in the classpath to use the adapted EqualsVerifier implementation.")
  }
}
