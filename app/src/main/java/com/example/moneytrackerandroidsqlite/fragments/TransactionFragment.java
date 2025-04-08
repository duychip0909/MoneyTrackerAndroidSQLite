package com.example.moneytrackerandroidsqlite.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moneytrackerandroidsqlite.R;
import com.example.moneytrackerandroidsqlite.adapters.TransactionAdapter;
import com.example.moneytrackerandroidsqlite.database.TransactionRepository;
import com.example.moneytrackerandroidsqlite.models.Transaction;
import com.example.moneytrackerandroidsqlite.utils.AuthManager;

import java.util.List;

public class TransactionFragment extends Fragment {
    RecyclerView rvTransaction;
    TransactionAdapter txAdapter;
    List<Transaction> transactions;
    TransactionRepository transactionRepository;
    AuthManager authManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);
        transactionRepository = new TransactionRepository(view.getContext());
        authManager = AuthManager.getInstance(view.getContext());
        rvTransaction = view.findViewById(R.id.rvTransactions);
        rvTransaction.setLayoutManager(new LinearLayoutManager(view.getContext()));
        transactions = transactionRepository.getAllTx(authManager.getCurrentUser().getId());
        txAdapter = new TransactionAdapter(view.getContext(), transactions);
        rvTransaction.setAdapter(txAdapter);
        return view;
    }
}