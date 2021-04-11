package inkapplications.shade.constructs.serialization

import inkapplications.shade.constructs.ColorTemperature
import inkapplications.shade.constructs.kelvin
import inkapplications.shade.constructs.mireds

class MiredSerializerTest: BijectiveSerializerTest<ColorTemperature, Int>(MiredSerializer) {
    override val transforms = mapOf(
        200.mireds to 200,
        5000.kelvin to 200,
    )
}
