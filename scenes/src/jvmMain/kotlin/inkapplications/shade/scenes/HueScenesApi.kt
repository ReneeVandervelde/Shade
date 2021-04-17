package inkapplications.shade.scenes

import inkapplications.shade.constructs.HueResponse
import inkapplications.shade.constructs.HueResult
import inkapplications.shade.constructs.IdToken
import inkapplications.shade.constructs.serialization.LocalDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import java.time.LocalDateTime

internal interface HueScenesApi {
    suspend fun getScenes(token: String): Map<String, Scene>
    suspend fun createScene(token: String, scene: CreateScene): List<HueResult<IdToken>>
    suspend fun getScene(token: String, sceneId: String): Scene
    suspend fun deleteScene(token: String, sceneId: String): HueResponse<String>
    suspend fun updateScene(token: String, sceneId: String, scene: UpdateScene.LightScene): HueResponse<JsonObject>
    suspend fun updateScene(token: String, sceneId: String, scene: UpdateScene.GroupScene): HueResponse<JsonObject>
}

@Serializable
sealed class Scene {
    abstract val name: String
    abstract val lights: List<String>
    abstract val owner: String
    abstract val recycle: Boolean
    abstract val locked: Boolean
    abstract val data: AppData
    abstract val picture: String?
    abstract val lastUpdated: LocalDateTime
    abstract val version: Int

    /**
     * Default Scene Type
     */
    @Serializable
    data class LightScene(
        override val name: String,
        override val lights: List<String>,
        override val owner: String,
        override val recycle: Boolean,
        override val locked: Boolean,
        @SerialName("appdata") override val data: AppData,
        override val picture: String?,
        @SerialName("lastupdated")
        @Serializable(with = LocalDateTimeSerializer::class)
        override val lastUpdated: LocalDateTime,
        override val version: Int
    ): Scene()

    /**
     * Represents a scene which links to a specific group.
     *
     * While creating a new GroupScene, the group attribute shall be provided.
     *
     * The lights array is a read-only attribute, it cannot be modified, and
     * shall not be provided upon GroupScene creation.
     *
     * When lights in a group is changed, the GroupScenes associated to this
     * group will be automatically updated with the new list of lights in the
     * group. The new lights added to the group will be assigned with default
     * states for associated GroupScenes.
     *
     * When a group is deleted or becomes empty, all the GroupScenes associated
     * to the group will be deleted automatically.
     */
    @Serializable
    data class GroupScene(
        override val name: String,
        val group: String,
        override val lights: List<String>,
        override val owner: String,
        override val recycle: Boolean,
        override val locked: Boolean,
        @SerialName("appdata") override val data: AppData,
        override val picture: String?,
        @SerialName("lastupdated")
        @Serializable(with = LocalDateTimeSerializer::class)
        override val lastUpdated: LocalDateTime,
        override val version: Int
    ): Scene()
}

internal sealed class CreateScene {
    abstract val name: String
    abstract val recycle: Boolean
    abstract val data: AppData?
    abstract val picture: String?

    @Serializable
    internal data class LightScene(
        override val name: String,
        val lights: List<String>,
        override val recycle: Boolean = false,
        @SerialName("appdata") override val data: AppData? = null,
        override val picture: String? = null
    ): CreateScene()

    @Serializable
    internal data class GroupScene(
        override val name: String,
        val group: String,
        override val recycle: Boolean = false,
        @SerialName("appdata")
        override val data: AppData? = null,
        override val picture: String? = null
    ): CreateScene()
}

@Serializable
data class AppData(
    val version: Int?,
    val data: String?
) {
    @Deprecated(
        message = "App Data is now a strictly typed object",
        level = DeprecationLevel.ERROR,
    )
    operator fun get(key: String): Any? = when (key) {
        "version" -> version
        "data" -> data
        else -> null
    }
}

internal sealed class UpdateScene {
    abstract val name: String?
    abstract val data: AppData?
    abstract val picture: String?

    @Serializable
    internal data class LightScene(
        override val name: String?,
        val lights: List<String>?,
        @SerialName("appdata") override val data: AppData? = null,
        override val picture: String? = null
    ): UpdateScene()

    @Serializable
    internal data class GroupScene(
        override val name: String?,
        @SerialName("appdata") override val data: AppData? = null,
        override val picture: String? = null
    ): UpdateScene()
}
