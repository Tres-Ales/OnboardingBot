package client

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import kotlinx.coroutines.delay
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
    private val collectionCardInfoType: TypeToken<List<CardInfo>> = object : TypeToken<List<CardInfo>>() {}
    private val collectionColumnType: TypeToken<List<Column>> = object : TypeToken<List<Column>>() {}

    val occupationBoardId: Map<String, Int> = mapOf(
        Pair("iOS", EXAMPLE_IOS_BOARD_ID),
        Pair("Backend", EXAMPLE_BACKEND_BOARD_ID)
    )

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

    suspend fun createBoardFromDefault(defaultBoard: Board, employeeName: String): Int {
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
            val dueDate = getDueDate(it)
            it.apply {
                it.board_id = newBoard.id
                it.column_id = columnId
                it.lane_id = newLaneArray[laneIdIndx]
                it.due_date = dueDate?.toString()
            }
            createCard(it)
        }
        defaultBoard.cards.filter { !it.archived && it.description_filled }.forEach {
            val fullCardInfo = getCardInfo(it.id)
            val dueDate = getDueDate(it)
            val laneIdIndx = defaultLaneArray.indexOf(it.lane_id)
            createCard(fullCardInfo.apply {
                this.board_id = newBoard.id
                this.column_id = columnId
                this.lane_id = newLaneArray[laneIdIndx]
                this.due_date = dueDate.toString()
            })
        }
        return newBoard.id
    }

    private fun getDueDate(it: Card): String? =
        if (it.title.contains("Документ", true)) {
            val localDate = LocalDateTime.now()
            val offsetDate = OffsetDateTime.of(localDate, ZoneOffset.UTC)
            offsetDate.format(DateTimeFormatter.ISO_DATE_TIME)
        } else {
            it.due_date
        }

    private suspend fun createCard(card: Card) {
        val response: HttpResponse =
            client.request("https://qiwi.kaiten.ru/api/latest/cards") {
                method = HttpMethod.Post
                headers.append(HttpHeaders.Authorization, KAITEN_BEARER_TOKEN)
                headers.append(HttpHeaders.ContentType, "application/json")
                setBody(gson.toJson(card))
            }
    }

    private suspend fun createCard(card: CardInfo) {
        val response: HttpResponse =
            client.request("https://qiwi.kaiten.ru/api/latest/cards") {
                method = HttpMethod.Post
                headers.append(HttpHeaders.Authorization, KAITEN_BEARER_TOKEN)
                headers.append(HttpHeaders.ContentType, "application/json")
                setBody(gson.toJson(card))
            }
    }

    private suspend fun getCardInfo(cardId: Int): CardInfo {
        delay(300L)
        val response: HttpResponse =
            client.request("https://qiwi.kaiten.ru/api/latest/cards/${cardId}") {
                method = HttpMethod.Get
                headers.append(HttpHeaders.Authorization, KAITEN_BEARER_TOKEN)
                headers.append(HttpHeaders.ContentType, "application/json")
            }
        return gson.fromJson(response.bodyAsText(), CardInfo::class.java)
    }

    suspend fun getTodayCards(boardId: Int): List<CardInfo> {
        val response: HttpResponse = client.request("https://qiwi.kaiten.ru/api/latest/cards") {
            method = HttpMethod.Get
            headers.append(HttpHeaders.Authorization, KAITEN_BEARER_TOKEN)
            headers.append(HttpHeaders.ContentType, "application/json")
            parameter("due_date_after", "2023-03-29")
            parameter("due_date_before", "2023-03-31")
            parameter("board_id", boardId)
        }
        return gson.fromJson(response.bodyAsText(), collectionCardInfoType)
    }

    suspend fun moveCardsInOtherColumn(card: CardInfo, columnNumber: Int): CardInfo {
        val columns = getColumnSortedList(card)
        val movedCardInfo = card.apply { this.column_id = columns[columnNumber].id}
        updateCard(movedCardInfo)
        return movedCardInfo
    }

    private suspend fun getColumnSortedList(card: CardInfo): List<Column> {
        val response: HttpResponse =
            client.request("https://qiwi.kaiten.ru/api/latest/boards/${card.board_id}/columns") {
                method = HttpMethod.Get
                headers.append(HttpHeaders.Authorization, KAITEN_BEARER_TOKEN)
                headers.append(HttpHeaders.ContentType, "application/json")
            }
        return gson.fromJson(response.bodyAsText(), collectionColumnType).sortedBy { it.sort_order }
    }

    private suspend fun updateCard(card: CardInfo) {
        client.request("https://qiwi.kaiten.ru/api/latest/cards/${card.id}") {
            method = HttpMethod.Patch
            headers.append(HttpHeaders.Authorization, KAITEN_BEARER_TOKEN)
            headers.append(HttpHeaders.ContentType, "application/json")
            setBody(gson.toJson(card))
        }
    }
}