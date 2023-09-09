package com.example.midterm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.midterm.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val viewModel =
            ViewModelProvider(this)[MainViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        // don't really needed it
//        val navController = findNavController(R.id.fragment)

        val adapter = Adapter()
        binding.recyclerview.adapter = adapter

        // reverse the list is better
//        val layoutManager = LinearLayoutManager(this)
//        layoutManager.reverseLayout = true
//        binding.recyclerview.layoutManager = layoutManager


        lifecycleScope.launch {

            viewModel.getPostsFlow().collect {
                Log.i("test", "$it")
                withContext(Dispatchers.Main) {
                    adapter.submitList(it.reversed())

                }
            }
        }


//        fun addData() {
//            val articles = FirebaseFirestore.getInstance()
//                .collection("articles")
//            val document = articles.document()
//            val data = hashMapOf(
//                "author" to hashMapOf(
//                    "email" to "wayne@school.appworks.tw",
//                    "id" to "waynechen323",
//                    "name" to "AKA小安老師"
//                ),
//                "title" to "888888888888888888",
//                "content" to "南韓歌手IU(李知恩)，但俗話說人無完美、美玉微瑕，曾再跟工作人員的互動影片中坦言自己 品味很奇怪，近日在IG上分享了宛如「媽媽們青春時代的玉女歌手」超復古穿搭造型，卻意外美出新境界。", "createdTime" to Calendar.getInstance()
//                    .timeInMillis, "id" to document.id, "category" to "Gossiping"
//            )
//            document.set(data) }
//
//        addData()
    }
}
