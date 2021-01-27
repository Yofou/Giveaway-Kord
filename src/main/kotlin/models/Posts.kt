package models

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.datetime

object Posts: IntIdTable("Posts") {
    val messageId = varchar("message_id", 20)
    val guildId = varchar("guild_id", 20)
    val channelId = varchar("channel_id", 20)
    val host = varchar("host", 20)
    val title = varchar("title", 2000)
    val deadline = datetime("deadline")
    val winners = integer("winners")
    val rolled = bool("rolled").default(false)
    val createdAt = datetime("created_at")
}