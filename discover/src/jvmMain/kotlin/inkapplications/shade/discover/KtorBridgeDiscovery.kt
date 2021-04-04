package inkapplications.shade.discover

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

internal class KtorBridgeDiscovery(
    private val client: HttpClient
): BridgeDiscovery {
    override suspend fun getDevices(): List<Device> {
        return client.get("/") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
        }
    }
}
