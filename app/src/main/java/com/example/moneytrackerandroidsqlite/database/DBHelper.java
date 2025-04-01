package com.example.moneytrackerandroidsqlite.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME="expense_tracker.db";
    private static final int DB_VERSION=1;
    private static DBHelper instance;

    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void insertDefaultCategories(SQLiteDatabase db) {

    }

    private void insertCategory(SQLiteDatabase db, Integer userId, String name, String type, int isDefault) {
        String sql = "INSERT INTO Categories (userId, name, type, isDefault) VALUES (?, ?, ?, ?, ?)";
        Object[] bindArgs = new Object[]{userId, name, type, isDefault};
        db.execSQL(sql, bindArgs);
    }
}
