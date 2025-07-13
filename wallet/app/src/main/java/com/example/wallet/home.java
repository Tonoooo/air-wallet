package com.example.wallet;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.wallet.databinding.HomeBinding;

public class home extends AppCompatActivity {

    HomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = HomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String username = getIntent().getStringExtra("USERNAME_KEY");

        HomeFragment homeFragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("USERNAME_KEY", username);
        homeFragment.setArguments(bundle);

        ProfileFragment profile_Fragment = new ProfileFragment();
        Bundle bundle_profile = new Bundle();
        bundle_profile.putString("USERNAME_KEY", username);
        profile_Fragment.setArguments(bundle_profile);


        replaceFragment(homeFragment);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                replaceFragment(homeFragment);
            } else if (itemId == R.id.swap) {
                replaceFragment(new SwapFragment());
            } else if (itemId == R.id.profile) {
                replaceFragment(profile_Fragment);
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}
