package inkapplications.shade.constructs

import inkapplications.shade.constructs.serialization.DelegatedSerializer
import inkapplications.spondee.math.Percentage
import inkapplications.spondee.math.percent
import inkapplications.spondee.math.toPercentage
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlin.math.min
import kotlin.math.round

private const val MAX_VALUE = 254

@ExperimentalUnsignedTypes
val Percentage.asByteValue: UByte get() {
    return min(round(this of MAX_VALUE), MAX_VALUE.toDouble())
        .toInt()
        .toByte()
        .toUByte()
}

/**
 * Serializes a percentage as a byte contained in an integer.
 */
@OptIn(ExperimentalUnsignedTypes::class)
object PercentageSerializer: DelegatedSerializer<Percentage, Int>() {
    override val backingSerializer: KSerializer<Int> = Int.serializer()
    override fun deserialize(value: Int): Percentage = value.bytePercentage
    override fun serialize(value: Percentage): Int = value.asByteValue.toInt()
}

/**
 * Convert a 0-254 byte value into a percentage value.
 *
 * This is Hue's native value system. It's unlikely you'd want to use this
 * unless you are working with other hue systems.
 */
val Number.bytePercentage get() = (toDouble() / MAX_VALUE).toPercentage()

/**
 * Convert a 0-254 byte value into a percentage value.
 *
 * This is Hue's native value system. It's unlikely you'd want to use this
 * unless you are working with other hue systems.
 */
@ExperimentalUnsignedTypes
val UByte.bytePercentage get() = toInt().bytePercentage
