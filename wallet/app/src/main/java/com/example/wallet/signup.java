package com.example.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class signup extends AppCompatActivity {

    private EditText signupUsername, signupPassword, signupConfirm;
    private Button signupButton;
    private TextView loginRedirectText;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        dbHelper = new DBHelper(this);

        signupUsername = findViewById(R.id.username_signup);
        signupPassword = findViewById(R.id.password_signup);
        signupConfirm = findViewById(R.id.password_confirm);
        signupButton = findViewById(R.id.btnsignup);
        loginRedirectText = findViewById(R.id.signupdirect);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = signupUsername.getText().toString().trim();
                String password = signupPassword.getText().toString().trim();
                String confirmPassword = signupConfirm.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(signup.this, "Semua form harus diisi", Toast.LENGTH_SHORT).show();
                } else {
                    if (password.equals(confirmPassword)) {
                        Boolean checkUser = dbHelper.checkUsername(username);
                        if (!checkUser) {
                            Boolean insert = dbHelper.insertData(username, password);
                            if (insert) {
                                Toast.makeText(signup.this, "Pendaftaran Berhasil!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), login.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(signup.this, "Pendaftaran Gagal!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(signup.this, "Username sudah terdaftar!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(signup.this, "Password tidak cocok!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), login.class);
                startActivity(intent);
            }
        });
    }
}