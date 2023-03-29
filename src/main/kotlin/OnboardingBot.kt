package bot

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.*
import com.github.kotlintelegrambot.entities.*
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.github.kotlintelegrambot.extensions.filters.Filter

private const val BOT_TOKEN = "5999987050:AAEZW4pMjVp1gLKwdyusnhVaE4LBvtBDCUs"
open class OnboardingBot {
    lateinit var employeeName: String
    var onboardingPath: String? = null
    lateinit var occupation: String
    var _chatId: ChatId.Id? = null
    val chatId by lazy { requireNotNull(_chatId) }

    fun createBot(): Bot {
        return bot {
            token = BOT_TOKEN

            dispatch {
                setUpCommands()
                setUpCallbacks()
                setUpCallbacksForPD(this@OnboardingBot)
                setUpCreateKaitenBoardCallback(this@OnboardingBot)
            }
        }
    }

    private fun Dispatcher.setUpCallbacks() {
        callbackQuery(callbackData = "start") {
            bot.sendMessage(chatId = chatId, text = "Отправь мне свое имя")
            message(Filter.Text) {
                val inlineKeyboardMarkup = InlineKeyboardMarkup.create(
                    listOf(
                        InlineKeyboardButton.CallbackData(
                            text = "Да, верно.",
                            callbackData = "rightName_label"
                        )
                    )
                )
                employeeName = message.text.toString()
                bot.sendMessage(
                    chatId = chatId,
                    text = "Твое имя - ${message.text}, верно? \n Если неверно, введи его ещё раз.",
                    replyMarkup = inlineKeyboardMarkup
                )
            }
        }

        callbackQuery (callbackData = "rightName_label") {
            bot.editMessageText(chatId,
                update.callbackQuery?.message?.messageId,
                text = update.callbackQuery?.message?.text ?: "")
            val inlineKeyboardMarkup = InlineKeyboardMarkup.create(
                listOf(
                    InlineKeyboardButton.CallbackData(
                        text = "Скрам мастер",
                        callbackData = "default_callback"
                    )
                ),
                listOf(
                    InlineKeyboardButton.CallbackData(
                        text = "Продуктовый разработчик",
                        callbackData = "productDeveloper_callback"
                    )
                )
            )

            bot.sendMessage(
                chatId = chatId,
                text = "Выбери направление для онбординга",
                replyMarkup = inlineKeyboardMarkup
            )
        }
    }

    private fun Dispatcher.setUpCommands() {
        command("start") {
            _chatId = ChatId.fromId(message.chat.id)
            bot.sendMessage(
                chatId = chatId,
                text = "Привет! Я бот для онбординга!"
            )
            val inlineKeyboardMarkup = InlineKeyboardMarkup.create(
                listOf(
                    InlineKeyboardButton.CallbackData(
                        text = "Представиться",
                        callbackData = "start"
                    )
                ))
            bot.sendMessage(
                chatId = chatId,
                text = "Для дальнейшей работы нам надо познакомиться",
                replyMarkup = inlineKeyboardMarkup
            )
        }
    }
}