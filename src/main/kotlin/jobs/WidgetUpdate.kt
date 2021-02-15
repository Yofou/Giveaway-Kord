package jobs

import com.kotlindiscord.kord.extensions.ExtensibleBot
import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.edit
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.firstOrNull
import io.kesselring.sukejura.pattern.Minutes
import io.kesselring.sukejura.sukejura
import kotlinx.coroutines.flow.count
import models.WidgetMessages
import models.WidgetRoles
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

suspend fun widgetUpdate(client: ExtensibleBot) = sukejura {
    schedule {
        minutes { (0..59 step 1).map { Minutes.M(it) } }

        task {
            val widgets = transaction { WidgetMessages.selectAll().toList() }
            for ( widget in widgets ) {
                val channel = client.kord.getChannel(Snowflake( widget[WidgetMessages.channelId] )) as TextChannel?
                    ?: return@task transaction { WidgetMessages.deleteWhere { WidgetMessages.id eq widget[WidgetMessages.id] } }

                val message = channel.getMessageOrNull(Snowflake( widget[WidgetMessages.messageId] ))
                    ?: return@task transaction { WidgetMessages.deleteWhere { WidgetMessages.id eq widget[WidgetMessages.id] } }


                val roles = transaction{ WidgetRoles.select { WidgetRoles.guildId eq widget[WidgetMessages.guildId] }.toList() }
                val count = roles.map { result ->
                    channel.guild.roles.firstOrNull { role -> role.id.asString == result[models.WidgetRoles.roleId] }?.let {
                        channel.guild.members.count { member ->
                            member.roleIds.contains(it.id)
                        }
                    } ?: 0
                }.sum()

                message.edit {
                    embed {
                        color = Color(127, 179, 213)
                        title = widget[WidgetMessages.title]
                        description = widget[WidgetMessages.content].replace("role_count", count.toString()).replace("\\n", "\n")
                    }
                }
            }
        }

    }
}