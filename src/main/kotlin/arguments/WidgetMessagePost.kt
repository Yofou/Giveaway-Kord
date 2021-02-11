package arguments

import com.kotlindiscord.kord.extensions.commands.converters.string
import com.kotlindiscord.kord.extensions.commands.parser.Arguments

class WidgetMessagePost: Arguments() {
    val title = string("title", "title of the embed")
    val content = string("content", "add any content in here\nKeywords - rolecount & totalcount")
}