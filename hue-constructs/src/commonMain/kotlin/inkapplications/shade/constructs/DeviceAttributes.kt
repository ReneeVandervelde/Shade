package inkapplications.shade.constructs

import kotlinx.serialization.Serializable

/**
 * Attributes for a device.
 *
 * I guess they made this whole thing, but never came up with anything
 * more than name as an attribute.
 * This is used in things like scans and renaming.
 *
 * @param name The user-readable name of the device.
 */
@Serializable
data class DeviceAttributes(val name: String)
