package inkapplications.shade.constructs.serialization

import inkapplications.shade.constructs.ColorTemperature
import inkapplications.shade.constructs.mireds
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer

object MiredSerializer: DelegatedSerializer<ColorTemperature, Int>() {
    override val backingSerializer: KSerializer<Int> = Int.serializer()

    override fun deserialize(value: Int): ColorTemperature = value.mireds
    override fun serialize(value: ColorTemperature): Int = value.miredValue
}
