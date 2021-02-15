package models

import org.jetbrains.exposed.dao.id.IntIdTable

object WidgetRoles: IntIdTable("WidgetRoles") {
    val guildId = varchar("guild_id", 20)
    val roleId = varchar("role_id", 20)
}