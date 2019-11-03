package inkapplications.shade.cli.auth

import com.github.ajalt.clikt.core.CliktCommand
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import inkapplications.shade.auth.TokenStorage

@Module
abstract class AuthModule {
    @Binds
    @IntoSet
    abstract fun command(command: Connect): CliktCommand

    @Binds
    abstract fun storage(fileStorage: FileStorage): TokenStorage
}
