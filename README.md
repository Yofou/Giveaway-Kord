# Giveaway-kord
Giveaway kord is a port from my node-js discord bot I made a while back when learning discord.js
I thought in tradition to learn kord and kord-ex (a kotlin discord api wrapper and framework) I would port over
the same bot with more or less features.

# SETUP
Open the project in intellij, 
 1) make sure to install all dependencies from the `build.gradle.kts`
 2) create the resources folder under `src/main/` with a `.env file` and `.db file`
 3) inside the .env file there's two key values `token` (this is where your bot token goes) and `db_uri` (this is where the db file you created under the resource folder).
    which should end up looking like this.
    ```dotenv
        token=Nzc2MTQ2ODk3NTQ2Mzc5MzA0.X6wpIA.Ipofx1oPQQWAL07IfaTleAEVJY4
        db_uri=C:/Users/Yofou/IdeaProjects/learning-kord-ex/src/main/resources/giveaway.db
    ```
 4) select MainKt top right (should be by default) and press the green play button
