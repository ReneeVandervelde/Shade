package inkapplications.shade

import inkapplications.shade.auth.ShadeAuthModule
import inkapplications.shade.auth.TokenStorage
import inkapplications.shade.config.ShadeConfig
import inkapplications.shade.lights.ShadeLightsModule
import okhttp3.OkHttpClient

/**
 * Provides services for accessign the Hue API
 *
 * @param config Properties for setting up your connection to the hue bridge.
 * @param storage A service for storing Hue's API token. By default,
 *        this token is only stored in memory during the application run.
 *        However, this parameter can be provided to store the token
 *        on disk permanently.
 * @param client The HTTP Client to use for requests. This can
 *        optionally be provided in order to modify how the HTTP
 *        requests are made. Useful for logging and debugging.
 */
open class Shade(
    config: ShadeConfig,
    storage: TokenStorage = InMemoryStorage,
    client: OkHttpClient = OkHttpClient()
) {
    /**
     * Services for Authenticating with Hue.
     */
    val auth = ShadeAuthModule().createAuth(client, config, storage)

    /**
     * Services for controlling Lights.
     */
    val lights = ShadeLightsModule().createLights(client,  config, storage)
}
