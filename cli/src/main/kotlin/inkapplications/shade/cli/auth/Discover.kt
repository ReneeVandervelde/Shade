package inkapplications.shade.cli.auth

import com.github.ajalt.clikt.core.CliktCommand
import dagger.Reusable
import inkapplications.shade.Shade
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.time.ExperimentalTime

@Reusable
@OptIn(ExperimentalTime::class)
class Discover @Inject constructor(
    private val shade: Shade
): CliktCommand(
    name = "discover",
    help = "Find hue bridges on the network"
) {
    override fun run() {
        runBlocking {
            shade.discovery.getDevices().forEach {
                echo("found device: ${it.id} ip: ${it.ip}")
            }
        }
    }
}
