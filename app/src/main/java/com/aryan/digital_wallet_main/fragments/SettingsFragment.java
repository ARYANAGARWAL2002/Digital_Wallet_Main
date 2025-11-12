package com.aryan.digital_wallet_main.fragments;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.aryan.digital_wallet_main.R;
import com.aryan.digital_wallet_main.activities.LoginActivity;
import com.aryan.digital_wallet_main.utils.SecurityHelper;
public class SettingsFragment extends Fragment {
    private TextView textViewName;
    private TextView textViewEmail;
    private Button buttonLogout;
    private Button buttonExport;
    private Button buttonImport;

    private Button buttonEditProfile;
    private Button buttonChangePassword;
    private SecurityHelper securityHelper;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        securityHelper = new SecurityHelper(requireContext());

        textViewName = view.findViewById(R.id.text_view_name);
        textViewEmail = view.findViewById(R.id.text_view_email);
        buttonLogout = view.findViewById(R.id.button_logout);
        buttonExport = view.findViewById(R.id.button_export);
        buttonImport = view.findViewById(R.id.button_import);
        buttonEditProfile = view.findViewById(R.id.button_edit_profile);
        buttonChangePassword = view.findViewById(R.id.button_change_password);

        textViewName.setText(securityHelper.getCurrentUserName());
        textViewEmail.setText(securityHelper.getCurrentUserEmail());

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        buttonExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportData();
            }
        });

        buttonImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importData();
            }
        });



        buttonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });

        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog();
            }
        });

        return view;
    }

    private void logout() {
        securityHelper.clearUserData();
        securityHelper.setLoggedIn(false, "");
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void exportData() {
        Toast.makeText(requireContext(), "Export functionality will be implemented in future updates", Toast.LENGTH_SHORT).show();
    }

    private void importData() {
        Toast.makeText(requireContext(), "Import functionality will be implemented in future updates", Toast.LENGTH_SHORT).show();
    }



    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Profile");

        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null);
        final EditText editTextName = view.findViewById(R.id.edit_text_name);
        final EditText editTextEmail = view.findViewById(R.id.edit_text_email);


        editTextName.setText(securityHelper.getCurrentUserName());
        editTextEmail.setText(securityHelper.getCurrentUserEmail());

        builder.setView(view);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = editTextName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();

                if (name.isEmpty() || email.isEmpty()) {
                    Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (securityHelper.updateUserProfile(name, email)) {

                    textViewName.setText(name);
                    textViewEmail.setText(email);
                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Change Password");

        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_change_password, null);
        final EditText editTextCurrentPassword = view.findViewById(R.id.edit_text_current_password);
        final EditText editTextNewPassword = view.findViewById(R.id.edit_text_new_password);
        final EditText editTextConfirmPassword = view.findViewById(R.id.edit_text_confirm_password);

        builder.setView(view);

        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String currentPassword = editTextCurrentPassword.getText().toString().trim();
                String newPassword = editTextNewPassword.getText().toString().trim();
                String confirmPassword = editTextConfirmPassword.getText().toString().trim();

                if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(requireContext(), "New passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (securityHelper.changePassword(currentPassword, newPassword)) {
                    Toast.makeText(requireContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Failed to change password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }
}