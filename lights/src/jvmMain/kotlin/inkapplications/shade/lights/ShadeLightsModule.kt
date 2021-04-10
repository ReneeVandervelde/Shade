package inkapplications.shade.lights

import inkapplications.shade.auth.TokenStorage
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlin.time.ExperimentalTime

/**
 * Constructs lights services.
 */
@ExperimentalTime
class ShadeLightsModule {
    /**
     * Create a new instance of the lighting interface.
     *
     * @param baseUrl URL of the Hue Bridge API
     * @param tokenStorage A place to read/write the auth token used for requests.
     */
    fun createLights(baseUrl: String, tokenStorage: TokenStorage): ShadeLights {
        val httpClient = HttpClient {
            defaultRequest {
                url("$baseUrl${url.encodedPath.trimStart('/')}")
            }
            val json = kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            }
            install(JsonFeature) {
                serializer = KotlinxSerializer(json)
            }
        }
        val api = KtorHueLightsApi(httpClient)

        return ApiLights(api, tokenStorage)
    }

    @Deprecated(
        message = "OkHttp Client parameter has been removed.",
        replaceWith = ReplaceWith("createGroups(baseUrl, tokenStorage)"),
        level = DeprecationLevel.ERROR,
    )
    fun createLights(baseUrl: String, client: Any, tokenStorage: TokenStorage): ShadeLights {
        return createLights(baseUrl, tokenStorage)
    }
}
