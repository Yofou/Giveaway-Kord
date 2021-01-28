package jobs

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.utils.toHuman
import com.kotlindiscord.kord.extensions.utils.toReaction
import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.edit
import dev.kord.core.entity.channel.TextChannel
import io.kesselring.sukejura.pattern.Minutes
import io.kesselring.sukejura.sukejura
import kotlinx.coroutines.flow.toList
import models.Posts
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun giveawayUpdate(client: ExtensibleBot) = sukejura {
    schedule {
        minutes { (0..59 step 1).map { Minutes.M(it) } }

        task {
            val posts = transaction {
                Posts.select { Posts.rolled eq false }.toList()
            }

            for ( post in posts ) {
                val channel = client.kord.getChannel( Snowflake( post[Posts.channelId] ) ) as TextChannel? ?: continue
                val message = channel.getMessageOrNull( Snowflake( post[Posts.messageId] ) ) ?: continue

                val remaining =  post[Posts.deadline].toEpochSecond( ZoneOffset.UTC ) - LocalDateTime.now().toEpochSecond( ZoneOffset.UTC )
                val duration = java.time.Duration.ofSeconds( remaining )
                val endtime = post[Posts.deadline].format(DateTimeFormatter.ofPattern("EEEE, LLLL d, Y"))

                message.edit {
                    when ( remaining < 0 ) {
                        true -> {
                            val users = message.getReactors( "🎉".toReaction() ).toList().filter { !it.isBot }
                            val picks = if ( users.size > 0 ) {
                                users.shuffled().take(post[Posts.winners]).map { it.mention }
                            } else {
                                listOf("No", "Winners")
                            }
                            val desc = "Winners: ${picks.joinToString(" ")}\nHosted by: <@${post[Posts.host]}>"

                            channel.createMessage {
                                val link = "https://discordapp.com/channels/${post[Posts.guildId]}/${post[Posts.channelId]}/${post[Posts.messageId]}"
                                content = "Prize: **${post[Posts.title]}**\n$desc\n$link"
                            }

                            transaction {
                                Posts.update({ Posts.id eq post[Posts.id] }) {
                                    it[rolled] = true
                                }

                                embed {
                                    color = Color(127, 179, 213)
                                    title = post[Posts.title]
                                    description = desc

                                    footer {
                                        text = "${post[Posts.winners]} Winners | Ended at • $endtime"
                                    }
                                }
                            }

                            message.unpin()
                        }

                        else -> {
                            embed {
                                color = Color(127, 179, 213)
                                title = post[Posts.title]
                                description =
                                    "React with with 🎉 to enter!\nTime remaining ${duration.toHuman()}\nHosted by <@${post[Posts.host]}>"
                                footer {
                                    text = "${post[Posts.winners]} Winners | Ends at • $endtime"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}