import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.extensions.Extension
import dev.kord.core.event.gateway.ReadyEvent
import io.github.cdimascio.dotenv.dotenv
import jobs.*
import models.Posts
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.logger.Level
import org.reflections.Reflections

suspend fun main() {
    val env = dotenv()

    val client = ExtensibleBot( env["token"] ) {
        koinLogLevel = Level.DEBUG

        commands {
            slashCommands = true
        }

        extensions {
            val reflections = Reflections("extenstions")
            for ( extension in reflections.getSubTypesOf( Extension::class.java ) ) {
                val extenPrimeConstructor = extension::kotlin.get().constructors.first()
                add( extenPrimeConstructor as (ExtensibleBot) -> Extension )
            }
        }
    }

    Database.connect("jdbc:sqlite:${env["db_uri"]}", "org.sqlite.JDBC")
    transaction { SchemaUtils.create(Posts) }

    // Start my cron jobs
    giveawayUpdate(client).start()
    giveawayDelete().start()

    client.start()
}