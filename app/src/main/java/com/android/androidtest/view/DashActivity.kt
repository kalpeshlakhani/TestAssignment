package com.android.androidtest.view

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.androidtest.R
import com.android.androidtest.adapter.OnPostAdapter
import com.android.androidtest.core.HttpStatus
import com.android.androidtest.databinding.ActivityMainBinding
import com.android.androidtest.pagination.PaginationScrollListener
import com.android.androidtest.responseBean.PostDataBean
import com.android.androidtest.utils.*
import com.android.androidtest.viewModel.DataViewModel
import com.techiness.progressdialoglibrary.ProgressDialog

class DashActivity : AppCompatActivity(), OnPostAdapter.OnPostEventListener {
    private var currentPage = 0
    private var totalPage = 10
    private lateinit var binding: ActivityMainBinding
    private var onPostAdapter: OnPostAdapter? = null
    private var isDataLastPage: Boolean = false
    private var isDataLoading: Boolean = false

    private val dataViewModel by lazy {
        ViewModelProvider(this)[DataViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        declaration()
    }

    private fun declaration() {
        setOnPostAdapter()
    }

    private fun callPostDataApi(isReloadData: Boolean) {
        if (!isNetworkAvailable()) {
            toastInfo(getString(R.string.check_connection))
            return
        }

        dataViewModel.getPostDataApi(
            this, currentPage, totalPage
        ).observe(this) { response ->
            when (response.status) {
                HttpStatus.SUCCESS -> {
                    hideProgressDialog()
                    val onPostDataList = response.data
                    if (onPostDataList != null && onPostDataList.size > 0) {
                        binding.idRvPostData.visible()
                        if (isReloadData) {
                            onPostAdapter?.removeLoadingFooter()
                            isDataLoading = false
                            onPostAdapter?.insertAllData(onPostDataList)
                            val count = onPostDataList.size
                            if (count < 10) {
                                isDataLastPage = true
                            } else {
                                val loadedPages = ((onPostAdapter?.itemCount ?: 0) / 10)
                                if (totalPage >= loadedPages) onPostAdapter?.addLoadingFooter()
                                else isDataLastPage = true
                            }
                        } else {
                            onPostAdapter?.insertAllData(onPostDataList, true)
                            val count = onPostDataList.size
                            if (count < 10) {
                                isDataLastPage = true
                            } else {
                                val loadedPages = ((onPostAdapter?.itemCount ?: 0) / 10)
                                if (totalPage >= loadedPages) onPostAdapter?.addLoadingFooter()
                                else isDataLastPage = true
                            }
                        }
                    } else {
                        if (!isReloadData) {
                            binding.idRvPostData.gone()
                        } else {
                            onPostAdapter?.removeLoadingFooter()
                        }
                    }
                }

                HttpStatus.LOADING -> {
                    if (!isReloadData) {
                        showProgressDialog(getString(R.string.please_wait), this@DashActivity)
                    }
                }

                HttpStatus.ERROR, HttpStatus.NETWORK_ERROR -> {
                    hideProgressDialog()
                    if (!isReloadData) {
                        binding.idRvPostData.gone()
                    } else {
                        onPostAdapter?.removeLoadingFooter()
                    }
                }
            }
        }
    }

    private fun setOnPostAdapter() {
        val linearLayoutManager =
            LinearLayoutManager(this@DashActivity, LinearLayoutManager.VERTICAL, false)
        onPostAdapter = OnPostAdapter(this, this@DashActivity)
        binding.idRvPostData.layoutManager = linearLayoutManager
        binding.idRvPostData.adapter = onPostAdapter

        binding.idRvPostData.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isDataLoading = true
                currentPage += 10
                callPostDataApi(true)
            }

            override val isLastPage: Boolean
                get() = isDataLastPage

            override var isLoading: Boolean
                get() = isDataLoading
                set(value) {
                }
        })

        callPostDataApi(false)
    }

    override fun onPostEvent(postData: PostDataBean) {
        navigateToNext(postData)
    }

    private var progressDialog: ProgressDialog? = null

    private fun hideProgressDialog() {
        progressDialog?.dismiss()
    }

    private fun showProgressDialog(msg: String?, context: Activity) {
        if (context.isFinishing) {
            return
        }
        progressDialog?.dismiss()
        progressDialog =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) //Check if Android API Level is greater than or equal to 30
            {
                ProgressDialog(
                    context, ProgressDialog.THEME_FOLLOW_SYSTEM
                )
            } else {
                ProgressDialog(
                    context, ProgressDialog.THEME_LIGHT
                )
            }

        msg?.let { progressDialog!!.setMessage(it) }
        progressDialog!!.show()
    }

    private fun navigateToNext(postDataBean: PostDataBean) {
        startActivityCustom(
            Intent(
                this, DetailActivity::class.java
            ).putExtra(CommonVariable.IntentExtras.POST_DATA, postDataBean)
        )
    }
}