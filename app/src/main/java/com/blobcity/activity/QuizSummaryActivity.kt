package com.blobcity.activity

import android.util.Log
import com.blobcity.R
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class QuizSummaryActivity : BaseActivity() {

    var firestore: FirebaseFirestore?=null

    override fun setLayout(): Int {
        return R.layout.activity_quiz_summary
    }

    override fun initView() {
        firestore = FirebaseFirestore.getInstance()
        firestore!!.collection("quiz")
            .addSnapshotListener(object : EventListener<QuerySnapshot>{
                override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                    if (p1 != null) {
                        Log.e("listen:error", p1.message);
                        return;
                    }
                }

            })
    }
}