package inkapplications.shade.constructs

import kotlinx.serialization.Serializable

/**
 * Just an object containing an ID
 *
 * This is used when creating a new object with the Hue bridge.
 *
 * @param id The ID of the referenced object in Hue's system.
 */
@Serializable
data class IdToken(val id: String)
