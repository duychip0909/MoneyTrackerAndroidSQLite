package com.example.moneytrackerandroidsqlite.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moneytrackerandroidsqlite.R;
import com.example.moneytrackerandroidsqlite.adapters.CategoryViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;


public class TestFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    CategoryViewPagerAdapter categoryViewPagerAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test, container, false);

        tabLayout = view.findViewById(R.id.tab_layout_frag);
        viewPager2 = view.findViewById(R.id.view_pager_frag);
        categoryViewPagerAdapter = new CategoryViewPagerAdapter(getActivity(), true);
        viewPager2.setAdapter(categoryViewPagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });

        return view;
    }
}