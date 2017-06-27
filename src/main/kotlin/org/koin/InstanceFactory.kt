package org.koin

import org.koin.bean.BeanDefinition
import org.koin.bean.BeanType
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Logger
import kotlin.reflect.KClass

/**
 * Instance factory - handle objects instances from BeanRegistry
 * @author - Arnaud GIULIANI
 */
@Suppress("UNCHECKED_CAST")
class InstanceFactory {

    val logger: Logger = Logger.getLogger(InstanceFactory::class.java.simpleName)

    val instances = ConcurrentHashMap<KClass<*>, Any>()

    /**
     * Retrieve or create bean instance
     */
    fun <T> retrieveOrCreateInstance(clazz: KClass<*>, def: BeanDefinition<*>, saveInstance: Boolean = true): T {
        var instance = findExistingInstance<T>(clazz)
        if (instance == null) {
            instance = createInstance(def, clazz, saveInstance)
        }
        return instance!!
    }

    /**
     * Find existing instance
     */
    fun <T> findExistingInstance(clazz: KClass<*>): T? {
        val existingClass = instances.keys.filter { it == clazz }.firstOrNull()
        if (existingClass != null) {
            return instances[existingClass] as? T
        } else {
            return null
        }
    }

    /**
     * create instance for given bean definition
     */
    fun <T> createInstance(def: BeanDefinition<*>, clazz: KClass<*>, saveInstance: Boolean = true): T {
        logger.fine("create instance for $def")

        val instance = def.definition.invoke() as Any
        if (saveInstance) {
            instances[clazz] = instance
        }
        return instance as T
    }

    fun <T> resolveInstance(def: BeanDefinition<*>, clazz: KClass<*>): T {
        return when (def.type) {
            BeanType.FACTORY -> createInstance(def, clazz, false)
            BeanType.SINGLETON -> {
                retrieveOrCreateInstance<T>(clazz, def)
            }
        }
    }

    fun remove(vararg kClasses: KClass<*>) {
        kClasses.forEach { instances.remove(it) }
    }
}