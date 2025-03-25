package com.igenesys.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.google.gson.Gson;
import com.igenesys.model.EsriConfigModel;
import com.igenesys.model.WorkAreaModel;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SharedPreferencesHandler {
    String masterKeyAlias;
    Activity activity;
    Context context;
    SharedPreferences sharedpref;

    {
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    public SharedPreferencesHandler(Activity activity) {
        try {
            sharedpref = EncryptedSharedPreferences.create(
                    Constants.appName,
                    masterKeyAlias,
                    activity,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        this.activity = activity;
    }

    public SharedPreferencesHandler(Context context) {
        this.context = context;
        try {
            sharedpref = EncryptedSharedPreferences.create(
                    Constants.appName,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedpref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void putIntString(String key, int value) {
        SharedPreferences.Editor editor = sharedpref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void putDoubString(String key, double value) {
        SharedPreferences.Editor editor = sharedpref.edit();
        editor.putString(key, "" + value);
        editor.apply();
    }

    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedpref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void ClearPreferences() {
        SharedPreferences.Editor editor = sharedpref.edit();
        editor.clear();
        editor.commit();
    }

    public String getString(String key) {
        return sharedpref.getString(key, Constants.notAvaiable);
    }

    public String getString(String key, String defaultValue) {
        return sharedpref.getString(key, defaultValue);
    }

    public int getIntString(String key, int value) {
        return sharedpref.getInt(key, value);
    }

    public boolean getBoolean(String key) {
        return sharedpref.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultVal) {
        return sharedpref.getBoolean(key, defaultVal);
    }

    public boolean containKey(String key) {
        return sharedpref.contains(key);
    }

    public WorkAreaModel getWorkArea(String key) {
        SharedPreferences.Editor editor = sharedpref.edit();
        Gson gson = new Gson();
        String json = sharedpref.getString("MyObject", "");
        WorkAreaModel obj = gson.fromJson(json, WorkAreaModel.class);
        return obj;
    }
    public void putWorkArea(String key, WorkAreaModel workAreaModel) {
        SharedPreferences.Editor editor = sharedpref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(workAreaModel);
        editor.putString("MyObject", json);
        editor.apply();
    }

    public EsriConfigModel getEsriConfig() {

        if(sharedpref.contains("EsriConfigData")) {

            String encData = sharedpref.getString("EsriConfigData", "");

            if(!TextUtils.isEmpty(encData)) {
                String decConfigData = AES.decrypt(encData);
                return new Gson().fromJson(decConfigData, EsriConfigModel.class);
            }
        }

        return null;
    }

    public void setEsriConfig(String esriConfigModel) {
        SharedPreferences.Editor editor = sharedpref.edit();
        editor.putString("EsriConfigData", esriConfigModel);
        editor.apply();
    }

    public void setEsriToken(String esriToken){
        SharedPreferences.Editor editor = sharedpref.edit();
        editor.putString("EsriToken", esriToken);
        editor.apply();
    }

    public String getEsriToken() {

        if(sharedpref.contains("EsriToken")) {
            return sharedpref.getString("EsriToken", "");
        }

        return null;
    }


    public void setShortNameJson(String jsonString){
        SharedPreferences.Editor editor = sharedpref.edit();
        editor.putString("shortname", jsonString);
        editor.apply();
    }

    public String getShortNameJson() {

        if(sharedpref.contains("shortname")) {
            return sharedpref.getString("shortname", "");
        }

        return null;
    }

    public void setDRPOfficerDetails(String jsonString) {
        SharedPreferences.Editor editor = sharedpref.edit();
        editor.putString("drp_officer_details", jsonString);
        editor.apply();
    }

    public String getDRPOfficerDetails() {

        if (sharedpref.contains("drp_officer_details")) {
            return sharedpref.getString("drp_officer_details", "");
        }

        return null;
    }
}