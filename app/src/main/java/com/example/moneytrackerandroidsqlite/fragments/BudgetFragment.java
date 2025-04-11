package com.example.moneytrackerandroidsqlite.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moneytrackerandroidsqlite.BudgetDetailActivity;
import com.example.moneytrackerandroidsqlite.CreateBudgetActivity;
import com.example.moneytrackerandroidsqlite.R;
import com.example.moneytrackerandroidsqlite.adapters.BudgetAdapter;
import com.example.moneytrackerandroidsqlite.database.BudgetRepository;
import com.example.moneytrackerandroidsqlite.databinding.FragmentBudgetBinding;
import com.example.moneytrackerandroidsqlite.models.Budget;
import com.example.moneytrackerandroidsqlite.utils.AuthManager;

import java.util.List;

public class BudgetFragment extends Fragment {
    FragmentBudgetBinding binding;
    RecyclerView rvBudget;
    BudgetAdapter budgetAdapter;
    BudgetRepository budgetRepository;
    AuthManager authManager;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBudgetBinding.inflate(inflater, container, false);

        budgetRepository = new BudgetRepository(getActivity());
        authManager = AuthManager.getInstance(getActivity());
        rvBudget = binding.rvBudgets;

        rvBudget.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadBudgets();
        binding.btnCreateBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateBudgetActivity.class);
                startActivity(intent);
            }
        });

        return binding.getRoot();
    }

    private void loadBudgets() {
        List<Budget> budgets = budgetRepository.getBudgetsByUserId(authManager.getCurrentUser().getId());
        if (budgetAdapter == null) {
            budgetAdapter = new BudgetAdapter(getActivity(), budgets, new BudgetAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Long budgetId) {
                    Intent intent = new Intent(getActivity(), BudgetDetailActivity.class);
                    intent.putExtra("BUDGET_ID", budgetId);
                    startActivity(intent);
                }
            });
            rvBudget.setAdapter(budgetAdapter);
        } else {
            budgetAdapter.setBudgets(budgets);
            budgetAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBudgets();
    }
}