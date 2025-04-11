package com.example.moneytrackerandroidsqlite.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moneytrackerandroidsqlite.R;
import com.example.moneytrackerandroidsqlite.activities.TransactionDetailFragment;
import com.example.moneytrackerandroidsqlite.adapters.DateGroupAdapter;
import com.example.moneytrackerandroidsqlite.database.TransactionRepository;
import com.example.moneytrackerandroidsqlite.models.Transaction;
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
        transactionRepository = new TransactionRepository(getActivity());
        authManager = AuthManager.getInstance(getActivity());
        rvTransaction = view.findViewById(R.id.rvTransactions);
        rvTransaction.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadTx();
        return view;
    }

    private void loadTx() {
        List<Transaction> transactions = transactionRepository.getAllTx(authManager.getCurrentUser().getId());

        if (dateGroupAdapter == null) {
            dateGroupAdapter = new DateGroupAdapter(getActivity(), transactions, new DateGroupAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Long transactionId) {
                    TransactionDetailFragment detailFragment = new TransactionDetailFragment();
                    Bundle args = new Bundle();
                    args.putLong("transactionId", transactionId);
                    detailFragment.setArguments(args);
                    getParentFragmentManager().beginTransaction()
                            .setCustomAnimations(
                                    R.anim.slide_in,
                                    R.anim.fade_out,
                                    R.anim.fade_in,
                                    R.anim.slide_out
                            )
                            .setReorderingAllowed(true)
                            .replace(R.id.fragment_container, detailFragment)
                            .addToBackStack("transactions")
                            .commit();
                }
            });
            rvTransaction.setAdapter(dateGroupAdapter);
        } else {
            dateGroupAdapter.setTransactions(transactions);
            dateGroupAdapter.notifyDataSetChanged();  // Add this line to force refresh
        }

        // Add this check to ensure adapter is attached
        if (rvTransaction.getAdapter() == null) {
            rvTransaction.setAdapter(dateGroupAdapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTx();
    }
}