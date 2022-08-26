package com.example.StateflowStudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.room.Room
import com.example.StateflowStudy.databinding.ActivityInsertBinding

class InsertActivity : AppCompatActivity() {
    private val binding by lazy{ActivityInsertBinding.inflate(layoutInflater)}
    private lateinit var db : Database
    private val dataViewModel : DataViewModel by viewModels {
        DataViewModelFactory(DataRepository(db.dataDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        db = Room.databaseBuilder(this, Database::class.java, "test db").build()
        Log.d("insert Activity", "onCreate")
    }

    override fun onStart() {
        super.onStart()
        Log.d("insert Activity", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("insert Activity", "onResume")
    }

    override fun onPause() {
        Log.d("insert Activity", "onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d("insert lifecycle", "onStop")
        super.onStop()
        if (binding.newId.text.isNotEmpty() && binding.newText.text.isNotEmpty()){
            dataViewModel.insertData(DBData(binding.newId.text.toString().toInt(), binding.newText.text.toString()))
            Log.d("insert Activity", "insert ${binding.newId.text} to ${binding.newText.text}")
        }
    }
}