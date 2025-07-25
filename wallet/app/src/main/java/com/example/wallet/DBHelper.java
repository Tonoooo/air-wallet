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

    public static final String COL_3 = "uang";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Skema tabel diubah untuk menggunakan 'username'
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (username TEXT PRIMARY KEY, password TEXT, uang INTEGER DEFAULT 100)");
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

    public int getUang(String username) { // Atau long, atau double
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_3 + " FROM " + TABLE_NAME + " WHERE " + COL_1 + " = ?", new String[]{username});
        int uang = 0; // Nilai default jika pengguna tidak ditemukan atau tidak ada saldo
        if (cursor.moveToFirst()) {
            uang = cursor.getInt(cursor.getColumnIndexOrThrow(COL_3));
        }
        cursor.close();
        return uang;
    }

    // Di dalam kelas DBHelper.java Anda

    /**
     * Memperbarui jumlah uang untuk pengguna tertentu.
     *
     * @param username Username pengguna yang uangnya akan diperbarui.
     * @param newAmount Jumlah uang baru. Bisa positif untuk menambah, negatif untuk mengurangi (jika Anda ingin mengelola perubahan).
     *                  Atau bisa juga nilai absolut baru untuk saldo.
     * @param isDelta Jika true, newAmount akan ditambahkan/dikurangkan dari saldo saat ini.
     *                Jika false, newAmount akan menjadi saldo baru (menggantikan yang lama).
     * @return true jika pembaruan berhasil (setidaknya satu baris terpengaruh), false sebaliknya.
     */
    public Boolean updateUang(String username, int newAmount, boolean isDelta) { // Gunakan long atau double jika tipe kolom uang Anda REAL
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        long finalAmount;

        if (isDelta) {
            // Ambil saldo saat ini terlebih dahulu
            int currentUang = getUang(username); // Menggunakan metode getUang yang sudah Anda buat
            finalAmount = (long) currentUang + newAmount; // Lakukan penambahan/pengurangan
            // Casting ke long untuk menghindari overflow jika hasilnya besar
        } else {
            finalAmount = newAmount; // Langsung set nilai baru
        }

        // Pastikan saldo tidak negatif jika itu adalah aturan bisnis Anda
        // if (finalAmount < 0) {
        //     finalAmount = 0; // Atau throw exception, atau return false
        // }

        contentValues.put(COL_3, finalAmount);

        // Melakukan pembaruan
        // Metode update mengembalikan jumlah baris yang terpengaruh.
        // Jika username ditemukan dan diperbarui, hasilnya akan > 0.
        int rowsAffected = db.update(TABLE_NAME, contentValues, COL_1 + " = ?", new String[]{username});

        // db.close(); // Sebaiknya jangan tutup db di sini jika helper dikelola oleh Android

        return rowsAffected > 0;
    }

    // --- Metode alternatif yang lebih sederhana jika Anda hanya ingin MENGGANTI saldo ---
    /**
     * Mengganti jumlah uang pengguna dengan nilai baru.
     *
     * @param username Username pengguna.
     * @param newBalance Saldo uang yang baru.
     * @return true jika pembaruan berhasil, false sebaliknya.
     */
    public Boolean setUang(String username, int newBalance) { // Gunakan long atau double jika tipe kolom uang Anda REAL
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_3, newBalance);

        int rowsAffected = db.update(TABLE_NAME, contentValues, COL_1 + " = ?", new String[]{username});
        return rowsAffected > 0;
    }

    // --- Metode alternatif jika Anda ingin MENAMBAH saldo (bisa menerima nilai negatif untuk mengurangi) ---
    /**
     * Menambah atau mengurangi jumlah uang pengguna.
     *
     * @param username Username pengguna.
     * @param amountToChange Jumlah yang akan ditambahkan (positif) atau dikurangkan (negatif).
     * @return true jika pembaruan berhasil, false sebaliknya.
     */
    public Boolean addUang(String username, int amountToChange) { // Gunakan long atau double jika tipe kolom uang Anda REAL
        SQLiteDatabase db = this.getWritableDatabase();

        // 1. Ambil saldo saat ini
        int currentUang = getUang(username); // Anda sudah punya metode ini

        // 2. Hitung saldo baru
        long newBalance = (long) currentUang + amountToChange;

        // (Opsional) Terapkan aturan bisnis, misalnya saldo tidak boleh negatif
        // if (newBalance < 0) {
        //     // Log.e("DBHelper", "Saldo tidak bisa negatif setelah operasi.");
        //     // return false; // Atau set ke 0, atau lempar exception
        //      newBalance = 0;
        // }

        // 3. Buat ContentValues
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_3, newBalance);

        // 4. Lakukan update
        int rowsAffected = db.update(TABLE_NAME, contentValues, COL_1 + " = ?", new String[]{username});
        return rowsAffected > 0;
    }
}