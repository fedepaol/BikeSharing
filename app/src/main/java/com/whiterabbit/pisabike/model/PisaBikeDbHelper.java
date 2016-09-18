/**********************************************************************************************************************************************************************
****** AUTO GENERATED FILE BY ANDROID SQLITE HELPER SCRIPT BY FEDERICO PAOLINELLI. ANY CHANGE WILL BE WIPED OUT IF THE SCRIPT IS PROCESSED AGAIN. *******
**********************************************************************************************************************************************************************/
package com.whiterabbit.pisabike.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import java.util.Date;

public class PisaBikeDbHelper {
    private static final String TAG = "PisaBike";

    private static final String DATABASE_NAME = "PisaBike.db";
    private static final int DATABASE_VERSION = 1;


    // Variable to hold the database instance
    protected SQLiteDatabase mDb;
    // Context of the application using the database.
    private final Context mContext;
    // Database open/upgrade helper
    private DbHelper mDbHelper;
    
    public PisaBikeDbHelper(Context context) {
        mContext = context;
        mDbHelper = new DbHelper(mContext, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    public PisaBikeDbHelper open() throws SQLException { 
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
                                                     
    public void close() {
        mDb.close();
    }

    public static final String ROW_ID = "_id";

    
    // -------------- STATION DEFINITIONS ------------
    public static final String STATION_TABLE = "Station";
    
    public static final String STATION_NAME_COLUMN = "name";
    public static final int STATION_NAME_COLUMN_POSITION = 1;
    
    public static final String STATION_CITY_COLUMN = "city";
    public static final int STATION_CITY_COLUMN_POSITION = 2;
    
    public static final String STATION_LATITUDE_COLUMN = "latitude";
    public static final int STATION_LATITUDE_COLUMN_POSITION = 3;
    
    public static final String STATION_LONGITUDE_COLUMN = "longitude";
    public static final int STATION_LONGITUDE_COLUMN_POSITION = 4;
    
    public static final String STATION_ADDRESS_COLUMN = "address";
    public static final int STATION_ADDRESS_COLUMN_POSITION = 5;
    
    public static final String STATION_AVAILABLE_COLUMN = "available";
    public static final int STATION_AVAILABLE_COLUMN_POSITION = 6;
    
    public static final String STATION_FREE_COLUMN = "free";
    public static final int STATION_FREE_COLUMN_POSITION = 7;
    
    public static final String STATION_BROKEN_COLUMN = "broken";
    public static final int STATION_BROKEN_COLUMN_POSITION = 8;
    
    public static final String STATION_LASTUPDATE_COLUMN = "lastUpdate";
    public static final int STATION_LASTUPDATE_COLUMN_POSITION = 9;
    
    public static final String STATION_FAVORITE_COLUMN = "favorite";
    public static final int STATION_FAVORITE_COLUMN_POSITION = 10;
    
    


    // -------- TABLES CREATION ----------

    
    // Station CREATION 
    private static final String DATABASE_STATION_CREATE = "create table " + STATION_TABLE + " (" +
                                "_id integer primary key autoincrement, " +
                                STATION_NAME_COLUMN + " text, " +
                                STATION_CITY_COLUMN + " text, " +
                                STATION_LATITUDE_COLUMN + " real, " +
                                STATION_LONGITUDE_COLUMN + " real, " +
                                STATION_ADDRESS_COLUMN + " text, " +
                                STATION_AVAILABLE_COLUMN + " integer, " +
                                STATION_FREE_COLUMN + " integer, " +
                                STATION_BROKEN_COLUMN + " integer, " +
                                STATION_LASTUPDATE_COLUMN + " integer, " +
                                STATION_FAVORITE_COLUMN + " integer" +
                                ")";
    

    
    // ----------------Station HELPERS -------------------- 
    public long addStation (String name, String city, double latitude, double longitude, String address, long available, long free, long broken, Date lastUpdate, int favorite) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(STATION_NAME_COLUMN, name);
        contentValues.put(STATION_CITY_COLUMN, city);
        contentValues.put(STATION_LATITUDE_COLUMN, latitude);
        contentValues.put(STATION_LONGITUDE_COLUMN, longitude);
        contentValues.put(STATION_ADDRESS_COLUMN, address);
        contentValues.put(STATION_AVAILABLE_COLUMN, available);
        contentValues.put(STATION_FREE_COLUMN, free);
        contentValues.put(STATION_BROKEN_COLUMN, broken);
        contentValues.put(STATION_LASTUPDATE_COLUMN, lastUpdate.getTime());
        contentValues.put(STATION_FAVORITE_COLUMN, favorite);
        return mDb.insert(STATION_TABLE, null, contentValues);
    }

    public long updateStation (long rowIndex,String name, String city, double latitude, double longitude, String address, long available, long free, long broken, Date lastUpdate, int favorite) {
        String where = ROW_ID + " = " + rowIndex;
        ContentValues contentValues = new ContentValues();
        contentValues.put(STATION_NAME_COLUMN, name);
        contentValues.put(STATION_CITY_COLUMN, city);
        contentValues.put(STATION_LATITUDE_COLUMN, latitude);
        contentValues.put(STATION_LONGITUDE_COLUMN, longitude);
        contentValues.put(STATION_ADDRESS_COLUMN, address);
        contentValues.put(STATION_AVAILABLE_COLUMN, available);
        contentValues.put(STATION_FREE_COLUMN, free);
        contentValues.put(STATION_BROKEN_COLUMN, broken);
        contentValues.put(STATION_LASTUPDATE_COLUMN, lastUpdate.getTime());
        contentValues.put(STATION_FAVORITE_COLUMN, favorite);
        return mDb.update(STATION_TABLE, contentValues, where, null);
    }

    public boolean removeStation(long rowIndex){
        return mDb.delete(STATION_TABLE, ROW_ID + " = " + rowIndex, null) > 0;
    }

    public boolean removeAllStation(){
        return mDb.delete(STATION_TABLE, null, null) > 0;
    }

    public Cursor getAllStation(){
    	return mDb.query(STATION_TABLE, new String[] {
                         ROW_ID,
                         STATION_NAME_COLUMN,
                         STATION_CITY_COLUMN,
                         STATION_LATITUDE_COLUMN,
                         STATION_LONGITUDE_COLUMN,
                         STATION_ADDRESS_COLUMN,
                         STATION_AVAILABLE_COLUMN,
                         STATION_FREE_COLUMN,
                         STATION_BROKEN_COLUMN,
                         STATION_LASTUPDATE_COLUMN,
                         STATION_FAVORITE_COLUMN
                         }, null, null, null, null, null);
    }

    public Cursor getStation(long rowIndex) {
        Cursor res = mDb.query(STATION_TABLE, new String[] {
                                ROW_ID,
                                STATION_NAME_COLUMN,
                                STATION_CITY_COLUMN,
                                STATION_LATITUDE_COLUMN,
                                STATION_LONGITUDE_COLUMN,
                                STATION_ADDRESS_COLUMN,
                                STATION_AVAILABLE_COLUMN,
                                STATION_FREE_COLUMN,
                                STATION_BROKEN_COLUMN,
                                STATION_LASTUPDATE_COLUMN,
                                STATION_FAVORITE_COLUMN
                                }, ROW_ID + " = " + rowIndex, null, null, null, null);

        if(res != null){
            res.moveToFirst();
        }
        return res;
    }
    

    private static class DbHelper extends SQLiteOpenHelper {
        public DbHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        // Called when no database exists in disk and the helper class needs
        // to create a new one. 
        @Override
        public void onCreate(SQLiteDatabase db) {      
            db.execSQL(DATABASE_STATION_CREATE);
            
        }

        // Called when there is a database version mismatch meaning that the version
        // of the database on disk needs to be upgraded to the current version.
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Log the version upgrade.
            Log.w(TAG, "Upgrading from version " + 
                        oldVersion + " to " +
                        newVersion + ", which will destroy all old data");
            
            // Upgrade the existing database to conform to the new version. Multiple 
            // previous versions can be handled by comparing _oldVersion and _newVersion
            // values.

            // The simplest case is to drop the old table and create a new one.
            db.execSQL("DROP TABLE IF EXISTS " + STATION_TABLE + ";");
            
            // Create a new one.
            onCreate(db);
        }
    }
}

