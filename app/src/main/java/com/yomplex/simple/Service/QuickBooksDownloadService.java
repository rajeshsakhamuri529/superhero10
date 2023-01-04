package com.yomplex.simple.Service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.yomplex.simple.database.QuizGameDataBase;
import com.yomplex.simple.model.Books;
import com.yomplex.simple.model.PlayCount;
import com.yomplex.simple.utils.SharedPrefs;
import com.yomplex.simple.utils.Utils;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.List;

public class QuickBooksDownloadService extends JobIntentService {

    public static final String URL = "url";
    /**
     * Unique job ID for this service.
     */
    static final int DOWNLOAD_JOB_ID = 1111;
    /**
     * Actions download
     */
    private static final String ACTION_DOWNLOAD = "action.DOWNLOAD_DATA";
    private static QuizGameDataBase dataBase;
    private static Context context1;
    private static String dirpath;
    //private static String usermail,userid,phone;
    private static FirebaseFirestore firestore;
    private static SharedPrefs sharedPrefs = null;
    private static final Type REVIEW_TYPE = new TypeToken<List<PlayCount>>() {
    }.getType();
    public static boolean isservice = false;
    public static int incvalue = 0;
    public static int totaldownloads = 0;

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.e("QuickBooksDownloadS","onDestroy......"+incvalue);
    }

    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueueWork(final Context context, FirebaseFirestore db) {
        dataBase = new QuizGameDataBase(context);
        QuickBooksDownloadService.context1 = context;
        firestore = db;
        sharedPrefs = new SharedPrefs();
        incvalue = 0;
        totaldownloads = 0;
        //usermail = sharedPrefs.getPrefVal(context1,"email");
        //userid = sharedPrefs.getPrefVal(context1, ConstantPath.UID);
        //phone = sharedPrefs.getPrefVal(context,"phonenumber");
        File f =  new File((context.getCacheDir()).getAbsolutePath());
        dirpath = f.getAbsolutePath();
        List<Integer> statuslist = dataBase.getquickbooksdownloadstatus();
        //Log.e("content download","status list........"+statuslist);
        Log.e("content download","status list....size...."+statuslist.size());
        //Log.e("content download","statuslist.contains(\"0\")...."+statuslist.contains("0"));
        if(statuslist.size() > 0){
            if(statuslist.contains(0)){
                Log.e("content download","statuslist.contains(\"0\").....if....");
                List<Books> bookslist = dataBase.getAllQuickBooks();
                for(int i= 0; i < bookslist.size();i++){
                    if(bookslist.get(i).getBookdownloadstatus() == 0){
                        String url = bookslist.get(i).getSourceUrl();
                        String version = bookslist.get(i).getVersion();
                        String title = bookslist.get(i).getTitle();
                        String category = bookslist.get(i).getCategory();
                        String Id = bookslist.get(i).getId();
                        String foldername = bookslist.get(i).getFolderName();
                        String sortorder = bookslist.get(i).getSortorder();
                        String PublishedOn = bookslist.get(i).getPublishedOn();
                        String visibility = bookslist.get(i).getVisibility();
                        isservice = true;
                        Intent intent = new Intent(context1, QuickBooksDownloadService.class);
                        intent.putExtra(URL, url);
                        intent.putExtra("sortorder", sortorder);
                        intent.putExtra("publishedon", PublishedOn);
                        intent.putExtra("version", version);
                        intent.putExtra("title", title);
                        intent.putExtra("category", category);
                        intent.putExtra("foldername", foldername);
                        intent.putExtra("id", Id);
                        intent.putExtra("visibility", visibility);
                        intent.setAction(ACTION_DOWNLOAD);
                        enqueueWork(context1, QuickBooksDownloadService.class, DOWNLOAD_JOB_ID, intent);

                    }
                }
            } else {
                Log.e("content download","statuslist.contains(\"0\").....else.....");

                CollectionReference docRef = db.collection("quickbooks");

                docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            Log.e("books download", "task.getResult().size()...."+task.getResult().size());

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Books books=new Books();
                                String title = (document.get("Title").toString());
                                String version = (document.get("version").toString());
                                String category = (document.get("Category").toString());
                                String url = (document.get("SourceUrl").toString());
                                String Id = (document.get("id").toString());
                                String foldername = (document.get("FolderName").toString());
                                String sortorder = document.get("sortorder").toString();
                                String PublishedOn = document.get("PublishedOn").toString();
                                String visibility = document.get("visibility").toString();
                                int count = dataBase.getQuickBooksCount(Id);
                                Log.e("books download", "count..."+count);
                                if(count == 0){
                                    Books books=new Books();
                                    books.setTitle(document.get("Title").toString());
                                    books.setVersion(document.get("version").toString());
                                    books.setSortorder(document.get("sortorder").toString());
                                    books.setPublishedOn(document.get("PublishedOn").toString());
                                    books.setSourceUrl(document.get("SourceUrl").toString());
                                    books.setThumbnail("");
                                    books.setFolderName(document.get("FolderName").toString());
                                    books.setId(document.get("id").toString());
                                    books.setCopystatus(1);
                                    books.setCategory(document.get("Category").toString());
                                    books.setVisibility(document.get("visibility").toString());
                                    isservice = true;
                                    dataBase.insertQuickBooks(books);
                                    Intent intent = new Intent(context1, QuickBooksDownloadService.class);
                                    intent.putExtra(URL, document.get("SourceUrl").toString());
                                    intent.putExtra("sortorder", document.get("sortorder").toString());
                                    intent.putExtra("publishedon", document.get("PublishedOn").toString());
                                    intent.putExtra("version", document.get("version").toString());
                                    intent.putExtra("title", document.get("Title").toString());
                                    intent.putExtra("category", document.get("Category").toString());
                                    intent.putExtra("foldername", document.get("FolderName").toString());
                                    intent.putExtra("id", Id);
                                    intent.putExtra("visibility", document.get("visibility").toString());
                                    intent.setAction(ACTION_DOWNLOAD);
                                    enqueueWork(context1, QuickBooksDownloadService.class, DOWNLOAD_JOB_ID, intent);
                                }else{
                                    Log.e("books download", "id.....visibility"+Id+"......."+visibility);
                                    if(visibility.equals("1")){
                                        String dbversion = dataBase.getQuickBooksVersion(Id);
                                        dataBase.updatequickbookssortorderbasedonId(sortorder,Id);
                                        if(dbversion != null){
                                            if(Integer.parseInt(dbversion) < Integer.parseInt(version)) {
                                                // Log.e("books download", "i==0....if......"+version);
                                                //break;
                                                //downloadDataFromBackground(this@GradeActivity,url,version,"basic")

                                                //}else{
                                                isservice = true;
                                                dataBase.updatequickbooksdownloadstatusbasedonId(0,Id);
                                                Intent intent = new Intent(context1, QuickBooksDownloadService.class);
                                                intent.putExtra(URL, url);
                                                intent.putExtra("sortorder", sortorder);
                                                intent.putExtra("publishedon", PublishedOn);
                                                intent.putExtra("version", version);
                                                intent.putExtra("title", title);
                                                intent.putExtra("category", category);
                                                intent.putExtra("foldername", foldername);
                                                intent.putExtra("id", Id);
                                                intent.putExtra("visibility", visibility);
                                                intent.setAction(ACTION_DOWNLOAD);
                                                enqueueWork(context1, QuickBooksDownloadService.class, DOWNLOAD_JOB_ID, intent);
                                            }
                                        }

                                    }else{
                                        dataBase.updatequickbooksvisibilitybasedonId("0",Id);
                                        dataBase.updatequickbooksversionbasedonId(version,Id);
                                        dataBase.updatequickbookssortorderbasedonId(sortorder,Id);
                                    }

                                }


                            }
                        }
                    }
                });


            }
        }else {

            CollectionReference docRef = db.collection("quickbooks");

            docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        Log.e("grade activity", "task.getResult().size()...."+task.getResult().size());

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Books books=new Books();
                            books.setTitle(document.get("Title").toString());
                            books.setVersion(document.get("version").toString());
                            books.setSortorder(document.get("sortorder").toString());
                            books.setPublishedOn(document.get("PublishedOn").toString());
                            books.setSourceUrl(document.get("SourceUrl").toString());
                            books.setThumbnail("");
                            books.setCopystatus(1);
                            books.setId(document.get("id").toString());
                            books.setFolderName(document.get("FolderName").toString());
                            books.setCategory(document.get("Category").toString());
                            books.setVisibility(document.get("visibility").toString());
                            isservice = true;
                            dataBase.insertQuickBooks(books);
                            Intent intent = new Intent(context1, QuickBooksDownloadService.class);
                            intent.putExtra(URL, document.get("SourceUrl").toString());
                            intent.putExtra("sortorder", document.get("sortorder").toString());
                            intent.putExtra("publishedon", document.get("PublishedOn").toString());
                            intent.putExtra("version", document.get("version").toString());
                            intent.putExtra("title", document.get("Title").toString());
                            intent.putExtra("foldername", document.get("FolderName").toString());
                            intent.putExtra("category", document.get("Category").toString());
                            intent.putExtra("id", document.get("id").toString());
                            intent.putExtra("visibility", document.get("visibility").toString());

                            intent.setAction(ACTION_DOWNLOAD);
                            enqueueWork(context1, QuickBooksDownloadService.class, DOWNLOAD_JOB_ID, intent);
                        }
                    }
                }
            });



        }



    }
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_DOWNLOAD:
                    //mResultReceiver = intent.getParcelableExtra(RECEIVER);
                    final String url = intent.getStringExtra(URL);
                    final String version = intent.getStringExtra("version");
                    final String title = intent.getStringExtra("title");
                    final String category = intent.getStringExtra("category");
                    final String foldername = intent.getStringExtra("foldername");
                    final String publishedon = intent.getStringExtra("publishedon");
                    final String sortorder = intent.getStringExtra("sortorder");
                    final String id = intent.getStringExtra("id");
                    final String visibility = intent.getStringExtra("visibility");
                    Log.e("content download","dirpath........"+dirpath);
                    Log.e("content download","url........"+url);
                    Log.e("content download","title........"+title.toLowerCase());
                    Log.e("content download","category........"+category);
                    Log.e("content download","id........"+id);
                    Log.e("content download","visibility........"+visibility);
                    Log.e("content download","version........"+version);
                    incvalue++;

                    /*String filename ="";
                    if(testtype.equals("calculus1")){
                        filename = "/jee-calculus-1.zip";
                    }else if(testtype.equals("calculus2")){
                        filename = "/jee-calculus-2.zip";
                    }else if(testtype.equals("algebra")){
                        filename = "/ii-algebra.zip";
                    }else if(testtype.equals("other")){
                        filename = "/other.zip";
                    }else if(testtype.equals("geometry")){
                        filename = "/iii-geometry.zip";
                    }
                    Log.e("content download","filename........"+filename);
                    Log.e("content download","url........"+url);*/
                    // final String finalFilename = filename;
                    //+"/Books/"+category
                    int downloadId = PRDownloader.download(url, dirpath+"/QuickBooks/"+category, "/"+foldername+".zip")
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
                                        ///data/data/com.yomplex.tests/cache/Books/CALCULUS/linear-differential-equaltions
                                        ///data/data/com.yomplex.tests/cache/Books/CALCULUS/linear-differential-equaltions
                                        ///data/data/com.yomplex.tests/cache/Books/CALCULUS/linear-differential-equaltions
                                        //File dirFile = new File(context1.getCacheDir(),"test");
                                        //FileUtils.deleteDirectory(dirFile);
                                        dataBase.updatequickbooksreadfilestatusbasedonId(0,id);
                                        boolean iszip = Utils.unpackZip(dirpath+"/QuickBooks/"+category,"/"+foldername+".zip");
                                        if(iszip){
                                            File dirFile = new File(context1.getCacheDir(),"QuickBooks/"+category+"/"+foldername+".zip");
                                            dirFile.delete();
                                            File dirFile1 = new File(context1.getCacheDir(),"QuickBooks/"+category+"/"+foldername+"/thumbnail.svg");


                                            dataBase.updatequickbooksValues(id,version,1,1,dirFile1.getAbsolutePath(),title,url,sortorder,publishedon,category,visibility,foldername);

                                            totaldownloads++;
                                            Log.e("books download","totaldownloads......"+totaldownloads);
                                            if(incvalue == totaldownloads){
                                                isservice = false;
                                                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                                                String date = dataBase.getQuickBookContentDate();
                                                if(date != null){
                                                    dataBase.updateQuickBookContentDate(format1.format(Utils.date),1);
                                                }else{
                                                    //Log.e("content download","date......................."+format1.format(Utils.date));
                                                    dataBase.insertQuickBookContentUpdateDate(format1.format(Utils.date));
                                                }
                                            }
                                        }
                                    }catch (Exception e){

                                    }


                                    /*Bundle bundle = new Bundle();
                                    bundle.putString("data","success");
                                    mResultReceiver.send(SHOW_RESULT, bundle);*/
                                }

                                @Override
                                public void onError(Error error) {
                                    Log.e("job service","onerror....."+error.getConnectionException());
                                    isservice = false;
                                    dataBase.deletequickbookcontentdate();
                                    //SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                                    /*String date = dataBase.getBookContentDate();
                                    if(date != null){
                                        dataBase.updateBookContentDate("",1);
                                    }else{
                                        //Log.e("content download","date......................."+format1.format(Utils.date));
                                        dataBase.insertBookContentUpdateDate("");
                                    }*/
                                    //isservice = true;
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
