package inkapplications.shade.groups

import inkapplications.shade.constructs.*
import inkapplications.shade.constructs.serialization.DurationDecisecondSerializer
import inkapplications.shade.lights.AlertState
import inkapplications.shade.lights.LightEffect
import inkapplications.shade.lights.LightState
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * A Special group ID containing all lamps known by the bridge.
 */
const val GROUP_ALL = "0"

/**
 * Create the path for modifying the state of a Group
 */
internal fun createHueGroupsStateUrl(
    token: String,
    groupId: String,
) = "api/$token/groups/$groupId/action"

/**
 * Creates a Command object used for scheduling a future modification.
 *
 * @param token The Auth token to use when making the request.
 * @param group The Group of lights to modify the state of.
 * @param modification State settings to apply to the group.
 */
@ExperimentalTime
fun createGroupModificationCommand(
    token: String,
    group: String,
    modification: GroupStateModification
) = Command(
    address = createHueGroupsStateUrl(token, group),
    method = "PUT",
    body = Json.encodeToString(GroupStateModification.serializer(), modification)
)

/**
 * API Access for Hue's Groups endpoints.
 */
@ExperimentalTime
internal interface HueGroupsApi {
    /**
     * Gets a list of all groups that have been added to the bridge.
     *
     * A group is a list of lights that can be created, modified and
     * deleted by a user.
     */
    suspend fun getAll(token: String): Map<String, Group>

    /**
     * Creates a new group containing the lights specified and optional name.
     *
     * A new group is created in the bridge with the next available id.
     *
     * @param group The editable attributes of the group
     */
    suspend fun createGroup(token: String, group: MutableGroupAttributes): IdToken

    /**
     * Get a single group's attributes.
     *
     * @param groupId The unique ID of the group to fetch.
     */
    suspend fun getGroup(token: String, groupId: String): Group

    /**
     * Allows the user to modify the name, light and class membership of a group.
     *
     * @param groupId The unique ID of the group to update.
     * @param attributes The editable attributes to set. Optional data will be unmodified.
     */
    suspend fun updateGroup(
        token: String,
        groupId: String,
        attributes: MutableGroupAttributes
    ): HueResponse<HueProperties>

    /**
     * Modifies the state of all lights in a group.
     *
     * @param groupId The unique ID of the group to update.
     * @param state The state to assign to all lights in the group.
     */
    suspend fun setState(
        token: String,
        groupId: String,
        state: GroupStateModification
    ): HueResponse<HueProperties>

    /**
     * Deletes the specified group from the bridge.
     *
     * @param groupId The unique ID of the group to delete.
     */
    suspend fun deleteGroup(
        token: String,
        groupId: String
    ): HueResponse<String>
}

/**
 * A Group of hue devices.
 */
@Serializable
sealed class Group {
    /**
     * A unique, editable name given to the group.
     */
    abstract val name: String

    /**
     * The IDs of the lights that are in the group.
     */
    abstract val lights: List<String>?

    /**
     * Room Group.
     *
     * A group of lights that are physically located in the same place
     * in the house. Rooms behave similar as light groups, except:
     * (1) A room can be empty and contain 0 lights,
     * (2) a light is only allowed in one room and
     * (3) a room isn’t automatically deleted when all lights in that
     * room are deleted.
     *
     * @param name A unique, editable name given to the group.
     * @param lights The IDs of the lights that are in the group.
     * @param sensors The IDs of Hue sensors in the group.
     * @param recycle No documentation for this was provided.
     *        If you know what it does, let us know.
     * @param roomType Category of Room.
     * @param state current state descriptors about the entire room's devices
     * @param lastAction The light state of one of the lamps in the group.
     */
    @Serializable
    @SerialName("Room")
    data class Room(
        override val name: String,

        override val lights: List<String>?,

        val sensors: List<String>?,

        val recycle: Boolean,

        @SerialName("class")
        val roomType: RoomType,

        val state: GroupState,

        @SerialName("action")
        val lastAction: LightState?,
    ): Group()

    /**
     * Multisource luminaire group.
     *
     * A lighting installation of default groupings of hue lights.
     * The bridge will pre-install these groups for ease of use.
     * This type cannot be created manually.  Also, a light can only
     * be in a maximum of one luminaire group. See multisource
     * luminaires for more info.
     *
     * There is very little documentation about what this group type
     * is or what its properties are. If you know more, let me know.
     *
     * @param name A unique, editable name given to the group.
     * @param lights The IDs of the lights that are in the group.
     * @param modelId Uniquely identifies the hardware model of the luminaire.
     */
    @Serializable
    @SerialName("Luminaire")
    data class Luminaire(
        override val name: String,

        override val lights: List<String>?,

        @SerialName("modelid")
        val modelId: String,
    ): Group()

    /**
     * Lightsource Group.
     *
     * Note: This type has a copy/paste error in Hue's documentation.
     * As a result, there's no way to know what it does. Do you know?
     *
     * There is very little documentation about what this group type
     * is or what its properties are. If you know more, let me know.
     *
     * @param name A unique, editable name given to the group.
     * @param lights The IDs of the lights that are in the group.
     * @param uuid Unique Id in AA:BB:CC:DD format for Luminaire
     *        groups or AA:BB:CC:DD-XX format for Lightsource groups,
     *        where XX is the lightsource position.
     */
    @Serializable
    @SerialName("Lightsource")
    data class Lightsource(
        override val name: String,

        override val lights: List<String>?,

        @SerialName("uniqueid")
        val uuid: String,
    ): Group()

    /**
     * Light group.
     *
     * A group of lights that can be controlled together. This the
     * default group type that the bridge generates for user created
     * groups. Default type when no type is given on creation.
     *
     * There is very little documentation about what this group type
     * is or what its properties are. If you know more, let me know.
     *
     * @param name A unique, editable name given to the group.
     * @param lights The IDs of the lights that are in the group.
     */
    @Serializable
    @SerialName("LightGroup")
    data class LightGroup(
        override val name: String,
        override val lights: List<String>?,
    ): Group()

    /**
     * Represents an entertainment setup
     *
     * Entertainment group describe a group of lights that are used in
     * an entertainment setup. Locations describe the relative position
     * of the lights in an entertainment setup. E.g. for TV the
     * position is relative to the TV. Can be used to configure
     * streaming sessions.
     *
     * Entertainment group behave in a similar way as light groups,
     * with the exception: it can be empty and contain 0 lights. The
     * group is also not automatically recycled when lights are
     * deleted. The group of lights can be controlled together as in
     * LightGroup.
     *
     * There is very little documentation about what this group type
     * is or what its properties are. If you know more, let me know.
     *
     * @param name A unique, editable name given to the group.
     * @param lights The IDs of the lights that are in the group.
     * @param recycle No documentation for this was provided.
     *        If you know what it does, let us know.
     * @param stream Info about streaming state for the group.
     * @param locations The relative position of the lights in an
     *        entertainment setup. E.g. for TV the position is relative
     *        to the TV. Can be used to configure streaming sessions.
     */
    @Serializable
    @SerialName("Entertainment")
    data class Entertainment(
        override val name: String,
        override val lights: List<String>?,
        val recycle: Boolean,
        val stream: StreamInfo,
        val locations: Map<String, List<Float>>,
    ): Group()

    /**
     * Zones describe a group of lights that can be controlled together.
     *
     * Zones can be empty and contain 0 lights. A light is allowed to
     * be in multiple zones.
     *
     * No idea how this differs from any other group.
     * There is very little documentation about what this group type
     * is or what its properties are. If you know more, let me know.
     *
     * @param name A unique, editable name given to the group.
     * @param lights The IDs of the lights that are in the group.
     */
    @Serializable
    @SerialName("Zone")
    data class Zone(
        override val name: String,
        override val lights: List<String>?,
    ): Group()
}

/**
 * Info about streaming state for a group.
 *
 * There is very little documentation about what this info
 * is or what its properties are. If you know more, let me know.
 */
@Serializable
data class StreamInfo(
    @SerialName("proxymode")
    val proxyMode: String,

    @SerialName("proxynode")
    val proxyNode: String,

    @SerialName("active")
    val active: Boolean,

    @SerialName("owner")
    val owner: String?,
)

/**
 * Category of Room types.
 */
@Serializable
enum class RoomType {
    @SerialName("Living room") LIVING_ROOM,
    @SerialName("Kitchen") KITCHEN,
    @SerialName("Dining") DINING,
    @SerialName("Bedroom") BEDROOM,
    @SerialName("Kids bedroom") KIDS_BEDROOM,
    @SerialName("Bathroom") BATHROOM,
    @SerialName("Nursery") NURSERY,
    @SerialName("Recreation") RECREATION,
    @SerialName("Office") OFFICE,
    @SerialName("Gym") GYM,
    @SerialName("Hallway") HALLWAY,
    @SerialName("Toilet") TOILET,
    @SerialName("Front door") FRONT_DOOR,
    @SerialName("Garage") GARAGE,
    @SerialName("Terrace") TERRACE,
    @SerialName("Garden") GARDEN,
    @SerialName("Driveway") DRIVEWAY,
    @SerialName("Carport") CARPORT,
    @SerialName("Home") HOME,
    @SerialName("Downstairs") DOWNSTAIRS,
    @SerialName("Upstairs") UPSTAIRS,
    @SerialName("Top floor") TOP_FLOOR,
    @SerialName("Attic") ATTIC,
    @SerialName("Guest room") GUEST_ROOM,
    @SerialName("Staircase") STAIRCASE,
    @SerialName("Lounge") LOUNGE,
    @SerialName("Man cave") MAN_CAVE,
    @SerialName("Computer") COMPUTER,
    @SerialName("Studio") STUDIO,
    @SerialName("Music") MUSIC,
    @SerialName("TV") TV,
    @SerialName("Reading") READING,
    @SerialName("Closet") CLOSET,
    @SerialName("Storage") STORAGE,
    @SerialName("Laundry room") LAUNDRY_ROOM,
    @SerialName("Balcony") BALCONY,
    @SerialName("Porch") PORCH,
    @SerialName("Barbecue") BARBECUE,
    @SerialName("Pool") POOL,
    @SerialName("Other") OTHER
}

/**
 * Current state descriptors about the entire room's devices.
 *
 * @param allOn Whether all lights in the group are currently on
 * @param anyOn Whether any lights in the group are currently on
 */
@Serializable
data class GroupState(
    @SerialName("all_on")
    val allOn: Boolean,

    @SerialName("any_on")
    val anyOn: Boolean,
)

/**
 * Attributes of a Light group that are modifiable.
 */
@Serializable
sealed class MutableGroupAttributes {
    /**
     * A unique, editable name given to the group. (optional)
     */
    abstract val name: String?

    /**
     * The IDs of the lights that are in the group. (optional)
     */
    abstract val lights: Set<String>?

    /**
     * Attributes of a Luminaire group that are modifiable.
     *
     * TODO: Need more information about this object's attributes.
     *
     * @param name A unique, editable name given to the group. (optional)
     * @param lights The IDs of the lights that are in the group. (optional)
     */
    @Serializable
    @SerialName("Luminaire")
    data class Luminaire(
        override val name: String? = null,
        override val lights: Set<String>? = null
    ): MutableGroupAttributes()

    /**
     * Attributes of a Lightsource group that are modifiable.
     *
     * TODO: Need more information about this object's attributes.
     *
     * @param name A unique, editable name given to the group. (optional)
     * @param lights The IDs of the lights that are in the group. (optional)
     */
    @Serializable
    @SerialName("Lightsource")
    data class Lightsource(
        override val name: String? = null,
        override val lights: Set<String>? = null
    ): MutableGroupAttributes()

    /**
     * Attributes of a Light group that are modifiable.
     *
     * TODO: Need more information about this object's attributes.
     *
     * @param name A unique, editable name given to the group. (optional)
     * @param lights The IDs of the lights that are in the group. (optional)
     */
    @Serializable
    @SerialName("LightGroup")
    data class LightGroup(
        override val name: String? = null,
        override val lights: Set<String>? = null
    ): MutableGroupAttributes()

    /**
     * Attributes of a Room group that are modifiable.
     *
     * @param name A unique, editable name given to the group. (optional)
     * @param lights The IDs of the lights that are in the group. (optional)
     * @param sensors The IDs of the sensors that are in the group. (optional)
     * @param roomType Category of Room, default is OTHER
     */
    @Serializable
    @SerialName("Room")
    data class Room(
        override val name: String? = null,

        override val lights: Set<String>? = null,

        val sensors: Set<String>? = null,

        @SerialName("class")
        val roomType: RoomType = RoomType.OTHER
    ): MutableGroupAttributes()

    /**
     * Attributes of an entertainment group that are modifiable.
     *
     * TODO: Need more information about this object's attributes.
     *
     * @param name A unique, editable name given to the group. (optional)
     * @param lights The IDs of the lights that are in the group. (optional)
     */
    @Serializable
    @SerialName("Entertainment")
    data class Entertainment(
        override val name: String? = null,
        override val lights: Set<String>? = null
    ): MutableGroupAttributes()

    /**
     * Attributes of an zone group that are modifiable.
     *
     * TODO: Need more information about this object's attributes.
     *
     * @param name A unique, editable name given to the group. (optional)
     * @param lights The IDs of the lights that are in the group. (optional)
     */
    @Serializable
    @SerialName("Zone")
    data class Zone(
        override val name: String? = null,
        override val lights: Set<String>? = null
    ): MutableGroupAttributes()
}

/**
 * Options when modifying the state of a group of lights.
 *
 * This is *almost* identical to a `LightStateModification`
 * but also gives the option to set a scene.
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
 * @property scene The scene identifier if the scene you wish to recall.
 */
@ExperimentalTime
@Serializable
data class GroupStateModification(
    val on: Boolean? = null,

    @SerialName("bri")
    val brightness: Percentage? = null,

    val hue: Int? = null,

    @SerialName("sat")
    val saturation: Percentage? = null,

    val effect: LightEffect? = null,

    @SerialName("transitiontime")
    @Serializable(with = DurationDecisecondSerializer::class)
    val transitionTime: Duration? = null,

    @SerialName("xy")
    val cieColorCoordinates: Coordinates? = null,

    @SerialName("ct")
    val colorTemperature: ColorTemperature? = null,

    val alert: AlertState? = null,

    @SerialName("bri_inc")
    val brightnessIncrement: Percentage? = null,

    @SerialName("sat_inc")
    val saturationIncrement: Percentage? = null,

    @SerialName("hue_inc")
    val hueIncrement: Int? = null,

    @SerialName("ct_inc")
    val colorTemperatureIncrement: ColorTemperature? = null,

    @SerialName("xy_inc")
    val cieCoordinateTranslation: Coordinates? = null,

    val scene: String? = null,
)
