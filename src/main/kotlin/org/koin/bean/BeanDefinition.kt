package org.koin.bean

import kotlin.reflect.KClass

/**
 * Bean definition
 * @author - Arnaud GIULIANI
 *
 * Gather type of T
 * definied by lazy/function
 * has a type (clazz)
 * has a BeanType : default singleton
 */
data class BeanDefinition<out T>(val definition: () -> T, val clazz: KClass<*>, val type: BeanType = BeanType.SINGLETON, val scope: KClass<*>? = null)