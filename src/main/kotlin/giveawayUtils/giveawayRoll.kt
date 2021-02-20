package giveawayUtils

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder

val giveawayRoll = fun (
    postTitle: String,
    desc: String,
    endtime: String,
    postImage: String?,
    winners: Int,
): EmbedBuilder.() -> Unit = {
        color = Color(144, 238, 144)
        description = """
            Title: `$postTitle`
            $desc
            Finished at: `${endtime}`
        """.trimIndent()

        image = postImage
        footer {
            text = "$winners Winners | Ended at • $endtime"
        }
}