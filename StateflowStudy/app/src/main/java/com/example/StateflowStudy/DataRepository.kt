package com.example.StateflowStudy

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DataRepository(private val DBDao: DBDao) {
    suspend fun getAllData(): Resource<List<Data>>{
        return try{
            val response = DBDao.getAll()
            Resource.Success(response)
        } catch (e: Exception){
            Resource.Error(e.message ?: "error")
        }
    }

    fun get() = flow {
        emit(DataViewModel.DataEvent.Loading)
        DBDao.getAllFlow().collect{
            emit(DataViewModel.DataEvent.Success(it))
            Log.d("Repository", "collect $it")
            Log.d("Repository", "emit ${DataViewModel.DataEvent.Success(it)}")
        }
    }

    fun get2() = flow{
        DBDao.getAllFlow().collect{
            emit(Resource.Success(it))
        }
    }

    fun getAllDataFlow(): Flow<List<Data>> = DBDao.getAllFlow()

    fun insertData(dbData: DBData) = DBDao.insert(dbData)

}
