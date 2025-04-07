package com.example.moneytrackerandroidsqlite.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.moneytrackerandroidsqlite.fragments.ExpenseFragment;
import com.example.moneytrackerandroidsqlite.fragments.IncomeFragment;

public class CategoryViewPagerAdapter extends FragmentStateAdapter {
    public CategoryViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ExpenseFragment();
            case 1:
                return new IncomeFragment();
            default:
                return new ExpenseFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
