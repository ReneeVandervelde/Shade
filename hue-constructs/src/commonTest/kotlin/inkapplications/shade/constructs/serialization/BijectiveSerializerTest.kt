package inkapplications.shade.constructs.serialization

import org.junit.Test
import kotlin.test.assertEquals

/**
 * Converts values with a specified serializer and tests for equivalency.
 */
abstract class BijectiveSerializerTest<DESERIALIZED, SERIALIZED>(
    private val serializer: DelegatedSerializer<DESERIALIZED, SERIALIZED>
) {
    abstract val transforms: Map<DESERIALIZED, SERIALIZED>

    @Test
    fun serializations() {
        transforms.forEach { deserialized, serialized ->
            assertEquals(serialized, serializer.serialize(deserialized))
        }
    }

    @Test
    fun deserializations() {
        transforms.forEach { deserialized, serialized ->
            assertEquals(deserialized, serializer.deserialize(serialized))
        }
    }

    @Test
    fun bijective() {
        transforms.forEach { deserialized, serialized ->
            val serializeFirst = serializer.serialize(deserialized).let(serializer::deserialize)
            val deserializeFirst = serializer.deserialize(serialized).let(serializer::serialize)

            assertEquals(deserialized, serializeFirst, "Serialize-forward transformation is not bijective.")
            assertEquals(serialized, deserializeFirst, "Deserialize-forward transformation is not bijective.")
        }
    }
}
