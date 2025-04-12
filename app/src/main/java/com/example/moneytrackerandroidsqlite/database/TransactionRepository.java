package com.example.moneytrackerandroidsqlite.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.moneytrackerandroidsqlite.models.Category;
import com.example.moneytrackerandroidsqlite.models.Transaction;
import com.example.moneytrackerandroidsqlite.utils.AuthManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionRepository {
    private final DBHelper dbHelper;
    private final AuthManager authManager;
    private final SimpleDateFormat simpleDateFormat;
    public TransactionRepository(Context context) {
        dbHelper = DBHelper.getInstance(context);
        authManager = AuthManager.getInstance(context);
        simpleDateFormat = new SimpleDateFormat("DD-MM-YYYY", Locale.getDefault());
    }
    public boolean addTransaction(long userId, long categoryId, double amount,
                                  String type, String notes, long dateMillis) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("category_id", categoryId);
        values.put("amount", amount);
        values.put("type", type);
        values.put("notes", notes);
        values.put("date", dateMillis);


        long result = db.insert("Transactions", null, values);
        return result != -1;
    }
    public boolean updateTransaction(long txId, long categoryId, double amount,
                                     String type, String notes, long dateMillis) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean success = false;
        ContentValues values = new ContentValues();
        values.put("category_id", categoryId);
        values.put("amount", amount);
        values.put("type", type);
        values.put("notes", notes);
        values.put("date", dateMillis);
        int affectedRows = db.update("Transactions", values, "id = ?", new String[]{String.valueOf(txId)});
        success = affectedRows > 0;
        return success;
    }
    public boolean deleteTransaction(long tId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean success = false;
        int affectedRows = db.delete("Transactions", "id = ?", new String[]{String.valueOf(tId)});
        success = affectedRows > 0;
        return success;
    }
    public Transaction getTxById(long uId, long tId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Transaction transaction = null;
        String query = "SELECT t.*, c.name as category_name " +
                "FROM Transactions t " +
                "JOIN Categories c ON t.category_id = c.id " +
                "WHERE t.id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(tId)});
        if (cursor.moveToFirst()) {
            transaction = cursorToTx(cursor);
        }
        cursor.close();
        return transaction;
    }
    public List<Transaction> getAllTx(long uId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Transaction> txs = new ArrayList<>();
        String query = "SELECT t.*, c.name as category_name " +
                "FROM Transactions t " +
                "JOIN Categories c ON t.category_id = c.id " +
                "WHERE t.user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(uId)});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Transaction tx = cursorToTx(cursor);
            txs.add(tx);
            cursor.moveToNext();
        }
        cursor.close();
        return txs;
    }

    public List<Transaction> getNearestTransactions(String targetDate, int limit, long uid) {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT t.*, c.name as category_name " +
                "FROM Transactions t " +
                "JOIN Categories c ON t.category_id = c.id " +
                "WHERE t.user_id = ? " +
                "ORDER BY ABS(julianday(t.date) - julianday(?)), t.date DESC " +
                "LIMIT ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(uid), targetDate, String.valueOf(limit)});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Transaction transaction = cursorToTx(cursor);
            transactions.add(transaction);
            cursor.moveToNext();
        }
        cursor.close();
        return transactions;
    }

    private Transaction cursorToTx(Cursor cursor) {
        Transaction transaction = new Transaction();
        transaction.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        int userIdIndex = cursor.getColumnIndexOrThrow("user_id");
        if (!cursor.isNull(userIdIndex)) {
            transaction.setUserId(cursor.getInt(userIdIndex));
        }
        transaction.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow("category_id")));
        transaction.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
        transaction.setNotes(cursor.getString(cursor.getColumnIndexOrThrow("notes")));
        String typeStr = cursor.getString(cursor.getColumnIndexOrThrow("type"));
        transaction.setType(Transaction.Type.valueOf(typeStr));

        String dateStr = cursor.getString(cursor.getColumnIndexOrThrow("date"));
        String createdAtStr = cursor.getString(cursor.getColumnIndexOrThrow("created_at"));
        String updatedAtStr = cursor.getString(cursor.getColumnIndexOrThrow("updated_at"));

        try {
            transaction.setDate(Long.parseLong(dateStr));
            transaction.setCreatedAt(simpleDateFormat.parse(createdAtStr));
            transaction.setUpdatedAt(simpleDateFormat.parse(updatedAtStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        transaction.setCategoryName(cursor.getString(cursor.getColumnIndexOrThrow("category_name")));

        return transaction;
    }


    public double getTotalIncome(long userId) {
        double total = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT SUM(amount) FROM transactions WHERE user_id = ? AND type = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId),
                String.valueOf(Transaction.Type.INCOME)});

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();

        return total;
    }

    public double getTotalExpenses(long userId) {
        double total = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT SUM(amount) FROM transactions WHERE user_id = ? AND type = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId),
                String.valueOf(Transaction.Type.EXPENSE)});

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();

        return total;
    }

    public double getCurrentBalance(long userId) {
        double income = getTotalIncome(userId);
        double expenses = getTotalExpenses(userId);

        return income - expenses;
    }
}
