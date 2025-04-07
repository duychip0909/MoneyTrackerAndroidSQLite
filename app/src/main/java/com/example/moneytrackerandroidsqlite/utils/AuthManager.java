package com.example.moneytrackerandroidsqlite.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.moneytrackerandroidsqlite.database.UserRepository;
import com.example.moneytrackerandroidsqlite.models.User;

public class AuthManager {
    private final SharedPreferences preferences;
    private static AuthManager instance = null;
    private final UserRepository userRepository;
    private User currentUser;
    private AuthManager(Context context) {
        preferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
        userRepository = new UserRepository(context);
    }
    public static synchronized AuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new AuthManager(context);
        }
        return instance;
    }
    public boolean isLoggedIn() {
        return preferences.contains("user_id");
    }
    public void setLoggedInUser(User user) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("user_id", user.getId());
        editor.putString("username", user.getUsername());
        editor.apply();
        editor.commit();
        this.currentUser = user;
    }
    public void logout() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        currentUser = null;
    }
    public User getCurrentUser() {
        if (currentUser == null && isLoggedIn()) {
            long userId = preferences.getLong("user_id", -1);
            currentUser = userRepository.getUserById(userId);
        }
        return currentUser;
    }
}
