package models

data class Owner(
    val activated: Boolean,
    val avatar_initials_url: String,
    val avatar_type: Int,
    val avatar_uploaded_url: String,
    val created: String,
    val email: String,
    val full_name: String,
    val id: Int,
    val initials: String,
    val lng: String,
    val theme: String,
    val timezone: String,
    val ui_version: Int,
    val updated: String,
    val username: String,
    val virtual: Boolean
)