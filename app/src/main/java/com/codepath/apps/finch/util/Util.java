package com.codepath.apps.finch.util;

import android.content.Context;
import android.text.format.DateUtils;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by nathansass on 8/3/16.
 */
public class Util {

    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String[] parts = relativeDate.split(" ");
        if (parts.length <= 1) {
            return relativeDate;
        }
        else {
            String dateToShow = parts[0] + parts[1].charAt(0);
            return dateToShow;
        }
    }

    public static void handleJsonFailure(JSONObject e) {
//        Log.d("DEBUG", e.toString());
        // was causing its own error
    }

    public static void handleJsonFailure(Context context,JSONObject e) {
        Toast.makeText(context, "Unable to retrieve more tweets :(", Toast.LENGTH_SHORT).show();
        handleJsonFailure(e);
    }

    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;

    }

}
