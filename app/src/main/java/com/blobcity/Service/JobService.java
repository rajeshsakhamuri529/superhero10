package com.blobcity.Service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import android.util.Log;

import com.blobcity.database.QuizGameDataBase;
import com.blobcity.utils.Utils;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;

import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * Created by Sambhaji Karad on 01/11/18.
 */

public class JobService extends JobIntentService {

    private static final String TAG = JobService.class.getSimpleName();
    public static final String RECEIVER = "receiver";
    public static final String URL = "url";
    public static final int SHOW_RESULT = 123;
    /**
     * Result receiver object to send results
     */
    private ResultReceiver mResultReceiver;
    /**
     * Unique job ID for this service.
     */
    static final int DOWNLOAD_JOB_ID = 1000;
    /**
     * Actions download
     */
    private static final String ACTION_DOWNLOAD = "action.DOWNLOAD_DATA";
    private static String dirpath;
    private static Context context1;
    private static QuizGameDataBase dataBase;
    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueueWork(Context context,String url,String version) {
        dataBase = new QuizGameDataBase(context);
        File f =  new File((context.getExternalFilesDir(null)).getAbsolutePath());
        dirpath = f.getAbsolutePath();
        JobService.context1 = context;
        Intent intent = new Intent(context, JobService.class);
        //intent.putExtra(RECEIVER, workerResultReceiver);
        intent.putExtra(URL, url);
        intent.putExtra("version", version);
        intent.setAction(ACTION_DOWNLOAD);
        enqueueWork(context, JobService.class, DOWNLOAD_JOB_ID, intent);
    }



    @SuppressLint("DefaultLocale")
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d(TAG, "onHandleWork() called with: intent = [" + intent + "]");
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_DOWNLOAD:
                    //mResultReceiver = intent.getParcelableExtra(RECEIVER);
                    final String url = intent.getStringExtra(URL);
                    final String version = intent.getStringExtra("version");
                    int downloadId = PRDownloader.download(url, dirpath, "/testcontent.rar")
                            .build()
                            .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                                @Override
                                public void onStartOrResume() {
                                    Log.e("job service","onStartOrResume.....");
                                }
                            })
                            .setOnPauseListener(new OnPauseListener() {
                                @Override
                                public void onPause() {
                                    Log.e("job service","onPause.....");
                                }
                            })
                            .setOnCancelListener(new OnCancelListener() {
                                @Override
                                public void onCancel() {
                                    Log.e("job service","onCancel.....");
                                }
                            })
                            .setOnProgressListener(new OnProgressListener() {
                                @Override
                                public void onProgress(Progress progress) {
                                    Log.e("job service","onProgress....."+progress);
                                }
                            })
                            .start(new OnDownloadListener() {
                                @Override
                                public void onDownloadComplete() {
                                    Log.e("job service","onDownloadComplete.....");
                                    try{
                                        File dirFile = new File(context1.getExternalFilesDir(null),"test");
                                        FileUtils.deleteDirectory(dirFile);
                                    }catch (Exception e){

                                    }
                                    boolean iszip = Utils.unpackZip(dirpath,"/testcontent.rar");
                                    if(iszip){
                                        File dirFile = new  File(context1.getExternalFilesDir(null),"testcontent.rar");
                                        dirFile.delete();
                                        dataBase.updatetestcontentversion(version);
                                        dataBase.updatetestcontentdownloadstatus(1);
                                    }
                                    /*Bundle bundle = new Bundle();
                                    bundle.putString("data","success");
                                    mResultReceiver.send(SHOW_RESULT, bundle);*/
                                }

                                @Override
                                public void onError(Error error) {
                                    Log.e("job service","onerror....."+error);
                                   // JobService.enqueueWork(context1,url,version);
                                    /*Bundle bundle = new Bundle();
                                    bundle.putString("data","failure");
                                    mResultReceiver.send(SHOW_RESULT, bundle);*/
                                }

                                /*@Override
                                public void onError(Error error) {
                                    Log.e("job service","onerror....."+error);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("data","failure");
                                    mResultReceiver.send(SHOW_RESULT, bundle);
                                }*/


                            });
                    /*for(int i=0;i<10;i++){
                        try {
                            Thread.sleep(1000);
                            Bundle bundle = new Bundle();
                            bundle.putString("data",String.format("Showing From JobIntent Service %d", i));
                            mResultReceiver.send(SHOW_RESULT, bundle);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }*/
                    break;
            }
        }
    }
}