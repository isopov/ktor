package io.ktor.client.features

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.response.*
import io.ktor.http.*
import io.ktor.util.*

private val JUMPS = AttributeKey<Int>("HttpRedirectJumps")

class HttpRedirect(
        val maxJumps: Int
) {

    class Config {
        var maxJumps = Int.MAX_VALUE
    }

    companion object Feature : HttpClientFeature<Config, HttpRedirect> {
        override val key: AttributeKey<HttpRedirect> = AttributeKey("HttpRedirect")

        override fun prepare(block: Config.() -> Unit): HttpRedirect = HttpRedirect(Config().apply(block).maxJumps)

        override fun install(feature: HttpRedirect, scope: HttpClient) {
            scope.responsePipeline.intercept(HttpResponsePipeline.Receive) { (_, response) ->
                if (response !is HttpResponse || !response.status.isRedirect()) return@intercept
                val jumps = response.call.request.attributes.getOrNull(JUMPS) ?: 0

                if (jumps == feature.maxJumps) return@intercept
                val location = response.headers[HttpHeaders.Location] ?: return@intercept

                val request = request {
                    takeFrom(response.call.request)
                }
            }
        }
    }
}

private fun HttpStatusCode.isRedirect(): Boolean = TODO()