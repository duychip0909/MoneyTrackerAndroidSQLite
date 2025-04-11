package com.example.moneytrackerandroidsqlite.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.moneytrackerandroidsqlite.R;
import com.example.moneytrackerandroidsqlite.database.UserRepository;
import com.example.moneytrackerandroidsqlite.databinding.ActivityLoginBinding;
import com.example.moneytrackerandroidsqlite.models.User;
import com.example.moneytrackerandroidsqlite.utils.AuthManager;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    TextView tvRegister;
    TextInputEditText username, password;
    Button loginBtn;
    UserRepository userRepository;
    AuthManager authManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_screen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvRegister = binding.tvRegisterPrompt;
        username = binding.etLoginUsername;
        password = binding.etLoginPassword;
        loginBtn = binding.btnLogin;
        authManager = AuthManager.getInstance(this);
        userRepository = new UserRepository(this);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        if (authManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            return;
        }
    }

    private void handleLogin() {
        String usernameData = username.getText().toString().trim();
        String passwordData = password.getText().toString().trim();

        if (TextUtils.isEmpty(usernameData)) {
            username.setError("Username is required");
            username.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(passwordData)) {
            username.setError("Password is required");
            username.requestFocus();
            return;
        }

        User user = userRepository.authenticate(usernameData, passwordData);

        if (user != null) {
            authManager.setLoggedInUser(user);
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}