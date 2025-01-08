package nl.jqno.equalsverifier.kotlin

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationCache
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationCacheBuilder
import nl.jqno.equalsverifier.internal.reflection.annotations.SupportedAnnotations
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KotlinCompilerGeneratedAnnotationTest {

  val cache = AnnotationCache()
  val cacheBuilder = AnnotationCacheBuilder(SupportedAnnotations.values(), HashSet())

  @Test
  fun `Kotlin classes are recognised as such`() {
    assertThat(checkAnnotationFor(KotlinClass::class.java)).isTrue()
  }

  @Test
  fun `Java classes are not recognised as Kotlin classes`() {
    assertThat(checkAnnotationFor(EqualsVerifier::class.java)).isFalse()
  }

  private fun checkAnnotationFor(type: Class<*>): Boolean {
    cacheBuilder.build(type, cache)
    return cache.hasClassAnnotation(type, SupportedAnnotations.KOTLIN)
  }
}

class KotlinClass
