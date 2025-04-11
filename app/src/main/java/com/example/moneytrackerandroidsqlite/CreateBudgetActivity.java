package com.example.moneytrackerandroidsqlite;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
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

import com.example.moneytrackerandroidsqlite.activities.CategoryActivity;
import com.example.moneytrackerandroidsqlite.database.BudgetRepository;
import com.example.moneytrackerandroidsqlite.databinding.ActivityCreateBudgetBinding;
import com.example.moneytrackerandroidsqlite.models.Budget;
import com.example.moneytrackerandroidsqlite.utils.AuthManager;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.Date;
import java.util.Calendar;
import java.util.Locale;

public class CreateBudgetActivity extends AppCompatActivity {
    private ActivityCreateBudgetBinding binding;
    private TextInputEditText etBudgetAmount;
    private LinearLayout layoutStartDate, layoutEndDate;
    private RadioGroup rgPeriodType;
    private TextView tvStartDate, tvEndDate, tvBudgetFormTitle;
    private Button btnSaveBudget, btnCancel;

//    private FinanceDBHelper dbHelper;

    private Budget existingBudget;
    private long userId;
    private boolean isEditMode = false;
    private AuthManager authManager;
    private long selectedCategoryId;

    private long startDate;
    private long endDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCreateBudgetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.action_budget), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        authManager = AuthManager.getInstance(this);
        userId = authManager.getCurrentUser().getId();
        initViews();
        if (getIntent().hasExtra("BUDGET_ID")) {
            isEditMode = true;
            long budgetId = getIntent().getLongExtra("BUDGET_ID", -1);
            loadBudgetData(budgetId);
            tvBudgetFormTitle.setText("Edit Budget");
            btnSaveBudget.setText("Update Budget");
        } else {
            // Set default dates for new budget
            setupDefaultDates();
        }

        binding.spinnerCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateBudgetActivity.this, CategoryActivity.class);
                onSelectedCategory.launch(intent);
            }
        });

        setupListeners();
    }

    private void setupDefaultDates() {
        Calendar calendar = Calendar.getInstance();
        startDate = calendar.getTimeInMillis();
        setupEndDate();
        updateDateDisplay();
    }

    private void loadBudgetData(long budgetId) {
        
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
                        binding.spinnerCategory.setText(categoryName);
                    }
                }
            }
    );

    private void initViews() {
        tvBudgetFormTitle = binding.tvBudgetFormTitle;
        etBudgetAmount = binding.etBudgetAmount;
        rgPeriodType = binding.rgPeriodType;
        tvStartDate = binding.tvStartDate;
        tvEndDate = binding.tvEndDate;
        btnSaveBudget = binding.btnSaveBudget;
        btnCancel = binding.btnCancel;
        layoutStartDate = binding.layoutStartDate;
        layoutEndDate = binding.layoutEndDate;
    }

    private void setupEndDate() {
        if (startDate == -1) return;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startDate);

        int checkedId = rgPeriodType.getCheckedRadioButtonId();
        if (checkedId == R.id.rbWeekly) {
            calendar.add(Calendar.WEEK_OF_MONTH, 1);
        } else if (checkedId == R.id.rbMonthly) {
            calendar.add(Calendar.MONTH, 1);
        } else if (checkedId == R.id.rbYearly) {
            calendar.add(Calendar.YEAR, 1);
        }

        // Subtract one day to make it inclusive of the end date
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        endDate = calendar.getTimeInMillis();
    }

    private void updateDateDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY", Locale.getDefault());

        if (startDate != -1) {
            tvStartDate.setText(dateFormat.format(startDate));
        }

        if (endDate != -1) {
            tvEndDate.setText(dateFormat.format(endDate));
        }
    }

    private void setupListeners() {
        rgPeriodType.setOnCheckedChangeListener((group, checkedId) -> {
            setupEndDate();
            updateDateDisplay();
        });

        layoutStartDate.setOnClickListener(v -> showDatePickerDialog(true));

        layoutEndDate.setOnClickListener(v -> showDatePickerDialog(false));

        // Save button
        btnSaveBudget.setOnClickListener(v -> saveBudget());

        // Cancel button
        btnCancel.setOnClickListener(v -> finish());
    }

    private void showDatePickerDialog(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        if (!isStartDate && endDate != -1) {
            calendar.setTimeInMillis(endDate);
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    if (isStartDate) {
                        startDate = calendar.getTimeInMillis();
                        setupEndDate();
                    } else {
                        endDate = calendar.getTimeInMillis();
                    }
                    updateDateDisplay();
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void saveBudget() {
        if (!validateInputs()) {
            return;
        }

        Budget budget = isEditMode ? existingBudget : new Budget();
        budget.setUserId(userId);
        budget.setCategoryId(selectedCategoryId);
        budget.setAmount(Double.parseDouble(etBudgetAmount.getText().toString()));

        // Get period type
        int checkedId = rgPeriodType.getCheckedRadioButtonId();
        if (checkedId == R.id.rbWeekly) {
            budget.setPeriodType(Budget.PeriodType.WEEKLY);
        } else if (checkedId == R.id.rbMonthly) {
            budget.setPeriodType(Budget.PeriodType.MONTHLY);
        } else if (checkedId == R.id.rbYearly) {
            budget.setPeriodType(Budget.PeriodType.YEARLY);
        }

        budget.setStartDate(startDate);
        budget.setEndDate(endDate);

        BudgetRepository budgetRepository = new BudgetRepository(this);
        boolean success;

        if (isEditMode) {
            int rowsAffected = budgetRepository.updateBudget(budget);
            success = rowsAffected > 0;
        } else {
            long budgetId = budgetRepository.createBudget(budget);
            success = budgetId != -1;
        }

        if (success) {
            Toast.makeText(this, isEditMode ? "Budget updated" : "Budget created", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Error saving budget", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputs() {
        String amountStr = etBudgetAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            etBudgetAmount.setError("Amount is required");
            return false;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                etBudgetAmount.setError("Amount must be greater than zero");
                return false;
            }
        } catch (NumberFormatException e) {
            etBudgetAmount.setError("Invalid amount");
            return false;
        }

        if (selectedCategoryId == -1) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (startDate == -1) {
            Toast.makeText(this, "Please select a start date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (endDate == -1) {
            Toast.makeText(this, "Please select an end date", Toast.LENGTH_SHORT).show();
            return false;
        }
        int compRes = Long.compare(endDate, startDate);
        if (compRes < 0) {
            Toast.makeText(this, "End date cannot be before start date", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}