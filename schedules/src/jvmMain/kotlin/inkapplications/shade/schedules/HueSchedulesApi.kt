package inkapplications.shade.schedules

import inkapplications.shade.constructs.*
import inkapplications.shade.constructs.serialization.LocalDateTimeSerializer
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * API Access for Hue's Schedules
 */
internal interface HueSchedulesApi {
    /**
     * Gets a list of all schedules that have been added to the bridge.
     */
    suspend fun getSchedules(token: String): Map<String, Schedule>

    /**
     * Allows the user to create new schedules.
     *
     * The bridge can store up to 100 schedules.
     */
    suspend fun createSchedule(token: String, schedule: ScheduleCreation): HueResponse<IdToken>

    /**
     * Allows the user to change attributes of a schedule.
     */
    suspend fun updateSchedule(
        token: String,
        schedule: String,
        modification: ScheduleModification
    ): HueResponse<HueProperties>

    /**
     * Gets all attributes for a schedule.
     */
    suspend fun getSchedule(token: String, schedule: String): Schedule

    /**
     * Deletes a schedule from the bridge.
     */
    suspend fun deleteSchedule(
        token: String,
        schedule: String
    ): HueResponse<String>
}

/**
 * Attributes of a scheduled operation.
 *
 * @param name The name of the schedule.
 * @param description Description of the schedule.
 * @param command Request to execute when the scheduled event occurs.
 * @param localTime Time when the scheduled event will occur.
 * @param created Timestamp when the schedule was created
 * @param status The current execution status of the schedule
 * @param autoDelete If set to true, the schedule will be removed
 *        automatically if expired, if set to false it will be
 *        disabled. Default is true
 * @param startTime Timestamp that the timer was started. Only provided
 *        for timers.
 */
@Serializable
data class Schedule(
    val name: String,
    val description: String,
    val command: Command,
    @SerialName("localtime")
    val localTime: TimePattern?,
    @Serializable(with = LocalDateTimeSerializer::class)
    val created: LocalDateTime,
    val status: Status?,
    @SerialName("autodelete")
    val autoDelete: Boolean?,
    @SerialName("starttime")
    @Serializable(with = LocalDateTimeSerializer::class)
    val startTime: Instant?
) {
    @Deprecated(
        message = "Use LocalTime",
        level = DeprecationLevel.ERROR,
        replaceWith = ReplaceWith("localTime")
    )
    @Transient
    val time: TimePattern? = localTime
}

/**
 * Attributes for creating a schedule.
 *
 * @param name The name of the schedule.
 * @param description Description of the schedule.
 * @param command Request to execute when the scheduled event occurs.
 * @param localTime Time when the scheduled event will occur.
 * @param status The current execution status of the schedule
 * @param autoDelete If set to true, the schedule will be removed
 *        automatically if expired, if set to false it will be
 *        disabled. Default is true.
 * @param recycle When true: Resource is automatically deleted when not
 *        referenced anymore in any resource link. Only on creation of
 *        resource. “false” when omitted.
 */
@Serializable
internal data class ScheduleCreation(
    val command: Command,
    @SerialName("localtime") val localTime: TimePattern,
    val name: String? = null,
    val description: String? = null,
    val status: Status? = null,
    @SerialName("autodelete") val autoDelete: Boolean? = null,
    val recycle: Boolean? = null
)

/**
 *Attributes for editing an existing schedule.
 *
 * @param name The name of the schedule.
 * @param description Description of the schedule.
 * @param command Request to execute when the scheduled event occurs.
 * @param localTime Time when the scheduled event will occur.
 * @param status The current execution status of the schedule
 * @param autoDelete If set to true, the schedule will be removed
 *        automatically if expired, if set to false it will be
 *        disabled. Default is true.
 */
internal data class ScheduleModification(
    val command: Command? = null,
    @SerialName("localtime") val localTime: TimePattern? = null,
    val name: String? = null,
    val description: String? = null,
    val status: Status? = null,
    @SerialName("autodelete") val autoDelete: Boolean? = null
)

/**
 * Status states for a schedule.
 *
 * Application is only allowed to set “enabled” or “disabled”.
 * Disabled causes a timer to reset when activated (i.e. stop & reset).
 */
enum class Status {
    @SerialName("enabled")
    ENABLED,
    @SerialName("disabled")
    DISABLED,
}
