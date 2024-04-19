package com.android.androidtest.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.androidtest.R
import com.android.androidtest.databinding.PaginateLoaderViewBinding
import com.android.androidtest.databinding.RvPostBinding
import com.android.androidtest.responseBean.PostDataBean


class OnPostAdapter(
    private val mContext: Context, private val onPostListener: OnPostEventListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onPostList: MutableList<PostDataBean> = mutableListOf()
    private var isLoadingAdded = false

    init {
        onPostList = mutableListOf()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            ITEM -> {
                val binding =
                    RvPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return OnBaseViewHolder(binding)
            }
            LOADING -> {
                val binding = PaginateLoaderViewBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return OnLoadingViewHolder(binding)
            }
        }
        val binding = RvPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OnBaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val startTime = System.currentTimeMillis()
        when (getItemViewType(position)) {
            ITEM -> {

                holder as OnBaseViewHolder
                with(onPostList[position]) {
                    holder.onViewBinding.idCvPost.setOnClickListener {
                        onPostListener.onPostEvent(this)
                    }

                    if (title != null && title!!.isNotEmpty()) {
                        holder.onViewBinding.idTvTitle.text =
                            "${HtmlCompat.fromHtml(title!!, HtmlCompat.FROM_HTML_MODE_LEGACY)}"
                    }

                    if (id != null && id!!.isNotEmpty()) {
                        holder.onViewBinding.idTvId.text =
                            "${mContext.getString(R.string.id)} : $id"
                    }

                    if (description != null && description!!.isNotEmpty()) {
                        holder.onViewBinding.idTvDesc.text = "${
                            HtmlCompat.fromHtml(
                                description!!, HtmlCompat.FROM_HTML_MODE_LEGACY
                            )
                        }"
                    }
                }
            }
            LOADING -> {
                val onLoadingViewHolder = holder as OnLoadingViewHolder
                onLoadingViewHolder.onLoaderViewBinding.loadMoreProgress.visibility = View.VISIBLE
            }
        }
        val endTime = System.currentTimeMillis()
        val elapsedTime = endTime - startTime
        Log.e(this.javaClass.simpleName,"ComputationTime Item at position $position: Elapsed time: $elapsedTime ms")

    }

    override fun getItemCount(): Int {
        return if (onPostList == null) 0 else onPostList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == onPostList.size - 1 && isLoadingAdded) LOADING else ITEM
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        insertData(PostDataBean())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position = onPostList.size - 1
        val result = getItem(position)
        if (result != null) {
            onPostList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun insertData(PostDataBean: PostDataBean) {
        onPostList.add(PostDataBean)
        notifyItemInserted(onPostList.size - 1)
    }

    fun insertAllData(moveResults: MutableList<PostDataBean>, isClearData: Boolean = false) {
        if (isClearData) {
            if (onPostList.size > 0) {
                onPostList.clear()
                onPostList = mutableListOf()
                notifyDataSetChanged()
            }
        }
        for (result in moveResults) {
            insertData(result)
        }
    }

    private fun getItem(position: Int): PostDataBean {
        return onPostList[position]
    }

    class OnBaseViewHolder(var onViewBinding: RvPostBinding) :
        RecyclerView.ViewHolder(onViewBinding.root)


    class OnLoadingViewHolder(var onLoaderViewBinding: PaginateLoaderViewBinding) :
        RecyclerView.ViewHolder(onLoaderViewBinding.root)

    interface OnPostEventListener {
        fun onPostEvent(postData: PostDataBean)
    }

    companion object {
        private const val LOADING = 0
        private const val ITEM = 1
    }
}