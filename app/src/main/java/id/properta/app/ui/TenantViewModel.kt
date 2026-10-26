package id.properta.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import id.properta.app.data.PropertaRepository
import id.properta.app.data.local.TenantEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TenantViewModel(
    private val repository: PropertaRepository
) : ViewModel() {

    val properties = repository.observeProperties().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val units = repository.observeAllUnits().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val tenants = repository.observeTenants().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addTenant(
        fullName: String,
        phone: String = "",
        unitId: Long? = null,
        deposit: Long = 0,
        notes: String = "",
        identityType: String = "",
        identityNumber: String = "",
        identityAddress: String = "",
        identityPhotoPath: String? = null
    ) {
        viewModelScope.launch {
            repository.addTenant(
                fullName = fullName,
                phone = phone,
                unitId = unitId,
                deposit = deposit,
                notes = notes,
                identityType = identityType,
                identityNumber = identityNumber,
                identityAddress = identityAddress,
                identityPhotoPath = identityPhotoPath
            )
        }
    }

    fun updateTenant(tenant: TenantEntity) {
        viewModelScope.launch {
            repository.updateTenant(tenant)
        }
    }

    fun deleteTenant(tenant: TenantEntity) {
        viewModelScope.launch {
            repository.deleteTenant(tenant)
        }
    }

    class Factory(
        private val repository: PropertaRepository
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {
            if (modelClass.isAssignableFrom(TenantViewModel::class.java)) {
                return TenantViewModel(repository) as T
            }
            throw IllegalArgumentException("ViewModel tidak dikenal")
        }
    }
}
