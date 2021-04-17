package inkapplications.shade.constructs


/**
 * Base/General class for all exceptions specific to shade.
 */
open class ShadeException(message: String? = null, cause: Throwable? = null): RuntimeException(message, cause)

/**
 * Error returned by the Hue API.
 */
open class ShadeApiError(val hueError: HueError): ShadeException(hueError.description)

/**
 * Exception thrown, but no additional information was able to be parsed.
 */
class UnknownException(
    cause: Throwable? = null
): ShadeException(
    message = "Something went wrong, but the error message was unavailable.",
    cause = cause
)

/**
 * Wrapper for multiple errors returned by the API
 */
open class ShadeCompositeApiError(val hueErrors: List<ShadeApiError>): ShadeException("Multiple Hue API Errors occurred")

/**
 * Convert a list of API errors into an API Exception.
 */
fun List<HueError>.asException(): ShadeException = when {
    isEmpty() -> ShadeException("Unknown Error: Empty Error list thrown.")
    size == 1 -> ShadeApiError(get(0))
    else -> ShadeCompositeApiError(map { ShadeApiError(it) })
}
