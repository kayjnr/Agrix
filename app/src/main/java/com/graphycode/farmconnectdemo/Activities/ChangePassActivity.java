package com.graphycode.farmconnectdemo.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.graphycode.farmconnectdemo.R;

public class ChangePassActivity extends AppCompatActivity {
    EditText oldPass, newPass;
    Button save;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);

        xMLToJava();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePass();
            }
        });
    }

    private void xMLToJava(){
        oldPass = findViewById(R.id.old_pass);
        newPass = findViewById(R.id.new_pass);
        save = findViewById(R.id.save);
    }

    private void changePass(){
        mUser = mAuth.getCurrentUser();
        if (mUser != null && oldPass.getText().toString() !=null){
            if (newPass.getText().toString().length() >= 6){
                mProgress.setMessage("Updating Password..");
                mProgress.show();
                mUser.updatePassword(newPass.getText().toString()).addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mProgress.dismiss();
                            Toast.makeText(ChangePassActivity.this,
                                    "Password Successfully Changed", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(
                                    ChangePassActivity.this, AccountActivity.class));
                        }
                    }
                });
            }else {
                Toast.makeText(ChangePassActivity.this,
                        "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
