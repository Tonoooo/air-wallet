package com.example.wallet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SwapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SwapFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SwapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyqrFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SwapFragment newInstance(String param1, String param2) {
        SwapFragment fragment = new SwapFragment();
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
        View view = inflater.inflate(R.layout.fragment_swap, container, false);


        // Temukan Spinner di dalam View Fragment
        Spinner spinner = view.findViewById(R.id.spinner_pay_token);

        // Pastikan Spinner tidak null sebelum digunakan
        if (spinner != null) {
            // Array adapter
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    getContext(), R.array.swap_token_item,
                    android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }

        // spinner recieve
        // Temukan Spinner di dalam View Fragment
        Spinner spinner_recieve = view.findViewById(R.id.spinner_recieve_token);

        // Pastikan Spinner tidak null sebelum digunakan
        if (spinner_recieve != null) {
            // Array adapter
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    getContext(), R.array.swap_token_item,
                    android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_recieve.setAdapter(adapter);
        }

        return view;
    }
}