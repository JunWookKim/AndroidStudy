package com.example.apimanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.apimanager.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private val binding by lazy{ActivityMainBinding.inflate(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //서버에 미리 저장된 학생 정보
        val sSchool = "HwaRangE"
        val sName = "aiden"
        val sGrade = "3"
        val sClass = "01"
        val sNumber = "20"

        binding.loginButton.setOnClickListener {
            ApiManager.instance.login(sSchool, sName, sGrade, sClass, sNumber, object : Callback<LoginResponse>{
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    response.body()?.let{
                        //올바른 정보로 로그인 시도시 loginCode 는 200 이다
                        if (it.loginCode == 200){
                            ApiManager.instance.apply{
                                isLogin = true
                                this.sSchool = it.sSchool
                                this.sName = sName
                                sCode = it.sCode.toString()
                            }
                            Toast.makeText(this@MainActivity, "로그인 성공 Welcome! $sName", Toast.LENGTH_SHORT).show()
                        }
                        //서버에 존재하지 않는 학생일 시
                        else {
                          Toast.makeText(this@MainActivity, "존재 하지 않는 학생 정보입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Api 통신 실패 ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.d("login error", t.message.toString())
                }
            })
        }

        binding.lessonButton.setOnClickListener {
            if(ApiManager.instance.isLogin){
                ApiManager.instance.weekAllLesson(ApiManager.instance.sSchool, ApiManager.instance.sCode, object: Callback<WeekAllLessonResponse>{
                    override fun onResponse(call: Call<WeekAllLessonResponse>, response: Response<WeekAllLessonResponse>) {
                        //로그인이 되어있는 상태에서 성공적으로 응답이 오면 ApiManager 의 lesson 에 수업 정보를 저장한다.
                        response.body()?.let {
                            ApiManager.instance.lessons = it.lessonInfoList as MutableList<LessonInfo>
                            val lessonText = getSimpleInfo(ApiManager.instance.lessons)
                            binding.lessonTextView.text = lessonText
                        }
                    }
                    override fun onFailure(call: Call<WeekAllLessonResponse>, t: Throwable) {
                        Toast.makeText(this@MainActivity, "Api 통신 실패 ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this@MainActivity, "로그인을 먼저 해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getSimpleInfo(lessons: MutableList<LessonInfo>): String {
        var result = ""
        for(x in lessons){
            result += "${x.lCode} ${x.lSubName} ${x.lContent} \n"
        }

        return result
    }
}