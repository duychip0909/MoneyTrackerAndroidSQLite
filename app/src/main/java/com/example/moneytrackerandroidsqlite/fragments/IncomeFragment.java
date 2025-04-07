package com.example.moneytrackerandroidsqlite.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moneytrackerandroidsqlite.R;
import com.example.moneytrackerandroidsqlite.adapters.ExpenseCategoryAdapter;
import com.example.moneytrackerandroidsqlite.database.CategoryRepository;
import com.example.moneytrackerandroidsqlite.models.Category;
import com.example.moneytrackerandroidsqlite.utils.AuthManager;

import java.util.List;


public class IncomeFragment extends Fragment {
    RecyclerView incomeCateRv;
    ExpenseCategoryAdapter icAdapter;
    CategoryRepository categoryRepository;
    List<Category> incomeCates;
    AuthManager authManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_income, container, false);
        categoryRepository = new CategoryRepository(view.getContext());
        incomeCateRv = view.findViewById(R.id.income_rv);
        authManager = AuthManager.getInstance(view.getContext());
        incomeCates = categoryRepository.getCategoriesByType(authManager.getCurrentUser().getId(), Category.Type.INCOME);
        incomeCateRv.setLayoutManager(new LinearLayoutManager(view.getContext()));
        icAdapter = new ExpenseCategoryAdapter(view.getContext(), incomeCates);
        incomeCateRv.setAdapter(icAdapter);
        return view;
    }
}