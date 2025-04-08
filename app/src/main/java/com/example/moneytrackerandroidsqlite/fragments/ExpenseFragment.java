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

import com.example.moneytrackerandroidsqlite.R;
import com.example.moneytrackerandroidsqlite.adapters.ExpenseCategoryAdapter;
import com.example.moneytrackerandroidsqlite.database.CategoryRepository;
import com.example.moneytrackerandroidsqlite.models.Category;
import com.example.moneytrackerandroidsqlite.utils.AuthManager;

import java.util.List;


public class ExpenseFragment extends Fragment {
    RecyclerView expenseCateRv;
    ExpenseCategoryAdapter ecAdapter;
    CategoryRepository categoryRepository;
    List<Category> expenseCates;
    AuthManager authManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expense, container, false);
        categoryRepository = new CategoryRepository(view.getContext());
        expenseCateRv = view.findViewById(R.id.expense_rv);
        authManager = AuthManager.getInstance(view.getContext());
        expenseCates = categoryRepository.getCategoriesByType(authManager.getCurrentUser().getId(), Category.Type.EXPENSE);
        expenseCateRv.setLayoutManager(new LinearLayoutManager(getContext()));
        ecAdapter = new ExpenseCategoryAdapter(view.getContext(), expenseCates, new ExpenseCategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(Category category) {
                if (getActivity() != null) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("SELECTED_CATEGORY", category.getName());
                    getActivity().setResult(getActivity().RESULT_OK, resultIntent);
                    getActivity().finish();
                }
            }
        });
        expenseCateRv.setAdapter(ecAdapter);
        return view;
    }
}