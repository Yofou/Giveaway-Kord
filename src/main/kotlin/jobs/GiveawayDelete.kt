package jobs

import io.kesselring.sukejura.pattern.Minutes
import io.kesselring.sukejura.sukejura
import models.Posts
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.ZoneOffset


fun giveawayDelete() = sukejura {
    schedule {
        minutes { (0..59 step 1).map { Minutes.M(it) } }

        task {
            transaction {
                Posts.select { Posts.rolled eq true }
//                  Filter records that are 6 Months old & Delete them.
                    .filter { LocalDateTime.now().toEpochSecond(ZoneOffset.UTC).minus( it[Posts.createdAt].toEpochSecond(ZoneOffset.UTC) ) >= 15778800 }
                    .forEach {
                        println("Deleting ${it[Posts.id]}")
                        Posts.deleteWhere { Posts.id eq it[Posts.id] }
                    }
            }
        }
    }
}