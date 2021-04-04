package inkapplications.shade.constructs.serialization

import kotlinx.datetime.LocalDateTime

class LocalDateSerializerTest: BijectiveSerializerTest<LocalDateTime, String>(LocalDateTimeSerializer) {
    override val transforms: Map<LocalDateTime, String> = mapOf(
        LocalDateTime(
            year = 2021,
            monthNumber = 2,
            dayOfMonth = 3,
            hour = 4,
            minute = 5,
            second = 6,
            nanosecond = 0,
        ) to "2021-02-03T04:05:06"
    )

}
