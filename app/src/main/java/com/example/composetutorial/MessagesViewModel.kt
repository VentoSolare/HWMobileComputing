package com.example.composetutorial

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MessagesViewModel(application: Application) : AndroidViewModel(application) {
    private val db = MessageDatabase.getDatabase(application)
    private val messagesDao = db.messagesDao()

    val messages: StateFlow<List<Messages>> = messagesDao.getMessages()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addMessage(message: Messages) {
        viewModelScope.launch {
            messagesDao.insertMessage(message)
        }
    }

    fun deleteMessage(id: Int) {
        viewModelScope.launch {
            messagesDao.deleteMessageById(id)
        }
    }
}