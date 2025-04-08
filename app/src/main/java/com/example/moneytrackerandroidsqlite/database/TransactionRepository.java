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
import java.util.List;
import java.util.Locale;

public class TransactionRepository {
    private final DBHelper dbHelper;
    private final AuthManager authManager;
    private final SimpleDateFormat simpleDateFormat;
    public TransactionRepository(Context context) {
        dbHelper = DBHelper.getInstance(context);
        authManager = AuthManager.getInstance(context);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }
    public boolean addTransaction(long userId, long categoryId, double amount,
                                  String type, String notes, long dateMillis) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        Log.d("cid", String.valueOf(categoryId));
        Log.d("uid", String.valueOf(userId));
        Log.d("date", String.valueOf(dateMillis));
        Log.d("cate type", String.valueOf(dateMillis));
        values.put("user_id", userId);
        values.put("category_id", categoryId);
        values.put("amount", amount);
        values.put("type", type);
        values.put("notes", notes);
        values.put("date", dateMillis);


        long result = db.insert("Transactions", null, values);
        return result != -1;
    }
    public boolean updateTransaction(Transaction transaction) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean success = false;
        ContentValues values = new ContentValues();
        values.put("category_id", transaction.getId());
        values.put("amount", transaction.getAmount());
        values.put("type", String.valueOf(transaction.getType()));
        values.put("notes", transaction.getNotes());
        values.put("date", String.valueOf(transaction.getDate()));
        int affectedRows = db.update("Transactions", values, "id = ?", new String[]{String.valueOf(transaction.getId())});
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
        Cursor cursor = db.query("Transactions", null, "id = ? AND user_id = ?", new String[]{String.valueOf(tId)}, null, null, null);
        if (cursor.moveToFirst()) {
            transaction = cursorToTx(cursor);
        }
        cursor.close();
        return transaction;
    }
    public List<Transaction> getAllTx(long uId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Transaction> txs = new ArrayList<>();
        Cursor cursor = db.query("Transactions", null, "user_id = ?", new String[]{String.valueOf(uId)}, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Transaction tx = cursorToTx(cursor);
            txs.add(tx);
            cursor.moveToNext();
        }
        cursor.close();
        return txs;
    }

    private Transaction cursorToTx(Cursor cursor) {
        Transaction transaction = new Transaction();
        transaction.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        int userIdIndex = cursor.getColumnIndexOrThrow("user_id");
        if (!cursor.isNull(userIdIndex)) {
            transaction.setUserId(cursor.getInt(userIdIndex));
        }
        transaction.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
        transaction.setNotes(cursor.getString(cursor.getColumnIndexOrThrow("notes")));
        String typeStr = cursor.getString(cursor.getColumnIndexOrThrow("type"));
        transaction.setType(Transaction.Type.valueOf(typeStr));
        try {
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            if (date != null) {
                transaction.setDate(simpleDateFormat.parse(date));
            }
        } catch (ParseException e) {
            Log.e("UserRepo", "Error parsing date: " + e.getMessage());
        }
        return transaction;
    }
}
