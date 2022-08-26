package com.example.StateflowStudy

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class DataViewModel(private val dataRepository: DataRepository) : ViewModel() {
    sealed class DataEvent{
        class Success(val DBList : List<Data>) : DataEvent()
        class SuccessFlow(val DBListFlow : Flow<List<Data>>) : DataEvent()
        class Failure(val message : String) : DataEvent()
        object Loading : DataEvent()
        object Empty : DataEvent()
    }

    private val _data = MutableStateFlow<DataEvent>(DataEvent.Empty)
    val data: StateFlow<DataEvent> = _data

    suspend fun getAllData() = viewModelScope.launch(Dispatchers.IO) {
        _data.value = DataEvent.Loading
        when(val response = dataRepository.getAllData()){
            is Resource.Success -> {
//                Log.d("ViewModel", "get all data")
                _data.value = DataEvent.Success(response.data!!)
                Log.d("viewModel _data", ": ${_data.value}")
                Log.d("viewModel data", ": ${data.value}")
            }
            is Resource.Error -> _data.value = DataEvent.Failure(response.message!!)
        }
    }

    fun getAllDataFlow() = viewModelScope.launch{
        _data.value = DataEvent.Loading
        _data.value = DataEvent.SuccessFlow(dataRepository.getAllDataFlow())
    }

    fun getAllDataFlow2() = viewModelScope.launch {
        dataRepository.getAllDataFlow().collect{
            _data.value = DataEvent.Success(it)
        }
    }

    val stateFlowData : StateFlow<DataEvent> = dataRepository.get()
        .stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(3000), DataEvent.Empty
        )

    val stateFlowData2 : StateFlow<Resource<List<Data>>> = dataRepository.get2()
        .stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(3000), Resource.Error("error")
        )

    fun insertData(dbData: DBData) = viewModelScope.launch(Dispatchers.IO){
//        withContext(Dispatchers.Default){
//            Log.d("ViewModel 1", "insert data")
//            dataRepository.insertData(dbData)
//        }
        getAllData()
        Log.d("ViewModel 2", "insert data")
        dataRepository.insertData(dbData)
        _data.value = DataEvent.Loading
    }
}

class DataViewModelFactory(private val dataRepository: DataRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DataViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return DataViewModel(dataRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}