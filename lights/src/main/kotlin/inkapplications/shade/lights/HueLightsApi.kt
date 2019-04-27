package inkapplications.shade.lights

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import inkapplications.shade.hueclient.structures.Coordinates
import kotlinx.coroutines.Deferred
import org.threeten.bp.Instant
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Hue API endpoints for lights.
 */
internal interface HueLightsApi {
    /**
     * Get a list of all lights known to the hue bridge.
     */
    @GET("api/{token}/lights")
    fun getLights(@Path("token") token: String): Deferred<Map<String, Light>>

    /**
     * Set the state of a light.
     */
    @PUT("api/{token}/lights/{light}/state")
    fun setState(
        @Path("token") token: String,
        @Path("light") lightId: String,
        @Body modification: LightStateModification
    ): Deferred<Unit>
}

/**
 * A single light bulb.
 *
 * @property state Details the state of the light.
 * @property updateState An identifier for the software version running
 *           on the light.
 * @property type A fixed name describing the type of light
 *           e.g. “Extended color light”.
 * @property name A unique, editable name given to the light.
 * @property modelId The hardware model of the light.
 * @property manufacturer The manufacturer name.
 * @property productName The name of the product.
 * @property capabilities Defines the details of the light hardware.
 * @property config Settings for the bulb.
 * @property uuid Unique id of the device. The MAC address of the
 *           device with a unique endpoint id in the form:
 *           `AA:BB:CC:DD:EE:FF:00:11-XX`
 * @property softwareVersion An identifier for the software version
 *           running on the light.
 */
@JsonClass(generateAdapter = true)
data class Light(
    val state: LightState,
    @Json(name = "swupdate") val updateState: UpdateState,
    val type: String,
    val name: String,
    @Json(name = "modelid") val modelId: String,
    @Json(name = "manufacturername") val manufacturer: String,
    @Json(name = "productname") val productName: String,
    val capabilities: Capabilities,
    val config: LightConfig,
    @Json(name = "uniqueid") val uuid: String,
    @Json(name = "swversion") val softwareVersion: String
)

/**
 * Info and Constraints of the bulb hardware.
 *
 * @property certified Whether daddy Philips approved this light.
 * @property control Constraints on commands for the light.
 * @property streaming Current light supports streaming features.
 */
@JsonClass(generateAdapter = true)
data class Capabilities(
    val certified: Boolean,
    val control: ControlCapabilities?,
    val streaming: StreamingCapabilities?
)

/**
 * Capabilities for entertainment streaming.
 *
 * @property renderer Indicates if a lamp can be used for entertainment
 *           streaming as renderer
 * @property proxy Indicates if a lamp can be used for entertainment
 *           streaming as a proxy node
 */
@JsonClass(generateAdapter = true)
data class StreamingCapabilities(
    val renderer: Boolean,
    val proxy: Boolean
)

/**
 * Settings for the bulb.
 *
 * @property archetype What the fuck is this. Do you know? Please make a PR.
 * @property function Why is this always "mixed"? Is this useless?
 * @property direction No clue what this does
 * @property startup Parameters for how the light should turn on.
 */
@JsonClass(generateAdapter = true)
data class LightConfig(
    val archetype: String?,
    val function: String?,
    val direction: String?,
    val startup: LightStartupConfig?
)

/**
 * Parameters for how the light should turn on.
 *
 * @property mode Mode to default to when the light turns on.
 * @property configured Whether the configuration has been set by the user.
 * @property customSettings If set to a custom mode, this will contain
 *           specifics for light startup settings.
 */
@JsonClass(generateAdapter = true)
data class LightStartupConfig(
    val mode: String,
    val configured: Boolean,
    @Json(name = "customsettings") val customSettings: LightCustomStartupSettings?
)

/**
 * Custom parameters for how a light should turn on.
 *
 * @property brightness Brightness of the light. This is a scale from
 *           the minimum brightness the light is capable of, 1, to
 *           the maximum capable brightness, 254.
 * @property cieColorCoordinates The x and y coordinates of a color in
 *           CIE color space.The first entry is the x coordinate and
 *           the second entry is the y coordinate. Both x and y must be
 *           between 0 and 1.
 *           If the specified coordinates are not in the CIE color space,
 *           the closest color to the coordinates will be chosen.
 */
@JsonClass(generateAdapter = true)
data class LightCustomStartupSettings(
    @Json(name = "bri") val brightness: Int,
    @Json(name = "xy") val cieColorCoordinates: Coordinates
)

/**
 * Constraints on commands for the light.
 *
 * @property minimumDimLevel Minimum allowable brightness for the
 *           light in unknown units.
 * @property maximumLumens Maximum lumens for the light.
 * @property colorGamutType The class name of color gamut the light uses.
 * @property colorGamut XY Bounds for the displayable color gamut.
 * @property colorTemperatures Range of Mired Color temperatures that
 *           the light can display.
 */
@JsonClass(generateAdapter = true)
data class ControlCapabilities(
    @Json(name = "mindimlevel") val minimumDimLevel: Int?,
    @Json(name = "maxlumen") val maximumLumens: Int?,
    @Json(name = "colorgamuttype") val colorGamutType: String?,
    @Json(name = "colorgamut") val colorGamut: List<Coordinates>?,
    @Json(name = "ct") val colorTemperatures: IntRange?
)

/**
 * Information about the light's firmware.
 *
 * @property state Whether there are updates for the light. The values
 *           of this aren't documented anywhere I can find.
 * @property lastInstall Timestamp of the last firmware update.
 */
@JsonClass(generateAdapter = true)
data class UpdateState(
    val state: String,
    @Json(name="lastinstall") val lastInstall: Instant
)

/**
 * State of a light.
 *
 * @property on On/Off state of the light. On=true, Off=false
 * @property brightness Brightness of the light. This is a scale from
 *           the minimum brightness the light is capable of, 1, to the
 *           maximum capable brightness, 254.
 * @property hue Hue of the light. This is a wrapping value between 0
 *           and 65535. Note, that hue/sat values are hardware
 *           dependent which means that programming two devices with
 *           the same value does not garantuee that they will be the
 *           same color. Programming 0 and 65535 would mean that the
 *           light will resemble the color red, 21845 for green and
 *           43690 for blue.
 * @property saturation Saturation of the light. 254 is the most
 *           saturated (colored) and 0 is the least saturated (white).
 * @property effect The dynamic effect of the light.
 * @property cieColorCoordinates The x and y coordinates of a color
 *           in CIE color space.
 *           The first entry is the x coordinate and the second entry
 *           is the y coordinate. Both x and y are between 0 and 1.
 *           Using CIE xy, the colors can be the same on all lamps if
 *           the coordinates are within every lamps gamuts (example:
 *           “xy”:[0.409,0.5179] is the same color on all lamps). If
 *           not, the lamp will calculate it’s closest color and use
 *           that. The CIE xy color is absolute, independent from the
 *           hardware.
 * @property colorTemperature The Mired Color temperature of the light.
 *           2012 connected lights are capable of 153 (6500K) to 500 (2000K).
 * @property alert The alert effect is a temporary change to the bulb’s state.
 *           Note that this contains the last alert sent to the light and
 *           not its current state. i.e. After the breathe cycle has
 *           finished the bridge does not reset the alert to “none“.
 * @property colorMode Indicates the color mode in which the light is working,
 *           this is the last command type it received.
 *           This parameter is only present when the light supports at least
 *           one of the values.
 * @property mode Hue's Docs say nothing about this one.
 * @property reachable Indicates if a light can be reached by the bridge.
 */
@JsonClass(generateAdapter = true)
data class LightState(
    val on: Boolean,
    @Json(name="bri") val brightness: Int,
    val hue: Int?,
    @Json(name="sat") val saturation: Int?,
    val effect: LightEffect?,
    @Json(name="xy") val cieColorCoordinates: Coordinates?,
    @Json(name="ct") val colorTemperature: Int?,
    val alert: AlertState?,
    @Json(name="colormode") val colorMode: ColorMode?,
    val mode: String?,
    val reachable: Boolean
)

/**
 * Options when modifying the state of a light.
 *
 * @property on On/Off state of the light. On=true, Off=false
 * @property brightness Brightness of the light. This is a scale from
 *           the minimum brightness the light is capable of, 1, to the
 *           maximum capable brightness, 254.
 * @property hue Hue of the light. This is a wrapping value between 0
 *           and 65535. Note, that hue/sat values are hardware
 *           dependent which means that programming two devices with
 *           the same value does not garantuee that they will be the
 *           same color. Programming 0 and 65535 would mean that the
 *           light will resemble the color red, 21845 for green and
 *           43690 for blue.
 * @property saturation Saturation of the light. 254 is the most
 *           saturated (colored) and 0 is the least saturated (white).
 * @property effect The dynamic effect of the light.
 * @property transitionTime The duration of the transition from the
 *           light’s current state to the new state. This is given as
 *           a multiple of 100ms and defaults to 4 (400ms).
 *           For example, setting `transitiontime:10` will make the
 *           transition last 1 second.
 * @property cieColorCoordinates The x and y coordinates of a color
 *           in CIE color space.
 *           The first entry is the x coordinate and the second entry
 *           is the y coordinate. Both x and y are between 0 and 1.
 *           Using CIE xy, the colors can be the same on all lamps if
 *           the coordinates are within every lamps gamuts (example:
 *           “xy”:[0.409,0.5179] is the same color on all lamps). If
 *           not, the lamp will calculate it’s closest color and use
 *           that. The CIE xy color is absolute, independent from the
 *           hardware.
 * @property colorTemperature The Mired Color temperature of the light.
 *           2012 connected lights are capable of 153 (6500K) to 500 (2000K).
 * @property alert The alert effect is a temporary change to the bulb’s state.
 *           Note that this contains the last alert sent to the light and
 *           not its current state. i.e. After the breathe cycle has
 *           finished the bridge does not reset the alert to “none“.
 * @property brightnessIncrement Increments or decrements the value of the
 *           brightness.  this is ignored if the bri attribute is provided.
 *           Any ongoing bri transition is stopped. Setting a value of 0
 *           also stops any ongoing transition. The bridge will return the
 *           bri value after the increment is performed.
 * @property saturationIncrement Increments or decrements the value of
 *           the saturation. This is ignored if the sat attribute is
 *           provided. Any ongoing sat transition is stopped.
 *           Setting a value of 0 also stops any ongoing transition.
 *           The bridge will return the sat value after the increment
 *           is performed.
 * @property hueIncrement Increments or decrements the value of the hue.
 *           This is ignored if the hue attribute is provided. Any
 *           ongoing color transition is stopped. Setting a value of 0
 *           also stops any ongoing transition. The bridge will return
 *           the hue value after the increment is performed.Note if the
 *           resulting values are < 0 or > 65535 the result is wrapped.
 *           For example: `hueIncrement` of 1 on a hue value of 65535
 *           results in a hue of 0 and `hueIncrement` of -1 on a hue of
 *           0 results in a hue of 65534.
 * @property colorTemperatureIncrement Increments or decrements the
 *           value of the color temperature. This is ignored if the
 *           temperature attribute is provided. Any ongoing color
 *           transition is stopped. Setting a value of 0 also stops any
 *           ongoing transition. The bridge will return the temperature
 *           value after the increment is performed.
 * @property cieCoordinateTranslation Translates the coordinates of the
 *           CIE color. This is ignored if the CIE coordinates attribute
 *           is provided.
 *           Any ongoing color transition is stopped. Setting a value
 *           of 0 also stops any ongoing transition. Will stop at it’s
 *           gamut boundaries. The bridge will return the xy value after
 *           the increment is performed. Max value [0.5, 0.5].
 */
@JsonClass(generateAdapter = true)
data class LightStateModification(
    val on: Boolean? = null,
    @Json(name="bri") val brightness: Int? = null,
    val hue: Int? = null,
    @Json(name="sat") val saturation: Int? = null,
    val effect: LightEffect? = null,
    @Json(name="transitiontime") val transitionTime: Int? = null,
    @Json(name="xy") val cieColorCoordinates: Coordinates? = null,
    @Json(name="ct") val colorTemperature: Int? = null,
    val alert: AlertState? = null,
    @Json(name="bri_inc") val brightnessIncrement: Int? = null,
    @Json(name="sat_inc") val saturationIncrement: Int? = null,
    @Json(name="hue_inc") val hueIncrement: Int? = null,
    @Json(name="ct_inc") val colorTemperatureIncrement: Int? = null,
    @Json(name="xy_inc") val cieCoordinateTranslation: Coordinates? = null
)

/**
 * Dynamic Effects for lights.
 *
 * @property NONE Behaves normally.
 * @property COLOR_LOOP cycle through all hues using the current brightness
 *           and saturation settings.
 */
enum class LightEffect {
    @Json(name = "none") NONE,
    @Json(name = "colorloop") COLOR_LOOP
}

/**
 * Method of setting the color of the light.
 *
 * @property HUE_SATURATION A hue/saturation combination.
 * @property COORDINATES CIE Color Coordinates.
 * @property COLOR_TEMPERATURE Mired Color temperature.
 */
enum class ColorMode {
    @Json(name = "hs") HUE_SATURATION,
    @Json(name = "xy") COORDINATES,
    @Json(name = "ct") COLOR_TEMPERATURE
}

/**
 * Values for a light's Alert State.
 *
 * @property NONE The light is not performing an alert effect.
 * @property SELECT The light is performing one breathe cycle.
 * @property L_SELECT The light is performing breathe cycles for 15
 *           seconds or until an "alert": "none" command is received.
 */
enum class AlertState {
    @Json(name = "none") NONE,
    @Json(name = "select") SELECT,
    @Json(name = "lselect") L_SELECT
}
