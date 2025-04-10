package com.example.moneytrackerandroidsqlite.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moneytrackerandroidsqlite.CategoryDetailActivity;
import com.example.moneytrackerandroidsqlite.CreateCategoryFragment;
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
    AuthManager authManager;
    Boolean isEdit;
    public IncomeFragment(Boolean isEdit) {
        this.isEdit = isEdit;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_income, container, false);
        categoryRepository = new CategoryRepository(view.getContext());
        incomeCateRv = view.findViewById(R.id.income_rv);
        authManager = AuthManager.getInstance(view.getContext());
        incomeCateRv.setLayoutManager(new LinearLayoutManager(view.getContext()));
        loadCates();

        view.findViewById(R.id.cardCateAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getContext(), CreateCategoryActivity.class));
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new CreateCategoryFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCates();
    }

    private void loadCates() {
        List<Category> incomeCates;
        incomeCates = categoryRepository.getCategoriesByType(authManager.getCurrentUser().getId(), Category.Type.INCOME);
        if (icAdapter == null) {
            icAdapter = new ExpenseCategoryAdapter(getActivity(), incomeCates, new ExpenseCategoryAdapter.OnCategoryClickListener() {
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
            incomeCateRv.setAdapter(icAdapter);
        } else {
            icAdapter.setCategories(incomeCates);
        }
    }
}