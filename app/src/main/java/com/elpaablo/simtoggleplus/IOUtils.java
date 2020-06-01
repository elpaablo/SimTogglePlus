package com.elpaablo.simtoggleplus;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

final class IOUtils {

    private final static String TAG = "ChargingLimiter.IOUtils";

    public static String readFromFile(Context context, String pathToFile) {

        BufferedReader buffered_reader=null;
        try
        {
            buffered_reader = new BufferedReader(new FileReader(pathToFile));
            String line;
            StringBuilder ret = new StringBuilder();

            while ((line = buffered_reader.readLine()) != null)
            {
                ret.append(line);
            }
            return ret.toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return "";
        }
        finally
        {
            try
            {
                if (buffered_reader != null)
                    buffered_reader.close();
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    public static boolean writeToFile(Context context, String pathToFile, String data) {

        try {
            File file = new File(pathToFile);
            file.setWritable(true, false);

            if (!file.exists()) {
                //Log.e(TAG, "File not found: " + pathToFile);
                return false;
            }
            FileWriter writer = new FileWriter(file);
            writer.write(data);
            writer.flush();
            writer.close();
            file.setWritable(true, true);
            return true;
        } catch (IOException e) {
            //Log.e(TAG, "Can not write to file: " + e.toString());
            return false;
        }
    }
}
