package com.hari.autotasx;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Hari on 26-Mar-15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getName();

    // -- Parameters for Database creation
    private static final String DATABASE_NAME = "autotasx.DB";
    private static final int DATABASE_VERSION = 1;
    //DatabaseHelper dbHelper;
    ///private SQLiteDatabase database;

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Database create SQL command for LOCATION table
    private static final String LOCATION_CREATE = "CREATE TABLE LOCATION "
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, "
            + " LATITUDE REAL, LONGITUDE REAL, "
            + " RADIUS REAL , " + " SMS INTEGER DEFAULT 0, " + " WIFI INTEGER DEFAULT 0, " + " SILENT INTEGER DEFAULT 0, " + " REMINDER INTEGER DEFAULT 0, " + " MESSAGE TEXT NULL, " + " BLUETOOTH INTEGER DEFAULT 0," + " PARK INTEGER DEFAULT 0);";


    @Override
    public void onCreate(SQLiteDatabase database)
    {
        Log.i(TAG, "Creating databases");
        database.execSQL(LOCATION_CREATE);
    }

   //Upgrade Database on version change
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                + ", which will destroy all old data");

        db.execSQL("DROP TABLE IF EXISTS " + LOCATION_CREATE);
        onCreate(db);
    }


}
