package com.android.androidtest.repo

import android.content.Context
import com.android.androidtest.BuildConfig
import com.android.androidtest.core.Resource
import com.android.androidtest.networkApi.NetworkRestClient
import com.android.androidtest.responseBean.PostDataBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class ApiRepo {

    companion object {
        private var INSTANCE: ApiRepo? = null

        fun getInstance() = INSTANCE ?: ApiRepo().also {
            INSTANCE = it
        }
    }

    suspend fun getPostDataApi(
        context: Context, startPage: Int, limitPage: Int
    ): Resource<MutableList<PostDataBean>> = coroutineScope {
        return@coroutineScope try {
            withContext(Dispatchers.IO) {
                val response =
                    NetworkRestClient.getApiService(context, BuildConfig.API_BASE_URL).getPostsData(
                        startPage, limitPage
                    ).execute()

                if (response.isSuccessful) {
                    response.body()?.let { _result ->
                        Resource.Success(_result)
                    } ?: Resource.Error(response.message().toString())
                } else {
                    val message = if (response.code() == 403) {
                        "403 Forbidden Error"
                    } else {
                        ""
                    }
                    Resource.Error(message)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message!!)
        }
    }

}