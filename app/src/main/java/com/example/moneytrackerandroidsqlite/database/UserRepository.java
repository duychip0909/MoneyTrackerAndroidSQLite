package com.example.moneytrackerandroidsqlite.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.moneytrackerandroidsqlite.models.User;
import com.example.moneytrackerandroidsqlite.utils.SecurityUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserRepository {
    private DBHelper dbHelper;
    private SimpleDateFormat dateFormat;
    public UserRepository(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    }
    public long createNewUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = -1;
        try {
            ContentValues values = new ContentValues();
            values.put("username", user.getUsername());
            values.put("email", user.getEmail());
            values.put("password", user.getPassword());
            values.put("created_at", dateFormat.format(new Date()));

            id = db.insert("Users", null, values);
            if (id != -1) {
                user.setId(id);
            }
        } catch (Exception e) {
            Log.e("UserRepo", "Error inserting user: " + e.getMessage());
        }
        return id;
    }
    public boolean updateUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean success = false;
        try {
            ContentValues values = new ContentValues();
            values.put("username", user.getUsername());
            values.put("email", user.getEmail());
            if (user.getPassword() != null) {
                values.put("password", user.getPassword());
            }
            int rowsAffected = db.update("Users", values, "user_id = ?", new String[]{String.valueOf(user.getId())});
            success = rowsAffected > 0;
        } catch (Exception e) {
            Log.e("UserRepo", "Error updating user: " + e.getMessage());
        }
        return success;
    }
    public User getUserById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        User user = null;
        Cursor cursor = db.query("Users", null, "user_id = ?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }
        return user;
    }
    public User getUserByUsername(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        User user = null;
        try {
            Cursor cursor = db.query("Users", null, "username = ?", new String[]{username}, null, null, null);
            if (cursor.moveToFirst()) {
                user = cursorToUser(cursor);
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("UserRepo", "Error getting user by username: " + e.getMessage());
        }
        return user;
    }
    public User getUserByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        User user = null;
        try {
            Cursor cursor = db.query("Users", null, "email = ?", new String[]{email}, null, null, null);
            if (cursor.moveToFirst()) {
                user = cursorToUser(cursor);
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("UserRepo", "Error getting user by email: " + e.getMessage());
        }
        return user;
    }
    public User authenticate(String username, String password) {
        User user = getUserByUsername(username);
        if (user != null) {
            if (SecurityUtils.verifyPassword(password, user.getPassword())) {
                updateLastLogin(user.getId());
                return user;
            }
        }
        return null;
    }

    private void updateLastLogin(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("last_login", dateFormat.format(new Date()));
            db.update("Users", values, "id = ?", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.e("UserRepo", "Error updating last login: " + e.getMessage());
        }
    }

    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
        user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow("username")));
        user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
        user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("password")));
        try {
            String createdAtStr = cursor.getString(cursor.getColumnIndexOrThrow("created_at"));
            if (createdAtStr != null) {
                user.setCreatedAt(dateFormat.parse(createdAtStr));
            }

            String lastLoginStr = cursor.getString(cursor.getColumnIndexOrThrow("last_login"));
            if (lastLoginStr != null) {
                user.setLastLogin(dateFormat.parse(lastLoginStr));
            }
        } catch (ParseException e) {
            Log.e("UserRepo", "Error parsing date: " + e.getMessage());
        }
        return user;
    }
}
