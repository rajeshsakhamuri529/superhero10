package com.blobcity.activity

import android.app.ProgressDialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.Toast
import com.blobcity.R
import com.blobcity.model.Topic
import com.blobcity.utils.ConstantPath
import com.blobcity.utils.ConstantPath.ISNOTLOGIN
import com.blobcity.utils.SharedPrefs
import com.blobcity.utils.Utils
import com.blobcity.viewmodel.TopicStatusVM
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : BaseActivity(){

    var topicName: String?=""
    var topicStatusVM: TopicStatusVM?= null
    private lateinit var auth: FirebaseAuth

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mPDialog: ProgressDialog? = null
    //Google Login
    private val RC_SIGN_IN = 9001
    var sharedPrefs: SharedPrefs? = null
    var sound: Boolean = false
    lateinit var signIn: RelativeLayout;
    override var layoutID: Int = R.layout.activity_sign_in

    override fun initView() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        topicStatusVM = ViewModelProviders.of(this).get(TopicStatusVM::class.java)
        //val topic: Topic = intent.getSerializableExtra(ConstantPath.TOPIC) as Topic
        //topicName = topic.title

       // tv_topic_name.text = topicName
        sharedPrefs = SharedPrefs()

        iv_cancel_quiz_summary.setOnClickListener {
            sound = sharedPrefs?.getBooleanPrefVal(this!!, ConstantPath.SOUNDS) ?: true

            if(!sound) {
                //MusicManager.getInstance().play(context, R.raw.amount_low);
                // Is the sound loaded already?
                if (Utils.loaded) {
                    Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                    Log.e("Test", "Played sound...volume..." + Utils.volume);
                    //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
                }
                val intent = Intent(this!!, DashBoardActivity::class.java)

                startActivity(intent)
            }else{
                val intent = Intent(this!!, DashBoardActivity::class.java)

                startActivity(intent)
            }

        }

        /*google_image.setOnClickListener {
            finish()
        }
        */
        signin_txt.setOnClickListener {
            if(Utils.isOnline(this)){
                signIn()
            }else{
                Toast.makeText(this,"Please connect internet!",Toast.LENGTH_SHORT).show()
            }
        }
        signWithGoogleRLID.setOnClickListener {

           if(Utils.isOnline(this)){
               signIn()
            }else{
                Toast.makeText(this,"Please connect internet!",Toast.LENGTH_SHORT).show()
            }

       }
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)





    }

    private fun signIn() {
        Log.e("signin activity","sign in")
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                Log.e("sign in activity", "Google sign")
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.e("sign in activity", "Google sign in failed"+e)
            }
        }
    }
    fun showProgressDialog(loadText: String) {
        hideProgressDialog()
        try {
            mPDialog = ProgressDialog.show(
                ContextThemeWrapper(this@SignInActivity, R.style.DialogCustom),
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
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.e("sign in activity", "firebaseAuthWithGoogle:" + acct.id!!)
        showProgressDialog("Verifying your account...")
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)

        auth.currentUser?.linkWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.e("sign in activity", "linkWithCredential:success")
                    val user = task.result?.user
                    updateUI(user)
                } else {
                    Log.e("sign in activity", "linkWithCredential:failure", task.exception)
                    val prevUser = FirebaseAuth.getInstance().currentUser
                    Log.e("sign in activity","...redentical.....provder id."+prevUser?.uid);
                    prevUser?.delete()
                    try {
                        auth.signInWithCredential(credential)
                            .addOnSuccessListener { result ->
                                val currentUser = result.user
                                updateUI(currentUser)
                            }
                            .addOnFailureListener {
                            }
                    } catch (e:java.lang.Exception){
                        Log.e("sign in activity","e.........."+e)
                    }

                }
            }

        hideProgressDialog()

    }

    private fun updateUI(user: FirebaseUser?) {
        try {

            if (user != null) { // 8099256159
                Log.e("sign in activity","...update ui.....");
                sharedPrefs?.setBooleanPrefVal(this!!, ISNOTLOGIN, true)
                Toast.makeText(this,"Sign-In success!",Toast.LENGTH_SHORT).show()
                val intent = Intent(this!!, DashBoardActivity::class.java)
                startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}
