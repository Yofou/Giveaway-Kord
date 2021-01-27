package extenstions

import arguments.PostArguments
import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.ParseException
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.utils.addReaction
import com.kotlindiscord.kord.extensions.utils.toHuman
import dev.kord.common.Color
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.TextChannel
import models.Posts
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(KordPreview::class)
class Post(bot: ExtensibleBot) : Extension(bot) {
    override val name = "Post"

    override suspend fun setup() {
        slashCommand(::PostArguments) {
            name = "post"
            description = "Posts a giveaway message in a channel, that people can vote on"
            showSource = true
            guild = Snowflake("802200869755813958")

            val durationHelpMessage = "__How to use durations__\n\n" +
                    "Durations are specified in pairs of amounts and units - for example, `12d` would be 12 days. " +
                    "Compound durations are supported - for example, `2d12h` would be 2 days and 12 hours.\n\n" +
                    "The following units are supported:\n\n" +

                    "**Seconds:** `s`, `sec`, `second`, `seconds`\n" +
                    "**Minutes:** `m`, `mi`, `min`, `minute`, `minutes`\n" +
                    "**Hours:** `h`, `hour`, `hours`\n" +
                    "**Days:** `d`, `day`, `days`\n" +
                    "**Weeks:** `w`, `week`, `weeks`\n" +
                    "**Months:** `mo`, `month`, `months`\n" +
                    "**Years:** `y`, `year`, `years`"

            action {
                val duration = arguments.time.toHuman()
                    ?: throw ParseException("Ensure that you're duration value is larger than or equal to 1 second\n\n$durationHelpMessage")

                val host = arguments.host ?: this.member.asMember()
                val deadline = LocalDateTime.now().plusSeconds(arguments.time.seconds)
                val endtime = deadline.format(DateTimeFormatter.ofPattern("EEEE, LLLL d, Y"))

                guild.memberCount?.let {
                    if (arguments.winners > it) throw ParseException("Winner argument can't be larger than the guild member count itself")
                }

                var message: Message? = null
                val follow = followUp {
                    when (arguments.channel?.type?.value) {
                        null -> embed {
                            color = Color(127, 179, 213)
                            title = arguments.title
                            description =
                                "React with with 🎉 to enter!\nTime remaining $duration\nHosted by${host.mention}"
                            footer {
                                text = "${arguments.winners} Winners | Ends at • $endtime"
                            }
                        }

                        0 -> {
                            val channel = arguments.channel as TextChannel
                            message = channel.createEmbed {
                                color = Color(127, 179, 213)
                                title = arguments.title
                                description =
                                    "React with with 🎉 to enter!\nTime remaining $duration\nHosted by${host.mention}"
                                footer {
                                    text = "${arguments.winners} Winners | Ends at • $endtime"
                                }
                            }

                            content = "Giveaway Embed Created 🙌"
                        }

                        else -> content = "Please ensure you pick a text channel for a giveaway to be posted."
                    }
                }

                if ( message == null ) {
                    message = follow.message
                }

                if ( arguments.channel?.type?.value == null || arguments.channel?.type?.value == 0 ) {
                    message?.addReaction("🎉")

                    transaction {
                        Posts.insert {
                            it[messageId] = message!!.id.asString
                            it[guildId] = guild.id.asString
                            it[channelId] = message!!.channelId.asString
                            it[this.host] = host.id.asString
                            it[title] = arguments.title
                            it[this.deadline] = deadline
                            it[winners] = arguments.winners
                            it[createdAt] = LocalDateTime.now()
                        }
                    }
                }
            }
        }
    }
}