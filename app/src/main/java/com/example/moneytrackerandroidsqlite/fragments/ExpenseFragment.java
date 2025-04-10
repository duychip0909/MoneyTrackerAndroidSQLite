package com.example.moneytrackerandroidsqlite.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moneytrackerandroidsqlite.CategoryDetailActivity;
import com.example.moneytrackerandroidsqlite.CreateCategoryFragment;
import com.example.moneytrackerandroidsqlite.EditCategoryFragment;
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
    Boolean isEdit;
    AuthManager authManager;
    public ExpenseFragment(Boolean isEdit) {
        this.isEdit = isEdit;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expense, container, false);
        categoryRepository = new CategoryRepository(getActivity());
        expenseCateRv = view.findViewById(R.id.expense_rv);
        authManager = AuthManager.getInstance(getActivity());
        expenseCateRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadCates();

        view.findViewById(R.id.cardCateAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(view.getContext(), CreateCategoryActivity.class));
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new CreateCategoryFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    private void loadCates() {
        List<Category> expenseCates;
        expenseCates = categoryRepository.getCategoriesByType(authManager.getCurrentUser().getId(), Category.Type.EXPENSE);
        if (ecAdapter == null) {
            ecAdapter = new ExpenseCategoryAdapter(getActivity(), expenseCates, new ExpenseCategoryAdapter.OnCategoryClickListener() {
                @Override
                public void onCategoryClick(Category category) {
                    if (getActivity() != null) {
                        if (isEdit) {
                            Intent intent = new Intent(getActivity(), CategoryDetailActivity.class);
                            intent.putExtra("CATE_ID", category.getId());
                            startActivity(intent);
                        } else {
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("SELECTED_CATEGORY_NAME", category.getName());
                            resultIntent.putExtra("SELECTED_CATEGORY_ID", category.getId());
                            getActivity().setResult(Activity.RESULT_OK, resultIntent);
                            getActivity().finish();
                        }
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