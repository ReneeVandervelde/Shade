package inkapplications.shade.constructs.serialization

import inkapplications.shade.constructs.Coordinates

class CoordinateListSerializerTest: BijectiveSerializerTest<Coordinates, List<Float>>(CoordinateListSerializer) {
    override val transforms = mapOf(
        Coordinates(1.2f, 3.4f) to listOf(1.2f, 3.4f),
        Coordinates(0f, 0f) to listOf(0f, 0f),
    )
}
