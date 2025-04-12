package com.example.moneytrackerandroidsqlite.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.moneytrackerandroidsqlite.models.Budget;
import com.example.moneytrackerandroidsqlite.utils.AuthManager;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BudgetRepository {
    DBHelper dbHelper;
    AuthManager authManager;
    SimpleDateFormat dateFormat;
    public BudgetRepository(Context context) {
        dbHelper = DBHelper.getInstance(context);
        authManager = AuthManager.getInstance(context);
        dateFormat = new SimpleDateFormat("DD-MM-YYYY", Locale.getDefault());
    }
    public long createBudget(Budget budget) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", budget.getUserId());
        values.put("category_id", budget.getCategoryId());
        values.put("amount", budget.getAmount());
        values.put("period_type", budget.getPeriodType().toString());
        values.put("start_date", budget.getStartDate());
        values.put("end_date", budget.getEndDate());
        return db.insert("Budgets", null, values);
    }
    public Budget getBudgetById(long budgetId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                "Budgets",
                null,
                "id = ?",
                new String[]{String.valueOf(budgetId)},
                null,
                null,
                null
        );

        Budget budget = null;
        if (cursor != null && cursor.moveToFirst()) {
            budget = cursorToBudget(cursor);
            cursor.close();
        }
        return budget;
    }

    public List<Budget> getBudgetsByUserId(long userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Budget> budgets = new ArrayList<>();
        String query = "SELECT b.*, c.name as category_name " +
                "FROM Budgets b " +
                "JOIN Categories c ON b.category_id = c.id " +
                "WHERE b.id = ?";
        Cursor cursor = db.rawQuery(query,
                new String[]{String.valueOf(userId)}
        );
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            budgets.add(cursorToBudget(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return budgets;
    }
    public List<Budget> getActiveBudgetsByCategory(long userId, long categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Budget> budgets = new ArrayList<>();
        long currentTime = System.currentTimeMillis();

        Cursor cursor = db.query(
                "Budgets",
                null,
                "user_id = ? AND category_id = ? AND start_date <= ? AND end_date >= ?",
                new String[]{
                        String.valueOf(userId),
                        String.valueOf(categoryId),
                        String.valueOf(currentTime),
                        String.valueOf(currentTime)
                },
                null,
                null,
                null
        );
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            budgets.add(cursorToBudget(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return budgets;
    }

    public int updateBudget(Budget budget) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("category_id", budget.getCategoryId());
        values.put("amount", budget.getAmount());
        values.put("period_type", budget.getPeriodType().toString());
        values.put("start_date", budget.getStartDate());
        values.put("end_date", budget.getEndDate());
        values.put("updated_at", System.currentTimeMillis());

        return db.update(
                "Budgets",
                values,
                "id = ? AND user_id = ?",
                new String[]{String.valueOf(budget.getId()), String.valueOf(budget.getUserId())}
        );
    }

    public int deleteBudget(long budgetId, long userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(
                "Budgets",
                "id = ? AND user_id = ?",
                new String[]{String.valueOf(budgetId), String.valueOf(userId)}
        );
    }

    private Budget cursorToBudget(Cursor cursor) {
        Budget budget = new Budget();
        budget.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
        budget.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow("user_id")));
        budget.setCategoryId(cursor.getLong(cursor.getColumnIndexOrThrow("category_id")));
        budget.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));

        String periodType = cursor.getString(cursor.getColumnIndexOrThrow("period_type"));
        budget.setPeriodType(Budget.PeriodType.valueOf(periodType));

        String startDateStr = cursor.getString(cursor.getColumnIndexOrThrow("start_date"));
        budget.setStartDate(Long.parseLong(startDateStr));

        String endDate = cursor.getString(cursor.getColumnIndexOrThrow("end_date"));
        budget.setEndDate(Long.parseLong(endDate));

        budget.setCategoryName(cursor.getString(cursor.getColumnIndexOrThrow("category_name")));

        return budget;
    }
}
