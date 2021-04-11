package inkapplications.shade.constructs.serialization

import inkapplications.shade.constructs.ColorTemperature
import inkapplications.shade.constructs.mireds

internal class DelegatedColorTemperatureRangeSerializerTest: BijectiveSerializerTest<ClosedRange<ColorTemperature>, MinMaxIntToken>(
    serializer = DelegatedColorTemperatureRangeSerializer,
) {
    override val transforms: Map<ClosedRange<ColorTemperature>, MinMaxIntToken> = mapOf(
        12.mireds..25.mireds to MinMaxIntToken(12, 25),
    )
}
