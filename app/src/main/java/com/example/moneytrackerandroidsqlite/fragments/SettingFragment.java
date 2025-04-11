package com.example.moneytrackerandroidsqlite.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moneytrackerandroidsqlite.R;
import com.example.moneytrackerandroidsqlite.activities.CategoryEditActivity;
import com.example.moneytrackerandroidsqlite.activities.LoginActivity;
import com.example.moneytrackerandroidsqlite.utils.AuthManager;

public class SettingFragment extends Fragment {
    AuthManager authManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        authManager = AuthManager.getInstance(getContext());

        view.findViewById(R.id.edit_profile_option).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        view.findViewById(R.id.category_option).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CategoryEditActivity.class));
            }
        });

        view.findViewById(R.id.logout_option).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogout();
            }
        });
        return view;
    }

    private void handleLogout() {
        authManager.logout();
        startActivity(new Intent(getContext(), LoginActivity.class));
    }

}