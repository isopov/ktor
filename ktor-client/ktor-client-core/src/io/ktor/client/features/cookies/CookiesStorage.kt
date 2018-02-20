package io.ktor.client.features.cookies

import io.ktor.http.*
import java.util.*
import java.util.concurrent.*

/**
 * Storage for [Cookie].
 */
interface CookiesStorage {
    /**
     * Gets a map of [String] to [Cookie] for a specific [host].
     */
    operator fun get(host: String): Map<String, Cookie>?

    /**
     * Try to get a [Cookie] with the specified cookie's [name] for a [host].
     */
    operator fun get(host: String, name: String): Cookie?

    /**
     * Sets a [cookie] for the specified [host].
     */
    fun addCookie(host: String, cookie: Cookie)

    /**
     * Runs a [block] of code, for all the cookies set in the specified [host].
     */
    fun forEach(host: String, block: (Cookie) -> Unit)
}

/**
 * [CookiesStorage] that stores all the cookies in an in-memory map.
 */
open class AcceptAllCookiesStorage : CookiesStorage {
    private val data = ConcurrentHashMap<String, MutableMap<String, Cookie>>()

    override fun get(host: String): Map<String, Cookie>? = Collections.unmodifiableMap(data[host])

    override operator fun get(host: String, name: String): Cookie? = data[host]?.get(name)

    override fun addCookie(host: String, cookie: Cookie) {
        init(host)
        data[host]?.set(cookie.name, cookie)
    }

    override fun forEach(host: String, block: (Cookie) -> Unit) {
        init(host)
        data[host]?.values?.forEach(block)
    }

    private fun init(host: String) {
        if (!data.containsKey(host)) {
            data[host] = mutableMapOf()
        }
    }
}

/**
 * [CookiesStorage] that ignores [addCookie] and returns a list of specified [cookies] when constructed.
 */
class ConstantCookieStorage(vararg cookies: Cookie) : CookiesStorage {
    private val storage: Map<String, Cookie> = cookies.map { it.name to it }.toMap()

    override fun get(host: String): Map<String, Cookie>? = storage

    override fun get(host: String, name: String): Cookie? = storage[name]

    override fun addCookie(host: String, cookie: Cookie) {}

    override fun forEach(host: String, block: (Cookie) -> Unit) {
        storage.values.forEach(block)
    }
}
