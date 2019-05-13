package com.blobcity.model

import com.example.firebasedbexample.Bank

class Quiz {

    var appversion: Int ?= null
    var completed: Boolean ?= null
    var courseId: Int ?= null
    var courseName: String ?= null
    var os: String = "android"
    var quizSession: Map<String, Bank> ?= null

    fun toMap(type: String, bank: Bank): Map<String, Bank> {

        val result = HashMap<String, Bank>()
        result.put(type, bank)

        return result
    }
    var quizType: String ?= null
    var result: Boolean ?= null
    var timeStamp: Long ?= null
    var timeTaken: Int ?= null
    var topicId: Int ?= null
    var topicName: String?= null
    var uId: String?= null
    var androidVersion: Int?= null
}