package client

import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import models.*
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter

class KaitenClient {

    private val client = HttpClient(CIO) {
        expectSuccess = false
    }
    private val gson = Gson()
    private val KAITEN_BEARER_TOKEN = "Bearer 90f4e9b8-4dda-4048-a871-5d676f5b9dd2"
    private val ONBOARDING_SPACE_ID = 35455
    private val TEST_SPACE_ID = 20362
    private val EXAMPLE_BOARD_ID = 191076
    private val EXAMPLE_IOS_BOARD_ID = 322638
    private val EXAMPLE_BACKEND_BOARD_ID = 322460

    val occupationBoardId : Map<String, Int> = mapOf(
        Pair("iOS", EXAMPLE_IOS_BOARD_ID),
        Pair("Backend", EXAMPLE_BACKEND_BOARD_ID))

    suspend fun getBoardForProductDeveloperByOccupation(occupation: String): Board {
        val exampleBoardId = occupationBoardId[occupation] ?: EXAMPLE_BOARD_ID
        val response: HttpResponse =
            client.request("https://qiwi.kaiten.ru/api/latest/spaces/${ONBOARDING_SPACE_ID}/boards/${exampleBoardId}") {
                method = HttpMethod.Get
                headers.append(HttpHeaders.Authorization, KAITEN_BEARER_TOKEN)
                headers.append(HttpHeaders.ContentType, "application/json")
            }
        return gson.fromJson(response.bodyAsText(), Board::class.java)
    }

    suspend fun getDefaultBoardForProductDeveloper(): Board {
        val response: HttpResponse =
            client.request("https://qiwi.kaiten.ru/api/latest/spaces/${ONBOARDING_SPACE_ID}/boards/${EXAMPLE_BOARD_ID}") {
                method = HttpMethod.Get
                headers.append(HttpHeaders.Authorization, KAITEN_BEARER_TOKEN)
                headers.append(HttpHeaders.ContentType, "application/json")
            }
        return gson.fromJson(response.bodyAsText(), Board::class.java)
    }

    suspend fun createBoardFromDefault(defaultBoard: Board, employeeName: String) {
        val boardForCreate = defaultBoard.run {
            CreateBoard(
                title = "Онбординг $employeeName",
                columns = this.columns,
                lanes = this.lanes
            )
        }
        val response: HttpResponse =
            client.request("https://qiwi.kaiten.ru/api/latest/spaces/${TEST_SPACE_ID}/boards") {
                method = HttpMethod.Post
                headers.append(HttpHeaders.Authorization, KAITEN_BEARER_TOKEN)
                headers.append(HttpHeaders.ContentType, "application/json")
                setBody(gson.toJson(boardForCreate))
            }

        val newBoard = gson.fromJson(response.bodyAsText(), Board::class.java)
        val columnId = newBoard.columns.minByOrNull { it.sort_order }!!.id

        val defaultLaneArray = defaultBoard.lanes.sortedBy { it.sort_order }.map { it.id }
        val newLaneArray = newBoard.lanes.sortedBy { it.sort_order }.map { it.id }

        defaultBoard.cards.filter { !it.archived && !it.description_filled }.forEach {
            val laneIdIndx = defaultLaneArray.indexOf(it.lane_id)
            val dueDate = if(it.title.contains("Документ", true)){
                val localDate = LocalDateTime.now()
                val offsetDate = OffsetDateTime.of(localDate, ZoneOffset.UTC)
                offsetDate.format(DateTimeFormatter.ISO_DATE_TIME)
            } else {
                it.due_date
            }
            it.apply {
                it.board_id = newBoard.id
                it.column_id = columnId
                it.lane_id = newLaneArray[laneIdIndx]
                it.due_date = dueDate?.toString()
            }
            createCard(it)
        }
//        defaultBoard.cards.filter { !it.archived && it.description_filled }.forEach {
//            val fullCardInfo = getCardInfo(it.id)
//            val dueDate = if(it.title.contains("Документ", true)){
//                val dtStart = LocalDate.now().atStartOfDay(ZoneId.of("UTC")).toString()
//                val format = SimpleDateFormat("yyyy-MM-dd")
//                format.parse(dtStart)
//            } else {
//                it.due_date
//            }
//            val laneIdIndx = defaultLaneArray.indexOf(it.lane_id)
//            createCard(fullCardInfo.apply {
//                this.board_id = newBoard.id
//                this.column_id = columnId
//                this.lane_id = newLaneArray[laneIdIndx]
//                this.due_date = dueDate.toString()
//            })
//        }
    }

    private suspend fun createCard(card: Card) {
        val response: HttpResponse =
            client.request("https://qiwi.kaiten.ru/api/latest/cards") {
                method = HttpMethod.Post
                headers.append(HttpHeaders.Authorization, KAITEN_BEARER_TOKEN)
                headers.append(HttpHeaders.ContentType, "application/json")
                setBody(gson.toJson(card))
            }
        println(response.bodyAsText())
    }

    private suspend fun createCard(card: CardInfo) {
        val response: HttpResponse =
            client.request("https://qiwi.kaiten.ru/api/latest/cards") {
                method = HttpMethod.Post
                headers.append(HttpHeaders.Authorization, KAITEN_BEARER_TOKEN)
                headers.append(HttpHeaders.ContentType, "application/json")
                setBody(gson.toJson(card))
            }
        println(response.bodyAsText())
    }

    private suspend fun getCardInfo(cardId: Int): CardInfo {
        val response: HttpResponse =
            client.request("https://qiwi.kaiten.ru/api/latest/cards/${cardId}") {
                method = HttpMethod.Get
                headers.append(HttpHeaders.Authorization, KAITEN_BEARER_TOKEN)
                headers.append(HttpHeaders.ContentType, "application/json")
            }
//        println(response.bodyAsText())
        return gson.fromJson(response.bodyAsText(), CardInfo::class.java)
    }

//    private suspend fun getTodayCards() : List<CardInfo> {
    suspend fun getTodayCards() {
        val response: HttpResponse =
            client.request("https://qiwi.kaiten.ru/api/latest/time-logs") {
                method = HttpMethod.Get
                headers.append(HttpHeaders.Authorization, KAITEN_BEARER_TOKEN)
                headers.append(HttpHeaders.ContentType, "application/json")
                parameter("from", "2023-03-30")
                parameter("to", "2023-03-31")
            }
        println(response.bodyAsText())
//        return gson.fromJson(response.bodyAsText(), CardInfo)
    }
}