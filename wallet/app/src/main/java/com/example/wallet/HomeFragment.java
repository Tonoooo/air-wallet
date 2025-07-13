package com.example.wallet;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // TextView untuk menampilkan username
        TextView welcomeText = view.findViewById(R.id.txt_username);
        // Ambil data dari Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            String username = bundle.getString("USERNAME_KEY");
            welcomeText.setText("Hi, " + username + "!");
        } else {
            welcomeText.setText("Hi, Sahabat!");
        }



        // card view send untuk pindah ke activity send
        CardView cardSend = view.findViewById(R.id.card_send);
        cardSend.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), send.class);
            startActivity(intent);
        });
        // card view deposit untuk pindah ke activity deposit
        CardView cardDeposit = view.findViewById(R.id.card_deposit);
        cardDeposit.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), deposit.class);
            startActivity(intent);
        });
        // card view reciev qr untuk pindah ke activity receive
        CardView cardMyqr = view.findViewById(R.id.card_myqr);
        cardMyqr.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), receive.class);
            startActivity(intent);
        });

        return view;

    }
}