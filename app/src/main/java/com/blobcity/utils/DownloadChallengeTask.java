package com.blobcity.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.blobcity.interfaces.ChallengeItemDownloadListener;
import com.blobcity.model.ChallengeModel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class DownloadChallengeTask  {
    private Activity context;
    ChallengeModel model;
    ChallengeItemDownloadListener download;
    public DownloadChallengeTask(Activity context, ChallengeModel model, ChallengeItemDownloadListener download) {
        this.context = context;
        this.model = model;
        this.download = download;

        new downloadTask().execute(new String[] { model.getQuestion(), model.getHint(), model.getOpt1(), model.getOpt2(), model.getOpt3(), model.getOpt4() });
    }
    private class downloadTask extends AsyncTask<String, Integer, List<File>> {
   private static final int MEGABYTE = 1024 * 1024;

        List<File> rowItems;
        int noOfURLs;


        @Override
        protected List<File> doInBackground(String... urls) {
            noOfURLs = urls.length;
            rowItems = new ArrayList<File>();
            File map = null;
            for (String url : urls) {
                map = downloadImage(url);
                rowItems.add(map);
            }
            return rowItems;
        }

        private File downloadImage(String urlString) {

            int count = 0;
            File file = null;
            String[] arr= urlString.split("/");
            Log.e("download challenge","download image.....arr....."+arr.length);
            String name = arr[arr.length-1];
            URL url;
            InputStream inputStream = null;
            BufferedOutputStream outputStream = null;

            try {
                file = new File(context.getExternalFilesDir(null), "/Challenge/"+model.getSerialno());
                //dir = new File(sdCard.getAbsolutePath() + "/SvastiHome/BankAccount/" + customerNo);
                file.mkdirs();
                File file1 = new File(file, name);
                url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
               // URLConnection connection = url.openConnection();
                int lenghtOfFile = urlConnection.getContentLength();

                inputStream = urlConnection.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(file1);
                byte[] buffer = new byte[MEGABYTE];
                int bufferLength = 0;
                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, bufferLength);
                }
                fileOutputStream.close();

                /*inputStream = new BufferedInputStream(url.openStream());
                ByteArrayOutputStream dataStream = new ByteArrayOutputStream();

                outputStream = new BufferedOutputStream(dataStream);

                byte data[] = new byte[512];
                long total = 0;

                while ((count = inputStream.read(data)) != -1) {
                    total += count;
                    *//*publishing progress update on UI thread.
                    Invokes onProgressUpdate()*//*
                    //publishProgress((int)((total*100)/lenghtOfFile));

                    // writing data to byte array stream
                    outputStream.write(data, 0, count);
                }
                outputStream.flush();*/

                /*BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inSampleSize = 1;

                byte[] bytes = dataStream.toByteArray();
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,bmOptions);*/

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                FileUtilsNew.close(inputStream);
                FileUtilsNew.close(outputStream);
            }
            return file;
        }

        protected void onProgressUpdate(Integer... progress) {
           /* progressDialog.setProgress(progress[0]);
            if(rowItems != null) {
                progressDialog.setMessage("Loading " + (rowItems.size()+1) + "/" + noOfURLs);
            }*/
        }

        @Override
        protected void onPostExecute(List<File> rowItems) {

            Log.e("download challenge","onPostExecute....rowItems."+rowItems.size());
            download.onDownload();
            /*listViewAdapter = new CustomListViewAdapter(context, rowItems);
            listView.setAdapter(listViewAdapter);
            progressDialog.dismiss();*/
        }

    }



}
