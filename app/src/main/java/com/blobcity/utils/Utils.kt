package com.blobcity.utils

import android.content.Context
import android.content.res.AssetManager
import android.os.Environment
import android.util.Log
import java.io.*
import java.lang.Exception
import java.nio.charset.Charset
import android.os.Environment.getExternalStorageDirectory
import java.nio.channels.FileChannel


class Utils {
    companion object {
        fun makeDir(path: String){
            val folder = File(path)
            if (!folder.exists()){
                folder.mkdirs()
            }
        }

        fun copyFolder(name: String, outPath: String, context: Context){
            val assetManger: AssetManager = context.assets
            val files: Array<String>?
                files = assetManger.list(name)
            for (filenames in files){
                var inputStream: InputStream?
                var outputStream: OutputStream?
                try {
                    inputStream = assetManger.open(name+"/"+filenames)
                    outputStream = FileOutputStream(outPath+filenames)
                    copyFile(inputStream, outputStream)
                    inputStream.close()
                    inputStream?.let { inputStream = null }
                    outputStream.flush()
                    outputStream.close()
                    outputStream.let { outputStream = null }
                }
                catch (e : Exception){

                }
            }
        }

        fun copyAssetFile(name: String, path: String,outPath: String, context: Context){
            val assetManger: AssetManager = context.assets
            /*val files: Array<String>?
            files = assetManger.list(name)
            for (filenames in files){

            }*/
            var inputStream: InputStream?
            var outputStream: OutputStream?
            try {
                inputStream = assetManger.open(path+name)
                outputStream = FileOutputStream(outPath+"/"+name)
                copyFile(inputStream, outputStream)
                inputStream.close()
                inputStream?.let { inputStream = null }
                outputStream.flush()
                outputStream.close()
                outputStream.let { outputStream = null }
            }
            catch (e : Exception){

            }
        }

        private fun copyFile(inStream: InputStream, out: OutputStream) {
            val buffer = ByteArray(1024)
            while (inStream.read(buffer) != -1) {
                out.write(buffer, 0, inStream.read(buffer))
            }
        }

        fun loadJSONFromAsset(context: Context, path: String): String?{
            var json: String? = null
            try {
                val inputStream: InputStream = context.assets.open(path)
                val size: Int = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                json = String(buffer, Charset.forName("UTF-8"))
            }catch (ex: Exception){
                ex.printStackTrace()
                return null
            }
            return json
        }

        fun readFromFile(context: Context, path: String): String? {

            val yourFile = File(path)
            val stream = FileInputStream(yourFile)
            var jsonStr: String? = null
            try {
                val fc = stream.getChannel()
                val bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size())

                jsonStr = Charset.defaultCharset().decode(bb).toString()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                stream.close()
            }
            return jsonStr!!
        }
    }
}