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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.graphycode.farmconnectdemo.R;

public class ChangeEmailActivity extends AppCompatActivity {
    EditText oldMail, newMail;
    Button save;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        mAuth = FirebaseAuth.getInstance();
        mProgress =  new ProgressDialog(this);

        xMLToJava();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeEmail();
            }
        });
    }

    private void xMLToJava(){
        oldMail = findViewById(R.id.old_mail);
        newMail = findViewById(R.id.new_mail);
        save = findViewById(R.id.save);
    }

    private void changeEmail(){
        mUser = mAuth.getCurrentUser();
        String e = oldMail.getText().toString().trim();
        if (mUser != null && !TextUtils.isEmpty(e)){
            if (e.equals(mUser.getEmail())){
                mProgress.setMessage("Updating Email..");
                mProgress.show();
                String newE = newMail.getText().toString().trim();
                mUser.updateEmail(newE).addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mProgress.dismiss();
                            Toast.makeText(ChangeEmailActivity.this,
                                    "Email Successfully Updated", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(
                                    ChangeEmailActivity.this, AccountActivity.class));

                        }

                    }
                });
            }else {
                Toast.makeText(ChangeEmailActivity.this,
                        "Failed to update email", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
