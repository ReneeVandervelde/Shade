package inkapplications.shade.constructs

import java.io.PrintStream
import java.io.PrintWriter
import java.lang.RuntimeException

/**
 * Base/General class for all exceptions specific to shade.
 */
open class ShadeException(message: String? = null, cause: Throwable? = null): RuntimeException(message, cause)

/**
 * Error returned by the Hue API.
 */
open class ShadeApiError(val hueError: HueError): ShadeException(hueError.description)

/**
 * Wrapper for multiple errors returned by the API
 */
open class ShadeCompositeApiError(val hueErrors: List<ShadeApiError>): ShadeException("Multiple Hue API Errors occurred") {
    override fun printStackTrace() {
        super.printStackTrace()
        hueErrors.forEach { printStackTrace() }
    }

    override fun printStackTrace(stream: PrintStream?) {
        super.printStackTrace(stream)
        hueErrors.forEach { printStackTrace(stream) }
    }

    override fun printStackTrace(writer: PrintWriter?) {
        super.printStackTrace(writer)
        hueErrors.forEach { printStackTrace(writer) }
    }
}

/**
 * Convert a list of API errors into an API Exception.
 */
fun List<HueError>.asException(): ShadeException = when {
    isEmpty() -> ShadeException("Unknown Error: Empty Error list thrown.")
    size == 1 -> ShadeApiError(get(0))
    else -> ShadeCompositeApiError(map { ShadeApiError(it) })
}