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
        //create users table
        db.execSQL("CREATE TABLE Users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL UNIQUE, " +
                "email TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "last_login TIMESTAMP" + ")");

        //create category table
        db.execSQL("CREATE TABLE Categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "name TEXT NOT NULL, " +
                "type TEXT NOT NULL CHECK (type IN ('EXPENSE', 'INCOME')), " +
                "is_default INTEGER DEFAULT 0 CHECK (is_default IN (0, 1)), " +
                "FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE, " +
                "UNIQUE(user_id, name)" + ")");

        //create transaction table
        db.execSQL("CREATE TABLE Transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "category_id INTEGER NOT NULL, " +
                "amount REAL NOT NULL, " +
                "type TEXT NOT NULL CHECK (type IN ('EXPENSE', 'INCOME')), " +
                "notes TEXT, " +
                "date TIMESTAMP NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (category_id) REFERENCES Categories(id) ON DELETE RESTRICT" + ")");

        //create budget table
        db.execSQL("CREATE TABLE Budgets (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "category_id INTEGER NOT NULL, " +
                "amount REAL NOT NULL, " +
                "period_type TEXT NOT NULL CHECK (period_type IN ('WEEKLY', 'MONTHLY', 'YEARLY')), " +
                "start_date TIMESTAMP NOT NULL, " +
                "end_date TIMESTAMP NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (category_id) REFERENCES Categories(id) ON DELETE RESTRICT" + ")");

        //create third table for tx and budget
        db.execSQL("CREATE TABLE BudgetTransactions (" +
                "budget_id INTEGER NOT NULL, " +
                "transaction_id INTEGER NOT NULL, " +
                "PRIMARY KEY (budget_id, transaction_id), " +
                "FOREIGN KEY (budget_id) REFERENCES Budgets(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (transaction_id) REFERENCES Transactions(id) ON DELETE CASCADE" + ")");

        // Create triggers for updated_at fields
        db.execSQL("CREATE TRIGGER update_transaction_timestamp " +
                "AFTER UPDATE ON Transactions " +
                "BEGIN " +
                "    UPDATE Transactions SET updated_at = CURRENT_TIMESTAMP WHERE id = NEW.id; " +
                "END");

        db.execSQL("CREATE TRIGGER update_budget_timestamp " +
                "AFTER UPDATE ON Budgets " +
                "BEGIN " +
                "    UPDATE Budgets SET updated_at = CURRENT_TIMESTAMP WHERE id = NEW.id; " +
                "END");

        insertDefaultCategories(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS BudgetTransactions");
        db.execSQL("DROP TABLE IF EXISTS Transactions");
        db.execSQL("DROP TABLE IF EXISTS Budgets");
        db.execSQL("DROP TABLE IF EXISTS Categories");
        db.execSQL("DROP TABLE IF EXISTS Users");

        onCreate(db);
    }

    private void insertDefaultCategories(SQLiteDatabase db) {
        // Insert expense categories
        insertCategory(db, null, "Food", "EXPENSE", 1);
        insertCategory(db, null, "Transportation", "EXPENSE", 1);
        insertCategory(db, null, "Housing", "EXPENSE", 1);
        insertCategory(db, null, "Entertainment", "EXPENSE", 1);
        insertCategory(db, null, "Health", "EXPENSE", 1);
        insertCategory(db, null, "Education", "EXPENSE", 1);
        insertCategory(db, null, "Shopping", "EXPENSE", 1);
        insertCategory(db, null, "Electronics", "EXPENSE", 1);
        insertCategory(db, null, "Utilities", "EXPENSE", 1);

        // Insert income categories
        insertCategory(db, null, "Salary", "INCOME", 1);
        insertCategory(db, null, "Gift", "INCOME", 1);
    }

    private void insertCategory(SQLiteDatabase db, Integer userId, String name, String type, int isDefault) {
        String sql = "INSERT INTO Categories (userId, name, type, isDefault) VALUES (?, ?, ?, ?, ?)";
        Object[] bindArgs = new Object[]{userId, name, type, isDefault};
        db.execSQL(sql, bindArgs);
    }
}
