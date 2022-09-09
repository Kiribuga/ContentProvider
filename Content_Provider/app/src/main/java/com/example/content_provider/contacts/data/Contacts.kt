package com.example.content_provider.contacts.data

data class Contacts(
    val id: Long,
    val name: String,
    val phones: List<String> = emptyList(),
    val email: List<String> = emptyList()
)