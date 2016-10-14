package com.whiterabbit.pisabike.storage;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class PrefsStorage {
    private static final String LAST_UPDATE_ID = "com.whiterabbit.lastupdate";


    private SharedPreferences mPreferences;

    public PrefsStorage(Context c) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(c);
    }

    public void setLastUpdate(long lastUpdate) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(LAST_UPDATE_ID, lastUpdate);
        editor.apply();
    }

    public long getLastUpdate() {
        return mPreferences.getLong(LAST_UPDATE_ID, 0);
    }
}

