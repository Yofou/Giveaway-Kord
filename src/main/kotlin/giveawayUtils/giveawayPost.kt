package giveawayUtils

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder

val giveawayPost = fun (
    postTitle: String,
    postImage: String?,
    duration: String,
    mention: String,
    winners: Int,
    endtime: String
): EmbedBuilder.() -> Unit = {
    color = Color(127, 179, 213)
    title = postTitle
    image = postImage
    description =
        "React with with 🎉 to enter!\nTime remaining $duration\nHosted by$mention"
    footer {
        text = "$winners Winners | Ends at • $endtime"
    }
}