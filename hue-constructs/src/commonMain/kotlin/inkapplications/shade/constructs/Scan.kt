package inkapplications.shade.constructs

import inkapplications.shade.constructs.serialization.LocalDateTimeSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class ScanReference(val id: String, val name: String)

/**
 * Results for a new device scan.
 *
 * @param lastScan The timestamp that the scan was last updated
 */
// TODO: This class doesn't match API description anymore.
@Serializable
data class Scan(
    // TODO: This should be a sealed class, returns "active" and "none" as well.
    @Serializable(with = LocalDateTimeSerializer::class)
    val lastScan: LocalDateTime?,

    val lights: List<ScanReference>
)
