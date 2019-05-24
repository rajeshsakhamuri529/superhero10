package com.blobcity.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import com.blobcity.R
import com.blobcity.fragment.AstraCardFragment
import com.blobcity.fragment.ChapterFragment
import com.blobcity.fragment.SettingFragment
import com.blobcity.utils.ConstantPath.ANONYMOUS_USER
import com.google.firebase.auth.FirebaseAuth
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashBoardActivity : BaseActivity(), PermissionListener,
    View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener/*, ConnectivityReceiver.ConnectivityReceiverListener*/{

    var TAG: String?= "Dashboard"
    private lateinit var auth: FirebaseAuth
    /*val sharedPref: SharedPreferences = this.getSharedPreferences(ANONYMOUS_USER, Context.MODE_PRIVATE)
    val editor = sharedPref.edit()*/
    private var mSnackBar: Snackbar? = null
    var fragment : Fragment ?= null

    override fun setLayout(): Int {
        return com.blobcity.R.layout.activity_dashboard
    }

    override fun initView() {
        /*databaseRefrence = FirebaseDatabase.getInstance()
            .getReference("topic_status/"+UniqueUUid.id(this))
        databaseRefrence!!.keepSynced(true)*/
        val sharedPreferences = getSharedPreferences(ANONYMOUS_USER, Context.MODE_PRIVATE)
        auth = FirebaseAuth.getInstance()

        signin(sharedPreferences)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.nav_chapter ->{
                fragment = ChapterFragment()
            }

            com.blobcity.R.id.nav_astra_cards->{
                fragment = AstraCardFragment()
            }

            com.blobcity.R.id.nav_settings->{
                fragment = SettingFragment()
            }
        }

        return loadFragment(fragment!!)
    }

    private fun signin(sharedPreferences: SharedPreferences) {
        val editor = sharedPreferences.edit()
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {


            Log.d(TAG, "signInAnonymously:Already Logged In")
            val uid : String = sharedPreferences.getString("uid","noVALUE");
            Log.d(TAG,uid)
            Toast.makeText(baseContext, "UID "+uid, Toast.LENGTH_SHORT).show()

            val user = auth.currentUser
            TedPermission.with(this)
                .setPermissionListener(this)
                .setDeniedMessage("If you reject permission,you can not use this service\n"
                        + "\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check()

        } else {
            val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            val isConnected: Boolean = activeNetwork?.isConnected == true
            Log.d("isConnected",isConnected.toString()+"!")
            if(isConnected) {
                auth.signInAnonymously()
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(baseContext, "Logged In", Toast.LENGTH_SHORT).show()
                            Log.d(TAG, "signInAnonymously:success")
                            val user = auth.currentUser
                            editor.putBoolean("isLoggedIn", true)
                            editor.putString("uid", user!!.uid)
                            editor.apply()

                            TedPermission.with(this)
                                .setPermissionListener(this)
                                .setDeniedMessage(
                                    "If you reject permission,you can not use this service\n"
                                            + "\nPlease turn on permissions at [Setting] > [Permission]"
                                )
                                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .check()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.exception)
                            //.makeText(baseContext, "Authentication failed. Check Internet Connection", Toast.LENGTH_SHORT).show()
                            mSnackBar = Snackbar.make(
                                findViewById(com.blobcity.R.id.rl_dashboard),
                                "Auth Failed :(",
                                Snackbar.LENGTH_LONG
                            ) //Assume "rootLayout" as the root layout of every activity.
                            mSnackBar?.duration = Snackbar.LENGTH_INDEFINITE
                            mSnackBar?.setAction("Retry", { signin(sharedPreferences) })
                            mSnackBar?.show()
                        }
                    }
            }else{
                mSnackBar = Snackbar.make(
                    findViewById(com.blobcity.R.id.rl_dashboard),
                    "No Internet Connection",
                    Snackbar.LENGTH_LONG
                ) //Assume "rootLayout" as the root layout of every activity.
                mSnackBar?.duration = Snackbar.LENGTH_INDEFINITE
                mSnackBar?.setAction("Retry", { signin(sharedPreferences) })
                mSnackBar?.show()
            }
        }
    }

    override fun onPermissionGranted() {
        loadFragment(ChapterFragment())
        navigation.setOnNavigationItemSelectedListener(this);

        /*databaseRefrence!!.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                topicStatusModelList = ArrayList()
                for (postSnapshot in dataSnapshot.children) {
                    Log.e("snap", postSnapshot.value.toString())
                    val topicStatusModel: TopicStatusModel = postSnapshot!!.getValue(TopicStatusModel::class.java)!!
                    topicStatusModelList!!.add(topicStatusModel)
                    if (topicStatusModelList != null){
                        if (topicStatusModelList!!.size > 0){
                            for (branchItem in branchesItemList!!) {
                                val branchId = branchItem.id
                                branchItem.basic = 0
                                branchItem.intermediate = 0
                                branchItem.advance = 0
                                for (topicStatusModels in topicStatusModelList!!) {
                                    val id = topicStatusModels.topicId
                                    val level = topicStatusModels.topicLevel

                                    if (id!!.contains(branchId)) {
                                        if (level!!.contains("basic")) {
                                            branchItem.basic = 1
                                        }
                                        if (level.contains("intermediate")) {
                                            branchItem.intermediate = 1
                                        }
                                        if (level.contains("advance")) {
                                            branchItem.advance = 1
                                        }
                                    }

                                }
                            }
                        }
                    }
                    adapter!!.notifyDataSetChanged()
                    Log.e("topicStatusDb", topicStatusModel.topicLevel)
                }
            }
        })*/

    }

    override fun onPermissionDenied(deniedPermissions: List<String>) {

    }

     private fun loadFragment(fragment: Fragment): Boolean {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit()

            return true
        }
        return false
    }




    override fun onClick(v: View?) {
        /*when(v?.id){
            R.id.rl_chapter_one ->{
                callIntent(branchesItemList!!.get(0).topic.folderName, branchesItemList!!.get(0).id)
            }

            R.id.rl_chapter_two ->{
                callIntent(branchesItemList!!.get(1).topic.folderName, branchesItemList!!.get(1).id)
            }
        }*/
    }



    override fun onStart() {
        super.onStart()
        Log.d("onStart","Dashboard")

    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        postponeEnterTransition()
        if (resultCode == Activity.RESULT_OK) {
            (fragment as AstraCardFragment).activityReenter(data!!)
        }
        /*val myFragment = fragmentManager.findFragmentByTag("AstraCardFragment") as AstraCardFragment
        if (myFragment != null && myFragment!!.isVisible()) {
            // add your code here

        }*/
    }
}