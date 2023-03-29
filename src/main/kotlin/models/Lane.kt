package models

data class Lane(
    val board_id: Int,
    val condition: Int,
    val default_card_type_id: Any,
    val external_id: Any,
    val id: Int,
    val sort_order: Double,
    val title: String
)