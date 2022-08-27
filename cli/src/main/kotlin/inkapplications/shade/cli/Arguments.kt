package inkapplications.shade.cli

import com.github.ajalt.clikt.parameters.arguments.ProcessedArgument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.options.NullableOption
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.int
import inkapplications.shade.structures.ResourceId
import inkapplications.spondee.measure.ColorTemperature
import inkapplications.spondee.measure.Kelvin
import inkapplications.spondee.measure.toColorTemperature
import inkapplications.spondee.scalar.Percentage
import inkapplications.spondee.scalar.WholePercentage

/**
 * Convert a string argument into a resource ID
 */
fun ProcessedArgument<String, String>.resourceId() = convert { ResourceId(it) }

/**
 * Convert an argument into a percentage object
 */
fun NullableOption<String, String>.percentage(): NullableOption<Percentage, Percentage> {
    return convert { it.trimEnd(' ', '%') }
        .int()
        .convert { WholePercentage.of(it) }
}

/**
 * Convert an argument into an on/off boolean representation
 */
fun NullableOption<String, String>.power(): NullableOption<Boolean, Boolean> {
    return choice("on", "off", ignoreCase = true)
        .convert {
            it == "on"
        }
}

/**
 * Convert an argument into a color temperature in kelvin
 */
fun NullableOption<String, String>.kelvin(): NullableOption<ColorTemperature, ColorTemperature> {
    return convert { it.trimEnd('K', 'k', ' ') }
        .int()
        .convert { Kelvin.of(it).toColorTemperature() }
}
