package inkapplications.shade.delegates

import inkapplications.shade.auth.TokenStorage
import inkapplications.shade.scenes.AppData
import inkapplications.shade.scenes.Scene
import inkapplications.shade.scenes.ShadeScenes
import inkapplications.shade.scenes.ShadeScenesModule

internal class ScenesDelegate(
    initialUrl: String?,
    private val storage: TokenStorage
): EndpointDelegate<ShadeScenes>(initialUrl), ShadeScenes {
    override fun createEndpoint(baseUrl: String): ShadeScenes {
        return ShadeScenesModule().createScenes(baseUrl, storage)
    }
    override suspend fun getScenes(): Map<String, Scene> = delegate.getScenes()

    override suspend fun createLightScene(name: String, lights: List<String>, picture: String?, data: AppData?): String {
        return delegate.createLightScene(name, lights, picture, data)
    }

    override suspend fun createGroupScene(name: String, group: String, picture: String?, data: AppData?): String {
        return delegate.createGroupScene(name, group, picture, data)
    }

    override suspend fun updateLightScene(id: String, name: String?, lights: List<String>?, picture: String?, data: AppData?) {
        return delegate.updateLightScene(id, name, lights, picture, data)
    }

    override suspend fun updateGroupScene(id: String, name: String?, picture: String?, data: AppData?) {
        return delegate.updateGroupScene(id, name, picture, data)
    }

    override suspend fun getScene(id: String): Scene = delegate.getScene(id)

    override suspend fun deleteScene(id: String) = delegate.deleteScene(id)
}
