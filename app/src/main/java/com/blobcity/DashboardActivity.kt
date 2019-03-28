package com.blobcity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.blobcity.Constant.Companion.assetAdvanced1429Path
import com.blobcity.Constant.Companion.assetAdvanced1430Path
import com.blobcity.Constant.Companion.assetAdvanced1431Path
import com.blobcity.Constant.Companion.assetAdvanced1432Path
import com.blobcity.Constant.Companion.assetAdvanced398Path
import com.blobcity.Constant.Companion.assetAdvanced399Path
import com.blobcity.Constant.Companion.assetAdvanced403Path
import com.blobcity.Constant.Companion.assetAdvanced404Path
import com.blobcity.Constant.Companion.assetAdvanced410Path
import com.blobcity.Constant.Companion.assetAdvanced411Path
import com.blobcity.Constant.Companion.assetAdvanced415Path
import com.blobcity.Constant.Companion.assetAdvanced416Path
import com.blobcity.Constant.Companion.assetAdvanced422Path
import com.blobcity.Constant.Companion.assetAdvanced423Path
import com.blobcity.Constant.Companion.assetAdvanced427Path
import com.blobcity.Constant.Companion.assetAdvanced428Path
import com.blobcity.Constant.Companion.assetBasic1421Path
import com.blobcity.Constant.Companion.assetBasic1422Path
import com.blobcity.Constant.Companion.assetBasic1423Path
import com.blobcity.Constant.Companion.assetBasic1424Path
import com.blobcity.Constant.Companion.assetBasic394Path
import com.blobcity.Constant.Companion.assetBasic395Path
import com.blobcity.Constant.Companion.assetBasic396Path
import com.blobcity.Constant.Companion.assetBasic397Path
import com.blobcity.Constant.Companion.assetBasic406Path
import com.blobcity.Constant.Companion.assetBasic407Path
import com.blobcity.Constant.Companion.assetBasic408Path
import com.blobcity.Constant.Companion.assetBasic409Path
import com.blobcity.Constant.Companion.assetBasic418Path
import com.blobcity.Constant.Companion.assetBasic419Path
import com.blobcity.Constant.Companion.assetBasic420Path
import com.blobcity.Constant.Companion.assetBasic421Path
import com.blobcity.Constant.Companion.assetBasic550Path
import com.blobcity.Constant.Companion.assetIntermediate1425Path
import com.blobcity.Constant.Companion.assetIntermediate1426Path
import com.blobcity.Constant.Companion.assetIntermediate1427Path
import com.blobcity.Constant.Companion.assetIntermediate1428Path
import com.blobcity.Constant.Companion.assetIntermediate400Path
import com.blobcity.Constant.Companion.assetIntermediate401Path
import com.blobcity.Constant.Companion.assetIntermediate402Path
import com.blobcity.Constant.Companion.assetIntermediate405Path
import com.blobcity.Constant.Companion.assetIntermediate412Path
import com.blobcity.Constant.Companion.assetIntermediate413Path
import com.blobcity.Constant.Companion.assetIntermediate414Path
import com.blobcity.Constant.Companion.assetIntermediate417Path
import com.blobcity.Constant.Companion.assetIntermediate424Path
import com.blobcity.Constant.Companion.assetIntermediate425Path
import com.blobcity.Constant.Companion.assetIntermediate426Path
import com.blobcity.Constant.Companion.assetIntermediate429Path
import com.blobcity.Constant.Companion.assetIntermediate549Path
import com.blobcity.Constant.Companion.assetTopicOnePath
import com.blobcity.Constant.Companion.assetTopicThreePath
import com.blobcity.Constant.Companion.assetTopicTwoPath
import com.blobcity.Constant.Companion.localAdvanced1429Path
import com.blobcity.Constant.Companion.localAdvanced1430Path
import com.blobcity.Constant.Companion.localAdvanced1431Path
import com.blobcity.Constant.Companion.localAdvanced1432Path
import com.blobcity.Constant.Companion.localAdvanced398Path
import com.blobcity.Constant.Companion.localAdvanced399Path
import com.blobcity.Constant.Companion.localAdvanced403Path
import com.blobcity.Constant.Companion.localAdvanced404Path
import com.blobcity.Constant.Companion.localAdvanced410Path
import com.blobcity.Constant.Companion.localAdvanced411Path
import com.blobcity.Constant.Companion.localAdvanced415Path
import com.blobcity.Constant.Companion.localAdvanced416Path
import com.blobcity.Constant.Companion.localAdvanced422Path
import com.blobcity.Constant.Companion.localAdvanced423Path
import com.blobcity.Constant.Companion.localAdvanced427Path
import com.blobcity.Constant.Companion.localAdvanced428Path
import com.blobcity.Constant.Companion.localBasic1421Path
import com.blobcity.Constant.Companion.localBasic1422Path
import com.blobcity.Constant.Companion.localBasic1423Path
import com.blobcity.Constant.Companion.localBasic1424Path
import com.blobcity.Constant.Companion.localBasic394Path
import com.blobcity.Constant.Companion.localBasic395Path
import com.blobcity.Constant.Companion.localBasic396Path
import com.blobcity.Constant.Companion.localBasic397Path
import com.blobcity.Constant.Companion.localBasic406Path
import com.blobcity.Constant.Companion.localBasic407Path
import com.blobcity.Constant.Companion.localBasic408Path
import com.blobcity.Constant.Companion.localBasic409Path
import com.blobcity.Constant.Companion.localBasic418Path
import com.blobcity.Constant.Companion.localBasic419Path
import com.blobcity.Constant.Companion.localBasic420Path
import com.blobcity.Constant.Companion.localBasic421Path
import com.blobcity.Constant.Companion.localBasic550Path
import com.blobcity.Constant.Companion.localIntermediate1425Path
import com.blobcity.Constant.Companion.localIntermediate1426Path
import com.blobcity.Constant.Companion.localIntermediate1427Path
import com.blobcity.Constant.Companion.localIntermediate1428Path
import com.blobcity.Constant.Companion.localIntermediate400Path
import com.blobcity.Constant.Companion.localIntermediate401Path
import com.blobcity.Constant.Companion.localIntermediate402Path
import com.blobcity.Constant.Companion.localIntermediate405Path
import com.blobcity.Constant.Companion.localIntermediate412Path
import com.blobcity.Constant.Companion.localIntermediate413Path
import com.blobcity.Constant.Companion.localIntermediate414Path
import com.blobcity.Constant.Companion.localIntermediate417Path
import com.blobcity.Constant.Companion.localIntermediate424Path
import com.blobcity.Constant.Companion.localIntermediate425Path
import com.blobcity.Constant.Companion.localIntermediate426Path
import com.blobcity.Constant.Companion.localIntermediate429Path
import com.blobcity.Constant.Companion.localIntermediate549Path
import com.blobcity.Constant.Companion.localOutputPath
import com.blobcity.Constant.Companion.localTestCoursePath
import com.blobcity.Constant.Companion.localTopicOnePath
import com.blobcity.Constant.Companion.localTopicThreePath
import com.blobcity.Constant.Companion.localTopicTwoPath
import com.blobcity.Utils.Companion.copyAssetFile
import com.blobcity.Utils.Companion.copyFolder
import com.blobcity.Utils.Companion.makeDir
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
        makeDir(localOutputPath)
        makeDir(localTestCoursePath)

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
    }
}