package com.example.midterm


enum class Category(val displayName: String)  {
    SCHOOLLIFE("SchoolLife"),
    BEAUTY("Beauty"),
    GOSSIP("Gossiping")
}

data class PostData(
    val title :String?,
    val author : AuthorInfo,
    val name : String?,
    val time : String?,
    val category : String?,
    val content : String?
)

data class AuthorInfo(
    val email:String?,
    val id: String?,
    val name : String?
)