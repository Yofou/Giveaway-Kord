package arguments

import com.kotlindiscord.kord.extensions.commands.converters.role
import com.kotlindiscord.kord.extensions.commands.parser.Arguments
import dev.kord.common.annotation.KordPreview

@KordPreview
class WidgetRoles: Arguments() {
    val role by role("role", "Selected member role for keeping track of.", null)
}