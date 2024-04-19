package com.android.androidtest.responseBean

import com.google.gson.annotations.SerializedName

class PostDataBean : java.io.Serializable{
    @SerializedName("userId")
    var userId: String? = null

    @SerializedName("id")
    var id: String? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("body")
    var description: String? = null

}