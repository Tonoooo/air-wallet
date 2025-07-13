package com.example.wallet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Login.db";
    public static final String TABLE_NAME = "users";
    public static final String COL_1 = "username"; // Diubah dari email
    public static final String COL_2 = "password";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Skema tabel diubah untuk menggunakan 'username'
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (username TEXT PRIMARY KEY, password TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Metode untuk memasukkan data dengan parameter 'username'
    public Boolean insertData(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, username);
        contentValues.put(COL_2, password);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    // Metode diubah menjadi 'checkUsername'
    public Boolean checkUsername(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE username = ?", new String[]{username});
        return cursor.getCount() > 0;
    }

    // Metode diubah menjadi 'checkUsernamePassword'
    public Boolean checkUsernamePassword(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE username = ? AND password = ?", new String[]{username, password});
        return cursor.getCount() > 0;
    }
}