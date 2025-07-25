package com.example.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.HashMap;
import java.util.Map;

public class send extends AppCompatActivity {

    private EditText amountEditText;
    private Button scanButton;
    private TextView balanceTextView;

    private DatabaseReference mDatabase;
    private String senderUsername;
    private long senderBalance;

    private ValueEventListener transactionStatusListener;
    private DatabaseReference activeTransactionRef;

    private DBHelper dbHelper;



    // Launcher modern untuk memulai activity dan menerima hasilnya
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Toast.makeText(send.this, "Scan dibatalkan", Toast.LENGTH_LONG).show();
                } else {
                    // Hasil scan (sessionId) didapatkan
                    String sessionId = result.getContents();
                    long amount = Long.parseLong(amountEditText.getText().toString());
                    Toast.makeText(send.this, "QR Scanned, memproses...", Toast.LENGTH_SHORT).show();
                    processTransaction(sessionId, amount);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        dbHelper = new DBHelper(this);

        amountEditText = findViewById(R.id.amountEditText);
        scanButton = findViewById(R.id.scanButton);
        balanceTextView = findViewById(R.id.balanceTextView);


        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Ambil data user dari Intent (diasumsikan dikirim dari activity sebelumnya)
        Intent intent = getIntent();
        senderUsername = intent.getStringExtra("USERNAME_EXTRA");

        senderBalance = dbHelper.getUang(senderUsername);

        // Kita butuh saldo juga untuk validasi
        // senderBalance = intent.getLongExtra("BALANCE_EXTRA", 0);

        if (senderUsername == null) {
            senderUsername = "PengirimDefault"; // Fallback
        }

        balanceTextView.setText("Rp " + senderBalance);

        scanButton.setOnClickListener(v -> {
            String amountStr = amountEditText.getText().toString();
            if (TextUtils.isEmpty(amountStr)) {
                Toast.makeText(this, "Masukkan nominal terlebih dahulu", Toast.LENGTH_SHORT).show();
                return;
            }

            long amount = Long.parseLong(amountStr);
            if (amount <= 0) {
                Toast.makeText(this, "Nominal tidak valid", Toast.LENGTH_SHORT).show();
                return;
            }

            if (amount > senderBalance) {
                Toast.makeText(this, "Saldo tidak mencukupi!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Jika semua validasi lolos, mulai scan
            startScan();
        });
    }

    private void startScan() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan QR Code Penerima");
        options.setCameraId(0);  // Gunakan kamera belakang
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(true);
        barcodeLauncher.launch(options);
    }

    private void processTransaction(String sessionId, long amount) {
        DatabaseReference transactionRef = mDatabase.child("transactions").child(sessionId);

        // Simpan referensi transaksi yang sedang aktif
        this.activeTransactionRef = transactionRef;

        transactionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && "waiting_for_sender".equals(snapshot.child("status").getValue(String.class))) {
                    // Session valid dan sedang menunggu pengirim
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("amount", amount);
                    updates.put("senderName", senderUsername);
                    updates.put("status", "data_sent_by_sender"); // Status baru

                    transactionRef.updateChildren(updates)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(send.this, "Menunggu konfirmasi dari penerima...", Toast.LENGTH_LONG).show();
                                // Mulai mendengarkan jawaban dari penerima
                                listenForTransactionCompletion(transactionRef, amount);
                            })
                            .addOnFailureListener(e -> Toast.makeText(send.this, "Gagal mengirim data.", Toast.LENGTH_SHORT).show());

                } else {
                    // Session tidak valid atau sudah digunakan
                    Toast.makeText(send.this, "Kode QR tidak valid atau sudah kedaluwarsa.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(send.this, "Gagal memverifikasi sesi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void listenForTransactionCompletion(DatabaseReference transactionRef, long amount) {
        transactionStatusListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;

                String status = snapshot.child("status").getValue(String.class);
                if ("completed_by_receiver".equals(status)) {
                    // Transaksi diterima!
                    transactionRef.removeEventListener(this); // Hentikan listener
                    deductBalance(amount);
                    showResultDialog("Transaksi Berhasil!", "Rp " + amount + " telah berhasil dikirim.");
                } else if ("rejected_by_receiver".equals(status)) {
                    // Transaksi ditolak
                    transactionRef.removeEventListener(this); // Hentikan listener
                    showResultDialog("Transaksi Ditolak", "Penerima menolak permintaan transfer Anda.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("SendActivity", "Gagal mendengarkan status transaksi.", error.toException());
                Toast.makeText(send.this, "Koneksi ke server terputus.", Toast.LENGTH_SHORT).show();
            }
        };
        transactionRef.addValueEventListener(transactionStatusListener);
    }

    private void deductBalance(long amount) {
        long newBalance = senderBalance - amount;
        dbHelper.setUang(senderUsername, (int) newBalance);
        // Asumsi struktur database Anda: /users/{username}/balance
//        mDatabase.child("users").child(senderUsername).child("balance").setValue(newBalance)
//                .addOnSuccessListener(aVoid -> {
//                    // Update saldo di UI
//                    senderBalance = newBalance;
//                    balanceTextView.setText("Rp " + newBalance);
//                })
//                .addOnFailureListener(e -> {
//                    // Handle jika gagal update saldo, ini kasus kritis
//                    Toast.makeText(this, "CRITICAL: Gagal update saldo!", Toast.LENGTH_LONG).show();
//                });
    }

    private void showResultDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> finish()) // Tutup activity setelah OK
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Pastikan untuk menghapus listener saat activity dihancurkan untuk mencegah memory leak
        if (activeTransactionRef != null && transactionStatusListener != null) {
            activeTransactionRef.removeEventListener(transactionStatusListener);
            Log.d("SendActivity", "ValueEventListener dihapus.");
        }
    }
}