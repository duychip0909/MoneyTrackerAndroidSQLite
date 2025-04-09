package com.example.moneytrackerandroidsqlite;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.moneytrackerandroidsqlite.database.TransactionRepository;
import com.example.moneytrackerandroidsqlite.databinding.ActivityTransactionDetailBinding;
import com.example.moneytrackerandroidsqlite.fragments.TransactionFragment;
import com.example.moneytrackerandroidsqlite.models.Transaction;
import com.example.moneytrackerandroidsqlite.utils.AuthManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TransactionDetailActivity extends AppCompatActivity {
    ActivityTransactionDetailBinding binding;
    Button tx_detail_del;
    ImageButton tx_detail_edit;
    Long txId;
    TransactionRepository transactionRepository;
    AuthManager authManager;
    Transaction transaction;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY", Locale.getDefault());

    private final ActivityResultLauncher<Intent> editTransactionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode() == RESULT_OK) {
                        loadTransactionData();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityTransactionDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detail_tx), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        transactionRepository = new TransactionRepository(this);
        authManager = AuthManager.getInstance(this);
        txId = getIntent().getLongExtra("TRANSACTION_ID", -1);
        if (txId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy giao dịch", Toast.LENGTH_SHORT).show();
            finish();
            return;
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
            Toast.makeText(this, "Transaction not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void launchEditTransaction() {
        Intent intent = new Intent(this, EditTransactionActivity.class);
        intent.putExtra("TRANSACTION_EDIT_ID", txId);
        editTransactionLauncher.launch(intent);
    }

    private void handleDeleteTx() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Transaction");
        builder.setMessage("Are you sure you want to delete this transaction? This action cannot be undone.");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (txId == -1) {
                    Toast.makeText(TransactionDetailActivity.this, "Error: Invalid transaction", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean success = transactionRepository.deleteTransaction(txId);
                if (success) {
                    Toast.makeText(TransactionDetailActivity.this, "Transaction deleted successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(TransactionDetailActivity.this, TransactionFragment.class));
                    finish();
                } else {
                    Toast.makeText(TransactionDetailActivity.this, "Failed to delete transaction", Toast.LENGTH_SHORT).show();
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