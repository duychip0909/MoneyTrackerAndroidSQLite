package com.example.moneytrackerandroidsqlite.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.moneytrackerandroidsqlite.models.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {
    private DBHelper dbHelper;
    public CategoryRepository(Context context) {
        this.dbHelper = DBHelper.getInstance(context);
    }
    public long createCategory(Category category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = -1;
        ContentValues values = new ContentValues();
        values.put("user_id", category.getUserId());
        values.put("name", category.getName());
        values.put("type", category.getType().toString());
        values.put("is_default", category.isDefault() ? 1 : 0);
        id = db.insert("Categories", null, values);
        if (id != -1) {
            category.setId(id);
        }
        return id;
    }
    public boolean updateCategory(Category category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean success = false;
        ContentValues values = new ContentValues();
        values.put("name", category.getName());
        values.put("type", category.getType().toString());
        int rowsAffected = db.update("Categories", values, "id = ?", new String[]{String.valueOf(category.getId())});
        success = rowsAffected > 0;
        return success;
    }
    public boolean deleteCategory(long categoryId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean success = false;
        Category category = getCategoryById(categoryId);
        if (category != null && category.isDefault()) {
            return false;
        }
        int rowsAffected = db.delete("Categories", "id = ?", new String[]{String.valueOf(categoryId)});
        success = rowsAffected > 0;
        return success;
    }

    public Category getCategoryById(long categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Category category = null;
        Cursor cursor = db.query("Categories", null, "id = ?", new String[]{String.valueOf(categoryId)}, null, null, null);
        if (cursor.moveToFirst()) {
            category = cursorToCategory(cursor);
            cursor.close();
        }
        return category;
    }
    public List<Category> getAllCategory(Long userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Category> categories = new ArrayList<>();
        Cursor cursor = db.query("Categories", null, "user_id IS NULL OR user_id = ?", new String[]{String.valueOf(userId)}, null, null, "type ASC, name ASC");
        while (cursor.moveToNext()) {
            categories.add(cursorToCategory(cursor));
        }
        cursor.close();
        return categories;
    }
    public List<Category> getCategoriesByType(Long userId, Category.Type type) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Category> categories = new ArrayList<>();
        Cursor cursor = db.query("Categories", null, "type = ? AND (user_id IS NULL OR user_id = ?)", new String[]{type.toString(), String.valueOf(userId)}, null, null, "name ASC");
        while (cursor.moveToNext()) {
            categories.add(cursorToCategory(cursor));
        }
        cursor.close();
        return categories;
    }

    private Category cursorToCategory(Cursor cursor) {
        Category category = new Category();
        category.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
        int userIdIndex = cursor.getColumnIndexOrThrow("user_id");
        if (!cursor.isNull(userIdIndex)) {
            category.setUserId(cursor.getLong(userIdIndex));
        }
        category.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
        String typeStr = cursor.getString(cursor.getColumnIndexOrThrow("type"));
        category.setType(Category.Type.valueOf(typeStr));
        int isDefault = cursor.getInt(cursor.getColumnIndexOrThrow("is_default"));
        category.setDefault(isDefault == 1);
        return category;
    }

}
