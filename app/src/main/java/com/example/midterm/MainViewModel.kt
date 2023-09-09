package com.example.midterm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel : ViewModel() {


    val db = FirebaseFirestore.getInstance()
    val collection = db.collection("articles").orderBy("createdTime")

    val isLogin =  MutableLiveData<Boolean>(false)

    var userInfo = MutableLiveData<FirebaseUser>()


    fun getPostsFlow(): Flow<List<PostData>> = callbackFlow {
        val listenerRegistration = collection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("retrieve", "Listen failed.", e)
                close(e)  // Close the flow with an exception
                return@addSnapshotListener
            }

            val posts = snapshot?.documents?.mapNotNull { document ->
                val authorMap = document.get("author") as? Map<String, Any>
                val email = authorMap?.get("email") as? String
                val id = authorMap?.get("id") as? String
                val name = authorMap?.get("name") as? String
                val authorInfo = AuthorInfo(email, id, name)
                val time: Long? = document.getLong("createdTime")
                val date = time?.let { Date(it) }
                val formattedDate = date?.let {
                    val targetFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
                    targetFormat.format(it)
                }



                Log.i("test234","$authorInfo")
                PostData(
                    title = document.getString("title") ,
                    name = document.getString("name") ,
                    author = authorInfo,
                    time = formattedDate,
                    category = document.getString("category"),
                    content = document.getString("content")
                )
            } ?: emptyList()
            Log.i("test3","$posts")


            // Emit the list of posts
            trySend(posts).isSuccess
        }

        // Remove the listener when the flow is cancelled
        awaitClose { listenerRegistration.remove() }
    }












}