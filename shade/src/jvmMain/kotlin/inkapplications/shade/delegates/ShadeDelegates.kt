package inkapplications.shade.delegates

import inkapplications.shade.InMemoryStorage
import inkapplications.shade.auth.TokenStorage
import kotlin.time.ExperimentalTime

/**
 * Container for the internal implementations of Shade's API's.
 *
 * @param initBaseUrl The base url to use for the endpoints initially. (optional)
 * @param appId The App ID to use for authentication.
 */
@ExperimentalTime
internal class ShadeDelegates(
    initBaseUrl: String?,
    appId: String,
    storage: TokenStorage = InMemoryStorage,
) {
    val auth = AuthDelegate(initBaseUrl, appId, storage)
    val lights = LightsDelegate(initBaseUrl, storage)
    val groups = GroupsDelegate(initBaseUrl, storage)
    val schedules = SchedulesDelegate(initBaseUrl, storage)
    val scenes = ScenesDelegate(initBaseUrl, storage)

    val all = listOf(auth, lights, groups, schedules, scenes)
}
