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
@file:Suppress("unused")

package ru.sokomishalov.skraper.provider

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import ru.sokomishalov.skraper.Skraper
import ru.sokomishalov.skraper.getChannelLogoByteArray
import ru.sokomishalov.skraper.internal.model.ProviderChannel


/**
 * @author sokomishalov
 */
abstract class ProviderTck {

    protected abstract val channel: ProviderChannel
    protected abstract val service: Skraper

    @Test
    fun `Check that channel memes has been fetched`() = runBlocking {
        val memes = service.fetchPosts(channel)

        assertFalse(memes.isNullOrEmpty())
        memes.forEach {
            assertNotNull(it.id)
            assertNotNull(it.publishedAt)
            it.attachments.forEach { a ->
                assertNotNull(a.type)
                assertTrue(a.url.isNotBlank())
            }
        }
    }

    @Test
    fun `Check that channel logo has been fetched`() = runBlocking {
        val image = service.getChannelLogoByteArray(channel) ?: ByteArray(0)

        assertNotEquals(0, image.size)
    }
}