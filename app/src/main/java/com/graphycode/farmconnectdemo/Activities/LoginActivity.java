package com.graphycode.farmconnectdemo.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.graphycode.farmconnectdemo.R;

public class LoginActivity extends AppCompatActivity {
    Button btnLogin, btnSignUp;
    EditText emailField, passField;
    TextView notNowTxt, forgotPass;

    ProgressDialog mProgress;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User");

        if (mAuth.getCurrentUser() != null){
            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);

            finish();
        }

        mProgress = new ProgressDialog(this);

        convertToJava();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });

        notNowTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.IsLoggedIn = true;
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                //finish();
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
                //finish();

            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
    }

    private void Login() {
        String email = emailField.getText().toString().trim();
        String pass = passField.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)){
            mProgress.setMessage("Processing..");
            mProgress.show();
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);

                        mProgress.dismiss();

                    }else {
                        mProgress.dismiss();
                        Toast.makeText(LoginActivity.this,
                                "Login Failed..", Toast.LENGTH_LONG).show();
                    }

                }
            });

        }else {
            Toast.makeText(this,
                    "All fields are required", Toast.LENGTH_LONG).show();
        }
    }

    private void convertToJava() {
        btnLogin =findViewById(R.id.btn_login);
        btnSignUp = findViewById(R.id.signup_btn);
        emailField = findViewById(R.id.emailField);
        passField = findViewById(R.id.passField);
        notNowTxt = findViewById(R.id.notNow);
        forgotPass = findViewById(R.id.forgotPass);

    }
}
