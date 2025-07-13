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

public class login extends AppCompatActivity {

    private EditText loginUsername, loginPassword;
    private Button loginButton;
    private TextView signupRedirectText;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        dbHelper = new DBHelper(this);

        loginUsername = findViewById(R.id.username);
        loginPassword = findViewById(R.id.password);
        loginButton = findViewById(R.id.btnlogin);
        signupRedirectText = findViewById(R.id.textView5);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = loginUsername.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(login.this, "Semua form harus diisi", Toast.LENGTH_SHORT).show();
                } else {
                    Boolean checkUser = dbHelper.checkUsernamePassword(username, password);
                    if (checkUser) {
                        Toast.makeText(login.this, "Login Berhasil!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), home.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(login.this, "Username atau Password Salah!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, signup.class);
                startActivity(intent);
            }
        });
    }
}