package arguments

import com.kotlindiscord.kord.extensions.commands.converters.role
import com.kotlindiscord.kord.extensions.commands.parser.Arguments

class WidgetRoles: Arguments() {
    val role  = role("role", "Selected member role for keeping track of.", null)
}