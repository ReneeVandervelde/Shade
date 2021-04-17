package inkapplications.shade.scenes

import inkapplications.shade.constructs.HueResponse
import inkapplications.shade.constructs.HueResult
import inkapplications.shade.constructs.IdToken
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonObject

internal class KtorHueScenesApi(
    private val client: HttpClient,
): HueScenesApi {
    override suspend fun getScenes(token: String): Map<String, Scene> {
        return client.get("api/$token/scenes") {
            accept(ContentType.Application.Json)
        }
    }

    override suspend fun createScene(token: String, scene: CreateScene): List<HueResult<IdToken>> {
        return client.post("api/$token/scenes") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            body = scene
        }
    }

    override suspend fun getScene(token: String, sceneId: String): Scene {
        return client.get("api/$token/scenes/$sceneId") {
            accept(ContentType.Application.Json)
        }
    }

    override suspend fun deleteScene(token: String, sceneId: String): HueResponse<String> {
        return client.delete("api/$token/scenes/$sceneId") {
            accept(ContentType.Application.Json)
        }
    }

    override suspend fun updateScene(token: String, sceneId: String, scene: UpdateScene.LightScene): HueResponse<JsonObject> {
        return client.put("api/$token/scenes/$sceneId") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            body = scene
        }
    }

    override suspend fun updateScene(token: String, sceneId: String, scene: UpdateScene.GroupScene): HueResponse<JsonObject> {
        return client.put("api/$token/scenes/$sceneId") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            body = scene
        }
    }
}
