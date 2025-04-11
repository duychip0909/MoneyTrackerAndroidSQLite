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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.moneytrackerandroidsqlite.R;
import com.example.moneytrackerandroidsqlite.database.CategoryRepository;
import com.example.moneytrackerandroidsqlite.database.TransactionRepository;
import com.example.moneytrackerandroidsqlite.databinding.ActivityCreateTransactionBinding;
import com.example.moneytrackerandroidsqlite.models.Category;
import com.example.moneytrackerandroidsqlite.utils.AuthManager;

import java.util.Calendar;
import java.util.Locale;

public class CreateTransactionActivity extends AppCompatActivity {
    private ActivityCreateTransactionBinding binding;
    private EditText etAmount, etNote;
    private Button dateBtn, cateBtn, createTxBtn;
    private TransactionRepository transactionRepository;
    private CategoryRepository categoryRepository;
    private Calendar selectedDate;
    private AuthManager authManager;
    private Long selectedCategoryId;
    private String selectedCategoryType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCreateTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.create_tx), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        etAmount = binding.editTextAmount;
        etNote = binding.editTextNotes;
        cateBtn = binding.buttonSelectCate;
        dateBtn = binding.buttonDate;
        createTxBtn = binding.createTxBtn;

        transactionRepository = new TransactionRepository(this);
        categoryRepository = new CategoryRepository(this);
        authManager = AuthManager.getInstance(this);

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSelectDate();
            }
        });

        createTxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCreateTx();
            }
        });

        cateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateTransactionActivity.this, CategoryActivity.class);
                onSelectedCategory.launch(intent);
            }
        });

        selectedDate = Calendar.getInstance();
        updateDateDisplay();
    }

    private void updateDateDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY", Locale.getDefault());
        dateBtn.setText(dateFormat.format(selectedDate.getTime()));
    }

    private void handleCreateTx() {
        if (etAmount.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }
        double amount = Double.parseDouble(etAmount.getText().toString());
        String notes = etNote.getText().toString();
        long dateMillis = selectedDate.getTimeInMillis();
        Category category = categoryRepository.getCategoryById(selectedCategoryId);
        boolean success = transactionRepository.addTransaction(authManager.getCurrentUser().getId(), selectedCategoryId, amount, category.getType().toString(), notes, dateMillis);
        if (success) {
            Toast.makeText(this, "Transaction saved successfully", Toast.LENGTH_SHORT).show();
            finish(); // Close activity and go back
        } else {
            Toast.makeText(this, "Failed to save transaction", Toast.LENGTH_SHORT).show();
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
                    }
                }
            }
    );

    private void buttonSelectDate() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                dateBtn.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                selectedDate.set(Calendar.YEAR, year);
                selectedDate.set(Calendar.MONTH, month);
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateDisplay();
//                lastSelectedYear = year;
//                lastSelectedMonth = monthOfYear;
//                lastSelectedDayOfMonth = dayOfMonth;
            }
        };

        DatePickerDialog datePickerDialog = null;
        datePickerDialog = new DatePickerDialog(this, dateSetListener, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}