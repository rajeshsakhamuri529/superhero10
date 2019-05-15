package com.blobcity.model

import java.io.Serializable

class ReviewModel : Serializable{
    var questionsItem: TopicOneQuestionsItem?=null
    var listOfOptions: ArrayList<String>? = null
    var answerList: ArrayList<String> ?= null
}