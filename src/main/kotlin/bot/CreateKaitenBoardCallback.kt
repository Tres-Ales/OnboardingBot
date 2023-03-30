package bot

import client.KaitenClient
import com.github.kotlintelegrambot.dispatcher.*
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
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

        // СОЗДАЕМ КАЙТЕН И ИЩЕМ ТАСКИ
        onboardingBot.cardsForToday =  runBlocking {
            val defaultBoard = kaitenClient.getBoardForProductDeveloperByOccupation(onboardingBot.occupation)
            val newBoardId = kaitenClient.createBoardFromDefault(defaultBoard, onboardingBot.employeeName)

            bot.sendMessage(
                chatId = onboardingBot.chatId,
                text = "Поздравляю, твоя доска в Кайтен создана!\n" +
                        "Дата окончания стажировки: 30.06.2022\n" +
                        "Мы рады, что ты с нами! \uD83E\uDDE1 \n" +
                        "И у тебя появились уже первые задачи! Давай-ка их посмотрим"
            )
            kaitenClient.getTodayCards(newBoardId)
        }.toList()

        // ВЫСЫЛАЕМ ТАСКИ
        var taskStr = ""
        var listTasks = ArrayList<List<InlineKeyboardButton>>(onboardingBot.cardsForToday.size)


        for (i in onboardingBot.cardsForToday.indices) {
            taskStr += (i+1).toString() + ") " + onboardingBot.cardsForToday[i].title + "\n"
            listTasks.add(listOf(
                InlineKeyboardButton.CallbackData(
                    text = onboardingBot.cardsForToday[i].title,
                    callbackData = "getTask_callback" + i.toString()
                )
            ))
        }

        bot.sendMessage(
            chatId = onboardingBot.chatId,
            text = "Карточки на сегодня:\n" + taskStr + "Чтобы посмотреть задачу подробнее выбери ее в списке ниже",
            replyMarkup = InlineKeyboardMarkup.create(listTasks)
        )
    }
}