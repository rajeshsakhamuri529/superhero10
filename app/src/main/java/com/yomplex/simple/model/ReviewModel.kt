package com.yomplex.simple.model

import java.io.Serializable

class ReviewModel : Serializable{
    var questionsItem: TopicOneQuestionsItem?=null
    var optionsWithAnswerList: ArrayList<OptionsWithAnswer>? = null
    var listOfOptions: ArrayList<String>?= null
}