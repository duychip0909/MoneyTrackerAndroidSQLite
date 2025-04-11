package com.example.moneytrackerandroidsqlite.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.moneytrackerandroidsqlite.R;
import com.example.moneytrackerandroidsqlite.database.CategoryRepository;
import com.example.moneytrackerandroidsqlite.databinding.ActivityCategoryDetailBinding;
import com.example.moneytrackerandroidsqlite.models.Category;

public class CategoryDetailActivity extends AppCompatActivity {
    ActivityCategoryDetailBinding binding;
    Long cateId;
    private final ActivityResultLauncher<Intent> editCategoryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode() == RESULT_OK) {
                        loadCategoryData();
                    }
                }
            }
    );

    private void loadCategoryData() {
        CategoryRepository categoryRepository = new CategoryRepository(this);
        Category category = categoryRepository.getCategoryById(cateId);
        if (category != null) {
            binding.tvCategoryName.setText(category.getName());
            binding.tvCategoryType.setText(String.valueOf(category.getType()));
        } else {
            Toast.makeText(this, "Category not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

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

        cateId = getIntent().getLongExtra("CATE_ID", -1);
        if (cateId == -1) {
            finish();
        }
        CategoryRepository categoryRepository = new CategoryRepository(this);
        loadCategoryData();
        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CategoryDetailActivity.this);
                builder.setTitle("Delete Category");
                builder.setMessage("Are you sure you want to delete this category? This action cannot be undone.");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (cateId == -1) {
                            Toast.makeText(CategoryDetailActivity.this, "Error: Invalid category", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        boolean success = categoryRepository.deleteCategory(cateId);
                        if (success) {
                            Toast.makeText(CategoryDetailActivity.this, "Category deleted successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(CategoryDetailActivity.this, "Failed to delete category", Toast.LENGTH_SHORT).show();
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
        });
        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoryDetailActivity.this, UpdateCategoryActivity.class);
                intent.putExtra("CATE_ID", cateId);
                editCategoryLauncher.launch(intent);
            }
        });
    }
}