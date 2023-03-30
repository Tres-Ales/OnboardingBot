package bot

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import kotlinx.coroutines.runBlocking

fun Dispatcher.setUpTasksCallbacks(onboardingBot: OnboardingBot) {
    callbackQuery(callbackData = "getTask_callback0") {
        val inlineKeyboard = InlineKeyboardMarkup.create(
            listOf(
                InlineKeyboardButton.CallbackData(
                    text = "В работу",
                    callbackData = "moveInProcess_callback0"
                )),
            listOf(InlineKeyboardButton.CallbackData(
                text = "Готово",
                callbackData = "moveDone_callback0"
            ))
        )

        bot.sendMessage(
            chatId = onboardingBot.chatId,
            text = onboardingBot.cardsForToday[0].title + "\n" +
                    "ссылка на задачу в Кайтен",
            replyMarkup = inlineKeyboard
        )
    }

    callbackQuery(callbackData = "moveInProcess_callback0") {
        bot.editMessageText(onboardingBot.chatId,
            update.callbackQuery?.message?.messageId,
            text = update.callbackQuery?.message?.text + "\nПеремещена в работу")
        runBlocking {
            kaitenClient.moveCardsInOtherColumn(onboardingBot.cardsForToday[0], 1)
        }
    }

    callbackQuery(callbackData = "moveDone_callback0") {
        bot.editMessageText(onboardingBot.chatId,
            update.callbackQuery?.message?.messageId,
            text = update.callbackQuery?.message?.text + "\nПеремещена в 'Готово'")
        runBlocking {
            kaitenClient.moveCardsInOtherColumn(onboardingBot.cardsForToday[0], 2)
        }
    }

    callbackQuery(callbackData = "getTask_callback1") {
        val inlineKeyboard = InlineKeyboardMarkup.create(
            listOf(
                InlineKeyboardButton.CallbackData(
                    text = "В работу",
                    callbackData = "moveInProcess_callback1"
                )),
            listOf(InlineKeyboardButton.CallbackData(
                text = "Готово",
                callbackData = "moveDone_callback1"
            ))
        )

        bot.sendMessage(
            chatId = onboardingBot.chatId,
            text = onboardingBot.cardsForToday[1].title + "\n" +
                    "ссылка на задачу в Кайтен",
            replyMarkup = inlineKeyboard
        )
    }

    callbackQuery(callbackData = "moveInProcess_callback1") {
        bot.editMessageText(onboardingBot.chatId,
            update.callbackQuery?.message?.messageId,
            text = update.callbackQuery?.message?.text + "\nПеремещена в работу")
        runBlocking {
            kaitenClient.moveCardsInOtherColumn(onboardingBot.cardsForToday[1], 1)
        }
    }

    callbackQuery(callbackData = "moveDone_callback1") {
        bot.editMessageText(onboardingBot.chatId,
            update.callbackQuery?.message?.messageId,
            text = update.callbackQuery?.message?.text + "\nПеремещена в 'Готово'")
        runBlocking {
            kaitenClient.moveCardsInOtherColumn(onboardingBot.cardsForToday[1], 2)
        }
    }
    
    callbackQuery(callbackData = "getTask_callback2") {
        val inlineKeyboard = InlineKeyboardMarkup.create(
            listOf(
                InlineKeyboardButton.CallbackData(
                    text = "В работу",
                    callbackData = "moveInProcess_callback2"
                )),
            listOf(InlineKeyboardButton.CallbackData(
                text = "Готово",
                callbackData = "moveDone_callback2"
            ))
        )

        bot.sendMessage(
            chatId = onboardingBot.chatId,
            text = onboardingBot.cardsForToday[2].title + "\n" +
                    "ссылка на задачу в Кайтен",
            replyMarkup = inlineKeyboard
        )
    }

    callbackQuery(callbackData = "moveInProcess_callback2") {
        bot.editMessageText(onboardingBot.chatId,
            update.callbackQuery?.message?.messageId,
            text = update.callbackQuery?.message?.text + "\nПеремещена в работу")
        runBlocking {
            kaitenClient.moveCardsInOtherColumn(onboardingBot.cardsForToday[2], 1)
        }
    }

    callbackQuery(callbackData = "moveDone_callback2") {
        bot.editMessageText(onboardingBot.chatId,
            update.callbackQuery?.message?.messageId,
            text = update.callbackQuery?.message?.text + "\nПеремещена в 'Готово'")
        runBlocking {
            kaitenClient.moveCardsInOtherColumn(onboardingBot.cardsForToday[2], 2)
        }
    }
}