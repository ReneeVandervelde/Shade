package inkapplications.shade.constructs.serialization

import inkapplications.shade.constructs.HueError
import inkapplications.shade.constructs.ShadeApiError
import inkapplications.shade.constructs.ShadeException
import io.ktor.client.features.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json

/**
 * Deserialize Error exceptions from responses.
 */
object ErrorParser {
    suspend fun parseError(response: ResponseException): ShadeException {
        return try {
            ShadeApiError(Json.decodeFromString(HueError.serializer(), response.response.readText()))
        } catch (error: Throwable) {
            return ShadeException("Problem deserializing error", error)
        }
    }
}

/**
 * Run a block of code, catching any internal errors.
 */
suspend inline fun <T> encapsulateErrors(block: () -> T): T {
    try {
        return block()
    } catch (httpError: ResponseException) {
        throw ErrorParser.parseError(httpError)
    }
}
