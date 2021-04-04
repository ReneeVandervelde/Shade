package inkapplications.shade.lights

import inkapplications.shade.constructs.DeviceAttributes
import inkapplications.shade.constructs.HueProperties
import inkapplications.shade.constructs.HueResponse
import inkapplications.shade.constructs.Scan
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
internal class KtorHueLightsApi(
    private val client: HttpClient,
): HueLightsApi {
    override suspend fun getLights(token: String): Map<String, Light> {
        return client.get("api/$token/lights") {
            accept(ContentType.Application.Json)
        }
    }

    override suspend fun setState(token: String, lightId: String, modification: LightStateModification): HueResponse<HueProperties> {
        return client.put(createHueLightsStatePath(token, lightId)) {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            body = modification
        }
    }

    override suspend fun getNewLights(token: String): Scan {
        return client.get("api/$token/lights/new") {
            accept(ContentType.Application.Json)
        }
    }

    override suspend fun searchLights(token: String, criteria: LightSearchCriteria): HueResponse<HueProperties> {
        return client.post("api/$token/lights") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            body = criteria
        }
    }

    override suspend fun searchLights(token: String): HueResponse<HueProperties> {
        return client.post("api/$token/lights") {
            accept(ContentType.Application.Json)
        }
    }

    override suspend fun getLightAttributes(token: String, lightId: String): Light {
        return client.get("api/$token/lights/$lightId") {
            accept(ContentType.Application.Json)
        }
    }

    override suspend fun setLightAttributes(token: String, lightId: String, attributes: DeviceAttributes): HueResponse<HueProperties> {
        return client.put("api/$token/lights/$lightId") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            body = attributes
        }
    }

    override suspend fun delete(token: String, lightId: String): HueResponse<String> {
        return client.delete("api/$token/lights/$lightId") {
            accept(ContentType.Application.Json)
        }
    }
}
