package io.ktor.client.utils

import io.ktor.http.*

/**
 * List of [CacheControl] known values.
 */
object CacheControl {
    val MAX_AGE = "max-age"
    val MIN_FRESH = "min-fresh"
    val ONLY_IF_CACHED = "only-if-cached"

    val MAX_STALE = "max-stale"
    val NO_CACHE = "no-cache"
    val NO_STORE = "no-store"
    val NO_TRANSFORM = "no-transform"

    val MUST_REVALIDATE = "must-revalidate"
    val PUBLIC = "private"
    val PRIVATE = "private"
    val PROXY_REVALIDATE = "proxy-revalidate"
    val S_MAX_AGE = "s-maxage"
}

/**
 * Returns a list of `Cache-Control` headers as strings.
 */
fun Headers.cacheControl(): List<String> = getAll(HttpHeaders.CacheControl) ?: listOf()

/**
 * Try to get the [CacheControl.MAX_AGE] in seconds or null if not available.
 */
fun Headers.maxAge(): Int? = cacheControl(CacheControl.MAX_AGE)

/**
 * Returns whether the [CacheControl.ONLY_IF_CACHED] is present or not.
 */
fun Headers.onlyIfCached(): Boolean = cacheControl(CacheControl.ONLY_IF_CACHED) ?: false

/**
 * Appends a [CacheControl.MAX_AGE] with the number of seconds specified by [value].
 */
fun HeadersBuilder.maxAge(value: Int) = append(HttpHeaders.CacheControl, "${CacheControl.MAX_AGE}=$value")

/**
 * Returns the specific [CacheControl] either as Int or Boolean or null.
 */
inline fun <reified T> Headers.cacheControl(key: String): T? = when (T::class) {
    Int::class -> cacheControl().intProperty(key) as T?
    Boolean::class -> cacheControl().booleanProperty(key) as T?
    else -> null
}

/**
 * Private: From a list of cacheControl(), check if a [key] is present.
 */
fun List<String>.booleanProperty(key: String): Boolean = contains(key)

/**
 * Private: From a list of cacheControl(), get the value associated to [key] as [Int] or null.
 */
fun List<String>.intProperty(key: String): Int? =
        find { it.startsWith(key) }?.split("=")?.getOrNull(1)?.toInt()
