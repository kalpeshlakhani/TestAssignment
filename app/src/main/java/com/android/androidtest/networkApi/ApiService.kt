package com.android.androidtest.networkApi

import com.android.androidtest.responseBean.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("posts")
    fun getPostsData(
        @Query("_start") start: Int,
        @Query("_limit") limit: Int
    ): Call<MutableList<PostDataBean>>
}