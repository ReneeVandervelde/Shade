package inkapplications.shade.schedules

import inkapplications.shade.auth.TokenStorage
import inkapplications.shade.auth.UnauthorizedException
import inkapplications.shade.constructs.Command
import inkapplications.shade.groups.GroupStateModification
import inkapplications.shade.groups.createGroupModificationCommand
import inkapplications.shade.lights.LightStateModification
import inkapplications.shade.lights.createLightModificationCommand
import org.threeten.bp.LocalDateTime

/**
 * Provides access to Hue's Schedules
 */
interface ShadeSchedules {
    /**
     * Get a list of all schedules that have been added to the bridge.
     */
    suspend fun getSchedules(): Map<String, Schedule>

    /**
     * Create a schedule to change a Group's state
     *
     * @param group The ID of the group to change.
     * @param modification The state changes to apply to the group.
     * @param time Local time when the scheduled event will occur.
     * @param name Name for the new schedule. If a name is not
     *        specified then the default name, “schedule”, is
     *        used. If the name is already taken a space and
     *        number will be appended by the bridge, e.g. “schedule 1”.
     * @param description Description of the new schedule. If the
     *        description is not specified it will be empty.
     * @param autoDelete If set to true, the schedule will be removed
     *        automatically if expired, if set to false it will be
     *        disabled. Default is true. Only for non-recurring schedules.
     * @param status Disabled causes a timer to reset when activated
     *        (i.e. stop & reset). “enabled” when not provided on creation.
     * @param recycle Automatically delete when not referenced anymore
     *        in any resource link. Only on creation of resource.
     *        “false” when omitted.
     * @return The ID of the schedule that was created.
     */
    suspend fun createGroupSchedule(
        group: String,
        modification: GroupStateModification,
        time: LocalDateTime,
        name: String? = null,
        description: String? = null,
        autoDelete: Boolean? = null,
        status: Status? = null,
        recycle: Boolean? = null
    ): String

    /**
     * Create a schedule to change a Light's state
     *
     * @param light The ID of the Light to change.
     * @param modification The state changes to apply to the light.
     * @param time Local time when the scheduled event will occur.
     * @param name Name for the new schedule. If a name is not
     *        specified then the default name, “schedule”, is
     *        used. If the name is already taken a space and
     *        number will be appended by the bridge, e.g. “schedule 1”.
     * @param description Description of the new schedule. If the
     *        description is not specified it will be empty.
     * @param autoDelete If set to true, the schedule will be removed
     *        automatically if expired, if set to false it will be
     *        disabled. Default is true. Only for non-recurring schedules.
     * @param status Disabled causes a timer to reset when activated
     *        (i.e. stop & reset). “enabled” when not provided on creation.
     * @param recycle Automatically delete when not referenced anymore
     *        in any resource link. Only on creation of resource.
     *        “false” when omitted.
     * @return The ID of the schedule that was created.
     */
    suspend fun createLightSchedule(
        light: String,
        modification: LightStateModification,
        time: LocalDateTime,
        name: String? = null,
        description: String? = null,
        autoDelete: Boolean? = null,
        status: Status? = null,
        recycle: Boolean? = null
    ): String

    /**
     * Create a generic Schedule.
     *
     * This will create a schedule for any command. For common
     * commands, such as Group and Light state changes, use
     * the convenience methods provided by this class.
     *
     * @see #createLightSchedule
     * @see #createGroupSchedule
     * @return The ID of the schedule that was created.
     */
    suspend fun createSchedule(
        command: Command,
        localTime: LocalDateTime,
        name: String? = null,
        description: String? = null,
        status: Status? = null,
        autoDelete: Boolean? = null,
        recycle: Boolean? = null
    ): String

    /**
     * Get data for a Schedule.
     *
     * @param schedule The ID of the schedule to fetch.
     */
    suspend fun getSchedule(schedule: String): Schedule
}

/**
 * Hue API Implementation of Shade's scheduling API.
 */
internal class ApiSchedules(
    private val schedulesApi: HueSchedulesApi,
    private val storage: TokenStorage
): ShadeSchedules {
    private suspend fun getToken() = storage.getToken() ?: throw UnauthorizedException()

    override suspend fun getSchedules(): Map<String, Schedule> = schedulesApi.getSchedules(getToken()).await()

    override suspend fun createSchedule(
        command: Command,
        localTime: LocalDateTime,
        name: String?,
        description: String?,
        status: Status?,
        autoDelete: Boolean?,
        recycle: Boolean?
    ) = schedulesApi.createSchedule(
        getToken(),
        ScheduleCreation(command, localTime, name, description, status, autoDelete, recycle)
    ).await().id

    override suspend fun createGroupSchedule(
        group: String,
        modification: GroupStateModification,
        time: LocalDateTime,
        name: String?,
        description: String?,
        autoDelete: Boolean?,
        status: Status?,
        recycle: Boolean?
    ): String {
        val token = getToken()

        return schedulesApi.createSchedule(
            token,
            ScheduleCreation(
                createGroupModificationCommand(token, group, modification),
                localTime = time,
                name = name,
                description = description,
                status = status,
                autoDelete = autoDelete,
                recycle = recycle
            )
        ).await().id
    }

    override suspend fun createLightSchedule(
        light: String,
        modification: LightStateModification,
        time: LocalDateTime,
        name: String?,
        description: String?,
        autoDelete: Boolean?,
        status: Status?,
        recycle: Boolean?
    ): String {
        val token = getToken()

        return schedulesApi.createSchedule(
            token,
            ScheduleCreation(
                createLightModificationCommand(token, light, modification),
                localTime = time,
                name = name,
                description = description,
                status = status,
                autoDelete = autoDelete,
                recycle = recycle
            )
        ).await().id
    }

    override suspend fun getSchedule(schedule: String): Schedule = schedulesApi.getSchedule(getToken(), schedule).await()
}