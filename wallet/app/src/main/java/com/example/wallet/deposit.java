package com.example.wallet;

import static com.example.wallet.R.*;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class deposit extends AppCompatActivity {

    private EditText plaintex;
    private Button bt10,bt100,bt1000;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_deposit);

        plaintex = findViewById(R.id.fx_usd);
        bt10 = findViewById(R.id.btn_10);
        bt100 = findViewById(R.id.btn_100);
        bt1000 = findViewById(id.btn_1000);

        bt10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plaintex.setText("10");
            }
        });

        bt100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plaintex.setText("100");
            }
        });

        bt1000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plaintex.setText("1000");
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}