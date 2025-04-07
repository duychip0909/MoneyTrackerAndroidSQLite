package com.example.moneytrackerandroidsqlite.fragments;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneytrackerandroidsqlite.R;
import com.example.moneytrackerandroidsqlite.adapters.TransactionAdapter;
import com.example.moneytrackerandroidsqlite.utils.AuthManager;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private TextView tvGreeting;
    private TextView tvDate;
    private TextView tvBalanceAmount;
    private TextView tvIncomeAmount;
    private TextView tvExpensesAmount;
    private RecyclerView rvRecentTransactions;
    private TransactionAdapter transactionAdapter;
    AuthManager authManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        authManager = AuthManager.getInstance(view.getContext());
        // Initialize views
        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvDate = view.findViewById(R.id.tvDate);
        tvBalanceAmount = view.findViewById(R.id.tvBalanceAmount);
        tvIncomeAmount = view.findViewById(R.id.tvIncomeAmount);
        tvExpensesAmount = view.findViewById(R.id.tvExpensesAmount);
        rvRecentTransactions = view.findViewById(R.id.rvRecentTransactions);

        // Set date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        tvDate.setText(dateFormat.format(new Date()));
//        Log.d("current_user", authManager.getCurrentUser().getUsername());
        tvGreeting.setText("fuck");
        // Set up RecyclerView
//        setupRecyclerView();

        // Load data
//        loadData();

        return view;
    }

//    private void setupRecyclerView() {
//        rvRecentTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
//        transactionAdapter = new TransactionAdapter(new ArrayList<>());
//        rvRecentTransactions.setAdapter(transactionAdapter);
//    }
}
