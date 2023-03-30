package bot

import com.github.kotlintelegrambot.dispatcher.*
import com.github.kotlintelegrambot.dispatcher.handlers.CallbackQueryHandlerEnvironment
import com.github.kotlintelegrambot.entities.*
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton

fun Dispatcher.setUpCallbacksForPD(onboardingBot: OnboardingBot) {
    callbackQuery(callbackData = "productDeveloper_callback") {
        onboardingBot.onboardingPath = "Продуктовый разработчик"
        bot.editMessageText(onboardingBot.chatId,
            update.callbackQuery?.message?.messageId,
            text = update.callbackQuery?.message?.text + "\n" + onboardingBot.onboardingPath)
        val inlineKeyboardMarkup = InlineKeyboardMarkup.create(
            listOf(
                InlineKeyboardButton.CallbackData(
                    text = "iOS",
                    callbackData = "ios_callback"
                )
            ),
            listOf(
                InlineKeyboardButton.CallbackData(
                    text = "Android",
                    callbackData = "default_callback"
                )
            ),
            listOf(
                InlineKeyboardButton.CallbackData(
                    text = "Web",
                    callbackData = "default_callback"
                )
            ),
            listOf(
                InlineKeyboardButton.CallbackData(
                    text = "Backend",
                    callbackData = "backend_callback"
                )
            )
        )
        val msg = bot.sendMessage(
            chatId = onboardingBot.chatId,
            text = "Выбери направление для онбординга",
            replyMarkup = inlineKeyboardMarkup
        )

    }
    callbackQuery (callbackData = "ios_callback") {
        onboardingBot.occupation = "iOS"
        gotOccupation(onboardingBot)
    }
    callbackQuery (callbackData = "backend_callback") {
        onboardingBot.occupation = "Backend"
        gotOccupation(onboardingBot)
    }
    callbackQuery (callbackData = "default_callback") {
        onboardingBot.onboardingPath = onboardingBot.onboardingPath ?: "default"
        onboardingBot.occupation = "default"
        gotOccupation(onboardingBot)
    }
}

fun CallbackQueryHandlerEnvironment.gotOccupation(onboardingBot: OnboardingBot) {
    bot.editMessageText(onboardingBot.chatId,
        update.callbackQuery?.message?.messageId,
        text = update.callbackQuery?.message?.text + "\n" + onboardingBot.occupation)
    val inlineKeyboardMarkup = InlineKeyboardMarkup.create(
        listOf(
            InlineKeyboardButton.CallbackData(
                text = "ДА",
                callbackData = "yes_label"
            )
        ),
        listOf(
            InlineKeyboardButton.CallbackData(
                text = "Ошибся",
                callbackData = "rightName_label"
            )
        )
    )
    bot.sendMessage(
        chatId = onboardingBot.chatId,
        text = "Ваше направление онбординга " +  onboardingBot.onboardingPath + " " + onboardingBot.occupation,
        replyMarkup = inlineKeyboardMarkup
    )
}

