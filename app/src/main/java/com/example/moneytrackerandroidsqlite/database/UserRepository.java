package com.example.moneytrackerandroidsqlite.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.moneytrackerandroidsqlite.models.User;
import com.example.moneytrackerandroidsqlite.utils.SecurityUtils;


import org.mindrot.jbcrypt.BCrypt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserRepository {
    private final DBHelper dbHelper;
    private final SimpleDateFormat dateFormat;
    public UserRepository(Context context) {
        this.dbHelper = DBHelper.getInstance(context);
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    }
    public boolean createNewUser(String email, String username, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        values.put("email", email);
        values.put("username", username);
        values.put("password", hashedPassword);

        long id = db.insert("Users", null, values);
        db.close();
        return id != -1;
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
            int rowsAffected = db.update("Users", values, "id = ?", new String[]{String.valueOf(user.getId())});
            success = rowsAffected > 0;
        } catch (Exception e) {
            Log.e("UserRepo", "Error updating user: " + e.getMessage());
        }
        return success;
    }
    public User getUserById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        User user = null;
        Cursor cursor = db.query("Users", null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);
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
        boolean valid = false;
        valid = BCrypt.checkpw(password, user.getPassword());
        if (valid) {
            updateLastLogin(user.getId());
            return user;
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

    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Users", null, "username = ?", new String[]{username}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Users", null, "email = ?", new String[]{email}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
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

    public boolean changeUserPassword(long userId, String newHashedPassword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean success = false;
        try {
            ContentValues values = new ContentValues();
            values.put("password", newHashedPassword);
            int rowsAffected = db.update("Users", values, "id = ?", new String[]{String.valueOf(userId)});
            success = rowsAffected > 0;
        } catch (Exception e) {
            Log.e("UserRepo", "Error changing password: " + e.getMessage());
        } finally {
            db.close();
        }
        return success;
    }
}
