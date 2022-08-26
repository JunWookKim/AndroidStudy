package com.example.bithumb_open_api_fragment

import android.os.Bundle
import android.os.Parcelable
import android.os.SystemClock
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ActionBarContainer
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.bithumb_open_api_fragment.databinding.FragmentMainBinding
import kotlinx.coroutines.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment(), CoroutineScope{
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding : FragmentMainBinding? = null
    private val binding get() = _binding!!
    var keyList = mutableListOf<String>()
    var infoList = mutableListOf(mapOf<String, String>())
    var integratedInfoList = mutableListOf<IntegratedInfo>()
    var clickedPosition = 0

    var nowTimestamp = 0L
    private val retrofitService = IRetrofitService.IRetrofitService.create()
    private var lastClickedTime = 0L

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private lateinit var db : AppDatabase
    var recyclerViewState : Parcelable? = null
    private var searching = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = Room.databaseBuilder(context!!, AppDatabase::class.java, "info_table").build()

        //Back stack 에서 돌아왔을때 searchBar 에서 text change 를 감지하기 위해 clear 후 검색값을 할당함
        binding.editTextSearch.text.clear()
        binding.editTextSearch.text = SpannableStringBuilder(searching)

        val recyclerAdapter = RecyclerAdapter(this, keyList, infoList)

        launch {
            searchInDB(binding.editTextSearch.text.toString(), recyclerAdapter)
            withContext(Dispatchers.Main){
                binding.recyclerView.adapter = recyclerAdapter
                binding.recyclerView.layoutManager = LinearLayoutManager(context)
            }
            setUpToolBar(binding.toolBar)
        }

        binding.btnDelete.setOnClickListener {
            launch { db.priceDao().deleteByDate(db.priceDao().getMinDate()) }
        }

        binding.fab.setOnClickListener {
            it.animate().rotationBy(360f)
            //빠르게 클릭하는것을 방지함
            if (SystemClock.elapsedRealtime() - lastClickedTime > 1000){
                launch {
                    recyclerViewState = binding.recyclerView.layoutManager?.onSaveInstanceState()
                    getAndSetData()
                    refreshDB()
                    searchInDB(binding.editTextSearch.text.toString(), recyclerAdapter)
                    setUpToolBar(binding.toolBar)
                }
            }
            else{
                Toast.makeText(context, "Too fast!!", Toast.LENGTH_SHORT).show()
            }
            lastClickedTime = SystemClock.elapsedRealtime()
        }

        binding.editTextSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                searching = p0.toString()
                launch { searchInDB(searching, recyclerAdapter) }
            }
        })
    }


    private suspend fun searchInDB(search: String, recyclerAdapter: RecyclerAdapter) {
        launch{
            val result = db.priceDao().getLatestValueByName(search, db.priceDao().getMaxDate())
            Log.d("searching", search + " " +convertTimestampToDate(db.priceDao().getMaxDate()!!))
            keyList.clear()
            infoList.clear()
            for(x in result){
                keyList.add(x.name)
                infoList.add(mapOf("opening_price" to x.opening.toString(), "closing_price" to x.closing.toString(), "min_price" to x.min.toString(), "max_price" to x.max.toString(), "units_traded" to x.unitTraded.toString(),
                    "acc_trade_value" to x.tradedValue.toString(), "prev_closing_price" to x.prevClosing.toString(), "units_traded_24H" to x.unitTraded24H.toString(),
                    "acc_trade_value_24H" to x.tradeValue24H.toString(), "fluctate_24H" to x.fluctate24H.toString(), "fluctate_rate_24H" to x.fluctateRate24H.toString()))
            }
        }.join()
        setUpRecyclerView(recyclerAdapter)
    }

    private suspend fun setUpToolBar(toolBar: Toolbar) = withContext(Dispatchers.Main){
        toolBar.title = convertTimestampToDate(withContext(Dispatchers.IO){db.priceDao().getMaxDate()!!})
    }

    private suspend fun setUpRecyclerView(recyclerAdapter: RecyclerAdapter) = withContext(Dispatchers.Main){
        recyclerAdapter.notifyDataSetChanged()
        binding.recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
        recyclerAdapter.setItemClickListener(object : RecyclerAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                clickedPosition = position
                Log.d("clicked_position", clickedPosition.toString())
            }
        })
    }

    private fun refreshDB(){
        for (x in integratedInfoList){
            db.priceDao().insertInfo(x)
        }
    }

    private suspend fun getAndSetData() = withContext(Dispatchers.IO){
        //기존값이 들어있는 list 초기화
//        Log.d("Coroutine_list_clear", "${keyList.toString() + infoList.toString() + integratedInfoList.toString()}")

        var json : JSONObject
        launch{
            json = JSONObject(retrofitService.getData().data.toString())
            parseJson(json)
            //integratedInfoList 채우기
            for(i in 0 until infoList.size){
                val model = IntegratedInfo(keyList[i], infoList[i]["opening_price"], infoList[i]["closing_price"], infoList[i]["min_price"], infoList[i]["max_price"],
                    infoList[i]["units_traded"], infoList[i]["acc_trade_value"], infoList[i]["units_traded_24H"], infoList[i]["acc_trade_value_24H"], infoList[i]["fluctate_24H"],
                    infoList[i]["fluctate_rate_24H"], infoList[i]["prev_closing_price"], nowTimestamp)
                integratedInfoList.add(model)
            }
            Log.d("size", integratedInfoList.size.toString())
        }
    }

    private suspend fun parseJson(json: JSONObject) = withContext(Dispatchers.Default){
        keyList.clear()
        infoList.clear()
        integratedInfoList.clear()
        val keys = json.keys()
        while(keys.hasNext()){
            keyList.add(keys.next())
        }

        for (key in keyList){
            if (key == "date"){
                nowTimestamp = json.getString(key).toLong()
                Log.d("nowTimestamp", nowTimestamp.toString())
            } else{
                val result = json.getJSONObject(key)
//                Log.d("Retrofit_result", "$key : $result")
                val map = stringToMap(result.toString())
//                Log.d("Retrofit_map", "$key : $map")
                infoList.add(map)
            }
        }
        Log.d("new_key_list", keyList.toString())
    }

    private fun convertTimestampToDate(nowTimestamp: Long) = SimpleDateFormat("yyyy-MM-dd, hh:mm:ss", Locale.KOREA).format(nowTimestamp)

    private fun stringToMap(string: String): Map<String, String> {
        val processedString = string.replace("{", "").replace("}", "").replace("\"", "")
        val map = processedString.split(",").associate{
            val (left, right) = it.split(":")
            left to right
        }
        return map
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}