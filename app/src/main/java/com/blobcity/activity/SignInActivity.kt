package com.blobcity.activity

import android.app.ProgressDialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Paint
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
import com.blobcity.ViewPager.adapter.MyPagerAdapter
import com.blobcity.ViewPager.enum.TypeFaceEnum
import com.blobcity.ViewPager.fragments.IntroFirstFragment
import com.blobcity.ViewPager.fragments.IntroFourthFragment
import com.blobcity.ViewPager.fragments.IntroSecondFragment
import com.blobcity.ViewPager.fragments.IntroThirdFragment
import com.blobcity.ViewPager.listener.ViewPagerListener
import com.blobcity.ViewPager.util.UiHelper
import com.blobcity.model.Topic
import com.blobcity.utils.ConstantPath
import com.blobcity.utils.ConstantPath.ISACCOUNTLINKED
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
    var firstTime:String? = null
    companion object {
        private const val MIN_SCALE = 0.65f
        private const val MIN_ALPHA = 0.3f
    }

    private lateinit var pagerAdapterView: MyPagerAdapter
    private val uiHelper = UiHelper()

    override var layoutID: Int = R.layout.activity_sign_in

    override fun initView() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        topicStatusVM = ViewModelProviders.of(this).get(TopicStatusVM::class.java)
        //val topic: Topic = intent.getSerializableExtra(ConstantPath.TOPIC) as Topic
        //topicName = topic.title
        sharedPrefs = SharedPrefs()
        firstTime = intent.getStringExtra(ConstantPath.FIRST_TIME)
        if(sharedPrefs!!.getBooleanPrefVal(this, ConstantPath.IS_FIRST_TIME)) {
            sharedPrefs!!.setBooleanPrefVal(this, ConstantPath.IS_FIRST_TIME, false)
            signin_layout.visibility = View.GONE
            getStartedButton.visibility = View.VISIBLE
        } else {
            signin_layout.visibility = View.VISIBLE
            getStartedButton.visibility = View.GONE
        }
       // tv_topic_name.text = topicName
//textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
 //       textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

      //  amount_text.paintFlags = amount_text.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        //val textView = findViewById(R.id.text) as TextView
        //amount_text.setPaintFlags(amount_text.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
        //slash.setPaintFlags(slash.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
        //months_text.setPaintFlags(months_text.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)

        pagerAdapterView = MyPagerAdapter(supportFragmentManager)
        addPagerFragments()
        myViewPager.adapter = pagerAdapterView
       // myViewPager.setPageTransformer(true, this::zoomOutTransformation)
        //getStartedButton.typeface = uiHelper.getTypeFace(TypeFaceEnum.BUTTON_TEXT, this)
        myViewPager.addOnPageChangeListener(ViewPagerListener(this::onPageSelected))






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
        getStartedButton.setOnClickListener {
            navigateToDashboard("GRADE 6")
        }
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
    fun navigateToDashboard(title: String){
        val intent = Intent(
            this@SignInActivity,
            DashBoardActivity::class.java
        )
       // intent.putExtra(ConstantPath.TITLE_TOPIC, title)
        startActivity(intent)
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
        Log.e("sign in sctivity","isaccountlinked......"+sharedPrefs?.getBooleanPrefVal(this, ConstantPath.ISACCOUNTLINKED)!!);
        if (sharedPrefs?.getBooleanPrefVal(this, ConstantPath.ISACCOUNTLINKED)!!) {
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
        }else{
            auth.currentUser?.linkWithCredential(credential)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.e("sign in activity", "linkWithCredential:success")
                        val user = task.result?.user
                        sharedPrefs?.setBooleanPrefVal(this!!, ISACCOUNTLINKED, true)
                        updateUI(user)
                    } else {
                        Log.e("sign in activity", "linkWithCredential:failure", task.exception)
                        val prevUser = FirebaseAuth.getInstance().currentUser
                        Log.e("sign in activity","...redentical.....provder id."+prevUser?.uid);
                        prevUser?.delete()
                        try {
                            sharedPrefs?.setBooleanPrefVal(this!!, ISACCOUNTLINKED, true)
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
        }





    }

    private fun updateUI(user: FirebaseUser?) {
        try {

            if (user != null) { // 8099256159
                Log.e("sign in activity","...update ui.....");
                sharedPrefs?.setBooleanPrefVal(this!!, ISNOTLOGIN, true)
                hideProgressDialog()
                Toast.makeText(this,"Sign-In success!",Toast.LENGTH_SHORT).show()
                val intent = Intent(this!!, DashBoardActivity::class.java)
                startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun onPageSelected(position: Int) {
        when (position) {
            0 -> {
                firstDotImageView.setImageResource(R.drawable.selected_dot)
                secondDotImageView.setImageResource(R.drawable.unselected_dot)
                thirdDotImageView.setImageResource(R.drawable.unselected_dot)
                fourthDotImageView.setImageResource(R.drawable.unselected_dot)
            }
            1 -> {
                firstDotImageView.setImageResource(R.drawable.unselected_dot)
                secondDotImageView.setImageResource(R.drawable.selected_dot)
                thirdDotImageView.setImageResource(R.drawable.unselected_dot)
                fourthDotImageView.setImageResource(R.drawable.unselected_dot)
            }
            2 -> {
                firstDotImageView.setImageResource(R.drawable.unselected_dot)
                secondDotImageView.setImageResource(R.drawable.unselected_dot)
                thirdDotImageView.setImageResource(R.drawable.selected_dot)
                fourthDotImageView.setImageResource(R.drawable.unselected_dot)
            }
            3 -> {
                firstDotImageView.setImageResource(R.drawable.unselected_dot)
                secondDotImageView.setImageResource(R.drawable.unselected_dot)
                thirdDotImageView.setImageResource(R.drawable.unselected_dot)
                fourthDotImageView.setImageResource(R.drawable.selected_dot)
            }
        }
    }

    private fun addPagerFragments() {
        pagerAdapterView.addFragments(IntroFirstFragment())
        pagerAdapterView.addFragments(IntroSecondFragment())
        pagerAdapterView.addFragments(IntroThirdFragment())
        pagerAdapterView.addFragments(IntroFourthFragment())
    }

    private fun zoomOutTransformation(page: View, position: Float) {
        when {
            position < -1 ->
                page.alpha = 0f
            position <= 1 -> {
                page.scaleX = Math.max(MIN_SCALE, 1 - Math.abs(position))
                page.scaleY = Math.max(MIN_SCALE, 1 - Math.abs(position))
                page.alpha = Math.max(MIN_ALPHA, 1 - Math.abs(position))
            }
            else -> page.alpha = 0f
        }
    }





}
