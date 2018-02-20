package io.ktor.client

import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.response.*
import io.ktor.util.*
import java.io.*

/**
 * Asynchronous client to perform HTTP requests.
 *
 * This is a genering implementation that uses a specific engine [HttpClientEngine].
 */
class HttpClient private constructor(
        private val engine: HttpClientEngine,
        private val config: HttpClientConfig
) : Closeable {
    /**
     * Constructs an asynchronous [HttpClient] using the specified [engineFactory]
     * and an optional [block] for configuring this client.
     */
    constructor(engineFactory: HttpClientEngineFactory<*>, block: HttpClientConfig.() -> Unit = {})
            : this(engineFactory.create(), HttpClientConfig().apply(block))

    /**
     * Pipeline used for processing all the requests sent by this client.
     */
    val requestPipeline = HttpRequestPipeline()

    /**
     * Pipeline used for processing all the responses sent by the server.
     */
    val responsePipeline = HttpResponsePipeline()

    /**
     * Typed attributes used as a lightweight container for this client.
     */
    val attributes = Attributes()

    init {
        config.install(HttpPlainText)
        config.install(HttpIgnoreBody)

        config.install(this)
    }

    /**
     * Creates a new [HttpRequest] from a request [builder] and a specific client [call].
     */
    fun createRequest(builder: HttpRequestBuilder, call: HttpClientCall): HttpRequest =
            engine.prepareRequest(builder, call)

    /**
     * Returns a new [HttpClient] copying this client configuration,
     * and aditionally configured by the [block] parameter.
     */
    fun config(block: HttpClientConfig.() -> Unit): HttpClient = HttpClient(engine, config.clone().apply(block))

    /**
     * Closes the underlying [engine].
     */
    override fun close() {
        engine.close()
    }
}
