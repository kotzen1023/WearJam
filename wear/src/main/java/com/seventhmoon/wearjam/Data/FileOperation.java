package com.seventhmoon.wearjam.Data;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class FileOperation {
    private static final String TAG = FileOperation.class.getName();

    public static File RootDirectory = new File("/");

    public static boolean init_wearjam_folder() {
        Log.i(TAG, "init_wearjam_folder() --- start ---");
        boolean ret = true;
        //RootDirectory = null;

        //path = new File("/");
        //RootDirectory = new File("/");
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //path = Environment.getExternalStorageDirectory();
            RootDirectory = Environment.getExternalStorageDirectory();
        }

        File folder_tennis = new File(RootDirectory.getAbsolutePath() + "/.wearJam/");

        if(!folder_tennis.exists()) {
            Log.i(TAG, "folder not exist");
            ret = folder_tennis.mkdirs();
            if (!ret)
                Log.e(TAG, "init_folder_and_files: failed to mkdir hidden");
            try {
                ret = folder_tennis.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!ret)
                Log.e(TAG, "init_info: failed to create hidden file");
        }

        while(true) {
            if(folder_tennis.exists())
                break;
        }

        Log.i(TAG, "init_wearjam_folder() ---  end  ---");
        return ret;
    }

    public static boolean check_record_exist(String fileName) {
        Log.i(TAG, "check_record_exist --- start ---");
        boolean ret = false;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //path = Environment.getExternalStorageDirectory();
            RootDirectory = Environment.getExternalStorageDirectory();
        }

        File file = new File(RootDirectory.getAbsolutePath() + "/.wearJam/"+fileName);

        if(file.exists()) {
            Log.i(TAG, "file exist");
            ret = true;
        }

        Log.i(TAG, "check_record_exist --- end ---");

        return ret;
    }

    public static boolean check_file_exist(String filePath) {
        Log.i(TAG, "check_file_exist --- start ---");
        boolean ret = false;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //path = Environment.getExternalStorageDirectory();
            RootDirectory = Environment.getExternalStorageDirectory();
        }

        File file = new File(filePath);

        if(file.exists()) {
            Log.i(TAG, "file exist");
            ret = true;
        }
        Log.i(TAG, "check_file_exist --- end ---");

        return ret;
    }

    public static String read_record(String fileName) {


        Log.i(TAG, "read_record() --- start ---");
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //path = Environment.getExternalStorageDirectory();
            RootDirectory = Environment.getExternalStorageDirectory();
        }

        File file = new File(RootDirectory.getAbsolutePath() + "/.wearJam/"+fileName);
        String message = "";

        //photo
        if (!file.exists())
        {
            Log.i(TAG, "read_record() "+file.getAbsolutePath()+ " not exist");

            return "";
        }
        else {
            try {

                FileReader fr = new FileReader(file.getAbsolutePath());
                BufferedReader br = new BufferedReader(fr);
                while (br.ready()) {

                    message = br.readLine();

                }
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "message = "+message);

            Log.i(TAG, "read_record() --- end ---");
        }


        return message;
    }

    public static int copyInputStreamToFile(InputStream in, String fileName) {
        OutputStream out = null;
        int ret = -1;
        boolean exist;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //path = Environment.getExternalStorageDirectory();
            RootDirectory = Environment.getExternalStorageDirectory();
        }

        File folder_wear = new File(RootDirectory.getAbsolutePath() + "/Music/WearJam/");

        //check dir if exist
        if(!folder_wear.exists()) {
            Log.i(TAG, "folder not exist");
            exist = folder_wear.mkdirs();
            if (!exist)
                Log.e(TAG, "folder_wear: failed to mkdir ");
            try {
                exist = folder_wear.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!exist)
                Log.e(TAG, "folder_wear: failed to create hidden file");
        }

        while(true) {
            if(folder_wear.exists())
                break;
        }


        File file = new File(RootDirectory.getAbsolutePath() + "/Music/WearJam/"+fileName);






        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            // Ensure that the InputStreams are closed even if there's an exception.
            try {
                if ( out != null ) {
                    out.close();
                }
                // If you want to close the "in" InputStream yourself then remove this
                // from here but ensure that you close it yourself eventually.
                in.close();

                Log.e(TAG, "save file size = "+file.length());

                ret = 0;
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
        }

        return ret;
    }
}
