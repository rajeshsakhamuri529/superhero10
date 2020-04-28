package com.blobcity.utils;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;
import com.blobcity.R;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static android.content.Context.AUDIO_SERVICE;

public class Utils {

    public static SoundPool soundPool;
    public static SoundPool soundPool1;
    public static SoundPool soundPool2;
    public static int soundID,soundID1,soundID2;
    public static boolean loaded = false;
    public static float volume;
    public static void getPlayer(Context context){
        // Set the hardware buttons to control the music
       // context.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        // Load the sound
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .build();
        } else {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        }


        //soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                                       int status) {
                loaded = true;
            }
        });
        soundID = soundPool.load(context, R.raw.amount_low, 1);

        // Getting the user sound settings
        AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        float actualVolume = (float) audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actualVolume/maxVolume;

    }


    /*public static void getPlayerForCorrect(Context context){
        // Set the hardware buttons to control the music
        // context.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        // Load the sound
        soundPool1 = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        soundPool1.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                                       int status) {
                loaded = true;
            }
        });
        soundID1 = soundPool1.load(context, R.raw.select_high_correct, 1);

        // Getting the user sound settings
        AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        float actualVolume = (float) audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actualVolume/maxVolume;

    }
    public static void getPlayerForwrong(Context context){
        // Set the hardware buttons to control the music
        // context.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        // Load the sound
        soundPool2 = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        soundPool2.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                                       int status) {
                loaded = true;
            }
        });
        soundID2 = soundPool2.load(context, R.raw.bounce_high_wrong, 1);

        // Getting the user sound settings
        AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        float actualVolume = (float) audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actualVolume/maxVolume;

    }*/
    public static boolean isOnline(Context _Context) {
        ConnectivityManager cm = (ConnectivityManager) _Context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (cm != null)
            netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void transition(Context context, final WebView webView, Boolean isColourGreen){
        int colorFrom = context.getResources().getColor(R.color.answer_bg);
        int colorTo = context.getResources().getColor(R.color.red_wrong_answer);
        if(isColourGreen)
        {
            colorTo = context.getResources().getColor(R.color.green_right_answer);
        }

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        final GradientDrawable background = (GradientDrawable) webView.getBackground();

        colorAnimation.setDuration(1000); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                //webView.setBackgroundColor((int) animator.getAnimatedValue());
               // Log.d("onAnimationUpdate",background.getColors()+"!")
                background.setColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
    }
    public static void challengetransition(Context context, final WebView webView, Boolean isColourGreen){
        int colorFrom = context.getResources().getColor(R.color.answer_bg);
        int colorTo = context.getResources().getColor(R.color.red_wrong_answer);
        if(isColourGreen)
        {
            colorTo = context.getResources().getColor(R.color.action_button);
        }

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        final GradientDrawable background = (GradientDrawable) webView.getBackground();

        colorAnimation.setDuration(1000); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                //webView.setBackgroundColor((int) animator.getAnimatedValue());
                // Log.d("onAnimationUpdate",background.getColors()+"!")
                background.setColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
    }

    public static void transitionBack(Context context, final WebView webView){
        int colorFrom = context.getResources().getColor(R.color.answer_bg);
        int colorTo = context.getResources().getColor(R.color.answer_bg);

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        final GradientDrawable background = (GradientDrawable) webView.getBackground();
        //final GradientDrawable background = (GradientDrawable) context.getResources().getDrawable(R.drawable.option_curved_border);
        colorAnimation.setDuration(1); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                //webView.setBackgroundColor((int) animator.getAnimatedValue());
                // Log.d("onAnimationUpdate",background.getColors()+"!")
                background.setColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
    }

    public static void makeDir(String path){
        File folder = new File(path);
        if (!folder.exists()){
            folder.mkdirs();
        }
    }

    public static String ofSize(String text, Integer start){
        Log.d("ofSize","!"+text+"!"+start);
        SpannableString ss1=  new SpannableString(text);
        ss1.setSpan(new RelativeSizeSpan(2f), start,start+4, 0); // set size
        //ss1.setSpan(new ForegroundColorSpan(Color.RED), 0, 5, 0);// set color
        /*TextView tv= (TextView) findViewById(R.id.textview);
        tv.setText(ss1);*/
        return ss1.toString();
    }

    public static ArrayList<String> listAssetFiles(String path, Context context) {

        String [] list;String dummyPath = path;
        Log.d("listAssetFiles",path);
        if(path.endsWith("/")){
            dummyPath = path;
            dummyPath = dummyPath.substring(0,dummyPath.length()-1);
            Log.d("dummyPath",dummyPath);
            Log.d("path",path);
        }
        ArrayList<String> l = new ArrayList<>();
        try {
            list = context.getAssets().list(dummyPath);
            if (list.length > 0) {
                Log.d("list length",list.length+"!");
                // This is a folder
                for (String file : list) {
                    /*if (!listAssetFiles(path + "/" + file,context))
                        return false;
                    else {*/
                        l.add(file);
                        // This is a file
                        // TODO: add file name to an array list
                    //}
                }
            }else{
                Log.d("list length",list.length+"!");
            }
        } catch (IOException e) {
            return new ArrayList<String>();
        }
        Log.d("listAssetFiles",l+"!");
        return l;
    }

    public static List<String> getListOfFilesFromFolder(String path){
        File file = new File(path);
        return Arrays.asList(file.list());
    }

    public static Boolean jsoupWrapper(String path,Context context){
        //String fileName = "path/to/file.html";
        InputStream is=null;
        Document doc = null;
        /*String path = pathWrong.substring(21);
        Log.d("jsoupWrapper",path+"!"+pathWrong);*/
        try {
            //doc = Jsoup.parse(new File(path), "utf-8","http://google.com/");
            //is= context.getAssets().open(path);
//            is= context.getAssets().open("courses/Test Course/topic-one/basic-396/opt1.html");
            is= context.getAssets().open(path);
            doc = Jsoup.parse(is, "UTF-8", "http://example.com/");
            Element divTag = doc.getElementById("wrapper");
            System.out.println(divTag.text());
            if(divTag.text().toLowerCase().equals("true"))
            {
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String jsoup2210(String pathWrong,Context context){
        //String fileName = "path/to/file.html";
        InputStream is=null;
        Document doc = null;
        Log.d("jsoup2210",pathWrong+"!");
        String path = pathWrong.substring(22);
        Log.d("jsoupWrapper",path+"!"+pathWrong);
        try {
            //doc = Jsoup.parse(new File(path), "utf-8","http://google.com/");
            //is= context.getAssets().open(path);
//            is= context.getAssets().open("courses/Test Course/topic-one/basic-396/opt1.html");
            is= context.getAssets().open(path);
            doc = Jsoup.parse(is, "UTF-8", "http://example.com/");
            Element divTag = doc.getElementById("wrapper");
            divTag.attr("rel", "stylesheet").attr("type", "text/css").attr("href", "AnswerQA.css");
            doc.getElementById("wrapper").attr("rel", "stylesheet").attr("type", "text/css").attr("href", "AnswerQA.css");
            System.out.println(divTag.text());
           /* if(divTag.text().equals("True"))
            {
                return true;
            }*/
            String htmldata = doc.html();
            Log.d("html",htmldata+"!");
            return htmldata;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String jsoup(String pathWrong,Context context){
        InputStream is=null;
        Document doc = null;
        Log.d("jsoup",pathWrong+"!");
        String path = pathWrong.substring(22);
        Log.d("jsoup2",path+"!"+pathWrong);
        try {
            //doc = Jsoup.parse(new File(path), "utf-8","http://google.com/");
            //is= context.getAssets().open(path);
//            is= context.getAssets().open("courses/Test Course/topic-one/basic-396/opt1.html");
            is= context.getAssets().open(path);
            doc = Jsoup.parse(is, "UTF-8", "http://example.com/");
            Element divTag = doc.getElementById("wrapper");

            Elements tables = doc.getElementsByTag("head"); //important part
            Log.d("tables",tables.toString()+"!");
            /*StringBuilder sb = new StringBuilder();
            sb.append("<HTML><HEAD><LINK href=\"styles.css\" type=\"text/css\" rel=\"stylesheet\"/></HEAD><body>");
            sb.append(tables.toString());
            sb.append("</body></HTML>");
            Log.d("sb",sb.toString()+"!");*/

            divTag.attr("rel", "stylesheet").attr("type", "text/css").attr("href", "AnswerQA.css");
            //doc.getElementById("wrapper").attr("rel", "stylesheet").attr("type", "text/css").attr("href", "AnswerQA.css");

            doc.head().append("<link rel=\"stylesheet\" href=\"../../../../AnswerQA.css\" type=\"text/css\" media=\"all\" />");
            String head = doc.getElementsByTag("head").toString();
            Log.d("head",head+"!");
            System.out.println(divTag.text());
           /* if(divTag.text().equals("True"))
            {
                return true;
            }*/
            String htmldata = doc.html();
            Log.d("html",htmldata+"!");
            return htmldata;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String readFromFile(String path){
        File yourFile = new File(path);
        FileInputStream stream = null;
        String jsonStr = null;
        try {
            stream = new FileInputStream(yourFile);
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

            jsonStr = Charset.defaultCharset().decode(bb).toString();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally {
            try {
                if (stream != null){
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return jsonStr;
    }

    public static boolean unpackZip(String path, String zipname)
    {
        InputStream is;
        ZipInputStream zis;
        try
        {
            String filename;
            is = new FileInputStream(path + zipname);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null)
            {
                filename = ze.getName();

                // Need to create directories if not exists, or
                // it will generate an Exception...
                if (ze.isDirectory()) {
                    File fmd = new File(path + filename);
                    fmd.mkdirs();
                    continue;
                }

                FileOutputStream fout = new FileOutputStream(path + filename);

                while ((count = zis.read(buffer)) != -1)
                {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
            }

            zis.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /*public static void copyFolder(String name, String outPath, Context context){
        AssetManager assetManager = context.getAssets();
        List<String> files = getListOfFilesFromAsset(name, context);
        if (files != null) {
            for (String filename : files) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = assetManager.open(name + "/" + filename);   // if files resides inside the "Files" directory itself
                    out = new FileOutputStream(outPath + filename);
                    copyFile(in, out);
                    in.close();
                    in = null;
                    out.flush();
                    out.close();
                    out = null;
                } catch (Exception e) {
                    Log.e("tag", e.getMessage());
                }
            }
        }
    }*/

    /*public static List<String> getListOfFilesFromAsset(String path, Context context){
        AssetManager assetManager = context.getAssets();
        try {
            return Arrays.asList(assetManager.list(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }*/



    public static void copyAssetFile(String name, String path, String outPath, Context context){
        AssetManager assetManager = context.getAssets();
            /*val files: Array<String>?
            files = assetManger.list(name)
            for (filenames in files){

            }*/
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(path+name);   // if files resides inside the "Files" directory itself
            out = new FileOutputStream(outPath+"/"+name);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch(Exception e) {
            Log.e("tag", e.getMessage());
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    /*public static String loadJSONFromAsset(Context context, String path) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(path);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }*/



    void encrypt(String fileName, String path, String ePath)
            throws IOException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException {
        // Here you read the cleartext.
        File extStore = Environment.getExternalStorageDirectory();
        FileInputStream fis = new FileInputStream(path + fileName);
        // This stream write the encrypted text. This stream will be wrapped by
        // another stream.
        FileOutputStream fos = new FileOutputStream(ePath + fileName);


        // Length is 16 byte
        SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes(),
                "AES");
        // Create cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks);
        // Wrap the output stream
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        // Write bytes
        int b;
        byte[] d = new byte[8];
        while ((b = fis.read(d)) != -1) {
            cos.write(d, 0, b);
        }
        // Flush and close streams.
        cos.flush();
        cos.close();
        fis.close();
    }

    void decrypt(String fileName, String path, String dPath)
            throws IOException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException {

        FileInputStream fis = new FileInputStream(path + fileName);

        FileOutputStream fos = new FileOutputStream(dPath + fileName);
        SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw ".getBytes(),
                "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sks);
        CipherInputStream cis = new CipherInputStream(fis, cipher);
        int b;
        byte[] d = new byte[8];
        while ((b = cis.read(d)) != -1) {
            fos.write(d, 0, b);
        }
        fos.flush();
        fos.close();
        cis.close();
    }



}