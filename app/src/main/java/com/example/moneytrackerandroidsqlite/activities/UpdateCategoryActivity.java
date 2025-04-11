package com.example.moneytrackerandroidsqlite.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.moneytrackerandroidsqlite.R;
import com.example.moneytrackerandroidsqlite.database.CategoryRepository;
import com.example.moneytrackerandroidsqlite.databinding.ActivityUpdateCategoryBinding;
import com.example.moneytrackerandroidsqlite.models.Category;

public class UpdateCategoryActivity extends AppCompatActivity {
    ActivityUpdateCategoryBinding binding;
    Long cateId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityUpdateCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.update_cate), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cateId = getIntent().getLongExtra("CATE_ID", -1);
        if (cateId == -1) {
            Toast.makeText(this, "Category id not found", Toast.LENGTH_SHORT).show();
            finish();
        }
        CategoryRepository categoryRepository = new CategoryRepository(this);
        Category category = categoryRepository.getCategoryById(cateId);
        loadCategoryData(category);

        binding.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.categoryNameEditText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(UpdateCategoryActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                    return;
                }
                String nameData = binding.categoryNameEditText.getText().toString().trim();
                int selectedId = binding.categoryTypeRadioGroup.getCheckedRadioButtonId();

                String selectedType;

                if (selectedId == R.id.expenseRadioButton) {
                    selectedType = "EXPENSE";
                } else if (selectedId == R.id.incomeRadioButton) {
                    selectedType = "INCOME";
                } else {
                    selectedType = null;
                }
                boolean success = categoryRepository.updateCategory(cateId, nameData, selectedType);

                if (success) {
                    Toast.makeText(UpdateCategoryActivity.this, "Category updated successfully", Toast.LENGTH_SHORT).show();
                    Intent resIntent = new Intent();
                    setResult(RESULT_OK, resIntent);
                    finish();
                } else {
                    Toast.makeText(UpdateCategoryActivity.this, "Failed to update category", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadCategoryData(Category category) {
        if (category != null) {
            binding.categoryNameEditText.setText(String.valueOf(category.getName()));
            if (category.getType() == Category.Type.EXPENSE) {
                binding.expenseRadioButton.setChecked(true);
            } else if (category.getType() == Category.Type.INCOME) {
                binding.incomeRadioButton.setChecked(true);
            }
        } else {
            Toast.makeText(this, "Error: Category not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}