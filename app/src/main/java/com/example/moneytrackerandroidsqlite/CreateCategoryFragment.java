package com.example.moneytrackerandroidsqlite;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.moneytrackerandroidsqlite.database.CategoryRepository;
import com.example.moneytrackerandroidsqlite.databinding.FragmentCreateCategoryBinding;
import com.example.moneytrackerandroidsqlite.models.Category;
import com.example.moneytrackerandroidsqlite.utils.AuthManager;

public class CreateCategoryFragment extends Fragment {
    CategoryRepository categoryRepository;
    AuthManager authManager;
    FragmentCreateCategoryBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateCategoryBinding.inflate(inflater, container, false);

        categoryRepository = new CategoryRepository(getContext());
        authManager = AuthManager.getInstance(getContext());

        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCreateCate();
            }
        });

        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        return binding.getRoot();
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
            Toast.makeText(getContext(), "Category created successfully", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        }
    }
}