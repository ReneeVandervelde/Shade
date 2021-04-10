package inkapplications.shade.constructs.serialization

import inkapplications.shade.constructs.Coordinates
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer

/**
 * Deserialize a list of coordinates into XY coordinates.
 */
internal object CoordinateListSerializer: DelegatedSerializer<Coordinates, List<Float>>() {
    override val backingSerializer: KSerializer<List<Float>> = ListSerializer(Float.serializer())
    override fun deserialize(value: List<Float>): Coordinates = Coordinates(value[0], value[1])
    override fun serialize(value: Coordinates): List<Float> = listOf(value.x, value.y)
}
