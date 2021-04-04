package inkapplications.shade.constructs.serialization

import com.soywiz.klock.*
import com.soywiz.klock.ISO8601.DATETIME_COMPLETE
import com.soywiz.klock.ISO8601.DATE_CALENDAR_COMPLETE
import com.soywiz.klock.ISO8601.TIME_LOCAL_COMPLETE
import com.soywiz.klock.TimeFormat.Companion.FORMAT_TIME
import inkapplications.shade.constructs.Coordinates
import inkapplications.shade.constructs.LocalTime
import inkapplications.shade.constructs.TimePattern
import kotlinx.datetime.*
import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

/**
 * Deserialize a list of coordinates into XY coordinates.
 */
internal object CoordinateListSerializer: DelegatedSerializer<Coordinates, List<Float>>() {
    override val backingSerializer: KSerializer<List<Float>> = ListSerializer(Float.serializer())
    override fun deserialize(value: List<Float>): Coordinates = Coordinates(value[0], value[1])
    override fun serialize(value: Coordinates): List<Float> = listOf(value.x, value.y)
}
