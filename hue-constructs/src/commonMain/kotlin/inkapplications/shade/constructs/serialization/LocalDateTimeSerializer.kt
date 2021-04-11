package inkapplications.shade.constructs.serialization

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer

/**
 * Serialize a LocalDateTime as an ISO string.
 *
 * Note: This truncates nanoseconds during serialization.
 */
object LocalDateTimeSerializer: DelegatedSerializer<LocalDateTime, String>() {
    override val backingSerializer: KSerializer<String> = String.serializer()

    override fun deserialize(value: String): LocalDateTime = LocalDateTime.parse(value)
    override fun serialize(value: LocalDateTime): String = LocalDateTime(
        year = value.year,
        monthNumber = value.monthNumber,
        dayOfMonth = value.dayOfMonth,
        hour = value.hour,
        minute = value.minute,
        second = value.second,
        nanosecond = 0,
    ).toString()
}
