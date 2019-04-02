package com.blobcity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.blobcity.utils.Constant.Companion.assetAdvanced1429Path
import com.blobcity.utils.Constant.Companion.assetAdvanced1430Path
import com.blobcity.utils.Constant.Companion.assetAdvanced1431Path
import com.blobcity.utils.Constant.Companion.assetAdvanced1432Path
import com.blobcity.utils.Constant.Companion.assetAdvanced398Path
import com.blobcity.utils.Constant.Companion.assetAdvanced399Path
import com.blobcity.utils.Constant.Companion.assetAdvanced403Path
import com.blobcity.utils.Constant.Companion.assetAdvanced404Path
import com.blobcity.utils.Constant.Companion.assetAdvanced410Path
import com.blobcity.utils.Constant.Companion.assetAdvanced411Path
import com.blobcity.utils.Constant.Companion.assetAdvanced415Path
import com.blobcity.utils.Constant.Companion.assetAdvanced416Path
import com.blobcity.utils.Constant.Companion.assetAdvanced422Path
import com.blobcity.utils.Constant.Companion.assetAdvanced423Path
import com.blobcity.utils.Constant.Companion.assetAdvanced427Path
import com.blobcity.utils.Constant.Companion.assetAdvanced428Path
import com.blobcity.utils.Constant.Companion.assetAdvanced434Path
import com.blobcity.utils.Constant.Companion.assetAdvanced435Path
import com.blobcity.utils.Constant.Companion.assetAdvanced439Path
import com.blobcity.utils.Constant.Companion.assetAdvanced440Path
import com.blobcity.utils.Constant.Companion.assetAdvanced446Path
import com.blobcity.utils.Constant.Companion.assetAdvanced447Path
import com.blobcity.utils.Constant.Companion.assetAdvanced451Path
import com.blobcity.utils.Constant.Companion.assetAdvanced452Path
import com.blobcity.utils.Constant.Companion.assetAdvanced458Path
import com.blobcity.utils.Constant.Companion.assetAdvanced459Path
import com.blobcity.utils.Constant.Companion.assetAdvanced463Path
import com.blobcity.utils.Constant.Companion.assetAdvanced464Path
import com.blobcity.utils.Constant.Companion.assetAdvanced470Path
import com.blobcity.utils.Constant.Companion.assetAdvanced471Path
import com.blobcity.utils.Constant.Companion.assetAdvanced475Path
import com.blobcity.utils.Constant.Companion.assetAdvanced476Path
import com.blobcity.utils.Constant.Companion.assetAdvanced482Path
import com.blobcity.utils.Constant.Companion.assetAdvanced483Path
import com.blobcity.utils.Constant.Companion.assetAdvanced487Path
import com.blobcity.utils.Constant.Companion.assetAdvanced488Path
import com.blobcity.utils.Constant.Companion.assetAdvanced494Path
import com.blobcity.utils.Constant.Companion.assetAdvanced495Path
import com.blobcity.utils.Constant.Companion.assetAdvanced499Path
import com.blobcity.utils.Constant.Companion.assetAdvanced500Path
import com.blobcity.utils.Constant.Companion.assetAdvanced506Path
import com.blobcity.utils.Constant.Companion.assetAdvanced507Path
import com.blobcity.utils.Constant.Companion.assetAdvanced511Path
import com.blobcity.utils.Constant.Companion.assetAdvanced512Path
import com.blobcity.utils.Constant.Companion.assetBasic1421Path
import com.blobcity.utils.Constant.Companion.assetBasic1422Path
import com.blobcity.utils.Constant.Companion.assetBasic1423Path
import com.blobcity.utils.Constant.Companion.assetBasic1424Path
import com.blobcity.utils.Constant.Companion.assetBasic394Path
import com.blobcity.utils.Constant.Companion.assetBasic395Path
import com.blobcity.utils.Constant.Companion.assetBasic396Path
import com.blobcity.utils.Constant.Companion.assetBasic397Path
import com.blobcity.utils.Constant.Companion.assetBasic406Path
import com.blobcity.utils.Constant.Companion.assetBasic407Path
import com.blobcity.utils.Constant.Companion.assetBasic408Path
import com.blobcity.utils.Constant.Companion.assetBasic409Path
import com.blobcity.utils.Constant.Companion.assetBasic418Path
import com.blobcity.utils.Constant.Companion.assetBasic419Path
import com.blobcity.utils.Constant.Companion.assetBasic420Path
import com.blobcity.utils.Constant.Companion.assetBasic421Path
import com.blobcity.utils.Constant.Companion.assetBasic430Path
import com.blobcity.utils.Constant.Companion.assetBasic431Path
import com.blobcity.utils.Constant.Companion.assetBasic432Path
import com.blobcity.utils.Constant.Companion.assetBasic433Path
import com.blobcity.utils.Constant.Companion.assetBasic442Path
import com.blobcity.utils.Constant.Companion.assetBasic443Path
import com.blobcity.utils.Constant.Companion.assetBasic444Path
import com.blobcity.utils.Constant.Companion.assetBasic445Path
import com.blobcity.utils.Constant.Companion.assetBasic454Path
import com.blobcity.utils.Constant.Companion.assetBasic455Path
import com.blobcity.utils.Constant.Companion.assetBasic456Path
import com.blobcity.utils.Constant.Companion.assetBasic457Path
import com.blobcity.utils.Constant.Companion.assetBasic466Path
import com.blobcity.utils.Constant.Companion.assetBasic467Path
import com.blobcity.utils.Constant.Companion.assetBasic468Path
import com.blobcity.utils.Constant.Companion.assetBasic469Path
import com.blobcity.utils.Constant.Companion.assetBasic478Path
import com.blobcity.utils.Constant.Companion.assetBasic479Path
import com.blobcity.utils.Constant.Companion.assetBasic480Path
import com.blobcity.utils.Constant.Companion.assetBasic481Path
import com.blobcity.utils.Constant.Companion.assetBasic490Path
import com.blobcity.utils.Constant.Companion.assetBasic491Path
import com.blobcity.utils.Constant.Companion.assetBasic492Path
import com.blobcity.utils.Constant.Companion.assetBasic493Path
import com.blobcity.utils.Constant.Companion.assetBasic502Path
import com.blobcity.utils.Constant.Companion.assetBasic503Path
import com.blobcity.utils.Constant.Companion.assetBasic504Path
import com.blobcity.utils.Constant.Companion.assetBasic505Path
import com.blobcity.utils.Constant.Companion.assetBasic550Path
import com.blobcity.utils.Constant.Companion.assetIntermediate1425Path
import com.blobcity.utils.Constant.Companion.assetIntermediate1426Path
import com.blobcity.utils.Constant.Companion.assetIntermediate1427Path
import com.blobcity.utils.Constant.Companion.assetIntermediate1428Path
import com.blobcity.utils.Constant.Companion.assetIntermediate400Path
import com.blobcity.utils.Constant.Companion.assetIntermediate401Path
import com.blobcity.utils.Constant.Companion.assetIntermediate402Path
import com.blobcity.utils.Constant.Companion.assetIntermediate405Path
import com.blobcity.utils.Constant.Companion.assetIntermediate412Path
import com.blobcity.utils.Constant.Companion.assetIntermediate413Path
import com.blobcity.utils.Constant.Companion.assetIntermediate414Path
import com.blobcity.utils.Constant.Companion.assetIntermediate417Path
import com.blobcity.utils.Constant.Companion.assetIntermediate424Path
import com.blobcity.utils.Constant.Companion.assetIntermediate425Path
import com.blobcity.utils.Constant.Companion.assetIntermediate426Path
import com.blobcity.utils.Constant.Companion.assetIntermediate429Path
import com.blobcity.utils.Constant.Companion.assetIntermediate436Path
import com.blobcity.utils.Constant.Companion.assetIntermediate437Path
import com.blobcity.utils.Constant.Companion.assetIntermediate438Path
import com.blobcity.utils.Constant.Companion.assetIntermediate441Path
import com.blobcity.utils.Constant.Companion.assetIntermediate448Path
import com.blobcity.utils.Constant.Companion.assetIntermediate449Path
import com.blobcity.utils.Constant.Companion.assetIntermediate450Path
import com.blobcity.utils.Constant.Companion.assetIntermediate453Path
import com.blobcity.utils.Constant.Companion.assetIntermediate460Path
import com.blobcity.utils.Constant.Companion.assetIntermediate461Path
import com.blobcity.utils.Constant.Companion.assetIntermediate462Path
import com.blobcity.utils.Constant.Companion.assetIntermediate465Path
import com.blobcity.utils.Constant.Companion.assetIntermediate472Path
import com.blobcity.utils.Constant.Companion.assetIntermediate473Path
import com.blobcity.utils.Constant.Companion.assetIntermediate474Path
import com.blobcity.utils.Constant.Companion.assetIntermediate477Path
import com.blobcity.utils.Constant.Companion.assetIntermediate484Path
import com.blobcity.utils.Constant.Companion.assetIntermediate485Path
import com.blobcity.utils.Constant.Companion.assetIntermediate486Path
import com.blobcity.utils.Constant.Companion.assetIntermediate489Path
import com.blobcity.utils.Constant.Companion.assetIntermediate496Path
import com.blobcity.utils.Constant.Companion.assetIntermediate497Path
import com.blobcity.utils.Constant.Companion.assetIntermediate498Path
import com.blobcity.utils.Constant.Companion.assetIntermediate501Path
import com.blobcity.utils.Constant.Companion.assetIntermediate508Path
import com.blobcity.utils.Constant.Companion.assetIntermediate509Path
import com.blobcity.utils.Constant.Companion.assetIntermediate510Path
import com.blobcity.utils.Constant.Companion.assetIntermediate513Path
import com.blobcity.utils.Constant.Companion.assetIntermediate549Path
import com.blobcity.utils.Constant.Companion.assetOutputPath
import com.blobcity.utils.Constant.Companion.assetTestCoursePath
import com.blobcity.utils.Constant.Companion.assetTopicEightPath
import com.blobcity.utils.Constant.Companion.assetTopicFivePath
import com.blobcity.utils.Constant.Companion.assetTopicFourPath
import com.blobcity.utils.Constant.Companion.assetTopicNinePath
import com.blobcity.utils.Constant.Companion.assetTopicOnePath
import com.blobcity.utils.Constant.Companion.assetTopicSixPath
import com.blobcity.utils.Constant.Companion.assetTopicTenPath
import com.blobcity.utils.Constant.Companion.assetTopicThreePath
import com.blobcity.utils.Constant.Companion.assetTopicTwoPath
import com.blobcity.utils.Constant.Companion.localAdvanced1429Path
import com.blobcity.utils.Constant.Companion.localAdvanced1430Path
import com.blobcity.utils.Constant.Companion.localAdvanced1431Path
import com.blobcity.utils.Constant.Companion.localAdvanced1432Path
import com.blobcity.utils.Constant.Companion.localAdvanced398Path
import com.blobcity.utils.Constant.Companion.localAdvanced399Path
import com.blobcity.utils.Constant.Companion.localAdvanced403Path
import com.blobcity.utils.Constant.Companion.localAdvanced404Path
import com.blobcity.utils.Constant.Companion.localAdvanced410Path
import com.blobcity.utils.Constant.Companion.localAdvanced411Path
import com.blobcity.utils.Constant.Companion.localAdvanced415Path
import com.blobcity.utils.Constant.Companion.localAdvanced416Path
import com.blobcity.utils.Constant.Companion.localAdvanced422Path
import com.blobcity.utils.Constant.Companion.localAdvanced423Path
import com.blobcity.utils.Constant.Companion.localAdvanced427Path
import com.blobcity.utils.Constant.Companion.localAdvanced428Path
import com.blobcity.utils.Constant.Companion.localAdvanced434Path
import com.blobcity.utils.Constant.Companion.localAdvanced435Path
import com.blobcity.utils.Constant.Companion.localAdvanced439Path
import com.blobcity.utils.Constant.Companion.localAdvanced440Path
import com.blobcity.utils.Constant.Companion.localAdvanced446Path
import com.blobcity.utils.Constant.Companion.localAdvanced447Path
import com.blobcity.utils.Constant.Companion.localAdvanced451Path
import com.blobcity.utils.Constant.Companion.localAdvanced452Path
import com.blobcity.utils.Constant.Companion.localAdvanced458Path
import com.blobcity.utils.Constant.Companion.localAdvanced459Path
import com.blobcity.utils.Constant.Companion.localAdvanced463Path
import com.blobcity.utils.Constant.Companion.localAdvanced464Path
import com.blobcity.utils.Constant.Companion.localAdvanced470Path
import com.blobcity.utils.Constant.Companion.localAdvanced471Path
import com.blobcity.utils.Constant.Companion.localAdvanced475Path
import com.blobcity.utils.Constant.Companion.localAdvanced476Path
import com.blobcity.utils.Constant.Companion.localAdvanced482Path
import com.blobcity.utils.Constant.Companion.localAdvanced483Path
import com.blobcity.utils.Constant.Companion.localAdvanced487Path
import com.blobcity.utils.Constant.Companion.localAdvanced488Path
import com.blobcity.utils.Constant.Companion.localAdvanced494Path
import com.blobcity.utils.Constant.Companion.localAdvanced495Path
import com.blobcity.utils.Constant.Companion.localAdvanced499Path
import com.blobcity.utils.Constant.Companion.localAdvanced500Path
import com.blobcity.utils.Constant.Companion.localAdvanced506Path
import com.blobcity.utils.Constant.Companion.localAdvanced507Path
import com.blobcity.utils.Constant.Companion.localAdvanced511Path
import com.blobcity.utils.Constant.Companion.localAdvanced512Path
import com.blobcity.utils.Constant.Companion.localBasic1421Path
import com.blobcity.utils.Constant.Companion.localBasic1422Path
import com.blobcity.utils.Constant.Companion.localBasic1423Path
import com.blobcity.utils.Constant.Companion.localBasic1424Path
import com.blobcity.utils.Constant.Companion.localBasic394Path
import com.blobcity.utils.Constant.Companion.localBasic395Path
import com.blobcity.utils.Constant.Companion.localBasic396Path
import com.blobcity.utils.Constant.Companion.localBasic397Path
import com.blobcity.utils.Constant.Companion.localBasic406Path
import com.blobcity.utils.Constant.Companion.localBasic407Path
import com.blobcity.utils.Constant.Companion.localBasic408Path
import com.blobcity.utils.Constant.Companion.localBasic409Path
import com.blobcity.utils.Constant.Companion.localBasic418Path
import com.blobcity.utils.Constant.Companion.localBasic419Path
import com.blobcity.utils.Constant.Companion.localBasic420Path
import com.blobcity.utils.Constant.Companion.localBasic421Path
import com.blobcity.utils.Constant.Companion.localBasic430Path
import com.blobcity.utils.Constant.Companion.localBasic431Path
import com.blobcity.utils.Constant.Companion.localBasic432Path
import com.blobcity.utils.Constant.Companion.localBasic433Path
import com.blobcity.utils.Constant.Companion.localBasic442Path
import com.blobcity.utils.Constant.Companion.localBasic443Path
import com.blobcity.utils.Constant.Companion.localBasic444Path
import com.blobcity.utils.Constant.Companion.localBasic445Path
import com.blobcity.utils.Constant.Companion.localBasic454Path
import com.blobcity.utils.Constant.Companion.localBasic455Path
import com.blobcity.utils.Constant.Companion.localBasic456Path
import com.blobcity.utils.Constant.Companion.localBasic457Path
import com.blobcity.utils.Constant.Companion.localBasic466Path
import com.blobcity.utils.Constant.Companion.localBasic467Path
import com.blobcity.utils.Constant.Companion.localBasic468Path
import com.blobcity.utils.Constant.Companion.localBasic469Path
import com.blobcity.utils.Constant.Companion.localBasic478Path
import com.blobcity.utils.Constant.Companion.localBasic479Path
import com.blobcity.utils.Constant.Companion.localBasic480Path
import com.blobcity.utils.Constant.Companion.localBasic481Path
import com.blobcity.utils.Constant.Companion.localBasic490Path
import com.blobcity.utils.Constant.Companion.localBasic491Path
import com.blobcity.utils.Constant.Companion.localBasic492Path
import com.blobcity.utils.Constant.Companion.localBasic493Path
import com.blobcity.utils.Constant.Companion.localBasic502Path
import com.blobcity.utils.Constant.Companion.localBasic503Path
import com.blobcity.utils.Constant.Companion.localBasic504Path
import com.blobcity.utils.Constant.Companion.localBasic505Path
import com.blobcity.utils.Constant.Companion.localBasic550Path
import com.blobcity.utils.Constant.Companion.localIntermediate1425Path
import com.blobcity.utils.Constant.Companion.localIntermediate1426Path
import com.blobcity.utils.Constant.Companion.localIntermediate1427Path
import com.blobcity.utils.Constant.Companion.localIntermediate1428Path
import com.blobcity.utils.Constant.Companion.localIntermediate400Path
import com.blobcity.utils.Constant.Companion.localIntermediate401Path
import com.blobcity.utils.Constant.Companion.localIntermediate402Path
import com.blobcity.utils.Constant.Companion.localIntermediate405Path
import com.blobcity.utils.Constant.Companion.localIntermediate412Path
import com.blobcity.utils.Constant.Companion.localIntermediate413Path
import com.blobcity.utils.Constant.Companion.localIntermediate414Path
import com.blobcity.utils.Constant.Companion.localIntermediate417Path
import com.blobcity.utils.Constant.Companion.localIntermediate424Path
import com.blobcity.utils.Constant.Companion.localIntermediate425Path
import com.blobcity.utils.Constant.Companion.localIntermediate426Path
import com.blobcity.utils.Constant.Companion.localIntermediate429Path
import com.blobcity.utils.Constant.Companion.localIntermediate436Path
import com.blobcity.utils.Constant.Companion.localIntermediate437Path
import com.blobcity.utils.Constant.Companion.localIntermediate438Path
import com.blobcity.utils.Constant.Companion.localIntermediate441Path
import com.blobcity.utils.Constant.Companion.localIntermediate448Path
import com.blobcity.utils.Constant.Companion.localIntermediate449Path
import com.blobcity.utils.Constant.Companion.localIntermediate450Path
import com.blobcity.utils.Constant.Companion.localIntermediate453Path
import com.blobcity.utils.Constant.Companion.localIntermediate460Path
import com.blobcity.utils.Constant.Companion.localIntermediate461Path
import com.blobcity.utils.Constant.Companion.localIntermediate462Path
import com.blobcity.utils.Constant.Companion.localIntermediate465Path
import com.blobcity.utils.Constant.Companion.localIntermediate472Path
import com.blobcity.utils.Constant.Companion.localIntermediate473Path
import com.blobcity.utils.Constant.Companion.localIntermediate474Path
import com.blobcity.utils.Constant.Companion.localIntermediate477Path
import com.blobcity.utils.Constant.Companion.localIntermediate484Path
import com.blobcity.utils.Constant.Companion.localIntermediate485Path
import com.blobcity.utils.Constant.Companion.localIntermediate486Path
import com.blobcity.utils.Constant.Companion.localIntermediate489Path
import com.blobcity.utils.Constant.Companion.localIntermediate496Path
import com.blobcity.utils.Constant.Companion.localIntermediate497Path
import com.blobcity.utils.Constant.Companion.localIntermediate498Path
import com.blobcity.utils.Constant.Companion.localIntermediate501Path
import com.blobcity.utils.Constant.Companion.localIntermediate508Path
import com.blobcity.utils.Constant.Companion.localIntermediate509Path
import com.blobcity.utils.Constant.Companion.localIntermediate510Path
import com.blobcity.utils.Constant.Companion.localIntermediate513Path
import com.blobcity.utils.Constant.Companion.localIntermediate549Path
import com.blobcity.utils.Constant.Companion.localOutputPath
import com.blobcity.utils.Constant.Companion.localTestCoursePath
import com.blobcity.utils.Constant.Companion.localTopicEightPath
import com.blobcity.utils.Constant.Companion.localTopicFivePath
import com.blobcity.utils.Constant.Companion.localTopicFourPath
import com.blobcity.utils.Constant.Companion.localTopicNinePath
import com.blobcity.utils.Constant.Companion.localTopicOnePath
import com.blobcity.utils.Constant.Companion.localTopicSevenPath
import com.blobcity.utils.Constant.Companion.localTopicSixPath
import com.blobcity.utils.Constant.Companion.localTopicTenPath
import com.blobcity.utils.Constant.Companion.localTopicThreePath
import com.blobcity.utils.Constant.Companion.localTopicTwoPath
import com.blobcity.utils.Utils.Companion.copyAssetFile
import com.blobcity.utils.Utils.Companion.copyFolder
import com.blobcity.utils.Utils.Companion.loadJSONFromAsset
import com.blobcity.utils.Utils.Companion.makeDir
import com.blobcity.model.BranchesItem
import com.blobcity.model.TopicResponseModel
import com.blobcity.utils.Utils
import com.blobcity.utils.Utils.Companion.readFromFile
import com.google.gson.Gson
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity(), PermissionListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        rcv_dashboard.adapter = ChaptersAdapter(this)
        rl_chapter_one.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        TedPermission.with(this)
            .setPermissionListener(this)
            .setDeniedMessage("If you reject permission,you can not use this service\n" +
                    "\nPlease turn on permissions at [Setting] > [Permission]")
            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .check()
    }

    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
        
    }

    override fun onPermissionGranted() {
        storeFileLocally()
    }

    fun storeFileLocally(){
        makeDir(localOutputPath)
        makeDir(localTestCoursePath)

        copyAssetFile("Courses.json", assetOutputPath, localOutputPath, this)
        copyAssetFile("topic.json", assetTestCoursePath, localTestCoursePath, this)

        makeDir(localTopicOnePath)
        makeDir(localBasic394Path)
        makeDir(localBasic395Path)
        makeDir(localBasic396Path)
        makeDir(localBasic397Path)
        makeDir(localBasic408Path)
        makeDir(localBasic409Path)
        makeDir(localBasic550Path)
        makeDir(localBasic1421Path)
        makeDir(localBasic1422Path)
        makeDir(localAdvanced398Path)
        makeDir(localAdvanced399Path)
        makeDir(localAdvanced403Path)
        makeDir(localAdvanced404Path)
        makeDir(localAdvanced1429Path)
        makeDir(localAdvanced1430Path)
        makeDir(localAdvanced1431Path)
        makeDir(localAdvanced1432Path)
        makeDir(localIntermediate400Path)
        makeDir(localIntermediate401Path)
        makeDir(localIntermediate402Path)
        makeDir(localIntermediate405Path)
        makeDir(localIntermediate413Path)
        makeDir(localIntermediate549Path)
        makeDir(localIntermediate1426Path)
        makeDir(localIntermediate1427Path)
        makeDir(localIntermediate1428Path)

        copyFolder(assetBasic394Path, localBasic394Path, this)
        copyFolder(assetBasic395Path, localBasic395Path, this)
        copyFolder(assetBasic396Path, localBasic396Path, this)
        copyFolder(assetBasic397Path, localBasic397Path, this)
        copyFolder(assetBasic408Path, localBasic408Path, this)
        copyFolder(assetBasic409Path, localBasic409Path, this)
        copyFolder(assetBasic550Path, localBasic550Path, this)
        copyFolder(assetBasic1421Path, localBasic1421Path, this)
        copyFolder(assetBasic1422Path, localBasic1422Path, this)
        copyFolder(assetAdvanced398Path, localAdvanced398Path, this)
        copyFolder(assetAdvanced399Path, localAdvanced399Path, this)
        copyFolder(assetAdvanced403Path, localAdvanced403Path, this)
        copyFolder(assetAdvanced404Path, localAdvanced404Path, this)
        copyFolder(assetAdvanced1429Path, localAdvanced1429Path, this)
        copyFolder(assetAdvanced1430Path, localAdvanced1430Path, this)
        copyFolder(assetAdvanced1431Path, localAdvanced1431Path, this)
        copyFolder(assetAdvanced1432Path, localAdvanced1432Path, this)
        copyFolder(assetIntermediate400Path, localIntermediate400Path, this)
        copyFolder(assetIntermediate401Path, localIntermediate401Path, this)
        copyFolder(assetIntermediate402Path, localIntermediate402Path, this)
        copyFolder(assetIntermediate405Path, localIntermediate405Path, this)
        copyFolder(assetIntermediate413Path, localIntermediate413Path, this)
        copyFolder(assetIntermediate549Path, localIntermediate549Path, this)
        copyFolder(assetIntermediate1426Path, localIntermediate1426Path, this)
        copyFolder(assetIntermediate1427Path, localIntermediate1427Path, this)
        copyFolder(assetIntermediate1428Path, localIntermediate1428Path, this)

        copyAssetFile("advanced.json", assetTopicOnePath, localTopicOnePath, this)
        copyAssetFile("basic.json", assetTopicOnePath, localTopicOnePath, this)
        copyAssetFile("intermediate.json", assetTopicOnePath, localTopicOnePath, this)


        makeDir(localTopicTwoPath)
        makeDir(localBasic406Path)
        makeDir(localBasic407Path)
        makeDir(localBasic1423Path)
        makeDir(localBasic1424Path)
        makeDir(localAdvanced410Path)
        makeDir(localAdvanced411Path)
        makeDir(localAdvanced415Path)
        makeDir(localAdvanced416Path)
        makeDir(localIntermediate412Path)
        makeDir(localIntermediate414Path)
        makeDir(localIntermediate417Path)
        makeDir(localIntermediate1425Path)

        copyFolder(assetBasic406Path, localBasic406Path, this)
        copyFolder(assetBasic407Path, localBasic407Path, this)
        copyFolder(assetBasic1423Path, localBasic1423Path, this)
        copyFolder(assetBasic1424Path, localBasic1424Path, this)
        copyFolder(assetAdvanced410Path, localAdvanced410Path, this)
        copyFolder(assetAdvanced411Path, localAdvanced411Path, this)
        copyFolder(assetAdvanced415Path, localAdvanced415Path, this)
        copyFolder(assetAdvanced416Path, localAdvanced416Path, this)
        copyFolder(assetIntermediate412Path, localIntermediate412Path, this)
        copyFolder(assetIntermediate414Path, localIntermediate414Path, this)
        copyFolder(assetIntermediate417Path, localIntermediate417Path, this)
        copyFolder(assetIntermediate1425Path, localIntermediate1425Path, this)

        copyAssetFile("advanced.json", assetTopicTwoPath, localTopicTwoPath, this)
        copyAssetFile("basic.json", assetTopicTwoPath, localTopicTwoPath, this)
        copyAssetFile("intermediate.json", assetTopicTwoPath, localTopicTwoPath, this)

        makeDir(localTopicThreePath)
        makeDir(localBasic418Path)
        makeDir(localBasic419Path)
        makeDir(localBasic420Path)
        makeDir(localBasic421Path)
        makeDir(localAdvanced422Path)
        makeDir(localAdvanced423Path)
        makeDir(localAdvanced427Path)
        makeDir(localAdvanced428Path)
        makeDir(localIntermediate424Path)
        makeDir(localIntermediate425Path)
        makeDir(localIntermediate426Path)
        makeDir(localIntermediate429Path)

        copyFolder(assetBasic418Path, localBasic418Path, this)
        copyFolder(assetBasic419Path, localBasic419Path, this)
        copyFolder(assetBasic420Path, localBasic420Path, this)
        copyFolder(assetBasic421Path, localBasic421Path, this)
        copyFolder(assetAdvanced422Path, localAdvanced422Path, this)
        copyFolder(assetAdvanced423Path, localAdvanced423Path, this)
        copyFolder(assetAdvanced427Path, localAdvanced427Path, this)
        copyFolder(assetAdvanced428Path, localAdvanced428Path, this)
        copyFolder(assetIntermediate424Path, localIntermediate424Path, this)
        copyFolder(assetIntermediate425Path, localIntermediate425Path, this)
        copyFolder(assetIntermediate426Path, localIntermediate426Path, this)
        copyFolder(assetIntermediate429Path, localIntermediate429Path, this)

        copyAssetFile("advanced.json", assetTopicThreePath, localTopicThreePath, this)
        copyAssetFile("basic.json", assetTopicThreePath, localTopicThreePath, this)
        copyAssetFile("intermediate.json", assetTopicThreePath, localTopicThreePath, this)


        makeDir(localTopicFourPath)
        makeDir(localBasic430Path)
        makeDir(localBasic431Path)
        makeDir(localBasic432Path)
        makeDir(localBasic433Path)
        makeDir(localAdvanced434Path)
        makeDir(localAdvanced435Path)
        makeDir(localAdvanced439Path)
        makeDir(localAdvanced440Path)
        makeDir(localIntermediate436Path)
        makeDir(localIntermediate437Path)
        makeDir(localIntermediate438Path)
        makeDir(localIntermediate441Path)

        copyFolder(assetBasic430Path, localBasic430Path, this)
        copyFolder(assetBasic431Path, localBasic431Path, this)
        copyFolder(assetBasic432Path, localBasic432Path, this)
        copyFolder(assetBasic433Path, localBasic433Path, this)
        copyFolder(assetAdvanced434Path, localAdvanced434Path, this)
        copyFolder(assetAdvanced435Path, localAdvanced435Path, this)
        copyFolder(assetAdvanced439Path, localAdvanced439Path, this)
        copyFolder(assetAdvanced440Path, localAdvanced440Path, this)
        copyFolder(assetIntermediate436Path, localIntermediate436Path, this)
        copyFolder(assetIntermediate437Path, localIntermediate437Path, this)
        copyFolder(assetIntermediate438Path, localIntermediate438Path, this)
        copyFolder(assetIntermediate441Path, localIntermediate441Path, this)

        copyAssetFile("advanced.json", assetTopicFourPath, localTopicFourPath, this)
        copyAssetFile("basic.json", assetTopicFourPath, localTopicFourPath, this)
        copyAssetFile("intermediate.json", assetTopicFourPath, localTopicFourPath, this)


        makeDir(localTopicFivePath)
        makeDir(localBasic442Path)
        makeDir(localBasic443Path)
        makeDir(localBasic444Path)
        makeDir(localBasic445Path)
        makeDir(localAdvanced446Path)
        makeDir(localAdvanced447Path)
        makeDir(localAdvanced451Path)
        makeDir(localAdvanced452Path)
        makeDir(localIntermediate448Path)
        makeDir(localIntermediate449Path)
        makeDir(localIntermediate450Path)
        makeDir(localIntermediate453Path)

        copyFolder(assetBasic442Path, localBasic442Path, this)
        copyFolder(assetBasic443Path, localBasic443Path, this)
        copyFolder(assetBasic444Path, localBasic444Path, this)
        copyFolder(assetBasic445Path, localBasic445Path, this)
        copyFolder(assetAdvanced446Path, localAdvanced446Path, this)
        copyFolder(assetAdvanced447Path, localAdvanced447Path, this)
        copyFolder(assetAdvanced451Path, localAdvanced451Path, this)
        copyFolder(assetAdvanced452Path, localAdvanced452Path, this)
        copyFolder(assetIntermediate448Path, localIntermediate448Path, this)
        copyFolder(assetIntermediate449Path, localIntermediate449Path, this)
        copyFolder(assetIntermediate450Path, localIntermediate450Path, this)
        copyFolder(assetIntermediate453Path, localIntermediate453Path, this)

        copyAssetFile("advanced.json", assetTopicFivePath, localTopicFivePath, this)
        copyAssetFile("basic.json", assetTopicFivePath, localTopicFivePath, this)
        copyAssetFile("intermediate.json", assetTopicFivePath, localTopicFivePath, this)


        makeDir(localTopicSixPath)
        makeDir(localBasic454Path)
        makeDir(localBasic455Path)
        makeDir(localBasic456Path)
        makeDir(localBasic457Path)
        makeDir(localAdvanced458Path)
        makeDir(localAdvanced459Path)
        makeDir(localAdvanced463Path)
        makeDir(localAdvanced464Path)
        makeDir(localIntermediate460Path)
        makeDir(localIntermediate461Path)
        makeDir(localIntermediate462Path)
        makeDir(localIntermediate465Path)

        copyFolder(assetBasic454Path, localBasic454Path, this)
        copyFolder(assetBasic455Path, localBasic455Path, this)
        copyFolder(assetBasic456Path, localBasic456Path, this)
        copyFolder(assetBasic457Path, localBasic457Path, this)
        copyFolder(assetAdvanced458Path, localAdvanced458Path, this)
        copyFolder(assetAdvanced459Path, localAdvanced459Path, this)
        copyFolder(assetAdvanced463Path, localAdvanced463Path, this)
        copyFolder(assetAdvanced464Path, localAdvanced464Path, this)
        copyFolder(assetIntermediate460Path, localIntermediate460Path, this)
        copyFolder(assetIntermediate461Path, localIntermediate461Path, this)
        copyFolder(assetIntermediate462Path, localIntermediate462Path, this)
        copyFolder(assetIntermediate465Path, localIntermediate465Path, this)

        copyAssetFile("advanced.json", assetTopicSixPath, localTopicSixPath, this)
        copyAssetFile("basic.json", assetTopicSixPath, localTopicSixPath, this)
        copyAssetFile("intermediate.json", assetTopicSixPath, localTopicSixPath, this)


        makeDir(localTopicSevenPath)
        makeDir(localBasic466Path)
        makeDir(localBasic467Path)
        makeDir(localBasic468Path)
        makeDir(localBasic469Path)
        makeDir(localAdvanced470Path)
        makeDir(localAdvanced471Path)
        makeDir(localAdvanced475Path)
        makeDir(localAdvanced476Path)
        makeDir(localIntermediate472Path)
        makeDir(localIntermediate473Path)
        makeDir(localIntermediate474Path)
        makeDir(localIntermediate477Path)

        copyFolder(assetBasic466Path, localBasic466Path, this)
        copyFolder(assetBasic467Path, localBasic467Path, this)
        copyFolder(assetBasic468Path, localBasic468Path, this)
        copyFolder(assetBasic469Path, localBasic469Path, this)
        copyFolder(assetAdvanced470Path, localAdvanced470Path, this)
        copyFolder(assetAdvanced471Path, localAdvanced471Path, this)
        copyFolder(assetAdvanced475Path, localAdvanced475Path, this)
        copyFolder(assetAdvanced476Path, localAdvanced476Path, this)
        copyFolder(assetIntermediate472Path, localIntermediate472Path, this)
        copyFolder(assetIntermediate473Path, localIntermediate473Path, this)
        copyFolder(assetIntermediate474Path, localIntermediate474Path, this)
        copyFolder(assetIntermediate477Path, localIntermediate477Path, this)

        copyAssetFile("advanced.json", assetTopicSixPath, localTopicSevenPath, this)
        copyAssetFile("basic.json", assetTopicSixPath, localTopicSevenPath, this)
        copyAssetFile("intermediate.json", assetTopicSixPath, localTopicSevenPath, this)


        makeDir(localTopicEightPath)
        makeDir(localBasic478Path)
        makeDir(localBasic479Path)
        makeDir(localBasic480Path)
        makeDir(localBasic481Path)
        makeDir(localAdvanced482Path)
        makeDir(localAdvanced483Path)
        makeDir(localAdvanced487Path)
        makeDir(localAdvanced488Path)
        makeDir(localIntermediate484Path)
        makeDir(localIntermediate485Path)
        makeDir(localIntermediate486Path)
        makeDir(localIntermediate489Path)

        copyFolder(assetBasic478Path, localBasic478Path, this)
        copyFolder(assetBasic479Path, localBasic479Path, this)
        copyFolder(assetBasic480Path, localBasic480Path, this)
        copyFolder(assetBasic481Path, localBasic481Path, this)
        copyFolder(assetAdvanced482Path, localAdvanced482Path, this)
        copyFolder(assetAdvanced483Path, localAdvanced483Path, this)
        copyFolder(assetAdvanced487Path, localAdvanced487Path, this)
        copyFolder(assetAdvanced488Path, localAdvanced488Path, this)
        copyFolder(assetIntermediate484Path, localIntermediate484Path, this)
        copyFolder(assetIntermediate485Path, localIntermediate485Path, this)
        copyFolder(assetIntermediate486Path, localIntermediate486Path, this)
        copyFolder(assetIntermediate489Path, localIntermediate489Path, this)

        copyAssetFile("advanced.json", assetTopicEightPath, localTopicEightPath, this)
        copyAssetFile("basic.json", assetTopicEightPath, localTopicEightPath, this)
        copyAssetFile("intermediate.json", assetTopicEightPath, localTopicEightPath, this)


        makeDir(localTopicNinePath)
        makeDir(localBasic490Path)
        makeDir(localBasic491Path)
        makeDir(localBasic492Path)
        makeDir(localBasic493Path)
        makeDir(localAdvanced494Path)
        makeDir(localAdvanced495Path)
        makeDir(localAdvanced499Path)
        makeDir(localAdvanced500Path)
        makeDir(localIntermediate496Path)
        makeDir(localIntermediate497Path)
        makeDir(localIntermediate498Path)
        makeDir(localIntermediate501Path)

        copyFolder(assetBasic490Path, localBasic490Path, this)
        copyFolder(assetBasic491Path, localBasic491Path, this)
        copyFolder(assetBasic492Path, localBasic492Path, this)
        copyFolder(assetBasic493Path, localBasic493Path, this)
        copyFolder(assetAdvanced494Path, localAdvanced494Path, this)
        copyFolder(assetAdvanced495Path, localAdvanced495Path, this)
        copyFolder(assetAdvanced499Path, localAdvanced499Path, this)
        copyFolder(assetAdvanced500Path, localAdvanced500Path, this)
        copyFolder(assetIntermediate496Path, localIntermediate496Path, this)
        copyFolder(assetIntermediate497Path, localIntermediate497Path, this)
        copyFolder(assetIntermediate498Path, localIntermediate498Path, this)
        copyFolder(assetIntermediate501Path, localIntermediate501Path, this)

        copyAssetFile("advanced.json", assetTopicNinePath, localTopicNinePath, this)
        copyAssetFile("basic.json", assetTopicNinePath, localTopicNinePath, this)
        copyAssetFile("intermediate.json", assetTopicNinePath, localTopicNinePath, this)


        makeDir(localTopicTenPath)
        makeDir(localBasic502Path)
        makeDir(localBasic503Path)
        makeDir(localBasic504Path)
        makeDir(localBasic505Path)
        makeDir(localAdvanced506Path)
        makeDir(localAdvanced507Path)
        makeDir(localAdvanced511Path)
        makeDir(localAdvanced512Path)
        makeDir(localIntermediate508Path)
        makeDir(localIntermediate509Path)
        makeDir(localIntermediate510Path)
        makeDir(localIntermediate513Path)

        copyFolder(assetBasic502Path, localBasic502Path, this)
        copyFolder(assetBasic503Path, localBasic503Path, this)
        copyFolder(assetBasic504Path, localBasic504Path, this)
        copyFolder(assetBasic505Path, localBasic505Path, this)
        copyFolder(assetAdvanced506Path, localAdvanced506Path, this)
        copyFolder(assetAdvanced507Path, localAdvanced507Path, this)
        copyFolder(assetAdvanced511Path, localAdvanced511Path, this)
        copyFolder(assetAdvanced512Path, localAdvanced512Path, this)
        copyFolder(assetIntermediate508Path, localIntermediate508Path, this)
        copyFolder(assetIntermediate509Path, localIntermediate509Path, this)
        copyFolder(assetIntermediate510Path, localIntermediate510Path, this)
        copyFolder(assetIntermediate513Path, localIntermediate513Path, this)

        copyAssetFile("advanced.json", assetTopicTenPath, localTopicTenPath, this)
        copyAssetFile("basic.json", assetTopicTenPath, localTopicTenPath, this)
        copyAssetFile("intermediate.json", assetTopicTenPath, localTopicTenPath, this)

        val jsonString: String? = loadJSONFromAsset(this, assetTestCoursePath+"topic.json")
        /*val jsonString: String? = readFromFile(this, localTestCoursePath+"/topic.json")*/
        val gsonFile = Gson()
        val topicResponseModel: TopicResponseModel = gsonFile.fromJson(jsonString, TopicResponseModel::class.java)!!

        val branchesItemList: List<BranchesItem> = topicResponseModel.branches
        branchesItemList.forEachIndexed { index, branchesItem ->
            if (index == 0){
                val index1: String = branchesItem.topic.index.toString()
                tv_topic_number1.setText(index1)
                tv_topic_name1.setText(branchesItem.topic.title)
            }
            if (index == 1){
                val index2: String = branchesItem.topic.index.toString()
                tv_topic_number2.setText(index2)
                tv_topic_name2.setText(branchesItem.topic.title)
            }
        }
        for (branchItem in branchesItemList){
            Log.e("DashBoard", branchItem.topic.title)
        }
    }
}