package com.example.moneytrackerandroidsqlite;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.moneytrackerandroidsqlite.database.TransactionRepository;
import com.example.moneytrackerandroidsqlite.databinding.FragmentTransactionDetailBinding;
import com.example.moneytrackerandroidsqlite.fragments.TransactionFragment;
import com.example.moneytrackerandroidsqlite.models.Transaction;
import com.example.moneytrackerandroidsqlite.utils.AuthManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TransactionDetailFragment extends Fragment {
    FragmentTransactionDetailBinding binding;
    Button tx_detail_del;
    ImageButton tx_detail_edit;
    Long txId;
    TransactionRepository transactionRepository;
    AuthManager authManager;
    Transaction transaction;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY", Locale.getDefault());
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTransactionDetailBinding.inflate(inflater, container, false);
        Toolbar toolbar = binding.toolbar;
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });
        if (getArguments() != null) {
            txId = getArguments().getLong("transactionId");

        }
        transactionRepository = new TransactionRepository(getActivity());
        authManager = AuthManager.getInstance(getActivity());
        if (txId == -1) {
            Toast.makeText(getActivity(), "Lỗi: Không tìm thấy giao dịch", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        }

        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDeleteTx();
            }
        });

        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchEditTransaction();
            }
        });

        loadTransactionData();
        
        return binding.getRoot();
    }

    private void loadTransactionData() {
        transaction = transactionRepository.getTxById(authManager.getCurrentUser().getId(), txId);
        if (transaction != null) {
            binding.tvAmount.setText(transaction.getFormated());
            binding.tvNotes.setText(transaction.getNotes());
            binding.tvCategory.setText(transaction.getCategoryName());
            Date date = new Date(transaction.getDate());
            binding.tvDate.setText(dateFormat.format(date));
        } else {
            Toast.makeText(getActivity(), "Transaction not found", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        }
    }

    private void launchEditTransaction() {

    }

    private void handleDeleteTx() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Transaction");
        builder.setMessage("Are you sure you want to delete this transaction? This action cannot be undone.");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (txId == -1) {
                    Toast.makeText(getActivity(), "Error: Invalid transaction", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean success = transactionRepository.deleteTransaction(txId);
                if (success) {
                    Toast.makeText(getActivity(), "Transaction deleted successfully", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getActivity(), "Failed to delete transaction", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}