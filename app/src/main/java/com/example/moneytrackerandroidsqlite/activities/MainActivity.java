package com.example.moneytrackerandroidsqlite.activities;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.moneytrackerandroidsqlite.R;
import com.example.moneytrackerandroidsqlite.databinding.ActivityMainBinding;
import com.example.moneytrackerandroidsqlite.fragments.BudgetFragment;
import com.example.moneytrackerandroidsqlite.fragments.HomeFragment;
import com.example.moneytrackerandroidsqlite.fragments.SettingFragment;
import com.example.moneytrackerandroidsqlite.fragments.TransactionFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FloatingActionButton fab;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = binding.bottomNavigation;
        bottomNavigationView.setBackground(null);
        replaceFragment(new HomeFragment());
        fab = binding.fabAddTransaction;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreateTransactionActivity.class));
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.home_frag) {
                    replaceFragment(new HomeFragment());
                    return true;
                } else if (id == R.id.tx_frag) {
                    replaceFragment(new TransactionFragment());
                    return true;
                } else if (id == R.id.budget_frag) {
                    replaceFragment(new BudgetFragment());
                    return true;
                } else if (id == R.id.setting_frag) {
                    replaceFragment(new SettingFragment());
                    return true;
                }
                return false;
            }
        });
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}