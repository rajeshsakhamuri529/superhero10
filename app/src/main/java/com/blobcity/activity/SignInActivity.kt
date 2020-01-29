package com.blobcity.activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
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

    //Google Login
    private val RC_SIGN_IN = 9001
    var sharedPrefs: SharedPrefs? = null
    //sharedPrefs?.getBooleanPrefVal(context!!, ConstantPath.SOUNDS) ?: true
    lateinit var signIn: RelativeLayout;
    override var layoutID: Int = R.layout.activity_sign_in

    override fun initView() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        topicStatusVM = ViewModelProviders.of(this).get(TopicStatusVM::class.java)
        val topic: Topic = intent.getSerializableExtra(ConstantPath.TOPIC) as Topic
        topicName = topic.title

       // tv_topic_name.text = topicName
        sharedPrefs = SharedPrefs()

        iv_cancel_quiz_summary.setOnClickListener {
            finish()
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

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.e("sign in activity", "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.e("signin activity", "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.e("sign in activity", "signInWithCredential:failure", task.exception)
                    //Snackbar.make(main_layout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                    updateUI(null)
                }

            }
    }

    private fun updateUI(user: FirebaseUser?) {
        try {

            if (user != null) { // 8099256159
                Log.e("sign in activity","...update ui.....");
                sharedPrefs?.setBooleanPrefVal(this!!, ISNOTLOGIN, false)
                Toast.makeText(this,"Sign-In success!",Toast.LENGTH_SHORT).show()
                val intent = Intent(this!!, DashBoardActivity::class.java)
                startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}
