package org.wenubey.wenuplayerfrontend.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.launch
import org.wenubey.wenuplayerfrontend.data.ApiResponse
import org.wenubey.wenuplayerfrontend.domain.repository.ApiService

// TODO: Change this viewmodel with the necessary functionalities
class MainViewModel(private val apiService: ApiService): ViewModel() {
    private val logger = Logger.withTag(MainViewModel::class.simpleName.toString())
    var item = mutableStateOf(ApiResponse("EMPTY"))
        private set

    init {
        fetchItems()
    }

    private fun fetchItems() {
        viewModelScope.launch {
            try {
                item.value = apiService.fetchData()
                logger.d("Success: ${item.value.title}")
            } catch (e: Exception) {
                logger.e("Error: ", e)
            }
        }
    }
}