package inkapplications.shade.constructs.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Serialize a class by converting to/from another class.
 *
 * This just removes the KotlinX.Serialization syntax in favor of a more
 * straightforward object transformation.
 */
abstract class DelegatedSerializer<DESERIALIZED, SERIALIZED>: KSerializer<DESERIALIZED> {
    abstract val backingSerializer: KSerializer<SERIALIZED>
    final override val descriptor: SerialDescriptor get() = backingSerializer.descriptor

    final override fun deserialize(decoder: Decoder): DESERIALIZED {
        return backingSerializer.deserialize(decoder).let(::deserialize)
    }

    override fun serialize(encoder: Encoder, value: DESERIALIZED) {
        backingSerializer.serialize(encoder, value.let(::serialize))
    }

    abstract fun deserialize(value: SERIALIZED): DESERIALIZED
    abstract fun serialize(value: DESERIALIZED): SERIALIZED
}
