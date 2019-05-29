package com.blobcity.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.util.Log;

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
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Utils {

    public static void makeDir(String path){
        File folder = new File(path);
        if (!folder.exists()){
            folder.mkdirs();
        }
    }

    public static List<String> getListOfFilesFromFolder(String path){
        File file = new File(path);
        return Arrays.asList(file.list());
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