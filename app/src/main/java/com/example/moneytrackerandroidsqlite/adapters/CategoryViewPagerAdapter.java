package com.example.moneytrackerandroidsqlite.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.moneytrackerandroidsqlite.fragments.ExpenseFragment;
import com.example.moneytrackerandroidsqlite.fragments.IncomeFragment;

public class CategoryViewPagerAdapter extends FragmentStateAdapter {
    Boolean isEdit;
    public CategoryViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, Boolean isEdit) {
        super(fragmentActivity);
        this.isEdit = isEdit;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ExpenseFragment(isEdit);
            case 1:
                return new IncomeFragment(isEdit);
            default:
                return new ExpenseFragment(isEdit);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
