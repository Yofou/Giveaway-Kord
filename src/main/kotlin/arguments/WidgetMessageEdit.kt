package arguments

import com.kotlindiscord.kord.extensions.commands.converters.message
import com.kotlindiscord.kord.extensions.commands.converters.string
import com.kotlindiscord.kord.extensions.commands.parser.Arguments
import dev.kord.common.annotation.KordPreview

@KordPreview
class WidgetMessageEdit: Arguments() {
    val message by message("message", "Widget message id.")
    val title by string("title", "title of the embed")
    val content by string("content", "add any content in here\nKeywords - role_count")
}