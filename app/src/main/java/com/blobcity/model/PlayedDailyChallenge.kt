package com.blobcity.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PlayedDailyChallenge : Serializable {

    @SerializedName("challengedate")
    @Expose
    var challengedate: String? = null

    @SerializedName("createddate")
    @Expose
    var createddate: String? = null

    @SerializedName("qid")
    @Expose
    var qid: String? = null

    @SerializedName("result")
    @Expose
    var result: Boolean? = false

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("uid")
    @Expose
    var uid: String? = null

    override fun toString(): String {
        return "PlayedDailyChallenge(challengedate=$challengedate, createddate=$createddate, qid=$qid, result=$result, title=$title, uid=$uid)"
    }


}