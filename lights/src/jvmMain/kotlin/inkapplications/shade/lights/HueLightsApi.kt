package inkapplications.shade.lights

import inkapplications.shade.constructs.*
import inkapplications.shade.constructs.serialization.DurationDecisecondSerializer
import inkapplications.shade.constructs.serialization.LocalDateTimeSerializer
import inkapplications.shade.lights.AlertState.*
import inkapplications.shade.lights.ColorMode.*
import inkapplications.shade.lights.LightEffect.COLOR_LOOP
import inkapplications.shade.lights.LightEffect.NONE
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

internal fun createHueLightsStatePath(
    token: String,
    light: String,
) = "api/$token/lights/$light/state"

/**
 * Creates a Command object used for scheduling a future modification.
 *
 * @param token The Auth token to use when making the request.
 * @param light The light to modify the state of.
 * @param modification State settings to apply to the group.
 */
@ExperimentalTime
fun createLightModificationCommand(
    token: String,
    light: String,
    modification: LightStateModification
) = Command(
    address = createHueLightsStatePath(token, light),
    method = "PUT",
    body = Json.encodeToString(LightStateModification.serializer(), modification)
)

/**
 * Hue API endpoints for lights.
 */
@ExperimentalTime
internal interface HueLightsApi {
    /**
     * Get a list of all lights known to the hue bridge.
     */
    suspend fun getLights(token: String): Map<String, Light>

    /**
     * Set the state of a light.
     */
    suspend fun setState(
        token: String,
        lightId: String,
        modification: LightStateModification
    ): HueResponse<HueProperties>

    /**
     * Gets a list of lights that were discovered the last time a
     * search for new lights was performed. The list of new lights is
     * always deleted when a new search is started.
     */
    suspend fun getNewLights(token: String): Scan

    /**
     * Starts searching for new lights.
     *
     * The bridge will open the network for 40s. The overall search
     * might take longer since the configuration of (multiple) new
     * devices can take longer. If many devices are found the command
     * will have to be issued a second time after discovery time has
     * elapsed. If the command is received again during search the
     * search will continue for at least an additional 40s.
     *
     * When the search has finished, new lights will be available using
     * the get new lights command. In addition, the new lights will now
     * be available by calling get all lights or by calling get group
     * attributes on group 0. Group 0 is a special group that cannot
     * be deleted and will always contain all lights known by the bridge.
     *
     * @param criteria Serial numbers of lights to search for.
     * @return a pretty useless map that just contains the endpoint that was hit.
     */
    suspend fun searchLights(token: String, criteria: LightSearchCriteria): HueResponse<HueProperties>

    /**
     * Starts searching for new lights.
     *
     * @see HueLightsApi.searchLights(StringIndexOutOfBoundsException, LightSearchCriteria)
     */
    suspend fun searchLights(token: String): HueResponse<HueProperties>

    /**
     * Gets the attributes and state of a given light.
     *
     * @param lightId The local ID of the light to get attributes of.
     * @return The state of the light.
     */
    suspend fun getLightAttributes(token: String, lightId: String): Light

    /**
     * Used to rename lights.
     *
     * A light can have its name changed when in any state, including
     * when it is unreachable or off.
     *
     * @param lightId The local ID of the light to set attributes of.
     * @param attributes The Device Attributes to set.
     * @return a pretty useless map that just contains the endpoint that was hit.
     */
    suspend fun setLightAttributes(
        token: String,
        lightId: String,
        attributes: DeviceAttributes
    ): HueResponse<HueProperties>

    /**
     * Deletes a light from the bridge.
     */
    suspend fun delete(
        token: String,
        lightId: String
    ): HueResponse<String>
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
@Serializable
data class Light(
    val state: LightState,
    @SerialName("swupdate") val updateState: UpdateState,
    val type: String,
    val name: String,
    @SerialName("modelid") val modelId: String,
    @SerialName("manufacturername") val manufacturer: String,
    @SerialName("productname") val productName: String,
    val capabilities: Capabilities,
    val config: LightConfig,
    @SerialName("uniqueid") val uuid: String,
    @SerialName("swversion") val softwareVersion: String
)

/**
 * Info and Constraints of the bulb hardware.
 *
 * @property certified Whether daddy Philips approved this light.
 * @property control Constraints on commands for the light.
 * @property streaming Current light supports streaming features.
 */
@Serializable
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
@Serializable
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
@Serializable
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
@Serializable
data class LightStartupConfig(
    val mode: String,
    val configured: Boolean,
    @SerialName("customsettings") val customSettings: LightCustomStartupSettings?
)

/**
 * Custom parameters for how a light should turn on.
 *
 * @property brightness Brightness of the light.
 * @property cieColorCoordinates The x and y coordinates of a color in
 *           CIE color space.The first entry is the x coordinate and
 *           the second entry is the y coordinate. Both x and y must be
 *           between 0 and 1.
 *           If the specified coordinates are not in the CIE color space,
 *           the closest color to the coordinates will be chosen.
 * @property colorTemperature The Color temperature of the light.
 *           2012 connected lights are capable of 153 (6500K) to 500 (2000K).
 * @property hue Hue of the light. This is a wrapping value between 0
 *           and 65535. Note, that hue/sat values are hardware
 *           dependent which means that programming two devices with
 *           the same value does not garantuee that they will be the
 *           same color. Programming 0 and 65535 would mean that the
 *           light will resemble the color red, 21845 for green and
 *           43690 for blue.
 * @property saturation Saturation of the light. 254 is the most
 *           saturated (colored) and 0 is the least saturated (white).
 */
@Serializable
data class LightCustomStartupSettings(
    @SerialName("bri") val brightness: Percentage?,
    @SerialName("xy") val cieColorCoordinates: Coordinates?,
    @SerialName("ct") val colorTemperature: ColorTemperature?,
    val hue: Int?,
    @SerialName("sat") val saturation: Percentage?
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
@Serializable
data class ControlCapabilities(
    @SerialName("mindimlevel") val minimumDimLevel: Int?,
    @SerialName("maxlumen") val maximumLumens: Int?,
    @SerialName("colorgamuttype") val colorGamutType: String?,
    @SerialName("colorgamut") val colorGamut: List<Coordinates>?,
    @SerialName("ct") val colorTemperatures: ClosedRange<ColorTemperature>?
)

/**
 * Information about the light's firmware.
 *
 * @property state Whether there are updates for the light. The values
 *           of this aren't documented anywhere I can find.
 * @property lastInstall Timestamp of the last firmware update.
 */
@Serializable
data class UpdateState(
    val state: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    @SerialName("lastinstall")
    val lastKnownInstall: LocalDateTime?,
) {
    @Deprecated(
        message = "Use the nullable `lastKnownInstall` field",
        replaceWith = ReplaceWith("lastKnownInstall"),
        level = DeprecationLevel.ERROR,
    )
    val lastInstall: LocalDateTime get() = lastKnownInstall ?: Instant.DISTANT_PAST.toLocalDateTime(TimeZone.UTC)
}

/**
 * State of a light.
 *
 * @property on On/Off state of the light. On=true, Off=false
 * @property brightness Brightness of the light.
 * @property hue Hue of the light. This is a wrapping value between 0
 *           and 65535. Note, that hue/sat values are hardware
 *           dependent which means that programming two devices with
 *           the same value does not garantuee that they will be the
 *           same color. Programming 0 and 65535 would mean that the
 *           light will resemble the color red, 21845 for green and
 *           43690 for blue.
 * @property saturation Saturation of the light.
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
 * @property colorTemperature The Color temperature of the light.
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
@Serializable
data class LightState(
    val on: Boolean,
    @SerialName("bri") val brightness: Percentage?,
    val hue: Int?,
    @SerialName("sat") val saturation: Percentage?,
    val effect: LightEffect?,
    @SerialName("xy") val cieColorCoordinates: Coordinates?,
    @SerialName("ct") val colorTemperature: ColorTemperature?,
    val alert: AlertState?,
    @SerialName("colormode") val colorMode: ColorMode?,
    val mode: String?,
    val reachable: Boolean?
)

/**
 * Options when modifying the state of a light.
 *
 * @property on On/Off state of the light. On=true, Off=false
 * @property brightness Brightness of the light.
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
 * @property colorTemperature The Color temperature of the light.
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
@ExperimentalTime
@Serializable
data class LightStateModification(
    val on: Boolean? = null,
    @SerialName("bri") val brightness: Percentage? = null,
    val hue: Int? = null,
    @SerialName("sat") val saturation: Percentage? = null,
    val effect: LightEffect? = null,
    @SerialName("transitiontime")
    @Serializable(with = DurationDecisecondSerializer::class)
    val transitionTime: Duration? = null,
    @SerialName("xy") val cieColorCoordinates: Coordinates? = null,
    @SerialName("ct") val colorTemperature: ColorTemperature? = null,
    val alert: AlertState? = null,
    @SerialName("bri_inc") val brightnessIncrement: Percentage? = null,
    @SerialName("sat_inc") val saturationIncrement: Percentage? = null,
    @SerialName("hue_inc") val hueIncrement: Int? = null,
    @SerialName("ct_inc") val colorTemperatureIncrement: ColorTemperature? = null,
    @SerialName("xy_inc") val cieCoordinateTranslation: Coordinates? = null
)

/**
 * Dynamic Effects for lights.
 *
 * @property NONE Behaves normally.
 * @property COLOR_LOOP cycle through all hues using the current brightness
 *           and saturation settings.
 */
@Serializable
enum class LightEffect {
    @SerialName("none") NONE,
    @SerialName("colorloop") COLOR_LOOP
}

/**
 * Method of setting the color of the light.
 *
 * @property HUE_SATURATION A hue/saturation combination.
 * @property COORDINATES CIE Color Coordinates.
 * @property COLOR_TEMPERATURE Mired Color temperature.
 */
@Serializable
enum class ColorMode {
    @SerialName("hs") HUE_SATURATION,
    @SerialName("xy") COORDINATES,
    @SerialName("ct") COLOR_TEMPERATURE
}

/**
 * Values for a light's Alert State.
 *
 * @property NONE The light is not performing an alert effect.
 * @property SELECT The light is performing one breathe cycle.
 * @property L_SELECT The light is performing breathe cycles for 15
 *           seconds or until an "alert": "none" command is received.
 */
@Serializable
enum class AlertState {
    @SerialName("none") NONE,
    @SerialName("select") SELECT,
    @SerialName("lselect") L_SELECT
}

/**
 * Used to specify Device ID's when finding new lights.
 *
 * @param deviceId Serial numbers of devices to search for.
 */
@Serializable
internal data class LightSearchCriteria(
    @SerialName("deviceid") val deviceId: List<String>
)
