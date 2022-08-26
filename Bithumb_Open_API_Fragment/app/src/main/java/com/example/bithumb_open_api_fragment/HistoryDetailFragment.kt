package com.example.bithumb_open_api_fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bithumb_open_api_fragment.databinding.FragmentHistoryDetailBinding
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryDetailFragment : Fragment(), CoroutineScope {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentHistoryDetailBinding? = null
    private val binding get() = _binding!!
    private var param1: String? = null
    private var param2: String? = null

    lateinit var db: RoomDatabase

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

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
        _binding = FragmentHistoryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments
        val name = bundle?.getString("name")
        val timestamp = bundle?.getLong("timestamp")
        db = Room.databaseBuilder(context!!, AppDatabase::class.java, "info_table").build()

        setUpToolbar(binding.toolBar,name)
        launch {
            showDetail((db as AppDatabase).priceDao().getByNameAndDate(name!!, timestamp))
        }
    }

    private suspend fun showDetail(result: IntegratedInfo) = withContext(Dispatchers.Main){
        with(binding){
            textName.text = result.name
            textOpening.text = result.opening
            textClosing.text = result.closing
            textMin.text = result.min
            textMax.text = result.max
            textUnitTraded.text = result.unitTraded
            textTradeValue.text = result.tradedValue
            textUnitTraded24H.text = result.unitTraded24H
            textTradeValue24H.text = result.tradeValue24H
            textFluctate24H.text = result.fluctate24H
            textFluctateRate24H.text = result.fluctateRate24H
            textPrevClosing.text = result.prevClosing
            textTimestamp.text = SimpleDateFormat("yyyy-MM-dd, hh:mm:ss", Locale.KOREA).format(result.date)
        }
    }

    private fun setUpToolbar(toolBar: Toolbar, name: String?) {
        (activity as MainActivity).setSupportActionBar(toolBar)
        (activity as MainActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        toolBar.title = "$name 상세"
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        job.cancel()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HistoryDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HistoryDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}