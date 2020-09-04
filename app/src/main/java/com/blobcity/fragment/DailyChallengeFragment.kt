package com.blobcity.fragment

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler

import androidx.core.app.ActivityCompat

import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import com.blobcity.R
import com.blobcity.activity.ChallengeQuestionActivity

import com.blobcity.adapter.DailyChallengeAdapter
import com.blobcity.database.DatabaseHandler
import com.blobcity.entity.DailyChallenge
import com.blobcity.interfaces.ChallengeItemClickListener
import com.blobcity.interfaces.ChallengeItemDownloadListener
import com.blobcity.model.ChallengeModel
import com.blobcity.utils.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.daily_challenge.view.*

import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class DailyChallengeFragment: Fragment(), ChallengeItemClickListener,
    ChallengeItemDownloadListener {


    override fun onDownload() {

        try {
            if(isQuestionVersionChange){
                isQuestionVersionChange = false

                databaseHandler?.updateQuestionVersion(challenge?.questionversion,challenge?.documentid)
            }

            hideProgressDialog()
            val intent = Intent(activity, ChallengeQuestionActivity::class.java)
            intent.putExtra("challenge", challenge)
            startActivity(intent)
        }catch (e:java.lang.Exception){

        }

    }

    override fun onClick(model: ChallengeModel) {
        this.challenge = model
        sound = sharedPrefs?.getBooleanPrefVal(context!!, ConstantPath.SOUNDS) ?: true
        if(!sound) {
            //MusicManager.getInstance().play(context, R.raw.amount_low);
            // Is the sound loaded already?
            if (Utils.loaded) {
                Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                Log.e("Test", "Played sound...volume..." + Utils.volume);
                //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
            }
        }
        if(activity?.let { hasPermissions(it, *PERMISSIONS) }!!){
           try{
               dailyList = databaseHandler?.getDailyChallengeList() as ArrayList<DailyChallenge>?
               for (x in 0..(dailyList?.size!!-1)) {
                   if (challenge?.documentid == dailyList?.get(x)?.docid) {

                       if (dailyList?.get(x)?.getQuestionversion() != challenge?.questionversion) {
                           Log.e("revision fragment",".......challenge?.questionversion....."+challenge?.questionversion)
                           isQuestionVersionChange = true
                           break
                       }

                   }
               }
               Log.e("daily challenge","isQuestionVersionChange....."+isQuestionVersionChange);
               if(isQuestionVersionChange){
                   if(Utils.isOnline(activity)){
                       Log.e("daily challenge","links.........."+challenge?.question);
                       if(challenge?.question.equals("") || challenge?.question.equals("null")){
                           Toast.makeText(activity,"Links are required!", Toast.LENGTH_LONG).show();
                       }else{
                           var folderPath = File(
                               activity!!.getExternalFilesDir(null),
                               "/Challenge/" + model.serialno!!
                           )
                           var deleted = false
                           if (folderPath.isDirectory()) {
                               val children = folderPath.list()
                               Log.e("photos upload", "children..." + children!!.size)
                               for (j in children!!.indices) {
                                   deleted = File(folderPath, children!![j]).delete()
                               }
                               deleted = folderPath.delete()
                           }

                           showProgressDialog("Please wait...")
                           val task = DownloadChallengeTask(activity,challenge,this@DailyChallengeFragment)
                       }


                   }else{
                       Toast.makeText(activity,"Internet is required!", Toast.LENGTH_LONG).show();
                   }
               }else{

                   mFile = File(activity?.getExternalFilesDir(null), "/Challenge/"+challenge?.serialno)
                   if(mFile!!.exists()){
                       val intent = Intent(activity, ChallengeQuestionActivity::class.java)
                       intent.putExtra("challenge", challenge)

                       startActivity(intent)
                   }else{
                       if(Utils.isOnline(activity)){
                           Log.e("daily challenge","links.........."+challenge?.question);
                           if(challenge?.question.equals("") || challenge?.question.equals("null")){
                               Toast.makeText(activity,"Links are required!", Toast.LENGTH_LONG).show();
                           }else{
                               showProgressDialog("Please wait...")
                               //DownloadTask(activity,revision?.pdfLink,revision?.filename,this@RevisionFragment)
                               val task = DownloadChallengeTask(activity,challenge,this@DailyChallengeFragment)
                           }

                           // task.execute(arrayOf<String>(model.question, model.hint, model.opt1))
                       }else{
                           Toast.makeText(activity,"Internet is required!", Toast.LENGTH_LONG).show();
                       }
                   }

               }
           }catch (e:java.lang.Exception){

           }






        }else{

            requestPermissions(PERMISSIONS, 112)
        }



    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 112) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try{
                    dailyList = databaseHandler?.getDailyChallengeList() as ArrayList<DailyChallenge>?
                    for (x in 0..(dailyList?.size!!-1)) {
                        if (challenge?.documentid == dailyList?.get(x)?.docid) {

                            if (dailyList?.get(x)?.getQuestionversion() != challenge?.questionversion) {
                                Log.e("revision fragment",".......challenge?.questionversion....."+challenge?.questionversion)
                                isQuestionVersionChange = true
                                break
                            }

                        }
                    }
                    if(isQuestionVersionChange){
                        if(Utils.isOnline(activity)){
                            Log.e("daily challenge","links.........."+challenge?.question);
                            if(challenge?.question.equals("") || challenge?.question.equals("null")){
                                Toast.makeText(activity,"Links are required!", Toast.LENGTH_LONG).show();
                            }else{
                                var file = File(
                                    activity!!.getExternalFilesDir(null),
                                    "/Challenge/" + challenge?.serialno!!
                                )
                                //dir = new File(sdCard.getAbsolutePath() + "/SvastiHome/BankAccount/" + customerNo);
                                // file.mkdirs()
                                var folderPath = File(
                                    activity!!.getExternalFilesDir(null),
                                    "/Challenge/" + challenge?.serialno!!
                                )
                                var deleted = false
                                if (folderPath.isDirectory()) {
                                    val children = folderPath.list()
                                    Log.e("photos upload", "children..." + children!!.size)
                                    for (j in children!!.indices) {
                                        deleted = File(folderPath, children!![j]).delete()
                                    }
                                    deleted = folderPath.delete()
                                }
                                showProgressDialog("Please wait...")
                                val task = DownloadChallengeTask(activity,challenge,this@DailyChallengeFragment)

                            }

                        }else{
                            Toast.makeText(activity,"Internet is required!", Toast.LENGTH_LONG).show();
                        }
                    }else{

                        mFile = File(activity?.getExternalFilesDir(null), "/Challenge/"+challenge?.serialno)
                        if(mFile!!.exists()){
                            val intent = Intent(activity, ChallengeQuestionActivity::class.java)
                            intent.putExtra("challenge", challenge)

                            startActivity(intent)
                        }else{
                            if(Utils.isOnline(activity)){
                                Log.e("daily challenge","links.........."+challenge?.question);
                                if(challenge?.question.equals("") || challenge?.question.equals("null")){
                                    Toast.makeText(activity,"Links are required!", Toast.LENGTH_LONG).show();
                                }else{
                                    showProgressDialog("Please wait...")
                                    //DownloadTask(activity,revision?.pdfLink,revision?.filename,this@RevisionFragment)
                                    val task = DownloadChallengeTask(activity,challenge,this@DailyChallengeFragment)
                                    // task.execute(arrayOf<String>(model.question, model.hint, model.opt1))
                                }

                            }else{
                                Toast.makeText(activity,"Internet is required!", Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                }catch (e:java.lang.Exception){

                }
            }else{
                Toast.makeText(activity,"Permissions are required!",Toast.LENGTH_LONG).show()
            }
        }
    }

    private val PERMISSIONS = arrayOf<String>(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
    }
    var sharedPrefs: SharedPrefs? = null
    var sound: Boolean = false
    private lateinit var mHandler: Handler
    private lateinit var mRunnable:Runnable
    var isQuestionVersionChange:Boolean = false
    var dailyList: ArrayList<DailyChallenge>?=null
    var tempItemList: ArrayList<DailyChallenge>?=null
    var databaseHandler: DatabaseHandler?= null
    var mFile: File? = null
    private var dailyChallengeList:ArrayList<ChallengeModel>?=null
    private var afterSortingdailyChallengeList:ArrayList<ChallengeModel>?=null
    var challengeModel:ChallengeModel? = null
    var dailyChallenge:DailyChallenge? = null
    var challenge:ChallengeModel? = null
    var adapter: DailyChallengeAdapter?= null
    private var mPDialog: ProgressDialog? = null
    var isFirstTime:Boolean = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.daily_challenge, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dailyChallengeList = ArrayList<ChallengeModel>()
        afterSortingdailyChallengeList = ArrayList<ChallengeModel>();
        databaseHandler = DatabaseHandler(activity);
        dailyList = ArrayList()
        tempItemList = ArrayList<DailyChallenge>()
// Initialize the handler instance
        mHandler = Handler()
        sharedPrefs = SharedPrefs()


        //swipe_container.setOnRefreshListener(activity);
        view.swipe_container.setOnRefreshListener {
            gettingChallengesFromFireStore();
        }

        view.swipe_container.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        gettingChallengesFromFireStore();

    }



    fun gettingChallengesFromFireStore() : ArrayList<ChallengeModel>? {
// Hide swipe to refresh icon animation
        view!!.swipe_container.isRefreshing = true
        tempItemList?.clear()
        dailyChallengeList?.clear()
        dailyList?.clear()
        afterSortingdailyChallengeList?.clear()
        /*var inputFormat: DateFormat = SimpleDateFormat("ddMMyyyy")
        var outputFormat: DateFormat = SimpleDateFormat("MMM dd, yyyy")
        var inputDateStr = dailyChallengeList[position].challengedate.toString()
        var date = inputFormat.parse(inputDateStr)
        var outputDateStr = outputFormat.format(date)*/
       /* var currentdate:String
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("ddMMyyyy HH:mm")
            currentdate =  current.format(formatter)
            Log.e("answer","if........date...."+currentdate)
        } else {
            var date = Date();
            val formatter = SimpleDateFormat("ddMMyyyy HH:mm")
            currentdate = formatter.format(date)
            Log.e("answer","else.........date...."+currentdate)
        }*/


        val rootRef = FirebaseFirestore.getInstance()
        val attractionsRef = rootRef.collection("dailychallenge").orderBy("serialNo", Query.Direction.DESCENDING);
            //.whereLessThanOrEqualTo("challengedate",currentdate)
        //attractionsRef.whereLessThanOrEqualTo("challengetime","16:15")
        attractionsRef.get().addOnCompleteListener(object: OnCompleteListener<QuerySnapshot> {
            override fun onComplete(@NonNull task: Task<QuerySnapshot>) {
                if (task.isSuccessful())
                {
                    // val attractionsList = ArrayList()
                    for (document in task.getResult()!!)
                    {
                        //Log.e("revision fragment", "New city: ${document.id}")
                        // Log.e("revision fragment", "New city: ${document.data.get("tags")}")
                        challengeModel = ChallengeModel()

                        challengeModel?.qid = document.data.get("qid").toString()
                        challengeModel?.questiontype = document.data.get("questiontype").toString()
                        challengeModel?.question = document.data.get("question").toString()
                        challengeModel?.opt1 = document.data.get("opt1").toString()
                        challengeModel?.opt2 = document.data.get("opt2").toString()
                        challengeModel?.opt3 = document.data.get("opt3").toString()
                        challengeModel?.opt4 = document.data.get("opt4").toString()
                        challengeModel?.hint = document.data.get("hint").toString()
                        challengeModel?.challengedate = document.data.get("challengedate").toString()

                        challengeModel?.challengetitle = document.data.get("challengetitle").toString()
                        challengeModel?.questionversion = document.data.get("questionversion").toString()
                        challengeModel?.serialno = document.data.get("serialNo").toString()
                        challengeModel?.documentid = document.id

                        Log.e("daily challenge","doc id..........."+challengeModel?.documentid);


                        challengeModel?.let { dailyChallengeList?.add(it) }

                        dailyChallenge = DailyChallenge()
                        dailyChallenge!!.setQid(challengeModel!!.qid)
                        dailyChallenge!!.setQtype(challengeModel!!.questiontype)
                        dailyChallenge!!.setDate(challengeModel!!.challengedate)
                        dailyChallenge!!.setTitle(challengeModel!!.challengetitle)
                        dailyChallenge!!.setDocid(challengeModel!!.documentid)
                        dailyChallenge!!.setQuestionversion(challengeModel!!.questionversion)
                        dailyChallenge!!.setAttempt("0")

                        tempItemList?.add(dailyChallenge!!)




                    }
                    Log.e("daily challenge","dailyChallengeList.......size..."+dailyChallengeList?.size);

                    Log.e("daily challenge","tempItemList.....size....."+tempItemList?.size);


                    try {
                        dailyList = databaseHandler?.getDailyChallengeList() as ArrayList<DailyChallenge>?
                        Log.e("daily challenge","daily list........."+dailyList?.size);
                        if(dailyList?.size == 0){
                            for (challenge in dailyChallengeList!!) {
                                Log.e("daily challenge","for loop....."+tempItemList?.size);
                                val dailyChallenge = DailyChallenge()
                                dailyChallenge!!.setQid(challenge!!.qid)
                                dailyChallenge!!.setQtype(challenge!!.questiontype)
                                dailyChallenge!!.setDate(challenge!!.challengedate)
                                dailyChallenge!!.setTitle(challenge!!.challengetitle)
                                dailyChallenge!!.setDocid(challenge!!.documentid)
                                dailyChallenge!!.setQuestionversion(challenge!!.questionversion)
                                dailyChallenge!!.setAttempt("0")
                                databaseHandler?.insertDailyChallenge(dailyChallenge)
                            }
                        }else if(tempItemList?.size!! > dailyList?.size!!){
                            for (challenge in tempItemList!!) {

                                if (!dailyList!!.contains(challenge)) {
                                    val dailyChallenge = DailyChallenge()
                                    dailyChallenge!!.setQid(challenge!!.qid)
                                    dailyChallenge!!.setQtype(challenge!!.qtype)
                                    dailyChallenge!!.setDate(challenge!!.date)
                                    dailyChallenge!!.setTitle(challenge!!.title)
                                    dailyChallenge!!.setDocid(challenge!!.docid)
                                    dailyChallenge!!.setQuestionversion(challenge!!.questionversion)
                                    dailyChallenge!!.setAttempt("0")
                                    databaseHandler?.insertDailyChallenge(dailyChallenge)
                                }
                            }
                        }



                    }catch (e:Exception){

                    }

                    try {

                       // var sortedList = dailyChallengeList?.sortedByDescending{ it.serialno }
                       //dailyChallengeList?.sortBy({ it.serialno })                        // val sortedList:ArrayList<RevisionModel> = (revisionItemList?.sortedWith(compareBy({ it.sortorder })) as ArrayList<RevisionModel>?)!!
                        // revisionItemList = sortedList
                       // Log.e("revision fragment","...sorted list..."+sortedList+"...size..."+sortedList?.size);
                      //  hideProgressDialog()
                      //  isDataFromFirebase = true
                        /*var currentdate:String
                        var current:Date;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            current = LocalDateTime.now()
                            val formatter = DateTimeFormatter.ofPattern("ddMMyyyy HH:mm")
                            currentdate =  current.format(formatter)
                            Log.e("answer","if........date...."+currentdate)
                        } else {
                            current = Date();
                            val formatter = SimpleDateFormat("ddMMyyyy HH:mm")
                            currentdate = formatter.format(current)
                            Log.e("answer","else.........date...."+currentdate)
                        }*/

                        var current = Date();
                        var inputFormat: DateFormat = SimpleDateFormat("ddMMyyyy HH:mm")
                        var currentdate = inputFormat.format(current)
                        var date = inputFormat.parse(currentdate)
                        Log.e("daily challenge","...current date......."+date);
                        for (challenge in dailyChallengeList!!) {
                            var inputFormat: DateFormat = SimpleDateFormat("ddMMyyyy HH:mm")
                           // var outputFormat: DateFormat = SimpleDateFormat("MMM dd, yyyy")
                            var inputDateStr = challenge.challengedate.toString()
                            var date1 = inputFormat.parse(inputDateStr)
                            //var outputDateStr = outputFormat.format(date)
                            var compare:Int = date1.compareTo(date);
                            if(compare < 0){
                                afterSortingdailyChallengeList?.add(challenge)
                            }else if(compare == 0){
                                afterSortingdailyChallengeList?.add(challenge)
                            }

                        }

                        Log.e("daily challenge","afterSortingdailyChallengeList.....size...."+afterSortingdailyChallengeList?.size);
                        adapter = DailyChallengeAdapter(activity!!, afterSortingdailyChallengeList!!, this@DailyChallengeFragment)
                        //rcv_revision.addItemDecoration(itemDecorator)
                        //rcv_chapter.addItemDecoration(DividerItemDecoration(context,))
                        if(!isFirstTime){
                            isFirstTime = true
                            view!!.rcv_daily_challenges.addItemDecoration(VerticalSpaceItemDecoration(48));
                        }

                        view!!.rcv_daily_challenges.adapter = adapter
                        // Hide swipe to refresh icon animation
                        view!!.swipe_container.isRefreshing = false

                    }catch (e:Exception){
                        Log.e("revision fragment",".....exception..."+e)
                        // Hide swipe to refresh icon animation
                       // swipe_container.isRefreshing = false
                    }


                }
            }
        })



        return dailyChallengeList


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
}