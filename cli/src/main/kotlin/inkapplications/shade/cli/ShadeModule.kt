package inkapplications.shade.cli

import dagger.Module
import dagger.Provides
import dagger.Reusable
import inkapplications.shade.Shade
import inkapplications.shade.cli.auth.FileStorage
import kotlin.time.ExperimentalTime

@Module
@OptIn(ExperimentalTime::class)
class ShadeModule {
    @Provides
    @Reusable
    fun shade(fileStorage: FileStorage): Shade {
        val url = fileStorage.getUrl()
        return Shade(
            appId = fileStorage.getAppId(),
            initBaseUrl = url,
            storage = fileStorage
        )
    }
}
