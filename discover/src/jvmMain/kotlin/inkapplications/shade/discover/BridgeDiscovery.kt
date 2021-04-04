package inkapplications.shade.discover

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Endpoint to discover hue Bridges on the network.
 *
 * This is the N-UPNP discovery strategy, and requires a working internet
 * connection, unlike local UPNP.
 */
interface BridgeDiscovery {
    /**
     * Get Bridges reported on the network.
     */
    suspend fun getDevices(): List<Device>
}

/**
 * A Bridge device on the network.
 *
 * @param id A Unique Identifier for the bridge.
 * @param ip The internal IP address of the bridge.
 */
@Serializable
data class Device(
    val id: String,
    @SerialName("internalipaddress")
    val ip: String
) {
    /**
     * A base-URL that can be used to connect to the hue bridge locally.
     *
     * Note: Since this resolves directly to an IP, it cannot use https.
     */
    val url: String = "http://$ip/"
}
