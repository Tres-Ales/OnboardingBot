package models

data class CreateBoard(
    val title: String,
    val columns: List<Column>,
    val lanes: List<Lane>
)