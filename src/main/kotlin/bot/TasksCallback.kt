package bot

import client.KaitenClient
import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton

fun Dispatcher.setUpTasksCallbacks(onboardingBot: OnboardingBot) {
    for (i in onboardingBot.cardsForToday.indices) {
        val currentCard = onboardingBot.cardsForToday[i]

        callbackQuery(callbackData = "getTask_callback" + i.toString()) {
            val inlineKeyboard = InlineKeyboardMarkup.create(
                listOf(
                    InlineKeyboardButton.CallbackData(
                        text = "В работу",
                        callbackData = "moveInProcess_callback" + i.toString()
                    )),
                listOf(InlineKeyboardButton.CallbackData(
                    text = "Готово",
                    callbackData = "moveDone_callback" + i.toString()
                ))
            )

            bot.sendMessage(
                chatId = onboardingBot.chatId,
                text = currentCard.title + "\n" +
                        "ссылка на задачу в Кайтен",
                replyMarkup = inlineKeyboard
            )
        }

        callbackQuery(callbackData = "moveInProcess_callback" + i) {
            bot.editMessageText(onboardingBot.chatId,
                update.callbackQuery?.message?.messageId,
                text = update.callbackQuery?.message?.text + "\nПеремещена в работу")
            // Двигать задачку в "В РАБОТЕ"
        }

        callbackQuery(callbackData = "moveDone_callback" + i) {
            bot.editMessageText(onboardingBot.chatId,
                update.callbackQuery?.message?.messageId,
                text = update.callbackQuery?.message?.text + "\nПеремещена в 'Готово'")
            // Двигать задачку в "ГОТОВО"
        }
    }
}