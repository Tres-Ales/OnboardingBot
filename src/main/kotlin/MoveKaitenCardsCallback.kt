import bot.OnboardingBot
import client.KaitenClient
import com.github.kotlintelegrambot.dispatcher.Dispatcher
import kotlinx.coroutines.runBlocking
import models.CardInfo

val kaitenClient = KaitenClient()

fun Dispatcher.setUpCreateKaitenBoardCallback(onboardingBot: OnboardingBot, cardInfo: CardInfo) {


    runBlocking {
        kaitenClient.moveCardsInOtherColumn(cardInfo, 1)
    }


}