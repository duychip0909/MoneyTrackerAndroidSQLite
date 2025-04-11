package com.example.moneytrackerandroidsqlite.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moneytrackerandroidsqlite.database.CategoryRepository;
import com.example.moneytrackerandroidsqlite.database.TransactionRepository;
import com.example.moneytrackerandroidsqlite.databinding.ActivityEditTransactionBinding;
import com.example.moneytrackerandroidsqlite.models.Category;
import com.example.moneytrackerandroidsqlite.models.Transaction;
import com.example.moneytrackerandroidsqlite.utils.AuthManager;

import java.util.Calendar;
import java.util.Locale;

public class EditTransactionActivity extends AppCompatActivity {
    ActivityEditTransactionBinding binding;
    private EditText etAmount, etNote;
    private Button dateBtn;
    private Button cateBtn;
    private TransactionRepository transactionRepository;
    private CategoryRepository categoryRepository;
    private Calendar selectedDate;
    private AuthManager authManager;
    Long txId;
    Long selectedCategoryId;
    String selectedCategoryType;
    Transaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityEditTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.edit_tx), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        txId = getIntent().getLongExtra("TRANSACTION_EDIT_ID", -1);

        if (txId == -1) {
            Toast.makeText(this, "Transaction id not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        authManager = AuthManager.getInstance(this);
        transactionRepository = new TransactionRepository(this);
        categoryRepository = new CategoryRepository(this);
        transaction = transactionRepository.getTxById(authManager.getCurrentUser().getId(), txId);

        etAmount = binding.editTextAmount;
        etNote = binding.editTextNotes;
        dateBtn = binding.buttonDate;
        cateBtn = binding.buttonSelectCate;
        Button updateTxBtn = binding.updateTxBtn;
        selectedDate = Calendar.getInstance();

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSelectDate();
            }
        });

        updateTxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleUpdateTx();
            }
        });

        cateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditTransactionActivity.this, CategoryActivity.class);
                onSelectedCategory.launch(intent);
            }
        });

        loadTransactionData(transaction);
    }

    private void loadTransactionData(Transaction transaction) {
        if (transaction != null) {
            etAmount.setText(String.valueOf(transaction.getAmount()));
            etNote.setText(transaction.getNotes());

            // Set date
            selectedDate.setTimeInMillis(transaction.getDate());
            updateDateDisplay();

            // Set category
            selectedCategoryId = transaction.getCategoryId();
            Category category = categoryRepository.getCategoryById(selectedCategoryId);
            if (category != null) {
                cateBtn.setText(category.getName());
                selectedCategoryType = category.getType().toString();
            }
        } else {
            Toast.makeText(this, "Error: Transaction not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateDateDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        dateBtn.setText(dateFormat.format(selectedDate.getTime()));
    }

    private void handleUpdateTx() {
        if (etAmount.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }
        double amount = Double.parseDouble(etAmount.getText().toString());
        String notes = etNote.getText().toString();
        long dateMillis = selectedDate.getTimeInMillis();

        boolean success = transactionRepository.updateTransaction(
                txId,
                selectedCategoryId,
                amount,
                selectedCategoryType,
                notes,
                dateMillis
        );

        if (success) {
            Toast.makeText(this, "Transaction updated successfully", Toast.LENGTH_SHORT).show();
            Intent resIntent = new Intent();
            this.setResult(RESULT_OK, resIntent);
            finish(); // Close activity and go back
        } else {
            Toast.makeText(this, "Failed to update transaction", Toast.LENGTH_SHORT).show();
        }
    }

    ActivityResultLauncher<Intent> onSelectedCategory = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode() == CategoryActivity.RESULT_OK) {
                        Intent data = o.getData();
                        String categoryName = data.getStringExtra("SELECTED_CATEGORY_NAME");
                        selectedCategoryId = data.getLongExtra("SELECTED_CATEGORY_ID", -1);
                        cateBtn.setText(categoryName);

                        // Update category type
                        Category category = categoryRepository.getCategoryById(selectedCategoryId);
                        if (category != null) {
                            selectedCategoryType = category.getType().toString();
                        }
                    }
                }
            }
    );

    private void buttonSelectDate() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                selectedDate.set(Calendar.YEAR, year);
                selectedDate.set(Calendar.MONTH, month);
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateDisplay();
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener,
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}