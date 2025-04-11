package com.example.moneytrackerandroidsqlite;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.moneytrackerandroidsqlite.database.CategoryRepository;
import com.example.moneytrackerandroidsqlite.databinding.ActivityCreateCategoryBinding;
import com.example.moneytrackerandroidsqlite.models.Category;
import com.example.moneytrackerandroidsqlite.utils.AuthManager;

public class CreateCategoryActivity extends AppCompatActivity {
    CategoryRepository categoryRepository;
    AuthManager authManager;
    ActivityCreateCategoryBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCreateCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.create_cate_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        categoryRepository = new CategoryRepository(this);
        authManager = AuthManager.getInstance(this);

        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCreateCate();
            }
        });

        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private Category.Type getCategoryType() {
        RadioGroup categoryTypeRadioGroup = binding.categoryTypeRadioGroup;
        int selectedRadioButtonId = categoryTypeRadioGroup.getCheckedRadioButtonId();

        if (selectedRadioButtonId == R.id.incomeRadioButton) {
            return Category.Type.INCOME;
        } else if (selectedRadioButtonId == R.id.expenseRadioButton) {
            return Category.Type.EXPENSE;
        } else {
            return Category.Type.EXPENSE;
        }
    }

    private void handleCreateCate() {
        String name = binding.categoryNameEditText.getText().toString().trim();
        Category.Type type = getCategoryType();
        if (name.isEmpty()) {
            binding.categoryNameEditText.setError("Category name is required");
            return;
        }
        Category category = new Category();
        category.setName(name);
        category.setType(type);
        category.setUserId(authManager.getCurrentUser().getId());
        long res = categoryRepository.createCategory(category);
        if (res != -1) {
            Toast.makeText(this, "Category created successfully", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}