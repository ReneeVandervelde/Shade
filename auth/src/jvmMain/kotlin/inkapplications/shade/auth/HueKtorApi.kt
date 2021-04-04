package inkapplications.shade.auth

import inkapplications.shade.constructs.HueResult
import inkapplications.shade.constructs.ShadeApiError
import inkapplications.shade.constructs.UnknownException
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

internal class HueKtorApi(
    private val client: HttpClient
): HueAuthApi {
    override suspend fun createToken(devicetype: DeviceType): AuthToken {
        val response = client.post<List<HueResult<AuthToken>>>("api") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            body = devicetype
        }
        val success = response.singleOrNull()?.success
        val error = response.singleOrNull()?.error

        when {
            success != null -> return success
            error != null -> throw ShadeApiError(error)
            else -> throw UnknownException()
        }
    }
}
