package id.properta.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import id.properta.app.data.PropertaRepository
import id.properta.app.data.local.PropertyEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PropertyViewModel(
    private val repository: PropertaRepository
) : ViewModel() {

    val properties = repository.observeProperties().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addProperty(name: String, address: String, notes: String) {
        viewModelScope.launch {
            repository.addProperty(name, address, notes)
        }
    }

    fun deleteProperty(property: PropertyEntity) {
        viewModelScope.launch {
            repository.deleteProperty(property)
        }
    }

    class Factory(
        private val repository: PropertaRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PropertyViewModel::class.java)) {
                return PropertyViewModel(repository) as T
            }
            throw IllegalArgumentException("ViewModel tidak dikenal")
        }
    }
}
