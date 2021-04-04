package inkapplications.shade.scenes

import inkapplications.shade.auth.TokenStorage
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*

/**
 * Constructs scenes services.
 */
class ShadeScenesModule {
    /**
     * Create new instance of the Scenes service.
     *
     * @param baseUrl URL of the Hue Bridge API
     * @param client Client to create hue requests with
     * @param tokenStorage A place to read/write the auth token used for requests.
     */
    fun createScenes(baseUrl: String, tokenStorage: TokenStorage): ShadeScenes {
        val httpClient = HttpClient {
            defaultRequest {
                url("$baseUrl${url.encodedPath.trimStart('/')}")
            }
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }
        val api = KtorHueScenesApi(httpClient)

        return ApiScenes(api, tokenStorage)
    }

    @Deprecated(
        message = "OkHttp Client parameter has been removed.",
        replaceWith = ReplaceWith("createGroups(baseUrl, tokenStorage)"),
        level = DeprecationLevel.ERROR,
    )
    fun createScenes(baseUrl: String, client: Any, tokenStorage: TokenStorage): ShadeScenes {
        return createScenes(baseUrl, tokenStorage)
    }
}
