package extenstions

import arguments.GiveawayFinderArguments
import com.kotlindiscord.kord.extensions.CommandException
import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.utils.toReaction
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.edit
import kotlinx.coroutines.flow.toList
import models.Posts
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import giveawayUtils.giveawayRoll
import java.time.format.DateTimeFormatter

@KordPreview
class Roll(bot: ExtensibleBot): Extension(bot) {
    override val name = "Roll"

    override suspend fun setup() {
        slashCommand(::GiveawayFinderArguments) {
            name = "roll"
            val notice = "(IMPORTANT after 6 months of it's initial roll, it can no longer be rolled anymore)"
            description =
                "roll's a giveaway of it's prize before or after it's deadline"
            guild = Snowflake("796293218941534238")

            action {
                val message = arguments.message.parsed
                val post = transaction {
                    Posts.select { Posts.messageId eq message.id.asString }.firstOrNull()
                } ?: throw CommandException("Please ensure you have picked a giveaway message. $notice")

                val endtime = post[Posts.deadline].format(DateTimeFormatter.ofPattern("EEEE, LLLL d, Y"))
                val users = message.getReactors("🎉".toReaction()).toList().filter { !it.isBot }
                val picks = if (users.isNotEmpty()) {
                    users.shuffled().take(post[Posts.winners]).map { it.mention }
                } else {
                    listOf("No", "Winners")
                }
                val desc = "Winners: ${picks.joinToString(" ")}\nHosted by: <@${post[Posts.host]}>"

                message.edit {
                    transaction {
                        Posts.update({ Posts.id eq post[Posts.id] }) {
                            it[rolled] = true
                        }

                        embed(
                            giveawayRoll(
                                post[Posts.title],
                                desc,
                                endtime,
                                post[Posts.image],
                                post[Posts.winners]
                            )
                        )
                    }
                }.also {
                    it.unpin()
                }

                message.channel.createMessage {
                    val link = "https://discordapp.com/channels/${post[Posts.guildId]}/${post[Posts.channelId]}/${post[Posts.messageId]}"
                    content = "Prize: **${post[Posts.title]}**\n$desc\n$link"
                }
            }
        }
    }
}