package com.example.moneytracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MoneyTracker.db";
    private static final int DATABASE_VERSION = 2; // Subimos versión
    private static final String TABLE_TRANSACTIONS = "transactions";

    public DatabaseHelper(Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSION); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Agregamos payment_method al CREATE
        db.execSQL("CREATE TABLE " + TABLE_TRANSACTIONS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT, amount REAL, category TEXT, description TEXT, date INTEGER, payment_method TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        onCreate(db);
    }

    public boolean addTransaction(Transaction t) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("type", t.getType());
        v.put("amount", t.getAmount());
        v.put("category", t.getCategory());
        v.put("description", t.getDescription());
        v.put("date", t.getDate());
        v.put("payment_method", t.getPaymentMethod()); // NUEVO
        return db.insert(TABLE_TRANSACTIONS, null, v) != -1;
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        Cursor c = this.getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_TRANSACTIONS + " ORDER BY date DESC", null);
        if (c.moveToFirst()) {
            do {
                Transaction t = new Transaction();
                t.setId(c.getInt(0));
                t.setType(c.getString(1));
                t.setAmount(c.getDouble(2));
                t.setCategory(c.getString(3));
                t.setDescription(c.getString(4));
                t.setDate(c.getLong(5));
                // Verificamos si existe la columna (por seguridad)
                int pmIndex = c.getColumnIndex("payment_method");
                if (pmIndex != -1) t.setPaymentMethod(c.getString(pmIndex));

                list.add(t);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public double getBalance() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c1 = db.rawQuery("SELECT SUM(amount) FROM transactions WHERE type='INCOME'", null);
        double inc = c1.moveToFirst() ? c1.getDouble(0) : 0; c1.close();
        Cursor c2 = db.rawQuery("SELECT SUM(amount) FROM transactions WHERE type='EXPENSE'", null);
        double exp = c2.moveToFirst() ? c2.getDouble(0) : 0; c2.close();
        return inc - exp;
    }

    public void deleteTransaction(int id) { this.getWritableDatabase().delete(TABLE_TRANSACTIONS, "id=?", new String[]{String.valueOf(id)}); }

    public int updateTransaction(Transaction t) {
        ContentValues v = new ContentValues();
        v.put("type", t.getType());
        v.put("amount", t.getAmount());
        v.put("category", t.getCategory());
        v.put("description", t.getDescription());
        v.put("payment_method", t.getPaymentMethod()); // NUEVO
        return this.getWritableDatabase().update(TABLE_TRANSACTIONS, v, "id=?", new String[]{String.valueOf(t.getId())});
    }

    public void resetDatabase() { this.getWritableDatabase().delete(TABLE_TRANSACTIONS, null, null); }

    public Cursor getExpensesByCategory() {
        return this.getReadableDatabase().rawQuery("SELECT category, SUM(amount) FROM transactions WHERE type='EXPENSE' GROUP BY category", null);
    }
}