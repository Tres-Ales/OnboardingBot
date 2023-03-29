package models

data class Column(
    val board_id: Int,
    val col_count: Int,
    val column_id: Any,
    val external_id: Any,
    val id: Int,
    val pause_sla: Boolean,
    val rules: Int,
    val sort_order: Double,
    val title: String,
    val type: Int
)