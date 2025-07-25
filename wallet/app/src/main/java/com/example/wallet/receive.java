package com.example.wallet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.HashMap;
import java.util.Map;

public class receive extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private ValueEventListener sessionListener;
    private DatabaseReference sessionRef;

    private String receivedUsername;
    private long receivedBalance;
    private ImageView qrCodeImageView;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);

        dbHelper = new DBHelper(this);

        qrCodeImageView = findViewById(R.id.imageView12);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USERNAME_EXTRA")) {
            receivedUsername = intent.getStringExtra("USERNAME_EXTRA");
            receivedBalance = intent.getLongExtra("BALANCE_EXTRA", 0);
        } else {
            receivedUsername = "PenggunaDefault"; // Nilai fallback
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        startTransactionSession();
    }

    private void startTransactionSession() {
        DatabaseReference transactionsRef = mDatabase.child("transactions");
        String sessionId = transactionsRef.push().getKey();

        if (sessionId == null) {
            Log.e("ReceiveActivity", "Gagal mendapatkan sessionId dari Firebase.");
            Toast.makeText(this, "Gagal memulai sesi, coba lagi.", Toast.LENGTH_SHORT).show();
            finish(); // Tutup activity jika sesi tidak bisa dibuat
            return;
        }

        // Simpan referensi sesi untuk digunakan nanti
        sessionRef = transactionsRef.child(sessionId);

        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("status", "waiting_for_sender"); // Status awal yang jelas
        sessionData.put("receiverName", receivedUsername);

        sessionRef.setValue(sessionData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("ReceiveActivity", "Sesi berhasil dibuat dengan ID: " + sessionId);
                    generateQrCode(sessionId);
                    listenForTransactionUpdates();
                })
                .addOnFailureListener(e -> {
                    Log.e("ReceiveActivity", "Gagal menyimpan data sesi.", e);
                    Toast.makeText(this, "Gagal terhubung ke server.", Toast.LENGTH_SHORT).show();
                });
    }

    private void generateQrCode(String textToEncode) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(textToEncode, BarcodeFormat.QR_CODE, 600, 600);
            qrCodeImageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            Log.e("ReceiveActivity", "Error membuat QR Code", e);
            Toast.makeText(this, "Terjadi kesalahan saat menampilkan QR Code.", Toast.LENGTH_SHORT).show();
        }
    }

    private void listenForTransactionUpdates() {
        if (sessionRef == null) return;

        sessionListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) return;



                String status = dataSnapshot.child("status").getValue(String.class);
                Log.d("ReceiveActivity", "Status sesi berubah: " + status);

                if ("data_sent_by_sender".equals(status)) {
                    sessionRef.removeEventListener(this); // Hentikan listener

                    Long amount = dataSnapshot.child("amount").getValue(Long.class);
                    String senderName = dataSnapshot.child("senderName").getValue(String.class);
                    if (amount == null) amount = 0L;

                    showConfirmationDialog(amount, senderName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("ReceiveActivity", "Gagal membaca data sesi.", databaseError.toException());
            }
        };
        sessionRef.addValueEventListener(sessionListener);
    }

    private void showConfirmationDialog(long amount, String senderName) {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Penerimaan")
                .setMessage("Anda akan menerima Rp " + amount + " dari " + senderName + ". Lanjutkan?")
                .setCancelable(false) // Pengguna harus memilih Ya atau Tidak
                .setPositiveButton("Ya, Terima", (dialog, which) -> {
                    // TODO: Logika untuk menambah saldo lokal Anda (misal di SharedPreferences)

                    // Update status di Firebase
                    sessionRef.child("status").setValue("completed_by_receiver");
                    Toast.makeText(receive.this, "Transaksi berhasil!", Toast.LENGTH_LONG).show();

                    long newBalance = receivedBalance + amount;
                    dbHelper.updateUang(receivedUsername, (int) newBalance, true);


                })
                .setNegativeButton("Tidak", (dialog, which) -> {
                    // Update status untuk memberitahu pengirim bahwa transaksi ditolak
                    sessionRef.child("status").setValue("rejected_by_receiver");
                    Toast.makeText(receive.this, "Transaksi dibatalkan.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sessionRef != null && sessionListener != null) {
            sessionRef.removeEventListener(sessionListener);
            Log.d("ReceiveActivity", "ValueEventListener dihapus.");
        }
    }
}