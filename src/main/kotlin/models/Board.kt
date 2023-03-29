package models

import java.util.LinkedList

data class Board(
    val auto_assign_enabled: Boolean,
    val automove_cards: Boolean,
    val backward_moves_enabled: Boolean,
    val board_id: Int,
    val card_properties: Any,
    val cards: List<Card>,
    val cell_wip_limits: Any,
    val columns: List<Column>,
    val created: String,
    val default_card_type_id: Int,
    val default_tags: Any,
    val description: Any,
    val email_key: String,
    val external_id: Any,
    val first_image_is_cover: Boolean,
    val hide_done_policies: Boolean,
    val hide_done_policies_in_done_column: Boolean,
    val id: Int,
    var lanes: List<Lane>,
    val left: Int,
    val move_parents_to_done: Boolean,
    val reset_lane_spent_time: Boolean,
    val sort_order: Double,
    val space_id: Int,
    val title: String,
    val top: Int,
    val uid: String,
    val updated: String
)