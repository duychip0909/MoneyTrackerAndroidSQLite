package com.example.moneytrackerandroidsqlite.fragments;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneytrackerandroidsqlite.R;
import com.example.moneytrackerandroidsqlite.activities.TransactionDetailFragment;
import com.example.moneytrackerandroidsqlite.adapters.TransactionAdapter;
import com.example.moneytrackerandroidsqlite.database.TransactionRepository;
import com.example.moneytrackerandroidsqlite.models.Transaction;
import com.example.moneytrackerandroidsqlite.utils.AuthManager;

import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;
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
    private TransactionRepository transactionRepository;

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

        transactionRepository = new TransactionRepository(view.getContext());

        // Set date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        tvDate.setText(dateFormat.format(new Date()));
        tvGreeting.setText(authManager.getCurrentUser().getUsername());
        // Set up RecyclerView
        rvRecentTransactions.setLayoutManager(new LinearLayoutManager(view.getContext()));
        loadTx();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTx();
    }

    private void loadTx() {
        List<Transaction> recentTxs;
        recentTxs = transactionRepository.getNearestTransactions("2025-04-09", 5, authManager.getCurrentUser().getId());
        if (transactionAdapter == null) {
            transactionAdapter = new TransactionAdapter(getContext(), recentTxs, new TransactionAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Long transactionId) {
                    TransactionDetailFragment detailFragment = new TransactionDetailFragment();
                    Bundle args = new Bundle();
                    args.putLong("transactionId", transactionId);
                    detailFragment.setArguments(args);
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, detailFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
            rvRecentTransactions.setAdapter(transactionAdapter);
        } else {
            transactionAdapter.setTransactions(recentTxs);
        }
    }
}
