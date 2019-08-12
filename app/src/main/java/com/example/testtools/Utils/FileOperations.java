package com.example.testtools.Utils;

import android.content.Context;
import android.os.Environment;

import java.io.FileOutputStream;

public class FileOperations {
    private String filename;
    private StringBuilder sb;
    private Context context;

    public FileOperations(String filename, StringBuilder sb, Context context){
        this.filename = filename;
        this.sb = sb;
        this.context = context;
    }

    public void saveFile(){
        //Text of the Document
        String filenameExternal = filename;
        //Checking the availability state of the External Storage.
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            //If it isn't mounted - we can't write into it.
            return;
        }
        //Create a new file that points to the root directory, with the given name:
        java.io.File file = new java.io.File(context.getExternalFilesDir(null), filenameExternal);
        //This point and below is responsible for the write operation
        FileOutputStream outputStream = null;
        try {
            file.createNewFile();
            //second argument of FileOutputStream constructor indicates whether
            //to append or create new file if one exists
            outputStream = new FileOutputStream(file, true);

            outputStream.write(sb.toString().getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
