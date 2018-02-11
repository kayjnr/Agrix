package com.graphycode.farmconnectdemo.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.graphycode.farmconnectdemo.Models.User;
import com.graphycode.farmconnectdemo.R;

public class AccountActivity extends AppCompatActivity {
    ActionBar actionBar;
    LinearLayout profile_layout, changePass_layout, changeEmail_layout,
            changeNumberLayout, myAds_layout,logout_layout;

    ImageView profileImg;
    TextView name;

    DatabaseReference mDatabase;
    FirebaseAuth mAuth;

    ProgressDialog mProgress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("User");
        mAuth = FirebaseAuth.getInstance();

        mProgress = new ProgressDialog(this);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        name = findViewById(R.id.name);
        profileImg = findViewById(R.id.profile_img);

        profile_layout =  findViewById(R.id.profile_layout);
        changePass_layout = findViewById(R.id.change_pass_layout);
        changeEmail_layout = findViewById(R.id.change_email_layout);
        myAds_layout = findViewById(R.id.myProduct_layout);
        logout_layout = findViewById(R.id.logout_layout);
        changeNumberLayout = findViewById(R.id.change_number_layout);

        String UserID = mAuth.getCurrentUser().getUid();
        DatabaseReference ref = mDatabase.child(UserID);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                name.setText(user.getFirstName()+" "+user.getLastName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        profile_layout.setOnClickListener( new ClickListener());
        changePass_layout.setOnClickListener(new ClickListener());
        changeEmail_layout.setOnClickListener(new ClickListener());
        changeNumberLayout.setOnClickListener(new ClickListener());
        myAds_layout.setOnClickListener(new ClickListener());
        logout_layout.setOnClickListener(new ClickListener());

    }
    class ClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case  R.id.profile_layout:
                    //Snackbar.make(v, "clicked Profile", Snackbar.LENGTH_LONG).show();
                    break;
                case R.id.change_pass_layout:
                    //Snackbar.make(v, "clicked Change password", Snackbar.LENGTH_LONG).show();
                    startActivity(new Intent(AccountActivity.this, ChangePassActivity.class));
                    break;
                case R.id.change_email_layout:
                    //Snackbar.make(v, "clicked Change password", Snackbar.LENGTH_LONG).show();
                    startActivity(new Intent(AccountActivity.this, ChangeEmailActivity.class));
                    break;
                case R.id.change_number_layout:
                    startActivity(new Intent(AccountActivity.this, ChangeNumberActivity.class));
                    break;
                case R.id.myProduct_layout:
                    //Snackbar.make(v, "clicked MyAds", Snackbar.LENGTH_LONG).show();
                    startActivity(new Intent(AccountActivity.this, MyAdsActivity.class));
                    break;
                case R.id.logout_layout:
                    mProgress.setMessage("Signing Out..");
                    mProgress.show();
                    mAuth.signOut();
                    mProgress.dismiss();
                    break;
                default:
            }

        }
    }
}
