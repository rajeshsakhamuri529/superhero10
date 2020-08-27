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
import kotlinx.android.synthetic.main.revision_layout.view.*

import com.google.firebase.firestore.QuerySnapshot
import com.google.android.gms.tasks.OnCompleteListener

import android.support.annotation.NonNull
import com.blobcity.interfaces.RevisionItemClickListener
import com.google.android.gms.tasks.Task
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.view.ContextThemeWrapper
import android.widget.Toast
import com.blobcity.R
import com.blobcity.activity.PDFViewerActivity
import com.blobcity.database.DatabaseHandler
import com.blobcity.entity.RevisionEntity
import com.blobcity.interfaces.RevisionItemDownloadListener
import com.blobcity.utils.*
import com.google.firebase.analytics.FirebaseAnalytics

import java.io.File


class RevisionFragment: Fragment(), RevisionItemClickListener,RevisionItemDownloadListener {


    override fun onDownload() {
        //Toast.makeText(activity,"download complete",Toast.LENGTH_LONG).show()
        try{

            if(isPDFVersionChange){
                isPDFVersionChange = false
                val revisionEntity = RevisionEntity()
                revisionEntity.setDocumentId(revision?.documentid)
                revisionEntity.setPdfVersion(revision?.pdfversion)
                databaseHandler?.updateContact(revisionEntity)
            }
            hideProgressDialog()

            revision?.rId?.let { moveToPDFActivity(it,revision!!.filename) }

            //revision?.filename?.let { moveToPDFActivity(it) }

        }catch (e:Exception){
            Log.e("revision fragment","...exception...."+e);
        }

    }

    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 2000 //3 seconds

    internal val mRunnable: Runnable = Runnable {
        if(!isDataFromFirebase){
            showProgressDialog("Please wait...");
        }

    }


    var revisionList: ArrayList<RevisionEntity>?=null
    var tempItemList: ArrayList<RevisionEntity>?=null
    var revisionEntity: RevisionEntity? = null
    private var mPDialog: ProgressDialog? = null
    private var revisionItemList:ArrayList<RevisionModel>?=null
    var revisionModel:RevisionModel? = null
    var revision:RevisionModel? = null
    var topicStatusVM: TopicStatusVM?= null
    var databaseHandler: DatabaseHandler?= null
    var sharedPrefs: SharedPrefs? = null
    var sound: Boolean = false
    var mFile: File? = null
    var isPDFVersionChange:Boolean = false
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var adapter: RevisionAdapter?= null
    var isDataFromFirebase:Boolean = false
    var position1 : Int = -1
    var view1:View? = null
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    /*private val PERMISSIONS = arrayOf<String>(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private fun hasPermissions(context: Context, vararg permissions:String):Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null)
        {
            for (permission in permissions)
            {
                if (ActivityCompat.checkSelfPermission(context, permission) !== PackageManager.PERMISSION_GRANTED)
                {
                    return false
                }
            }
        }
        return true
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

       // view = inflater.inflate(R.layout.revision_layout, container, false) as Nothing?
        /*if (view1 == null)
        {
            view1 = inflater.inflate(R.layout.revision_layout, container, false)
        }*/
        return inflater.inflate(R.layout.revision_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topicStatusVM = ViewModelProviders.of(this).get(TopicStatusVM::class.java)
        //DatabaseHandler db = new DatabaseHandler(this);
        databaseHandler = DatabaseHandler(activity);
        revisionList = ArrayList()
        sharedPrefs = SharedPrefs()
        view.tv_revision.elevation = 15F
        //Initialize the Handler
        mDelayHandler = Handler()
        //Navigate with delay
        mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)
        revisionItemList = ArrayList<RevisionModel>()
        tempItemList = ArrayList<RevisionEntity>()
       // if(activity?.let { hasPermissions(it, *PERMISSIONS) }!!){

            gettingDataFromFireStore()
       /* }else{

            requestPermissions(PERMISSIONS, 112)
        }*/
        firebaseAnalytics = FirebaseAnalytics.getInstance(activity!!)

        firebaseAnalytics.setCurrentScreen(activity!!, "Book", null /* class override */)
        view.frameLL.setOnClickListener {
            Log.e("revision adapter","frameLL onclick.....")
            view.frameLL.visibility = View.GONE
            view.textbooksrl.isEnabled = false
            if(adapter != null){
                adapter!!.closeMore(position1)
            }
        }

        view.textbooksrl.setOnClickListener {
            Log.e("revision adapter","textbooksrl onclick.....")
            view.frameLL.visibility = View.GONE
            view.textbooksrl.isEnabled = false
            if(adapter != null){
                adapter!!.closeMore(position1)
            }

        }


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
                        //Log.e("revision fragment", "New city: ${document.id}")
                       // Log.e("revision fragment", "New city: ${document.data.get("tags")}")

                        revisionModel=RevisionModel()
                        revisionModel?.rId = document.data.get("rid").toString()
                        revisionModel?.shortDescription = document.data.get("shortdescription").toString()
                        revisionModel?.tags = document.data.get("tags").toString()
                        revisionModel?.pdfLink = document.data.get("pdf").toString()
                        revisionModel?.imageLink = document.data.get("thumbnail").toString()
                        revisionModel?.tilte = document.data.get("title").toString()
                        revisionModel?.timeToRead = document.data.get("timetoread").toString()
                        revisionModel?.filename = document.data.get("filename").toString()
                        revisionModel?.pdfversion = document.data.get("pdfversion").toString()
                        revisionModel?.sortorder = document.data.get("sortorder").toString()
                        revisionModel?.sno = document.data.get("sno").toString()
                        revisionModel?.documentid = document.id
                        //Log.e("revision fragment", "New city: ${revisionModel?.pdfversion}")


                        databaseHandler?.insertBooksStatus(revisionModel?.documentid,revisionModel?.rId,"0");
                       // revisionItemList = sortedList as ArrayList<RevisionModel>?
                        revisionModel?.let { revisionItemList?.add(it) }
                        revisionEntity = RevisionEntity()
                        revisionEntity?.setDocumentId(revisionModel?.documentid)
                        revisionEntity?.setPdfVersion(revisionModel?.pdfversion)
                        tempItemList?.add(revisionEntity!!)

                    }
                    try{
                        revisionList = databaseHandler?.getAllContacts() as ArrayList<RevisionEntity>?
                        Log.e("revision fragement","...revision list.."+revisionList?.size);
                        Log.e("revision fragement","...tempItemList list.."+tempItemList?.size);
                        if(revisionList?.size == 0){
                            for (topicStatusModels in revisionItemList!!) {

                                val revisionEntity = RevisionEntity()
                                revisionEntity.setDocumentId(topicStatusModels?.documentid)
                                revisionEntity.setPdfVersion(topicStatusModels?.pdfversion)
                                databaseHandler?.addContact(revisionEntity)
                            }

                        }
                        else if(tempItemList?.size!! > revisionList?.size!!){

                            for (topicStatusModels in tempItemList!!) {

                                if(!revisionList!!.contains(topicStatusModels)){
                                    val revisionEntity = RevisionEntity()
                                    revisionEntity.setDocumentId(topicStatusModels?.documentId)
                                    revisionEntity.setPdfVersion(topicStatusModels?.pdfVersion)
                                    databaseHandler?.addContact(revisionEntity)
                                }

                                /*for(i in 0..(revisionList?.size!!-1)){
                                    if(topicStatusModels.documentId != revisionList!!.get(i).documentId){
                                        val revisionEntity = RevisionEntity()
                                        revisionEntity.setDocumentId(topicStatusModels?.documentId)
                                        revisionEntity.setPdfVersion(topicStatusModels?.pdfVersion)
                                        databaseHandler?.addContact(revisionEntity)
                                    }
                                }*/

                            }

                        }



                    }catch (e:Exception){

                    }
                    try {
                        var sortedList = revisionItemList?.sortedWith(compareBy({ it.sortorder }))

                       // val sortedList:ArrayList<RevisionModel> = (revisionItemList?.sortedWith(compareBy({ it.sortorder })) as ArrayList<RevisionModel>?)!!
                       // revisionItemList = sortedList
                        Log.e("revision fragment","...sorted list..."+sortedList+"...size..."+sortedList?.size);
                        hideProgressDialog()
                        isDataFromFirebase = true
                        adapter = RevisionAdapter(activity!!, sortedList!!, this@RevisionFragment,firebaseAnalytics)
                        //rcv_revision.addItemDecoration(itemDecorator)
                        //rcv_chapter.addItemDecoration(DividerItemDecoration(context,))
                        view!!.rcv_revision.addItemDecoration(VerticalSpaceItemDecoration(40));
                        view!!.rcv_revision.adapter = adapter

                    }catch (e:Exception){
                        Log.e("revision fragment",".....exception..."+e)
                    }

                }
            }
        })



            return revisionItemList


    }
    fun showProgressDialog(loadText: String) {
        hideProgressDialog()
        try {
            mPDialog = ProgressDialog.show(
                ContextThemeWrapper(activity, R.style.DialogCustom),
                "",
                loadText
            )
            mPDialog!!.setCancelable(false)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun hideProgressDialog() {
        try {
            if (mPDialog != null && mPDialog!!.isShowing()) {
                mPDialog!!.dismiss()
                mPDialog = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 112 ) {
            Log.e("revision fragment","...grant result..."+grantResults);
            Log.e("revision fragment","...grant result..."+grantResults[0]);
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // user rejected the permission
              //  Toast.makeText(activity,"permission granted",Toast.LENGTH_LONG).show()
                try {
                    revisionList = databaseHandler?.getAllContacts() as ArrayList<RevisionEntity>?

                    for (x in 0..(revisionList?.size!!-1)) {
                        Log.e(
                            "revision fragment",
                            "...version..." + (revisionList?.get(x)?.getPdfVersion())
                        );
                        // Log.e("revision fragment","...revisionModel?.pdfversion..."+revisionItemList?.get(x)?.pdfversion);

                        if (revision?.documentid == revisionList?.get(x)?.documentId) {
                            if (revisionList?.get(x)?.getPdfVersion() != revision?.pdfversion) {
                                isPDFVersionChange = true
                                break


                            }
                        }

                    }

                    if(isPDFVersionChange){

                        if(Utils.isOnline(activity)){
                            showProgressDialog("Please wait...")
                            DownloadTask(activity,revision?.pdfLink,revision?.filename,this@RevisionFragment)
                        }else{
                            Toast.makeText(activity,"Internet is required!",Toast.LENGTH_LONG).show();
                        }

                    }else{
                        mFile = File(activity?.getExternalFilesDir(null), revision?.filename+".pdf")
                        if(!mFile!!.exists()){
                            if(Utils.isOnline(activity)){
                                showProgressDialog("Please wait...")
                                DownloadTask(activity,revision?.pdfLink,revision?.filename,this@RevisionFragment)
                            }else{
                                Toast.makeText(activity,"Internet is required!",Toast.LENGTH_LONG).show();
                            }
                        }else{
                            revision?.rId?.let { moveToPDFActivity(it,revision!!.filename) }
                            //revision?.filename?.let { moveToPDFActivity(it) }

                        }


                    }

                }catch (e:java.lang.Exception){

                }


            }else {
                Toast.makeText(activity,"Permissions are required to view the file!",Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onClick(revision: RevisionModel) {
        Log.e("revision fragment",".......on click....."+revision.filename)
        sound = sharedPrefs?.getBooleanPrefVal(activity!!, ConstantPath.SOUNDS) ?: true
        if(!sound){
            if (Utils.loaded) {
                Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                Log.e("Test", "Played sound...volume..."+ Utils.volume);
            }
        }
        this.revision = revision
        //if(activity?.let { hasPermissions(it, *PERMISSIONS) }!!){
            try {
                revisionList = databaseHandler?.getAllContacts() as ArrayList<RevisionEntity>?
                Log.e("revision fragment",".......size....."+revisionList?.size!!)
                    for (x in 0..(revisionList?.size!!-1)) {

                        // Log.e("revision fragment","...revisionModel?.pdfversion..."+revisionItemList?.get(x)?.pdfversion);

                        if (revision.documentid == revisionList?.get(x)?.documentId) {

                            if (revisionList?.get(x)?.getPdfVersion() != revision?.pdfversion) {
                                Log.e("revision fragment",".......revision?.pdfversion....."+revision?.pdfversion)
                                isPDFVersionChange = true
                                break
                            }

                        }
                        Log.e("revision fragment",".......revision.documentid....."+revision.documentid)
                    }
                Log.e("revision fragment",".......isPDFVersionChange....."+isPDFVersionChange)
                if(isPDFVersionChange){

                    if(Utils.isOnline(activity)){
                        showProgressDialog("Please wait...")
                        DownloadTask(activity,revision?.pdfLink,revision?.filename,this@RevisionFragment)
                    }else{
                        Toast.makeText(activity,"Internet is required!",Toast.LENGTH_LONG).show();
                    }

                }else{
                    Log.e("revision fragment",".......else....."+isPDFVersionChange)
                    mFile = File(activity?.getExternalFilesDir(null), revision.filename+".pdf")
                    if(!mFile!!.exists()){
                        if(Utils.isOnline(activity)){
                            showProgressDialog("Please wait...")
                            DownloadTask(activity,revision?.pdfLink,revision?.filename,this@RevisionFragment)
                        }else{
                            Toast.makeText(activity,"Internet is required!",Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Log.e("revision fragment",".......else...file exist..")
                       // revision.filename?.let { moveToPDFActivity(it) }
                        revision?.rId?.let { moveToPDFActivity(it,revision!!.filename) }
                    }


               }



            }catch (e:java.lang.Exception){

            }

        /* }else{

             requestPermissions(PERMISSIONS, 112)
         }*/



    }
    private fun moveToPDFActivity(rID: String, filename: String?) {

        /*val bundle = Bundle()
        bundle.putString("Category", "Book")
        bundle.putString("Action", "OpenBook")
        bundle.putString("Label", filename)
        firebaseAnalytics?.logEvent("Book", bundle)*/

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, filename)
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Book")
        // bundle.putString("Label", "TestGo")
        firebaseAnalytics?.logEvent("OpenBook", bundle)
        val i = Intent(activity, PDFViewerActivity::class.java)
        i.putExtra("filename", filename)
        i.putExtra("rID", rID)
        startActivity(i)
        (activity as Activity).overridePendingTransition(0, 0)

    }

    override fun onMoreButttonClicked(open: String,position:Int) {

        if(open.equals("share")){
           //frameLL.visibility
        }else if(open.equals("frame")){

        }else if(open.equals("more")){
            Log.e("revision frag","position......"+position)
            view!!.frameLL.visibility = View.VISIBLE
            view!!.textbooksrl.isEnabled = true
            position1 = position
        }else if(open.equals("marked")){

        }
    }



}