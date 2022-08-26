package com.example.bithumb_open_api_fragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.animation.Animation
import androidx.core.os.bundleOf
import androidx.fragment.app.*
import com.example.bithumb_open_api_fragment.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val fragmentManager : FragmentManager by lazy { supportFragmentManager }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(savedInstanceState == null){
            fragmentManager.commit {
                setReorderingAllowed(true)
                setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                add<MainFragment>(R.id.fragment_container)
            }
        }
    }

    fun replaceFragment(fragment: String, name: String, timestamp: Long?) {
        when (fragment) {
            "Detail" -> {
                val bundle = bundleOf("name" to name)
                fragmentManager.commit {
                    setReorderingAllowed(true)     // animation 전환이 올바르게 작동하도록 최적화
                    addToBackStack(null)    // 소멸되지 않기 위해서 back stack 에 저장함(stopped 상태 -> 재호출시 resumed)
                    setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                    replace<DetailFragment>(R.id.fragment_container, args = bundle)
                }
            }
            "History_detail" -> {
                val bundle = bundleOf("name" to name, "timestamp" to timestamp)
                fragmentManager.commit {
                    setReorderingAllowed(true)
                    addToBackStack(null)
                    setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                    replace<HistoryDetailFragment>(R.id.fragment_container, args = bundle)
                }
            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

}