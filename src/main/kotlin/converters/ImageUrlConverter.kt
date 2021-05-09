package converters

import com.kotlindiscord.kord.extensions.CommandException
import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.commands.CommandContext
import com.kotlindiscord.kord.extensions.commands.converters.ConverterToOptional
import com.kotlindiscord.kord.extensions.commands.converters.OptionalConverter
import com.kotlindiscord.kord.extensions.commands.converters.SingleConverter
import com.kotlindiscord.kord.extensions.commands.parser.Argument
import com.kotlindiscord.kord.extensions.commands.parser.Arguments
import dev.kord.common.annotation.KordPreview
import dev.kord.rest.builder.interaction.OptionsBuilder
import dev.kord.rest.builder.interaction.StringChoiceBuilder
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.util.regex.Pattern

suspend fun isImageUrl(target: String): Boolean? {
    val match = Pattern.compile("([^\\s]+(\\.(?i)(jpg|png|gif|bmp))\$)")
    val test = match.matcher(target)

    return if ( test.matches() ) {
        try {
            HttpClient(CIO).use {
                it.get<HttpResponse>(target).contentType()?.contentType?.startsWith("image")
            }
        } catch (e: java.net.ConnectException) {
            null
        }
    } else {
        false
    }
}

@KordPreview
class ImageUrlConverter: SingleConverter<String>() {
    override val signatureTypeString = "url"

    override suspend fun parse(arg: String, context: CommandContext, bot: ExtensibleBot): Boolean {
        when (isImageUrl(arg)) {
            true -> this.parsed = arg
            false -> throw CommandException ("content response is not type of image from $arg\nor url is not valid (needs to end with .png for example)")
            else -> throw CommandException ("failed to find content type of url from $arg.")
        }

        return true
    }

    override suspend fun toSlashOption(arg: Argument<*>): OptionsBuilder =
        StringChoiceBuilder(arg.displayName, arg.description).apply { required = true }

}

@KordPreview
fun Arguments.imageUrl(displayName: String, description: String): SingleConverter<String> =
    arg(displayName, description, ImageUrlConverter())

@ConverterToOptional
@KordPreview
fun Arguments.optionalImageUrl(displayName: String, description: String, outputError: Boolean = false): OptionalConverter<String?> =
    arg(displayName, description, ImageUrlConverter().toOptional(outputError = outputError))