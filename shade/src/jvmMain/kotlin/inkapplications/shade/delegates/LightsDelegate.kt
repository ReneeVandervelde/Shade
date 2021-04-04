package inkapplications.shade.delegates

import inkapplications.shade.auth.TokenStorage
import inkapplications.shade.constructs.Scan
import inkapplications.shade.lights.Light
import inkapplications.shade.lights.LightStateModification
import inkapplications.shade.lights.ShadeLights
import inkapplications.shade.lights.ShadeLightsModule
import kotlin.time.ExperimentalTime

@ExperimentalTime
internal class LightsDelegate(
    initialUrl: String?,
    private val storage: TokenStorage
): EndpointDelegate<ShadeLights>(initialUrl), ShadeLights {
    override fun createEndpoint(baseUrl: String): ShadeLights {
        return ShadeLightsModule().createLights(baseUrl, storage)
    }

    override suspend fun getLights(): Map<String, Light> = delegate.getLights()
    override suspend fun getNewLights(): Scan = delegate.getNewLights()
    override suspend fun setState(id: String, state: LightStateModification) = delegate.setState(id, state)
    override suspend fun search(vararg deviceIds: String) = delegate.search(*deviceIds)
    override suspend fun getLight(id: String): Light = delegate.getLight(id)
    override suspend fun rename(id: String, name: String) = delegate.rename(id, name)
    override suspend fun delete(id: String) = delegate.delete(id)
}
