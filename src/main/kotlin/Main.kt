import bot.OnboardingBot

fun main() {
    val bot = OnboardingBot().createBot()

    bot.startPolling()
}

