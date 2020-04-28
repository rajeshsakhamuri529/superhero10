package com.blobcity.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ChallengeModel : Serializable {

    @SerializedName("qid")
    @Expose
    var qid: String? = null

    @SerializedName("questiontype")
    @Expose
    var questiontype: String? = null

    @SerializedName("question")
    @Expose
    var question: String? = null

    @SerializedName("opt1")
    @Expose
    var opt1: String? = null

    @SerializedName("opt2")
    @Expose
    var opt2: String? = null
    @SerializedName("opt3")
    @Expose
    var opt3: String? = null
    @SerializedName("opt4")
    @Expose
    var opt4: String? = null
    @SerializedName("hint")
    @Expose
    var hint: String? = null
    @SerializedName("challengedate")
    @Expose
    var challengedate: String? = null

    @SerializedName("challengetitle")
    @Expose
    var challengetitle: String? = null
    @SerializedName("questionversion")
    @Expose
    var questionversion: String? = null
    @SerializedName("serialNo")
    @Expose
    var serialno: String? = null
    var documentid: String? = null
    override fun toString(): String {
        return "ChallengeModel(qid=$qid, questiontype=$questiontype, question=$question, opt1=$opt1, opt2=$opt2, opt3=$opt3, opt4=$opt4, hint=$hint, challengedate=$challengedate, challengetitle=$challengetitle, questionversion=$questionversion, documentid=$documentid)"
    }


}