package com.example.traveling;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText firstNameEditText = findViewById(R.id.firstNameEditText);
        EditText lastNameEditText = findViewById(R.id.lastNameEditText);
        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {

            String firstname = firstNameEditText.getText().toString();
            String lastname = lastNameEditText.getText().toString();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("firstname", firstname);
            resultIntent.putExtra("lastname", lastname);

            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}