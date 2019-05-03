package inkapplications.shade.serialization

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson

@JsonClass(generateAdapter = true)
data class MinMax(val min: Int, val max: Int)

object RangeDeserializer {
    @FromJson fun fromJson(range: MinMax): IntRange = range.min..range.max
    @ToJson fun toJson(range: IntRange): MinMax = MinMax(range.start, range.endInclusive)
}