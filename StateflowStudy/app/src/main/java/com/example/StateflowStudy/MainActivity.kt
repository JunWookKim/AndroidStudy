package com.example.StateflowStudy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.StateflowStudy.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private lateinit var dataAdapter: DataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val db = Room.databaseBuilder(this, Database::class.java, "test db").build()
        val dataViewModel: DataViewModel by viewModels {
            DataViewModelFactory(DataRepository(db.dataDao()))
        }
        Log.d("main Activity", "$dataViewModel $db")

        binding.mainRv.layoutManager = LinearLayoutManager(this)
        dataAdapter = DataAdapter()
        binding.mainRv.adapter = dataAdapter

        binding.btnInsert.setOnClickListener {
            val intent = Intent(this, InsertActivity::class.java)
            startActivity(intent)
        }

        binding.showDB.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                Log.d("DB", "${dataViewModel.getAllData()}")
//                Log.d("DB", "${dataViewModel.getAllDataFlow()}")
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                dataViewModel.data.collect { event ->
//                    dataViewModel.getAllData()
                    when(event){
                        is DataViewModel.DataEvent.Success -> {
                            Log.d("collect succeed", "${event.DBList}")
                            dataAdapter.submitList(event.DBList)
                        }
                        is DataViewModel.DataEvent.SuccessFlow -> {
                            Log.d("flow collect succeed", "${event.DBListFlow}")
                            event.DBListFlow.collect{
                                dataAdapter.submitList(it)
                            }
                        }
                        is DataViewModel.DataEvent.Loading -> {
                            Log.d("collect loading", "loading")
                        }
                        is DataViewModel.DataEvent.Failure -> {
                            Log.d("collect failure", event.message)
                        }
                        is DataViewModel.DataEvent.Empty -> {
                            Log.d("collect empty", "empty")
                        }
                    }
                }
            }
        }

//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                dataViewModel.data.collect { event ->
//                    dataViewModel.getAllDataFlow()
//                    when (event) {
//                        is DataViewModel.DataEvent.Success -> {
//                            Log.d("collect succeed", "${event.DBList}")
//                            dataAdapter.submitList(event.DBList)
//                        }
//                        is DataViewModel.DataEvent.SuccessFlow -> {
//                            Log.d("flow collect succeed", "${event.DBListFlow}")
//                            event.DBListFlow.collect {
//                                dataAdapter.submitList(it)
//                            }
//                        }
//                        is DataViewModel.DataEvent.Loading -> {
//                            Log.d("collect loading", "loading")
//                        }
//                        is DataViewModel.DataEvent.Failure -> {
//                            Log.d("collect failure", "failure")
//                        }
//                        is DataViewModel.DataEvent.Empty -> {
//                            Log.d("collect empty", "empty")
//                        }
//                    }
//                }
//            }
//        }

//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED){
//                dataViewModel.stateFlowData.collect { event ->
//                    Log.d("main Activity", "collecting......")
//                    when(event){
//                        is DataViewModel.DataEvent.Success -> {
//                            Log.d("collect succeed", "${event.DBList}")
//                            dataAdapter.submitList(event.DBList)
//                        }
//                        is DataViewModel.DataEvent.SuccessFlow -> {
//                            Log.d("flow collect succeed", "${event.DBListFlow}")
//                            event.DBListFlow.collect{
//                                dataAdapter.submitList(it)
//                            }
//                        }
//                        is DataViewModel.DataEvent.Loading -> {
//                            Log.d("collect loading", "loading")
//                        }
//                        is DataViewModel.DataEvent.Failure -> {
//                            Log.d("collect failure", "failure")
//                        }
//                        is DataViewModel.DataEvent.Empty -> {
//                            Log.d("collect empty", "empty")
//                        }
//                    }
//                }
//            }
//        }

//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED){
//                dataViewModel.data.collect { event ->
////                    dataViewModel.getAllData()
//                    when(event){
//                        is DataViewModel.DataEvent.Success -> {
//                            Log.d("collect succeed", "${event.DBList}")
//                            dataAdapter.submitList(event.DBList)
//                        }
//                        is DataViewModel.DataEvent.SuccessFlow -> {
//                            Log.d("flow collect succeed", "${event.DBListFlow}")
//                            event.DBListFlow.collect{
//                                dataAdapter.submitList(it)
//                            }
//                        }
//                        is DataViewModel.DataEvent.Loading -> {
//                            Log.d("collect loading", "loading")
//                        }
//                        is DataViewModel.DataEvent.Failure -> {
//                            Log.d("collect failure", "failure")
//                        }
//                        is DataViewModel.DataEvent.Empty -> {
//                            Log.d("collect empty", "empty")
//                        }
//                    }
//                }
//            }
//        }



    }

    override fun onResume() {
        Log.d("main activity", "onResume")
        super.onResume()
    }

    override fun onStart() {
        Log.d("main activity", "onStart")
        super.onStart()
    }

    override fun onPause() {
        Log.d("main activity", "onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d("main activity", "onStop")
        super.onStop()
    }

}