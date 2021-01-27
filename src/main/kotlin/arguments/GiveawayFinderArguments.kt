package arguments

import com.kotlindiscord.kord.extensions.commands.converters.message
import com.kotlindiscord.kord.extensions.commands.parser.Arguments

class GiveawayFinderArguments: Arguments() {
    val message = message("message", "an active giveaway message")
}