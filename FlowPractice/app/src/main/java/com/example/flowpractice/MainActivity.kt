package com.example.flowpractice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flowpractice.databinding.ActivityMainBinding
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    private val keyList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setKeyList()

        val recyclerAdapter = RecyclerAdapter(keyList)
        initRecyclerView(recyclerAdapter)

        binding.fab.setOnClickListener {
            updateAdapter(recyclerAdapter)
        }
    }

    private fun setKeyList() {
        launch {
            val result = IRetrofitService.create().getData()
            Log.d("api result", "$result")
            val jsonModel = JSONObject(result.data.toString())
            jsonModel.keys().forEach {
                keyList.add(it)
                Log.d("key", it)
            }
        }
    }

    private fun initRecyclerView(recyclerAdapter: RecyclerAdapter) {
        binding.recyclerView.apply{
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = recyclerAdapter
        }
    }

    private fun updateAdapter(recyclerAdapter: RecyclerAdapter) {
        launch(Dispatchers.Main){
            withTimeoutOrNull(3500) {
                getInfoModel().collect {
                    recyclerAdapter.submitList(it)
                }
            }
        }
    }

    private fun getInfoModel(): Flow<List<InfoModel>> = flow {
        repeat(3) {
            val infoModelList = mutableListOf<InfoModel>()

            val result = IRetrofitService.create().getData()
            val jsonModel = JSONObject(result.data.toString())

            jsonModel.keys().forEach {
//                Log.d("key", it)
                if (it != "date") {
//                    Log.d("data", "${jsonModel.get(it)}")
                    val infoModel = Gson().fromJson(jsonModel.get(it).toString(), InfoModel::class.java)
//                    Log.d("gson", gson.toString())
                    infoModelList.add(infoModel)
                }
            }
            emit(infoModelList)
            delay(1000)
        }
    }.flowOn(Dispatchers.IO)
}
