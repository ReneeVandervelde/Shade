package inkapplications.shade.discover

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*

/**
 * Constructs Discovery services.
 */
class DiscoverModule {
    fun createDiscoveryClient(): BridgeDiscovery {
        return KtorBridgeDiscovery(
            client = HttpClient {
                defaultRequest {
                    url(
                        scheme = "https",
                        host = "discovery.meethue.com",
                    )
                }
                install(JsonFeature) {
                    serializer = KotlinxSerializer()
                }
            }
        )
    }

    @Deprecated(
        message = "OkHttp has been removed from Shade",
        replaceWith = ReplaceWith("createDiscoveryClient()"),
        level = DeprecationLevel.ERROR
    )
    fun createDiscoverclient(client: Any, apiUrl: String = ""): BridgeDiscovery = throw NotImplementedError()
}
