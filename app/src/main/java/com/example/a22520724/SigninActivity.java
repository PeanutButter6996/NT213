package com.example.a22520724;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a22520724.databinding.ActivitySigninBinding;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SigninActivity extends AppCompatActivity {

    private ActivitySigninBinding binding;
    private MySQLConnector mySQLConnector;

    // Phương thức onCreate được gọi khi hoạt động được tạo
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mySQLConnector = new MySQLConnector();

        // Kích hoạt nút đăng nhập khi được nhấn
        binding.signinButton.setOnClickListener(view -> {
            String username = binding.signinUser.getText().toString();
            String password = binding.signinPassword.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
            } else {
                new Thread(() -> {
                    try {
                        Future<Boolean> futureCheck = mySQLConnector.checkUserSignIn(username, password);
                        boolean checkCredentials = futureCheck.get(); // Wait and get the result

                        runOnUiThread(() -> {
                            if (checkCredentials) {
                                Toast.makeText(this, "Sign in successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(this, MainActivity.class);
                                intent.putExtra("UserName", username);
                                startActivity(intent);
                            } else {
                                Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show());
                    }
                }).start();
            }
        });

        // Kích hoạt nút đăng ký khi được nhấn
        binding.SignUpRedirectText.setOnClickListener(view -> {
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}