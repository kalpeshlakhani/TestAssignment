package com.android.androidtest.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.androidtest.R
import com.android.androidtest.databinding.ActivityDetailBinding

import com.android.androidtest.responseBean.PostDataBean
import com.android.androidtest.utils.CommonVariable

class DetailActivity : AppCompatActivity() {
    private var postDataModel: PostDataBean? = null
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setHeader()
        declaration()
    }

    private fun setHeader() {
        binding.header.idTvHeaderTitle.text = getString(R.string.overview)
        binding.header.idllBack.visibility = View.VISIBLE
        binding.header.idllBack.setOnClickListener {
            onBackPressed()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun declaration() {
        postDataModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            (intent?.getSerializableExtra(
                CommonVariable.IntentExtras.POST_DATA, PostDataBean::class.java
            ))
        } else {
            (intent.getSerializableExtra(CommonVariable.IntentExtras.POST_DATA) as PostDataBean)
        }

        if (postDataModel != null) {
            binding.idTvTitle.text = postDataModel!!.title

            binding.idTvDesc.text = Html.fromHtml(
                postDataModel!!.description, Html.FROM_HTML_MODE_LEGACY
            )

            binding.idTvIdValue.text = " : ${postDataModel!!.id}"

            binding.idTvUserIdValue.text = " : ${postDataModel!!.userId}"
        }
    }

}