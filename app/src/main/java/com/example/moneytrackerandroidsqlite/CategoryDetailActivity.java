package com.example.moneytrackerandroidsqlite;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.moneytrackerandroidsqlite.database.CategoryRepository;
import com.example.moneytrackerandroidsqlite.databinding.ActivityCategoryDetailBinding;
import com.example.moneytrackerandroidsqlite.models.Category;

public class CategoryDetailActivity extends AppCompatActivity {
    ActivityCategoryDetailBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCategoryDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.category_detail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        long cateId = getIntent().getLongExtra("CATE_ID", -1);
        if (cateId == -1) {
            finish();
        }
        CategoryRepository categoryRepository = new CategoryRepository(this);
        Category category = categoryRepository.getCategoryById(cateId);
        binding.tvCategoryName.setText(category.getName());
        binding.tvCategoryType.setText(String.valueOf(category.getType()));
        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}