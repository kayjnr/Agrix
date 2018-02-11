package com.graphycode.farmconnectdemo.Activities;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.graphycode.farmconnectdemo.R;

public class ForgotPasswordActivity extends AppCompatActivity {
    Button btnReset;
    EditText email;
    FirebaseAuth mAuth;
    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        btnReset = findViewById(R.id.btn_reset);
        email = findViewById(R.id.emailField);

        mAuth = FirebaseAuth.getInstance();
        mProgress =  new ProgressDialog(this);

        btnReset.setOnClickListener(new View.OnClickListener() {
            String e = email.getText().toString().trim();
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(e)){
                    mProgress.setMessage("Resetting Password");
                    mProgress.show();
                    mAuth.sendPasswordResetEmail(e).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                mProgress.dismiss();
                                Toast.makeText(ForgotPasswordActivity.this,
                                        "Check Your Mail To Reset Password",
                                        Toast.LENGTH_LONG).show();
                            }else {
                                mProgress.dismiss();
                                Toast.makeText(ForgotPasswordActivity.this,
                                        "Password Reset Failed",
                                        Toast.LENGTH_LONG).show();

                            }

                        }
                    });

                }else {
                    Toast.makeText(ForgotPasswordActivity.this,
                            "Enter Your Email", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
