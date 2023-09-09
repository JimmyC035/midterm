package com.example.midterm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.midterm.databinding.ActivityMainBinding
import com.example.midterm.databinding.DialogBinding
import com.example.midterm.databinding.LoginBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val viewModel =
            ViewModelProvider(this)[MainViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        auth = FirebaseAuth.getInstance()


        var jumpLoginDialog = false




        fun showLoginDialog() {
            val loginBinding = LoginBinding.inflate(LayoutInflater.from(this))
            val loginDialog = AlertDialog.Builder(this)
                .setView(loginBinding.root)
                .create()
            jumpLoginDialog = true


            loginBinding.btnLogin.setOnClickListener{
                val username = loginBinding.editUsername.text.toString()
                val password = loginBinding.editPassword.text.toString()

                    val auth = FirebaseAuth.getInstance()
                    auth.signInWithEmailAndPassword(username, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                viewModel.isLogin.value = true
                                viewModel.userInfo.value = FirebaseAuth.getInstance().currentUser
                                Log.d("Login", "signInWithEmail:success")
                                loginDialog.dismiss()
                            } else {
                                val errorMessage = task.exception?.message
                                Toast.makeText(this, "登入失敗：$errorMessage", Toast.LENGTH_SHORT).show()
                            }
                        }

            }

            loginBinding.btnRegister.setOnClickListener{
                val username = loginBinding.editUsername.text.toString()
                val password = loginBinding.editPassword.text.toString()
                val name = loginBinding.editName.text.toString()
                    val auth = FirebaseAuth.getInstance()
                try {
                    auth.createUserWithEmailAndPassword(username, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                Log.d("Register", "createUserWithEmail:success")
                                auth.signInWithEmailAndPassword(username, password)
                                    .addOnCompleteListener(this) { task ->
                                        if (task.isSuccessful) {
                                            viewModel.isLogin.value = true
                                            viewModel.userInfo.value = FirebaseAuth.getInstance().currentUser
                                            Log.d("Login", "signInWithEmail:success")
                                            loginDialog.dismiss()
                                        } else {
                                            val errorMessage = task.exception?.message
                                            Toast.makeText(this, "登入失敗：$errorMessage", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                loginDialog.dismiss()
                                val userInfo = FirebaseAuth.getInstance().currentUser
                                userInfo?.let {
                                    val userProfileChangeRequest = UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build()

                                    user?.updateProfile(userProfileChangeRequest)
                                        ?.addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Log.i("profile","success")
                                            }
                                        }
                                }
                            } else {
                                val errorMessage = task.exception?.message
                                Toast.makeText(this, "註冊失敗：$errorMessage", Toast.LENGTH_SHORT).show()
                            }
                        }
                }catch (e :Exception){
                    Log.i("test","$e")
                    Toast.makeText(this, "註冊失敗", Toast.LENGTH_SHORT).show()
                    showLoginDialog()
                }


                loginDialog.dismiss()
            }

            loginBinding.closeButton.setOnClickListener {
                loginDialog.dismiss()
            }
            loginDialog.show()
        }


        if (!jumpLoginDialog) {
            showLoginDialog()
        }





        // don't really needed it
//        val navController = findNavController(R.id.fragment)

        val adapter = Adapter()
        binding.recyclerview.adapter = adapter

        // reverse the list is better
//        val layoutManager = LinearLayoutManager(this)
//        layoutManager.reverseLayout = true
//        binding.recyclerview.layoutManager = layoutManager
        fun addData(title:String,content:String,category: String) {
            val articles = FirebaseFirestore.getInstance()
                .collection("articles")
            val document = articles.document()
            val data = hashMapOf(
                "author" to hashMapOf(
                    "email" to auth.currentUser?.email,
                    "id" to "waynechen323",
                    "name" to auth.currentUser?.displayName
                ),
                "title" to title,
                "content" to content, "createdTime" to Calendar.getInstance()
                    .timeInMillis, "id" to document.id, "category" to category
            )
            document.set(data)
        }


        lifecycleScope.launch {

            viewModel.getPostsFlow().collect {
                Log.i("test", "$it")
                withContext(Dispatchers.Main) {
                    adapter.submitList(it.reversed())
                }
            }
        }
        binding.swiperefresh.setOnRefreshListener {
            lifecycleScope.launch {

                viewModel.getPostsFlow().collect {
                    Log.i("test", "$it")
                    withContext(Dispatchers.Main) {
                        adapter.submitList(it.reversed())
                        binding.swiperefresh.isRefreshing = false

                    }
                }
            }

        }

        binding.button.setOnClickListener {
            val dialogBinding = DialogBinding.inflate(LayoutInflater.from(this))
            val alertDialog = AlertDialog.Builder(this)
                .setView(dialogBinding.root)
                .create()

            dialogBinding.btnSubmit.setOnClickListener {
                val title = dialogBinding.editTitle.text.toString()
                val category = dialogBinding.editCategory.text.toString()
                val content = dialogBinding.editContent.text.toString()

                if(viewModel.isLogin.value != false){
                    addData(title,content,category)
                }else{
                    Toast.makeText(baseContext, "You are not logged in",
                        Toast.LENGTH_SHORT).show()
                    showLoginDialog()
                }

                alertDialog.dismiss()
            }

            alertDialog.show()
        }







    }
}
