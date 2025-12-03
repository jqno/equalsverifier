package nl.jqno.equalsverifier.internal.reflection.kotlin

import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

object KotlinProbeAdditional {
  @JvmStatic
  fun getPrimaryConstructor(clazz: KClass<*>) = clazz.primaryConstructor
}
