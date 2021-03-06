/**
 * Copyright (c) 2019-present Mikhael Sokolov
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
package ru.sokomishalov.skraper.bot.telegram.service

import kotlinx.coroutines.runBlocking
import org.telegram.telegrambots.bots.TelegramWebhookBot
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update
import ru.sokomishalov.skraper.bot.telegram.autoconfigure.BotProperties

/**
 * @author sokomishalov
 */
class WebhookSkraperBot(
        private val bot: SkraperBot,
        private val botProperties: BotProperties
) : TelegramWebhookBot() {
    override fun getBotUsername(): String = botProperties.username
    override fun getBotToken(): String = botProperties.token
    override fun getBotPath(): String = "/webhook"
    override fun onWebhookUpdateReceived(update: Update): BotApiMethod<*>? = runBlocking { bot.receive(update) }
}