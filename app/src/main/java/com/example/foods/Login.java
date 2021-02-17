package com.example.foods;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
private Toolbar toolbar;
private FirebaseAuth firebaseAuth;
private Button registerButton, loginButton;
private EditText emailET, passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



    toolbar = findViewById(R.id.mToolBar);
    TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Login");
        mTitle.setTypeface(null, Typeface.BOLD);

    registerButton = findViewById(R.id.registerButton);
    loginButton = findViewById(R.id.loginButton);

    emailET = findViewById(R.id.emailEditText);
    passwordET = findViewById(R.id.passwordEditText);

    firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FavouritesActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String email = emailET.getText().toString().trim();
            String password = passwordET.getText().toString().trim();

            if(TextUtils.isEmpty(email)) {
                emailET.setError("Email is required.");
                return;
            }
            if(TextUtils.isEmpty(password)) {
                passwordET.setError("Email is required.");
                return;
            }

            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(Login.this, "User logged.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    }
                    else
                    {
                        Toast.makeText(Login.this, "Error."+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    });
}
}