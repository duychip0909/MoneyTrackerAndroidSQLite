package com.example.moneytrackerandroidsqlite.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import com.example.moneytrackerandroidsqlite.utils.AuthManager;

import java.util.ArrayList;
import java.util.List;

public class BudgetTransactionRepository {
    DBHelper dbHelper;
    AuthManager authManager;
    public BudgetTransactionRepository(Context context) {
        dbHelper = DBHelper.getInstance(context);
        authManager = AuthManager.getInstance(context);
    }
    public boolean associateTransactionWithBudget(long budgetId, long transactionId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("budget_id", budgetId);
        values.put("transaction_id", transactionId);

        try {
            return db.insert("BudgetTransactions", null, values) != -1;
        } catch (SQLiteConstraintException e) {
            // This happens if the association already exists or if foreign keys don't exist
            return false;
        }
    }
    public List<Long> getTransactionIdsByBudgetId(long budgetId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Long> transactionIds = new ArrayList<>();
        Cursor cursor = db.query(
                "BudgetTransactions",
                new String[]{"transaction_id"},
                "budget_id = ?",
                new String[]{String.valueOf(budgetId)},
                null,
                null,
                null
        );
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            transactionIds.add(cursor.getLong(0));
        }
        cursor.close();
        return transactionIds;
    }
    public List<Long> getBudgetIdsByTransactionId(long transactionId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Long> budgetIds = new ArrayList<>();

        Cursor cursor = db.query(
                "BudgetTransactions",
                new String[]{"budget_id"},
                "transaction_id = ?",
                new String[]{String.valueOf(transactionId)},
                null,
                null,
                null
        );
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            budgetIds.add(cursor.getLong(0));
        }
        cursor.close();
        return budgetIds;
    }
    public int removeTransactionFromBudget(long budgetId, long transactionId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(
                "BudgetTransactions",
                "budget_id = ? AND transaction_id = ?",
                new String[]{String.valueOf(budgetId), String.valueOf(transactionId)}
        );
    }
    public int removeAllTransactionsFromBudget(long budgetId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(
                "BudgetTransactions",
                "budget_id = ?",
                new String[]{String.valueOf(budgetId)}
        );
    }
    public int removeTransactionFromAllBudgets(long transactionId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(
                "BudgetTransactions",
                "transaction_id = ?",
                new String[]{String.valueOf(transactionId)}
        );
    }
    public boolean isTransactionAssociatedWithBudget(long budgetId, long transactionId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                "BudgetTransactions",
                new String[]{"budget_id"},
                "budget_id = ? AND transaction_id = ?",
                new String[]{String.valueOf(budgetId), String.valueOf(transactionId)},
                null,
                null,
                null
        );

        boolean exists = false;
        if (cursor != null) {
            exists = cursor.getCount() > 0;
            cursor.close();
        }

        return exists;
    }
}
