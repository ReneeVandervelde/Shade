package inkapplications.shade.internals

import inkapplications.shade.serialization.HueResponse
import inkapplications.shade.serialization.V1HueResponse
import inkapplications.shade.structures.*
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

internal class OkHttpHueClient(
    private val configurationContainer: HueConfigurationContainer,
    private val okHttp: OkHttpClient,
    private val json: Json,
    private val logger: KimchiLogger,
): HueHttpClient {
    override suspend fun <REQUEST, RESPONSE> sendRequest(
        method: String,
        pathSegments: Array<out String>,
        responseSerializer: KSerializer<HueResponse<RESPONSE>>,
        body: REQUEST?,
        requestSerializer: KSerializer<REQUEST>?
    ): RESPONSE {
        val request = createRequest(
            method = method,
            pathSegments = BaseUrl.v2(*pathSegments),
            body = body?.let { json.encodeToString(requestSerializer!!, it) },
        )
        val response = suspendCancellableCoroutine { context ->
            val response = runCatching { okHttp.newCall(request).also { logger.debug("Call created, executing") }.execute() }
            context.resumeWith(response)
        }

        return responseSerializer.parseResponse(response)
    }

    override suspend fun <REQUEST, RESPONSE> sendV1Request(
        method: String,
        pathSegments: Array<out String>,
        responseSerializer: KSerializer<List<V1HueResponse<RESPONSE>>>,
        body: REQUEST?,
        requestSerializer: KSerializer<REQUEST>?
    ): RESPONSE {
        TODO("Not yet implemented")
    }

    private fun createRequest(
        method: String,
        pathSegments: Array<out String>,
        body: String?,
    ): Request {
        val hostName = configurationContainer.hostname.value ?: throw HostnameNotSetException
        val url = HttpUrl.Builder()
            .host(hostName)
            .scheme("https")
            .apply {
                pathSegments.forEach(::addPathSegment)
            }
            .build()
        val requestBody = body?.toRequestBody()
        return Request.Builder()
            .url(url)
            .header("Accept", "application/json")
            .apply {
                configurationContainer.authToken.value?.run {
                    logger.debug("Attaching Application key to request")
                    header("hue-application-key", applicationKey)
                }
            }
            .method(method, requestBody)
            .build()
            .also { logger.debug("Created request: $it")}
    }

    private suspend fun <T> KSerializer<HueResponse<T>>.parseResponse(httpResponse: Response): T {
        val bodyText = httpResponse.body?.string().orEmpty()
        val response = try {
            json.decodeFromString(this, bodyText)
        } catch (e: Throwable) {
            throw SerializationError("Error thrown while deserializing response body.", e)
        }
        logger.debug("Decoded response: $response")
        when {
            response is HueResponse.Error -> throw ApiError(
                code = httpResponse.code,
                errors = response.errors.map { it.description }
            )
            !httpResponse.isSuccessful -> throw ApiStatusError(
                code = httpResponse.code,
            )
            response is HueResponse.Success -> return response.data
            else -> throw UnexpectedStateException("Unhandled response")
        }
    }
}
