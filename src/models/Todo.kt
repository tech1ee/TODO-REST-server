package com.example.models

data class Todo(
    val id: Int,
    val userId: Int,
    val todo: String,
    val done: Boolean
)