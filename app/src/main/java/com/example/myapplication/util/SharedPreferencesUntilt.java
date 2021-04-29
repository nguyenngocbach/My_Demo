package com.example.myapplication.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesUntilt {
    public static void saveString(Context context,String preference, String data){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(preference, data);
    }

    public static void getString(Context context, String preference, String data) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.getString(preference,data);
    }

    public static void saveBoolean(Context context,String preference, boolean data){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(preference, data);
    }

    public static void getBoolean(Context context, String preference, boolean data) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.getBoolean(preference,data);
    }
}
