package com.aryan.digital_wallet_main.activities;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;
import com.aryan.digital_wallet_main.R;
import com.aryan.digital_wallet_main.utils.SecurityHelper;
public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;
    private SecurityHelper securityHelper;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        securityHelper = new SecurityHelper(this);
        if (securityHelper.isLoggedIn()) {
            Log.d(TAG, "User already logged in, redirecting to MainActivity");
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        LottieAnimationView lottieAnimation = findViewById(R.id.lottie_animation);
        editTextEmail = findViewById(R.id.edit_text_email);
        editTextPassword = findViewById(R.id.edit_text_password);
        buttonLogin = findViewById(R.id.button_login);
        textViewRegister = findViewById(R.id.text_view_register);


        lottieAnimation.setAnimation(R.raw.login_animation); // Ensure file is in res/raw
        lottieAnimation.playAnimation();


        buttonLogin.setOnClickListener(v -> loginUser());

        textViewRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();


        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if (securityHelper.authenticate(email, password)) {

            securityHelper.setLoggedIn(true, email);
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Login successful for: " + email);
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {

            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Login failed for: " + email);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (editTextEmail != null) editTextEmail.setText("");
        if (editTextPassword != null) editTextPassword.setText("");
    }
}
