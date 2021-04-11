package inkapplications.shade.constructs.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

/**
 * Serialize a duration as Long Deciseconds.
 */
@ExperimentalTime
object DurationDecisecondSerializer: DelegatedSerializer<Duration, Long>() {
    override val backingSerializer: KSerializer<Long> = Long.serializer()

    override fun deserialize(value: Long): Duration = value.milliseconds * 100
    override fun serialize(value: Duration): Long = value.toLongMilliseconds() / 100L
}
