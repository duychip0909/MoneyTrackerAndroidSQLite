package com.example.moneytrackerandroidsqlite;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.moneytrackerandroidsqlite.database.UserRepository;
import com.example.moneytrackerandroidsqlite.models.User;
import com.example.moneytrackerandroidsqlite.utils.AuthManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.mindrot.jbcrypt.BCrypt;

public class ChangePasswordActivity extends AppCompatActivity {
    private TextInputLayout tilCurrentPassword, tilNewPassword, tilConfirmPassword;
    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private Button btnChangePassword;
    private ProgressBar progressBar;
    private UserRepository userRepository;
    private AuthManager authManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.change_password), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tilCurrentPassword = findViewById(R.id.tilCurrentPassword);
        tilNewPassword = findViewById(R.id.tilNewPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        progressBar = findViewById(R.id.progressBar);

        userRepository = new UserRepository(this);
        authManager = AuthManager.getInstance(this);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    private void changePassword() {
        tilCurrentPassword.setError(null);
        tilNewPassword.setError(null);
        tilConfirmPassword.setError(null);
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(currentPassword)) {
            tilCurrentPassword.setError("Enter your current password");
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            tilNewPassword.setError("Enter a new password");
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("Confirm your new password");
            return;
        }

        if (newPassword.length() < 6) {
            tilNewPassword.setError("Password must be at least 6 characters long");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            return;
        }

        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        btnChangePassword.setEnabled(false);

        // Get current user
        long userId = authManager.getCurrentUser().getId();
        User currentUser = userRepository.getUserById(userId);

        if (currentUser != null) {
            if (BCrypt.checkpw(currentPassword, currentUser.getPassword())) {
                String hashedNewPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
                currentUser.setPassword(hashedNewPassword);
                boolean success = userRepository.updateUser(currentUser);

                if (success) {
                    Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity and go back
                } else {
                    Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show();
                }
            } else {
                tilCurrentPassword.setError("Current password is incorrect");
            }
        } else {
            Toast.makeText(this, "User session error", Toast.LENGTH_SHORT).show();
        }
        progressBar.setVisibility(View.GONE);
        btnChangePassword.setEnabled(true);
    }
}