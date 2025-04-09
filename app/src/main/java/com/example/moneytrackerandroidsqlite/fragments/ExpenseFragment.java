package com.example.moneytrackerandroidsqlite.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moneytrackerandroidsqlite.CreateCategoryActivity;
import com.example.moneytrackerandroidsqlite.R;
import com.example.moneytrackerandroidsqlite.adapters.ExpenseCategoryAdapter;
import com.example.moneytrackerandroidsqlite.database.CategoryRepository;
import com.example.moneytrackerandroidsqlite.models.Category;
import com.example.moneytrackerandroidsqlite.utils.AuthManager;

import java.util.ArrayList;
import java.util.List;


public class ExpenseFragment extends Fragment {
    RecyclerView expenseCateRv;
    ExpenseCategoryAdapter ecAdapter;
    CategoryRepository categoryRepository;
    AuthManager authManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expense, container, false);
        categoryRepository = new CategoryRepository(view.getContext());
        expenseCateRv = view.findViewById(R.id.expense_rv);
        authManager = AuthManager.getInstance(view.getContext());
        expenseCateRv.setLayoutManager(new LinearLayoutManager(getContext()));
        loadCates();

        view.findViewById(R.id.cardCateAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(view.getContext(), CreateCategoryActivity.class));
            }
        });

        return view;
    }

    private void loadCates() {
        List<Category> expenseCates;
        expenseCates = categoryRepository.getCategoriesByType(authManager.getCurrentUser().getId(), Category.Type.EXPENSE);
        if (ecAdapter == null) {
            ecAdapter = new ExpenseCategoryAdapter(getContext(), expenseCates, new ExpenseCategoryAdapter.OnCategoryClickListener() {
                @Override
                public void onCategoryClick(Category category) {
                    if (getActivity() != null) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("SELECTED_CATEGORY_NAME", category.getName());
                        resultIntent.putExtra("SELECTED_CATEGORY_ID", category.getId());
                        getActivity().setResult(getActivity().RESULT_OK, resultIntent);
                        getActivity().finish();
                    }
                }
            });
            expenseCateRv.setAdapter(ecAdapter);
        } else {
            ecAdapter.setCategories(expenseCates);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCates();
    }
}