package arguments

import com.kotlindiscord.kord.extensions.commands.converters.*
import com.kotlindiscord.kord.extensions.commands.parser.Arguments
import dev.kord.common.annotation.KordPreview

@KordPreview
class PostArguments: Arguments() {
    val time by duration("time", "unit of time for when the giveaway will end.", true)
    val winners by int("winners", "The number of winners the giveaway will randomly pick.")
    val title by string("title", "Ideally what you're going to be giving away.")
    val channel by optionalChannel("channel", "The TextChannel the giveaway message will be posted in")
    val host by optionalMember("host", "The host of the giveaway.", null)
}