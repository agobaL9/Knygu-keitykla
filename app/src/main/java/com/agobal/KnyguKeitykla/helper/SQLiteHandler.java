package com.agobal.KnyguKeitykla.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 11;

    // Database Name
    private static final String DATABASE_NAME = "android_api";
    //test
    // Login table name
    private static final String TABLE_USER = "user";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_USERNAME = "userName";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";

    private static final String KEY_CITY = "city";
    private static final String KEY_FIRSTNAME = "firstName";
    private static final String KEY_LASTNAME = "lastName";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USERNAME+ " TEXT," +  KEY_EMAIL + " TEXT UNIQUE,"+ KEY_FIRSTNAME+ " TEXT," + KEY_LASTNAME + " TEXT," + KEY_CITY + " TEXT," + KEY_UID + " TEXT," + KEY_CREATED_AT + " TEXT" + ")";
        //crete table user(id INTEGER PRIMARY KEY,userName lala,email text, uid text,created text)
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    //public void addUser(String userName, String email, String uid, String created_at) {
    public void addUser(String uid, String userName, String email, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_UID, uid);
        values.put(KEY_USERNAME, userName);
        values.put(KEY_EMAIL, email);
        values.put(KEY_CREATED_AT, created_at);

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values); // err
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id +" "+ userName +" "+ email +" "+ uid);
    }
    public void addUserData(String firstName, String lastName, String city, String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FIRSTNAME, firstName); //
        values.put(KEY_LASTNAME, lastName); //
        values.put(KEY_CITY, city); //

        // updating Row
        //long id = db.update(TABLE_USER, null, values);
        long id = db.update(TABLE_USER, values, String.format("%s = ?", "email"),
                new String[]{email});


        db.close(); // Closing database connection
        Log.d(TAG, "New user DATA inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("userName", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("firstName", cursor.getString(3));
            user.put("lastName", cursor.getString(4));
            user.put("city", cursor.getString(5));
            user.put("uid", cursor.getString(6));
            user.put("created_at", cursor.getString(7));

        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

}