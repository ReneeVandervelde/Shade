package inkapplications.shade.lights

import inkapplications.shade.structures.ResourceId
import inkapplications.shade.structures.ResourceReference
import inkapplications.shade.structures.ResourceType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * State and capabilities of a light resource.
 */
@Serializable
data class Light(
    /**
     * Type of the supported resources
     */
    val type: ResourceType,

    /**
     * Unique identifier representing a specific resource instance
     */
    val id: ResourceId,

    /**
     * Owner of the service
     *
     * In case the owner service is deleted, the service also gets deleted.
     */
    val owner: ResourceReference,

    /**
     * On/Off state of the light
     */
    @SerialName("on")
    val powerInfo: PowerInfo,

    /**
     * Information about the light's dimming, if supported.
     */
    @SerialName("dimming")
    val dimmingInfo: DimmingInfo? = null,

    /**
     * Information about the color temperature and capabilities of the light.
     */
    @SerialName("color_temperature")
    val colorTemperatureInfo: ColorTemperatureInfo? = null,

    /**
     * Clip v1 resource identifier.
     */
    @Deprecated("V1 Resource")
    @SerialName("id_v1")
    val v1Id: String? = null,
)