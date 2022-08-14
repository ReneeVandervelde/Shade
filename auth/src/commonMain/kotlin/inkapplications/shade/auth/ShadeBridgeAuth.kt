package inkapplications.shade.auth

import inkapplications.shade.auth.structures.AppId
import inkapplications.shade.auth.structures.AuthRequest
import inkapplications.shade.internals.BaseUrl
import inkapplications.shade.internals.HueHttpClient
import inkapplications.shade.structures.ApiError
import inkapplications.shade.structures.AuthToken
import inkapplications.shade.structures.AuthorizationTimeoutException
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.delay
import kotlinx.serialization.serializer
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * Implements bridge auth with the hue client
 */
internal class ShadeBridgeAuth(
    private val client: HueHttpClient,
    private val logger: KimchiLogger,
): BridgeAuth {
    @ExperimentalTime
    override suspend fun awaitToken(
        appId: AppId,
        retries: Int,
        timeout: Duration,
    ): AuthToken {
        val authRequest = AuthRequest(
            appId = appId,
            generateClientKey = true,
        )

        repeat(retries) {
            try {
                return client.postV1DeserializedData(
                    body = authRequest,
                    pathSegments = BaseUrl.v1(),
                    requestSerializer = serializer(),
                    responseSerializer = serializer(),
                )
            } catch (e: ApiError) {
                if (e.code == 200) {
                    logger.debug("Received Expected API Error, Waiting $timeout before retry", e)
                    delay(timeout)
                } else {
                    throw e
                }
            }
        }

        throw AuthorizationTimeoutException
    }
}
