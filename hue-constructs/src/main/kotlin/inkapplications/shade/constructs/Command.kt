package inkapplications.shade.constructs

import kotlinx.serialization.Serializable

/**
 * A Request to run to the Hue API.
 *
 * These are used for schedules, but are created privately throughout
 * the Shade modules.
 *
 * @param address The URL to hit, relative to the baseUrl of the Hue API
 * @param method HTTP method to use when sending the request
 * @param body Body to post with the hue request.
 */
@Serializable
data class Command(
    val address: String,
    val method: String,
    val body: String?,
) {
    @Deprecated(
        message = "Body must be a JSON string.",
        replaceWith = ReplaceWith("this(address, method, body?.toString())"),
        level = DeprecationLevel.ERROR,
    )
    constructor(
        address: String,
        method: String,
        body: Any?,
    ): this(address, method, body?.toString())
}
