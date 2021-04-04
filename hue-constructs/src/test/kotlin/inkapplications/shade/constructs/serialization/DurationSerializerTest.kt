package inkapplications.shade.constructs.serialization

import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds
import kotlin.time.seconds

@OptIn(ExperimentalTime::class)
class DurationSerializerTest: BijectiveSerializerTest<Duration, Long>(DurationDecisecondSerializer) {
    override val transforms = mapOf(
        400.milliseconds to 4L,
        1.seconds to 10L,
    )
}
