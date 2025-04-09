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
import com.example.moneytrackerandroidsqlite.adapters.TransactionAdapter;
import com.example.moneytrackerandroidsqlite.database.TransactionRepository;
import com.example.moneytrackerandroidsqlite.models.Transaction;
import com.example.moneytrackerandroidsqlite.models.TransactionListItem;
import com.example.moneytrackerandroidsqlite.utils.AuthManager;

import java.util.List;

public class TransactionFragment extends Fragment {
    RecyclerView rvTransaction;
    TransactionAdapter txAdapter;
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
        rvTransaction.setLayoutManager(new LinearLayoutManager(view.getContext()));
        loadTx();
        return view;
    }

    private void loadTx() {
        List<Transaction> transactions;
        transactions = transactionRepository.getAllTx(authManager.getCurrentUser().getId());
        if (txAdapter == null) {
            txAdapter = new TransactionAdapter(getContext(), transactions);
            rvTransaction.setAdapter(txAdapter);
            rvTransaction.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                           @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                    int position = parent.getChildAdapterPosition(view);
                    if (position > 0) {
                        TransactionListItem currentItem = txAdapter.getItems().get(position);
                        TransactionListItem previousItem = txAdapter.getItems().get(position - 1);

                        if (currentItem.getType() == TransactionListItem.ItemType.TRANSACTION &&
                                previousItem.getType() == TransactionListItem.ItemType.TRANSACTION) {
                            outRect.top = 4; // Space between transactions
                        } else if (currentItem.getType() == TransactionListItem.ItemType.DATE_HEADER) {
                            outRect.top = 16; // Space before date headers
                        }
                    }
                }
            });
        } else {
            txAdapter.setTransactions(transactions);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTx();
    }
}