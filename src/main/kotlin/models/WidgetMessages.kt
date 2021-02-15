package models

import org.jetbrains.exposed.dao.id.IntIdTable

object WidgetMessages: IntIdTable("WidgetMessages") {
    val title = varchar("title", 50)
    val content = varchar("content", 1000)
    val messageId = varchar("message_id", 200)
    val channelId = varchar("channel_id", 200)
    val guildId = varchar("guild_id", 200)
}