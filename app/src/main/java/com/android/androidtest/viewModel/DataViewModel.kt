package com.android.androidtest.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.androidtest.core.Resource
import com.android.androidtest.repo.ApiRepo
import com.android.androidtest.responseBean.PostDataBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class DataViewModel : ViewModel(), CoroutineScope {
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun getPostDataApi(
        context: Context, startPage: Int, limitPage: Int
    ): MutableLiveData<Resource<MutableList<PostDataBean>>> {
        val result = MutableLiveData<Resource<MutableList<PostDataBean>>>()
        this.launch(context = coroutineContext) {
            result.postValue(Resource.Loading())
            result.postValue(
                ApiRepo.getInstance().getPostDataApi(context, startPage, limitPage)
            )
        }
        return result
    }
}