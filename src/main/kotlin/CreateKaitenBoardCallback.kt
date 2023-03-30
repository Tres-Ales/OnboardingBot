package bot

import client.KaitenClient
import com.github.kotlintelegrambot.dispatcher.*
import kotlinx.coroutines.runBlocking

val kaitenClient = KaitenClient()
fun Dispatcher.setUpCreateKaitenBoardCallback(onboardingBot: OnboardingBot) {
    callbackQuery(callbackData = "yes_label") {
        bot.editMessageText(onboardingBot.chatId,
            update.callbackQuery?.message?.messageId,
            text = update.callbackQuery?.message?.text ?: "")
        bot.apply {
            sendMessage(chatId = onboardingBot.chatId, text = "Создаем вашу доску в Кайтен...")
        }

        val cardsForToday =  runBlocking {
            val defaultBoard = kaitenClient.getBoardForProductDeveloperByOccupation(onboardingBot.occupation)
            val newBoardId = kaitenClient.createBoardFromDefault(defaultBoard, onboardingBot.employeeName)

            bot.sendMessage(
                chatId = onboardingBot.chatId,
                text = "Поздравляю, твоя доска в Кайтен создана"
            )

            kaitenClient.getTodayCards(newBoardId)
        }

        println(cardsForToday)

        // получить таски на сегодня
        // выслать задания на сегодня
    }
}