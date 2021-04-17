package inkapplications.shade.schedules

import inkapplications.shade.constructs.HueResponse
import inkapplications.shade.constructs.HueResult
import inkapplications.shade.constructs.IdToken
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonObject

internal class KtorHueSchedulesApi(
    private val client: HttpClient,
): HueSchedulesApi {
    override suspend fun getSchedules(token: String): Map<String, Schedule> {
        return client.get("api/$token/schedules") {
            accept(ContentType.Application.Json)
        }
    }

    override suspend fun createSchedule(token: String, schedule: ScheduleCreation): List<HueResult<IdToken>> {
        return client.post("api/$token/schedules") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
        }
    }

    override suspend fun updateSchedule(token: String, schedule: String, modification: ScheduleModification): HueResponse<JsonObject> {
        return client.put("api/$token/schedules/$schedule") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
        }
    }

    override suspend fun getSchedule(token: String, schedule: String): Schedule {
        return client.get("api/$token/schedules/$schedule") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
        }
    }

    override suspend fun deleteSchedule(token: String, schedule: String): HueResponse<String> {
        return client.delete("api/$token/schedules/$schedule") {
            accept(ContentType.Application.Json)
        }
    }
}
