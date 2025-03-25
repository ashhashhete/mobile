package com.igenesys.utils;

import android.app.Activity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

public class AppLog {

    private static String TAG = Constants.appName;
    public static String logFileName = null;

    public static void d(String msg) {
        Log.d(TAG, msg);
    }

    public static void e(String msg) {
        // Report_150424_134443
        Log.e(TAG, msg == null ? "" : msg);
    }

    public static void logData(Activity activity, String logMessage) {

        try {
            if (logMessage == null)
                logMessage = "Null Log";

            File logFile = new File(activity.getExternalFilesDir("logs"), logFileName);

            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                buf.append(Utils.convertDateToString(new Date()) + " : " + activity.getLocalClassName() + " -> " + logMessage);
                buf.newLine();
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            AppLog.e(ex.getMessage());
        }
    }

    public static void logCustomData(Activity activity, String fileName, String logMessage) {

        try {

            if (logMessage == null)
                logMessage = "Null Log";

            File logFile = new File(activity.getExternalFilesDir("logs"), fileName + ".txt");

            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            logMessage = Utils.getCurrentDate("yyyyMMddHHmmss") + " :: " + logMessage;

            try {
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                buf.append(logMessage);
                buf.newLine();
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            AppLog.e(ex.getMessage());
        }
    }

    public static void retrieveAppLogs(Activity activity, String logFileName, String directoryName, String extraLogs, boolean isLogcatRequired) {

        try {

            if (isLogcatRequired) {

                StringBuilder log = new StringBuilder();
                Process process = Runtime.getRuntime().exec("logcat -d -v time *:V");
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.contains(activity.getPackageName())) {
                        log.append(line);
                        log.append("\n");
                    }
                }

                logDataWhole(activity, logFileName, directoryName, extraLogs + "\n\n\n" + log);
            } else {
                logDataWhole(activity, logFileName, directoryName, extraLogs);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error retrieving app logs: " + e.getMessage());
        }
    }

    public static void logDataWhole(Activity activity, String logFileName, String directoryName, String logMessage) {

        try {

            File logFileMain = new File(activity.getExternalFilesDir("logs"), directoryName);

            if (!logFileMain.exists())
                logFileMain.mkdirs();

            File logFile = new File(logFileMain, logFileName);

            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                buf.append(Utils.convertDateToString(new Date()) + " : " + activity.getLocalClassName() + " -> " + logMessage);
                buf.newLine();
                buf.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (Exception ex) {
            AppLog.e(ex.getMessage());
        }
    }
}