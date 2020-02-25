package com.blobcity.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blobcity.adapter.RevisionAdapter
import com.blobcity.model.RevisionModel
import com.blobcity.viewmodel.TopicStatusVM
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.revision_layout.*

import com.google.firebase.firestore.QuerySnapshot
import com.google.android.gms.tasks.OnCompleteListener

import android.support.annotation.NonNull
import com.blobcity.interfaces.RevisionItemClickListener
import com.google.android.gms.tasks.Task
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Environment
import android.support.v4.app.FragmentActivity
import android.widget.Toast
import com.blobcity.R
import com.blobcity.activity.PDFViewActivity
import com.blobcity.activity.PDFViewerActivity
import com.blobcity.utils.*

import java.io.File
import java.io.IOException


class RevisionFragment: Fragment(), RevisionItemClickListener {

    private var revisionItemList:ArrayList<RevisionModel>?=null
    var revisionModel:RevisionModel? = null
    var topicStatusVM: TopicStatusVM?= null
    var sharedPrefs: SharedPrefs? = null
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var adapter: RevisionAdapter?= null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.revision_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topicStatusVM = ViewModelProviders.of(this).get(TopicStatusVM::class.java)
        sharedPrefs = SharedPrefs()
        revisionItemList = ArrayList<RevisionModel>()

        gettingDataFromFireStore()
        /*if(gettingDataFromFireStore()?.size!! > 0){

            adapter = RevisionAdapter(activity!!, revisionItemList!!)
            //rcv_revision.addItemDecoration(itemDecorator)
            //rcv_chapter.addItemDecoration(DividerItemDecoration(context,))
            rcv_revision.addItemDecoration(VerticalSpaceItemDecoration(48));
            rcv_revision.adapter = adapter
        }*/


    }

    fun gettingDataFromFireStore() : ArrayList<RevisionModel>? {

        val query = db.collection("revisions")
        val rootRef = FirebaseFirestore.getInstance()
        val attractionsRef = rootRef.collection("revisions")
        attractionsRef.get().addOnCompleteListener(object: OnCompleteListener<QuerySnapshot> {
            override fun onComplete(@NonNull task:Task<QuerySnapshot>) {
                if (task.isSuccessful())
                {
                   // val attractionsList = ArrayList()
                    for (document in task.getResult()!!)
                    {
                        Log.e("revision fragment", "New city: ${document.data.get("shortdescription")}")
                        Log.e("revision fragment", "New city: ${document.data.get("tags")}")
                        revisionModel=RevisionModel()
                        revisionModel?.rId = document.data.get("rid").toString()
                        revisionModel?.shortDescription = document.data.get("shortdescription").toString()
                        revisionModel?.tags = document.data.get("tags").toString()
                        revisionModel?.pdfLink = document.data.get("pdf").toString()
                        revisionModel?.imageLink = document.data.get("thumbnail").toString()
                        revisionModel?.tilte = document.data.get("title").toString()
                        revisionModel?.timeToRead = document.data.get("timetoread").toString()
                        revisionModel?.filename = document.data.get("filename").toString()

                        if(Utils.isOnline(activity)){
                            //Toast.makeText(activity,"if.....", Toast.LENGTH_LONG).show()
                            DownloadTask(activity,revisionModel?.pdfLink,revisionModel?.filename)
                        }


                        Log.e("revision fragment",".....revision model...."+revisionModel?.shortDescription);
                        revisionModel?.let { revisionItemList?.add(it) }
                    }
                    Log.e("revision fragment", "revisionItemList.........."+ (revisionItemList?.size))
                    try {
                        adapter = RevisionAdapter(activity!!, revisionItemList!!, this@RevisionFragment)
                        //rcv_revision.addItemDecoration(itemDecorator)
                        //rcv_chapter.addItemDecoration(DividerItemDecoration(context,))
                        rcv_revision.addItemDecoration(VerticalSpaceItemDecoration(48));
                        rcv_revision.adapter = adapter

                    }catch (e:Exception){
                        Log.e("revision fragment",".....exception..."+e)
                    }

                    // methodToProcessTheList(attractionsList)
                }
            }
        })

        /*val registration =  query.addSnapshotListener(MetadataChanges.INCLUDE) { querySnapshot, e ->
                if (e != null) {
                    Log.e("revision fragment", "Listen error", e)
                    return@addSnapshotListener
                }
                for (change in querySnapshot!!.documentChanges) {
                    if (change.type == DocumentChange.Type.ADDED) {
                        Log.e("revision fragment", "New city: ${change.document.data.get("shortdescription")}")
                        Log.e("revision fragment", "New city: ${change.document.data.get("tags")}")
                        // pendingFDMModel.setTimeStamp(document.get("timeStamp")!!.toString())
                        // pendingFDMModel.setAppVersion(document.get("appVersion")!!.toString())
                        revisionModel=RevisionModel()
                        revisionModel?.rId = change.document.data.get("rid").toString()
                        revisionModel?.shortDescription = change.document.data.get("shortdescription").toString()
                        revisionModel?.tags = change.document.data.get("tags").toString()
                        revisionModel?.pdfLink = change.document.data.get("pdf").toString()
                        revisionModel?.imageLink = change.document.data.get("thumbnail").toString()
                        revisionModel?.tilte = change.document.data.get("title").toString()
                        revisionModel?.timeToRead = change.document.data.get("timetoread").toString()

                        Log.e("revision fragment",".....revision model...."+revisionModel?.shortDescription);
                        revisionModel?.let { revisionItemList?.add(it) }
                    }

                    val source = if (querySnapshot.metadata.isFromCache)
                        "local cache"
                    else
                        "server"
                    Log.e("revision fragment", "Data fetched from $source")
                }
                //Thread.sleep(500)
                Log.e("revision fragment", "revisionItemList.........."+ (revisionItemList?.size))
            try {
                adapter = RevisionAdapter(activity!!, revisionItemList!!,this)
                //rcv_revision.addItemDecoration(itemDecorator)
                //rcv_chapter.addItemDecoration(DividerItemDecoration(context,))
                rcv_revision.addItemDecoration(VerticalSpaceItemDecoration(48));
                rcv_revision.adapter = adapter

            }catch (e:Exception){
                Log.e("revision fragment",".....exception..."+e)
            }


            }*/

            return revisionItemList


    }

    override fun onClick(rID: String) {
        Log.e("revision fragment",".......on click....."+rID)
        moveToPDFActivity(rID)

    }
    private fun moveToPDFActivity(rID: String) {

        val i = Intent(activity, PDFViewerActivity::class.java)
        i.putExtra("rID", rID)
        startActivity(i)
        (activity as Activity).overridePendingTransition(0, 0)

    }

    /*private class DownloadFile(activity: RevisionFragment?) : AsyncTask<String, Void, Void>() {
        var context:Context? = null
        protected override fun doInBackground(vararg strings:String): Void? {
            Log.v("pdf view activity", "doInBackground() Method invoked ")
            val fileUrl = strings[0] // -> http://maven.apache.org/maven-1.x/maven.pdf
            val fileName = strings[1] // -> maven.pdf

           // context = strings[2]
            val extStorageDirectory = Environment.getExternalStorageDirectory().toString()
            //File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            //mFile = new File(getActivity().getExternalFilesDir(null), "pic.jpg");
            val pdfFile = File(activity.getExternalFilesDir(null), fileName)
            Log.v("pdf view activity", "doInBackground() pdfFile invoked " + pdfFile.getAbsolutePath())
            Log.v("pdf view activity", "doInBackground() pdfFile invoked " + pdfFile.getAbsoluteFile())
            try
            {
                pdfFile.createNewFile()
                Log.v("pdf view activity", "doInBackground() file created" + pdfFile)
            }
            catch (e: IOException) {
                e.printStackTrace()
              //  Log.e("pdf view activity", "doInBackground() error" + e.getMessage())
                Log.e("pdf view activity", "doInBackground() error" + e.getStackTrace())
            }
            FileDownloader.downloadFile(fileUrl, pdfFile)
            Log.v("pdf view activity", "doInBackground() file download completed")
            return null
        }


    }*/

}