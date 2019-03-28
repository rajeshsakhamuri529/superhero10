package com.blobcity

import android.content.Context
import android.content.res.AssetManager
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception

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
    }
}