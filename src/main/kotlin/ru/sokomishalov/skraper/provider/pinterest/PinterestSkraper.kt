/**
 * Copyright 2019-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.sokomishalov.skraper.provider.pinterest

import com.fasterxml.jackson.databind.JsonNode
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import ru.sokomishalov.skraper.Skraper
import ru.sokomishalov.skraper.internal.model.Attachment
import ru.sokomishalov.skraper.internal.model.AttachmentType.IMAGE
import ru.sokomishalov.skraper.internal.model.Post
import ru.sokomishalov.skraper.internal.model.ProviderChannel
import ru.sokomishalov.skraper.internal.util.jsoup.fetchDocument
import ru.sokomishalov.skraper.internal.util.serialization.SKRAPER_OBJECT_MAPPER
import java.util.Locale.ROOT
import java.time.ZonedDateTime.parse as zonedDateTimeParse
import java.time.format.DateTimeFormatter.ofPattern as dateTimeFormatterOfPattern
import java.util.Date.from as dateFrom


/**
 * @author sokomishalov
 */
class PinterestSkraper : Skraper {

    companion object {
        private val DATE_FORMATTER = dateTimeFormatterOfPattern("EEE, d MMM yyyy HH:mm:ss Z", ROOT)
        private const val PINTEREST_URL = "https://www.pinterest.com"
    }

    override suspend fun fetchPosts(channel: ProviderChannel, limit: Int): List<Post> {
        val infoJsonNode = parseInitJson(channel)
        val feedList = infoJsonNode["resourceDataCache"][1]["data"]["board_feed"]
                .asIterable()
                .take(limit)

        return feedList
                .map {
                    val imageInfo = it["images"]["orig"]
                    Post(
                            id = it["id"].asText().orEmpty(),
                            caption = it["description"]?.asText(),
                            publishedAt = dateFrom(zonedDateTimeParse(it["created_at"]?.asText(), DATE_FORMATTER).toInstant()),
                            attachments = listOf(Attachment(
                                    type = IMAGE,
                                    url = imageInfo["url"]?.asText().orEmpty(),
                                    aspectRatio = imageInfo["width"].asDouble() / imageInfo["height"].asDouble()
                            ))
                    )
                }
    }

    override suspend fun getChannelLogoUrl(channel: ProviderChannel): String? {
        val infoJsonNode = parseInitJson(channel)

        return infoJsonNode["resourceDataCache"]
                ?.first()
                ?.get("data")
                ?.get("owner")
                ?.get("image_medium_url")
                ?.asText()
    }

    private suspend fun parseInitJson(channel: ProviderChannel): JsonNode {
        val webPage = fetchDocument("$PINTEREST_URL/${channel.uri}")
        val infoJson = webPage?.getElementById("jsInit1")?.html()
        return withContext(IO) { SKRAPER_OBJECT_MAPPER.readTree(infoJson) }
    }
}