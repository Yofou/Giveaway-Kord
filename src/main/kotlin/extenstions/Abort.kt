package extenstions

import arguments.*
import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.ParseException
import com.kotlindiscord.kord.extensions.extensions.Extension
import dev.kord.common.Color
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.edit
import models.Posts
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(KordPreview::class)
class Abort(bot: ExtensibleBot): Extension(bot) {
    override val name = "Abort"
    override suspend fun setup() {
        slashCommand(::GiveawayFinderArguments) {
            name = "abort"
            description = "Get's"
            guild = Snowflake("802200869755813958")

            action {
                val post = transaction {
                    Posts.select { Posts.messageId eq arguments.message.parsed.id.asString }.firstOrNull()
                } ?: throw ParseException("Please ensure you have picked a giveaway message.")

                val now = LocalDateTime.now().format( DateTimeFormatter.ofPattern("EEEE, LLLL d, Y") )
                arguments.message.parsed.edit {
                    embed {
                        color = Color(255, 114, 111)
                        title = post[Posts.title]
                        description = "**Sorry but this giveaway has been cancelled**"
                        footer { text = "${post[Posts.winners]} Winners | Canceled at • $now" }
                    }
                }.also {
                    it.deleteAllReactions()
                    it.unpin()
                }

                transaction {
                    Posts.deleteWhere {
                        Posts.messageId eq arguments.message.parsed.id.asString
                    }
                }
            }
        }
    }
}