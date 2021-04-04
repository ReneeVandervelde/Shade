package inkapplications.shade.schedules

import inkapplications.shade.auth.TokenStorage
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlin.time.ExperimentalTime

/**
 * Constructs Schedule Services
 */
@ExperimentalTime
class ShadeSchedulesModule {
    /**
     * Create a new instance of the schedules interface.
     *
     * @param baseUrl URL of the Hue Bridge API
     * @param tokenStorage A place to read/write the auth token used for requests.
     */
    fun createSchedule(baseUrl: String, tokenStorage: TokenStorage): ShadeSchedules {
        val httpClient = HttpClient {
            defaultRequest {
                url("$baseUrl${url.encodedPath.trimStart('/')}")
            }
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }
        val api = KtorHueSchedulesApi(httpClient)

        return ApiSchedules(api, tokenStorage)
    }

    @Deprecated(
        message = "OkHttp Client parameter has been removed.",
        replaceWith = ReplaceWith("createSchedule(baseUrl, tokenStorage)"),
        level = DeprecationLevel.ERROR,
    )
    fun createSchedule(baseUrl: String, client: Any, tokenStorage: TokenStorage): ShadeSchedules {
        return createSchedule(baseUrl, tokenStorage)
    }
}
