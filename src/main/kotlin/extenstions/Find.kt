package extenstions

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.ParseException
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.pagination.Paginator
import com.kotlindiscord.kord.extensions.pagination.pages.Page
import com.kotlindiscord.kord.extensions.pagination.pages.Pages
import com.kotlindiscord.kord.extensions.utils.toHuman
import dev.kord.common.Color
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.channel.TextChannel
import models.Posts
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.ZoneOffset

@OptIn(KordPreview::class)
class Find( bot: ExtensibleBot ): Extension(bot) {
    override val name = "Find"
    override suspend fun setup() {
        slashCommand {
            name = "find"
            description = "Finds all the giveaway's in this guild"
            guild = Snowflake("802200869755813958")
            showSource = true

            action {
                val posts = transaction { Posts.select { (Posts.guildId eq guild.id.asString) and (Posts.rolled eq false) }.toList().chunked(2) }

                if ( posts.isEmpty() ) throw ParseException("There are no active giveaway currently in ${guild.name}")
                val pages = posts.map {
                    val description = it.map {
                        val url = "https://discordapp.com/channels/${it[Posts.guildId]}/${it[Posts.channelId]}/${it[Posts.messageId]}"
                        val remaining =  it[Posts.deadline].toEpochSecond( ZoneOffset.UTC ) - LocalDateTime.now().toEpochSecond( ZoneOffset.UTC )
                        val duration = java.time.Duration.ofSeconds( remaining )
                        "$url\n```\n🔖 Title: ${it[Posts.title]}\n⏰ Time Remaining: ${duration.toHuman()}\n🏆 Number Of Winners: ${it[Posts.winners]}```"
                    }
                    Page(
                       title = "${guild.name}, Giveaways",
                       description = description.joinToString("\n".subSequence(0, 1)) { it },
                       color = Color(127, 179, 213)
                    )
                }

                val book = Pages()
                pages.forEach { book.addPage( it ) }
                Paginator(bot, channel as TextChannel, pages = book).also {
                    it.send()
                }
            }
        }
    }
}