package inkapplications.shade

import inkapplications.shade.auth.ShadeAuth
import inkapplications.shade.auth.TokenStorage
import inkapplications.shade.delegates.ShadeDelegates
import inkapplications.shade.discover.DiscoverModule
import inkapplications.shade.groups.*
import inkapplications.shade.lights.ShadeLights
import inkapplications.shade.scenes.ShadeScenes
import inkapplications.shade.schedules.ShadeSchedules
import kotlin.time.ExperimentalTime

/**
 * Provides services for accessing the Hue API
 *
 * @param storage A service for storing Hue's API token. By default,
 *        this token is only stored in memory during the application run.
 *        However, this parameter can be provided to store the token
 *        on disk permanently.
 */
@ExperimentalTime
open class Shade(
    appId: String = "Shade#Shade",
    initBaseUrl: String? = null,
    storage: TokenStorage = InMemoryStorage,
) {
    private val delegates = ShadeDelegates(initBaseUrl, appId, storage)

    /**
     * Services for finding bridges on the network.
     */
    open val discovery = DiscoverModule().createDiscoveryClient()

    /**
     * Services for Authenticating with Hue.
     */
    open val auth: ShadeAuth = delegates.auth

    /**
     * Services for controlling Lights.
     */
    open val lights: ShadeLights = delegates.lights

    /**
     * Services for controlling Light-Groups
     */
    open val groups: ShadeGroups = delegates.groups

    /**
     * Services for setting custom light schedules.
     */
    open val schedules: ShadeSchedules = delegates.schedules

    /**
     * Services for bridge scenes.
     */
    val scenes: ShadeScenes = delegates.scenes

    /**
     * Set the BaseURL of the Hue bridge.
     */
    fun setBaseUrl(baseUrl: String) {
        delegates.all.forEach { it.setUrl(baseUrl) }
    }
}
