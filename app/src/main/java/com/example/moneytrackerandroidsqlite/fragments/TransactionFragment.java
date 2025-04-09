package com.example.moneytrackerandroidsqlite.fragments;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moneytrackerandroidsqlite.R;
import com.example.moneytrackerandroidsqlite.adapters.DateGroupAdapter;
import com.example.moneytrackerandroidsqlite.adapters.TransactionAdapter;
import com.example.moneytrackerandroidsqlite.database.TransactionRepository;
import com.example.moneytrackerandroidsqlite.models.Transaction;
import com.example.moneytrackerandroidsqlite.models.TransactionListItem;
import com.example.moneytrackerandroidsqlite.utils.AuthManager;

import java.util.List;

public class TransactionFragment extends Fragment {
    RecyclerView rvTransaction;
    DateGroupAdapter dateGroupAdapter;
    TransactionRepository transactionRepository;
    AuthManager authManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);
        transactionRepository = new TransactionRepository(getContext());
        authManager = AuthManager.getInstance(getContext());
        rvTransaction = view.findViewById(R.id.rvTransactions);
        rvTransaction.setLayoutManager(new LinearLayoutManager(getContext()));
        loadTx();
        return view;
    }

    private void loadTx() {
        List<Transaction> transactions;
        transactions = transactionRepository.getAllTx(authManager.getCurrentUser().getId());
        if (dateGroupAdapter == null) {
            dateGroupAdapter = new DateGroupAdapter(getContext(), transactions);
            rvTransaction.setAdapter(dateGroupAdapter);
        } else {
            dateGroupAdapter.setTransactions(transactions);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTx();
    }
}