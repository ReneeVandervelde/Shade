package inkapplications.shade.groups

import inkapplications.shade.constructs.HueProperties
import inkapplications.shade.constructs.HueResponse
import inkapplications.shade.constructs.IdToken
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
internal class KtorHueGroupsApi(
    private val client: HttpClient,
): HueGroupsApi {
    override suspend fun getAll(token: String): Map<String, Group> {
        return client.get("api/$token/groups") {
            accept(ContentType.Application.Json)
        }
    }

    override suspend fun createGroup(token: String, group: MutableGroupAttributes): IdToken {
        return client.post("api/$token/groups") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            body = group
        }
    }

    override suspend fun getGroup(token: String, groupId: String): Group {
        return client.get("api/$token/groups/$groupId") {
            accept(ContentType.Application.Json)
        }
    }

    override suspend fun updateGroup(token: String, groupId: String, attributes: MutableGroupAttributes): HueResponse<HueProperties> {
        return client.put("api/$token/groups/$groupId") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            body = attributes
        }
    }

    override suspend fun setState(token: String, groupId: String, state: GroupStateModification): HueResponse<HueProperties> {
        return client.put(createHueGroupsStateUrl(token, groupId)) {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            body = state
        }
    }

    override suspend fun deleteGroup(token: String, groupId: String): HueResponse<String> {
        return client.delete("api/$token/groups/$groupId") {
            accept(ContentType.Application.Json)
        }
    }
}
