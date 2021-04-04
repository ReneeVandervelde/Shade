package inkapplications.shade.groups

import inkapplications.shade.auth.TokenStorage
import inkapplications.shade.lights.ShadeLightsModule
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlin.time.ExperimentalTime

/**
 * Constructs Groups services.
 */
@ExperimentalTime
class ShadeGroupsModule() {
    @Deprecated(
        message = "Lights Module reference no longer used",
        replaceWith = ReplaceWith("ShadeGroupsModule()"),
        level = DeprecationLevel.ERROR,
    )
    constructor(lightsModule: ShadeLightsModule): this()

    /**
     * Create new instance of the Groups services.
     *
     * @param baseUrl URL of the Hue Bridge API
     * @param tokenStorage A place to read/write the auth token used for requests.
     */
    fun createGroups(baseUrl: String, tokenStorage: TokenStorage): ShadeGroups {
        val httpClient = HttpClient {
            defaultRequest {
                url("$baseUrl${url.encodedPath.trimStart('/')}")
            }
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }
        val api = KtorHueGroupsApi(httpClient)

        return ApiGroups(api, tokenStorage)
    }

    @Deprecated(
        message = "OkHttp Client parameter has been removed.",
        replaceWith = ReplaceWith("createGroups(baseUrl, tokenStorage)"),
        level = DeprecationLevel.ERROR,
    )
    fun createGroups(baseUrl: String, client: Any, tokenStorage: TokenStorage): ShadeGroups {
        return createGroups(baseUrl, tokenStorage)
    }
}
