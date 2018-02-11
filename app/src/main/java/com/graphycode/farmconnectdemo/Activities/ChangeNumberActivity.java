package com.graphycode.farmconnectdemo.Activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.graphycode.farmconnectdemo.Models.User;
import com.graphycode.farmconnectdemo.R;

public class ChangeNumberActivity extends AppCompatActivity {
    EditText oldNumber, newNumber;
    Button save;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    FirebaseUser mUser;
    String number;
    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_number);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User");
        mProgress = new ProgressDialog(this);

        xMLToJava();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeNumber();
            }
        });
    }

    private void xMLToJava(){
        oldNumber = findViewById(R.id.old_number);
        newNumber = findViewById(R.id.new_number);
        save = findViewById(R.id.save);
    }

    private void changeNumber(){
        mUser = mAuth.getCurrentUser();
        if (mUser != null && !TextUtils.isEmpty(oldNumber.getText().toString())){
            final String n = oldNumber.getText().toString().trim();
            mProgress.setMessage("Updating Number..");
            mProgress.show();
            final DatabaseReference reference = mDatabase.child(mUser.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    number = user.getPhone();
                    if (number.equals(n)){
                        String newNum = newNumber.getText().toString().trim();
                        reference.child("Phone").setValue(newNum);
                        mProgress.dismiss();
                        Toast.makeText(ChangeNumberActivity.this,
                                "number updated successfully", Toast.LENGTH_SHORT).show();
                    }else {
                        mProgress.dismiss();
                        Toast.makeText(ChangeNumberActivity.this,
                                "failed to update number", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else {
            mProgress.dismiss();
            Toast.makeText(ChangeNumberActivity.this,
                    "failed", Toast.LENGTH_SHORT).show();
        }
    }

}
