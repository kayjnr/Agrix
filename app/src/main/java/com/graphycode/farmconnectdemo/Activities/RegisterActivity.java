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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.graphycode.farmconnectdemo.R;

public class RegisterActivity extends AppCompatActivity {
    Button btn_Login, btn_SignUp;
    EditText fNameField, lNameField, emailField, phoneField, passField;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User");

        mDialog = new ProgressDialog(this);

        convertToJava();


        btn_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
                //finish();

            }
        });

        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                //finish();
            }
        });
    }

    private void Register() {
        final String name1 = fNameField.getText().toString().trim();
        final String name2 = lNameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        final String phone = phoneField.getText().toString().trim();
        String pass = passField.getText().toString().trim();

        if (!TextUtils.isEmpty(name1) && !TextUtils.isEmpty(name2) && !TextUtils.isEmpty(email) &&
                !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pass)){
            if (pass.length() >= 6){
                mDialog.setMessage("Processing..");
                mDialog.show();
                mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(
                        this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    String user_id = mAuth.getCurrentUser().getUid();

                                    DatabaseReference mCurrentUser = mDatabase.child(user_id);
                                    mCurrentUser.child("FirstName").setValue(name1);
                                    mCurrentUser.child("LastName").setValue(name2);
                                    mCurrentUser.child("Phone").setValue(phone);

                                    mDialog.dismiss();

                                    Intent main = new Intent(RegisterActivity.this, MainActivity.class);
                                    main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(main);

                                }else {
                                    Toast.makeText(RegisterActivity.this,
                                            " Signing Up Failed..", Toast.LENGTH_LONG).show();
                                }

                            }
                        });
            }else {
                Toast.makeText(RegisterActivity.this,
                        "Password must be at least 6 characters", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void convertToJava() {
        btn_Login = findViewById(R.id.btn_login);
        btn_SignUp = findViewById(R.id.signup_btn);
        fNameField = findViewById(R.id.fName_txtField);
        lNameField = findViewById(R.id.lName_txtField);
        emailField = findViewById(R.id.email_txtField);
        phoneField = findViewById(R.id.phone_txtField);
        passField = findViewById(R.id.pass_txtField);
    }


}
