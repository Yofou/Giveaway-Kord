package extenstions

import arguments.WidgetMessageEdit
import arguments.WidgetMessagePost
import arguments.WidgetRoles
import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.pagination.Paginator
import com.kotlindiscord.kord.extensions.pagination.pages.Page
import com.kotlindiscord.kord.extensions.pagination.pages.Pages
import dev.kord.common.Color
import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.firstOrNull
import dev.kord.gateway.PrivilegedIntent
import kotlinx.coroutines.flow.*
import models.WidgetMessages
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

@KordPreview
class Widget(bot: ExtensibleBot): Extension(bot)  {

    override val name = "Widget"

    @OptIn(PrivilegedIntent::class)
    @KordExperimental
    override suspend fun setup() {
        slashCommand {
            name = "widget"
            description = "Custom widget command that keeps tracks of all the members with a cerain role"
            guild = Snowflake("796293218941534238")

            group("roles") {
                description = "group of commands to control the member roles to keep track of"

                subCommand(::WidgetRoles) {
                    name = "add"
                    description = "adds a role to the widget list"
                    action {
                        guild ?: return@action

                        val role = arguments.role
                        val roles = transaction {
                            models.WidgetRoles.select {
                                (models.WidgetRoles.guildId eq guild!!.id.asString) and (models.WidgetRoles.roleId eq role.id.asString)
                            }.firstOrNull()
                        }

                        if ( roles == null ) {
                            transaction {
                                models.WidgetRoles.insert {
                                    it[guildId] = guild!!.id.asString
                                    it[roleId] = role.id.asString
                                }
                            }
                        }

                        publicFollowUp {
                            content = "${role.name} has been added to the widget list"
                        }
                    }
                }

                subCommand(::WidgetRoles) {
                    name = "remove"
                    description = "removes a role to the widget list"
                    action {
                        guild ?: return@action

                        val role = arguments.role
                        val roles = transaction {
                            models.WidgetRoles.select {
                                (models.WidgetRoles.guildId eq guild!!.id.asString) and (models.WidgetRoles.roleId eq role.id.asString)
                            }.firstOrNull()
                        }

                        roles?.let {
                            transaction {
                                models.WidgetRoles.deleteWhere {
                                    models.WidgetRoles.roleId eq role.id.asString
                                }
                            }
                        }

                        publicFollowUp {
                            content = "${role.name} has beem removed from the widget list."
                        }
                    }
                }

                subCommand {
                    name = "list"
                    description = "list's all the widget member roles"
                    action {
                        guild ?: return@action

                        val roles = transaction {  models.WidgetRoles.select { models.WidgetRoles.guildId eq guild!!.id.asString }.chunked(2) }
                        val pages = roles
                            .map {
                                val widgetRoles = it.map {
                                    guild!!.roles.first { role ->
                                        role.id.asString == it[models.WidgetRoles.roleId]
                                    }
                                }
                                Page(
                                    title = "${guild!!.name}, Widget Roles",
                                    description = widgetRoles.joinToString("\n") { role ->
                                        "${role.mention} - ${role.id.asString}"
                                    },
                                    color = Color(127, 179, 213)
                                )
                        }

                        val book = Pages()
                        pages.forEach { book.addPage( it ) }

                        if (pages.isNotEmpty()) {
                            Paginator(bot, channel as TextChannel, pages = book).also {
                                it.send()
                            }
                        } else {
                            publicFollowUp {
                                content = "There's no widget roles selected, by default it will count everyone in the guild."
                            }
                        }

                    }
                }
            }

            group("message") {
                description = "group of commands that control the widget messages"

                subCommand(::WidgetMessagePost) {
                    name = "post"
                    description = "post's a new widget message"
                    action {

                        guild ?: return@action

                        val roles = transaction {
                            models.WidgetRoles.select { models.WidgetRoles.guildId eq guild!!.id.asString }.toList()
                        }

                        val count = roles.map { result ->
                            guild!!.roles.firstOrNull { role -> role.id.asString == result[models.WidgetRoles.roleId] }?.let {
                                guild!!.members.count { member ->
                                    member.roleIds.contains(it.id)
                                }
                            } ?: 0
                        }.sum()

                        val response = publicFollowUp {
                            embed {
                                color = Color(127, 179, 213)
                                title = arguments.title
                                description = arguments.content.replace("role_count", count.toString()).replace("\\n", "\n")
                            }
                        }

                        transaction {
                            WidgetMessages.insert {
                                it[title] = arguments.title
                                it[content] = arguments.content
                                it[guildId] = guild!!.id.asString
                                it[messageId] = response.message.id.asString
                                it[channelId] = response.channelId.asString
                            }
                        }
                    }
                }

                subCommand(::WidgetMessageEdit) {
                    name = "edit"
                    description = "edit's a exists widget message"
                    action {
                        val result = transaction {
                            WidgetMessages.update({ WidgetMessages.messageId eq arguments.message.id.asString }) {
                                it[title] = arguments.title
                                it[content] = arguments.content
                            }
                        }

                        publicFollowUp {
                            content = when (result) {
                                0 -> "Could not find the message by the id of ${arguments.message.id.asString}"
                                1 -> "Updated Widget Message 👍"
                                else -> "Something has went terribly wrong."
                            }
                        }
                    }
                }

            }
        }
    }
}