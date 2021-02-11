package extenstions

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.extensions.Extension
import dev.kord.common.entity.Snowflake

class Widget(bot: ExtensibleBot): Extension(bot)  {

    override val name = "Widget"

    override suspend fun setup() {
        slashCommand {
            name = "widget"
            description = "Custom widget command that keeps tracks of all the members with a cerain role"
            guild = Snowflake("802200869755813958")

            group("roles") {
                description = "group of commands to control the member roles to keep track of"

                subCommand {
                    name = "add"
                    description = "adds a role to the widet list"
                    action {
                        println("Roles add")
                    }
                }

                subCommand {
                    name = "remove"
                    description = "removes a role to the widget list"
                    action {
                        println("Roles remove")
                    }
                }

                subCommand {
                    name = "list"
                    description = "list's all the widget member roles"
                    action {
                        println("roles list")
                    }
                }
            }

            group("message") {

                description = "group of commands that control the widget messages"

                subCommand {
                    name = "post"
                    description = "post's a new widget message"
                    action {
                        println("message post")
                    }
                }

                subCommand {
                    name = "edit"
                    description = "edit's a exists widget message"
                    action {
                        println("message post")
                    }
                }

            }
        }
    }
}