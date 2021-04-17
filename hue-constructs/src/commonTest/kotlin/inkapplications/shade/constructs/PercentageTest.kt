package inkapplications.shade.constructs

import inkapplications.spondee.math.percent
import inkapplications.spondee.math.toPercentage
import org.junit.Test
import kotlin.test.assertEquals

class PercentageTest {
    @Test
    fun serializer() {
        assertEquals(33, PercentageSerializer.serialize(13.percent))
        assertEquals(36, PercentageSerializer.serialize(14.percent))
        assertEquals(0, PercentageSerializer.serialize(0.percent))
        assertEquals(0, PercentageSerializer.serialize(0.percent))
        assertEquals(254, PercentageSerializer.serialize(150.percent))

        assertEquals(127, PercentageSerializer.serialize(127.bytePercentage))
        assertEquals(55, PercentageSerializer.serialize(55.bytePercentage))
        assertEquals(0, PercentageSerializer.serialize(0.bytePercentage))
        assertEquals(254, PercentageSerializer.serialize(254.bytePercentage))
        assertEquals(254, PercentageSerializer.serialize(255.bytePercentage))

        assertEquals(127, PercentageSerializer.serialize(127.toUByte().bytePercentage))
        assertEquals(55, PercentageSerializer.serialize(55.toUByte().bytePercentage))
        assertEquals(0, PercentageSerializer.serialize(0.toUByte().bytePercentage))
        assertEquals(254, PercentageSerializer.serialize(254.toUByte().bytePercentage))
        assertEquals(254, PercentageSerializer.serialize(255.toUByte().bytePercentage))
    }

    @Test
    fun testConversionsMaxValue() {
        assertEquals(254.toUByte(), 1.0.toPercentage().asByteValue)
        assertEquals(254.toUByte(), 1.0f.toPercentage().asByteValue)
        assertEquals(254.toUByte(), 254.bytePercentage.asByteValue)
        assertEquals(254.toUByte(), 254.toUByte().bytePercentage.asByteValue)
        assertEquals(254.toUByte(), 100.percent.asByteValue)
    }

    @Test
    fun testConversionsMinValue() {
        assertEquals(0.toUByte(), 0.0.toPercentage().asByteValue)
        assertEquals(0.toUByte(), 0.0f.toPercentage().asByteValue)
        assertEquals(0.toUByte(), 0.bytePercentage.asByteValue)
        assertEquals(0.toUByte(), 0.toUByte().bytePercentage.asByteValue)
        assertEquals(0.toUByte(), 0.percent.asByteValue)
    }

    @Test
    fun testConversionsMidValue() {
        assertEquals(127.toUByte(), 0.5.toPercentage().asByteValue)
        assertEquals(127.toUByte(), 0.5f.toPercentage().asByteValue)
        assertEquals(127.toUByte(), 127.bytePercentage.asByteValue)
        assertEquals(127.toUByte(), 127.toUByte().bytePercentage.asByteValue)
        assertEquals(127.toUByte(), 50.percent.asByteValue)
    }

    @Test
    fun testPercentConversions() {
        assertEquals(.5, 127.toUByte().bytePercentage.asFraction())
        assertEquals(1.0, 254.toUByte().bytePercentage.asFraction())
        assertEquals(0.0, 0.toUByte().bytePercentage.asFraction())
    }
}
