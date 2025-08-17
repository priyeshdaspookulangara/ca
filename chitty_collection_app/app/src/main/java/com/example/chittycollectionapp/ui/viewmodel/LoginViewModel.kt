package com.example.chittycollectionapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chittycollectionapp.data.repository.ChittyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: ChittyRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(loginName: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val agent = repository.getAgentByLoginName(loginName)
            if (agent == null) {
                _loginState.value = LoginState.Error("Invalid login name")
                return@launch
            }

            val passwordHash = repository.hashPassword(password)
            if (agent.passwordHash == passwordHash) {
                _loginState.value = LoginState.Success
            } else {
                _loginState.value = LoginState.Error("Invalid password")
            }
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}
