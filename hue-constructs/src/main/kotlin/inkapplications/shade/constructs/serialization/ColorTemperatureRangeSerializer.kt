package inkapplications.shade.constructs.serialization

import inkapplications.shade.constructs.ColorTemperature
import inkapplications.shade.constructs.mireds
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

/**
 * Two ints as a serializable range object.
 */
@Serializable
internal data class MinMaxIntToken(val min: Int, val max: Int)

internal object DelegatedColorTemperatureRangeSerializer: DelegatedSerializer<ClosedRange<ColorTemperature>, MinMaxIntToken>() {
    override val backingSerializer: KSerializer<MinMaxIntToken> = MinMaxIntToken.serializer()

    override fun deserialize(value: MinMaxIntToken): ClosedRange<ColorTemperature> {
        return value.min.mireds..value.max.mireds
    }

    override fun serialize(value: ClosedRange<ColorTemperature>): MinMaxIntToken {
        return MinMaxIntToken(value.start.miredValue, value.endInclusive.miredValue)
    }
}

/**
 * Serialize a range of color temperatures.
 */
object ColorTemperatureRangeSerializer: KSerializer<ClosedRange<ColorTemperature>> by DelegatedColorTemperatureRangeSerializer
