package com.example.wallet;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log; // Untuk debugging jika perlu
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DBHelper dbHelper;
    private long userBalance;
    private String mParam1;
    private String mParam2;

    private String currentUsername; // Variabel untuk menyimpan username


    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // Di dalam onCreate() HomeFragment.java
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DBHelper(getActivity());
        currentUsername = getArguments().getString("USERNAME_KEY"); // Kunci ini HARUS SAMA dengan yang Anda set di home.java

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        TextView balanceText = view.findViewById(R.id.textView11);
        TextView welcomeText = view.findViewById(R.id.txt_username);
        if (currentUsername != null && !currentUsername.isEmpty()) {
            welcomeText.setText("Hi, " + currentUsername + "!");
            userBalance = dbHelper.getUang(currentUsername);

            balanceText.setText("$" + String.valueOf(userBalance));
        } else {
            // Jika currentUsername tidak ada dari arguments, mungkin Anda ingin mengambilnya
            // dari sumber lain atau memberikan default.
            // Untuk saat ini, kita ambil dari bundle seperti kode Anda sebelumnya.
            Bundle bundle = getArguments();
            if (bundle != null) {
                String usernameFromBundle = bundle.getString("USERNAME_KEY");
                if (usernameFromBundle != null) {
                    currentUsername = usernameFromBundle; // Update currentUsername jika belum diset di onCreate
                    welcomeText.setText("Hi, " + currentUsername + "!");
                    balanceText.setText("$" + String.valueOf(userBalance));
                } else {
                    welcomeText.setText("Hi, Sahabat!");
                }
            } else {
                welcomeText.setText("Hi, Sahabat!");
            }
        }

        CardView cardSend = view.findViewById(R.id.card_send);
        cardSend.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), send.class);
            if (currentUsername != null && !currentUsername.isEmpty()) {
                intent.putExtra("USERNAME_EXTRA", currentUsername); // "USERNAME_EXTRA" adalah key untuk Intent
                Log.d("HomeFragment", "Mengirim username: " + currentUsername);
            } else {
                Log.d("HomeFragment", "Username tidak tersedia untuk dikirim.");
                // Anda mungkin ingin mengirim nilai default atau tidak mengirim sama sekali
                // intent.putExtra("USERNAME_EXTRA", "Guest"); // Contoh nilai default
            }
            startActivity(intent);
        });

        CardView cardDeposit = view.findViewById(R.id.card_deposit);
        cardDeposit.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), deposit.class);
            startActivity(intent);
        });

        CardView cardMyqr = view.findViewById(R.id.card_myqr);
        cardMyqr.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), receive.class);
            // Pastikan currentUsername tidak null sebelum mengirim
            if (currentUsername != null && !currentUsername.isEmpty()) {
                intent.putExtra("USERNAME_EXTRA", currentUsername); // "USERNAME_EXTRA" adalah key untuk Intent
                Log.d("HomeFragment", "Mengirim username: " + currentUsername);
            } else {
                Log.d("HomeFragment", "Username tidak tersedia untuk dikirim.");
                // Anda mungkin ingin mengirim nilai default atau tidak mengirim sama sekali
                // intent.putExtra("USERNAME_EXTRA", "Guest"); // Contoh nilai default
            }
            startActivity(intent);
        });

        return view;
    }
}