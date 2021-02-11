package arguments

import com.kotlindiscord.kord.extensions.commands.converters.string
import com.kotlindiscord.kord.extensions.commands.parser.Arguments

class WidgetMessageEdit: Arguments() {
    val message = string("message", "widget message id that you want to edit.")
    val title = string("title", "title of the embed")
    val content = string("content", "add any content in here\nKeywords - rolecount & totalcount")
}