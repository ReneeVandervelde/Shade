package inkapplications.shade.auth

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*

/**
 * Constructs Auth resources.
 */
class ShadeAuthModule {
    /**
     * Create a new instance of Shade's Auth Interface.
     */
    fun createAuth(
        baseUrl: String,
        appId: String,
        tokenStorage: TokenStorage
    ): ShadeAuth {
        val httpClient = HttpClient {
            defaultRequest {
                url("$baseUrl${url.encodedPath.trimStart('/')}")
            }
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }
        val ktorApi = HueKtorApi(httpClient)

        return ApiAuth(ktorApi, appId, tokenStorage)
    }

    @Deprecated(
        message = "OkHttp has been removed from Shade",
        replaceWith = ReplaceWith("createAuth(baseUrl, appId, tokenStorage)"),
        level = DeprecationLevel.ERROR
    )
    fun createAuth(
        baseUrl: String,
        appId: String,
        client: Any,
        tokenStorage: TokenStorage
    ): ShadeAuth = throw NotImplementedError()
}
