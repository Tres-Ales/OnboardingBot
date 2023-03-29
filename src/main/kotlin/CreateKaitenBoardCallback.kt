package bot

import com.github.kotlintelegrambot.dispatcher.*
import com.github.kotlintelegrambot.entities.*

fun Dispatcher.setUpCreateKaitenBoardCallback(onboardingBot: OnboardingBot) {
    callbackQuery(callbackData = "yes_label") {
        bot.editMessageText(onboardingBot.chatId,
            update.callbackQuery?.message?.messageId,
            text = update.callbackQuery?.message?.text ?: "")
        bot.apply {
            sendMessage(chatId = onboardingBot.chatId, text = "Создаем вашу доску в Кайтен...")
        }
        // СЮДА СКРИПТ СОЗДАНИЯ ДОСКИ
        bot.sendMessage(
            chatId = onboardingBot.chatId,
            text = "Поздравляю, твоя доска в Кайтен создана"
        )
        // получить таски на сегодня
        // выслать задания на сегодня
    }
}