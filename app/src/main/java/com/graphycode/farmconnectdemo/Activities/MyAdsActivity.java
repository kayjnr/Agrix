package com.graphycode.farmconnectdemo.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.graphycode.farmconnectdemo.Adapters.MyProdAdapter;
import com.graphycode.farmconnectdemo.Models.Products;
import com.graphycode.farmconnectdemo.R;

import java.util.ArrayList;

public class MyAdsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    MyProdAdapter adapter;
    FloatingActionButton floatingActionButton;

    ArrayList<Products> mProducts;

    DatabaseReference mDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;

    ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Products");
        mDatabase.keepSynced(true);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");
        mAuth = FirebaseAuth.getInstance();

        mProgress = new ProgressDialog(this);

        mProducts = new ArrayList<>();

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Products products = dataSnapshot.getValue(Products.class);
                if (products.getUserID() != null && mAuth.getCurrentUser().getUid() != null){
                    if (products.getUserID().equals(mAuth.getCurrentUser().getUid())) {
                        products.setProduct_Key(dataSnapshot.getKey());
                        mProducts.add(products);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                mProgress.setMessage("removing");
                mProgress.show();
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                startActivity(intent);
                mProgress.dismiss();

                String item = dataSnapshot.child("Product_Name").getValue().toString();
                Toast.makeText(MyAdsActivity.this,
                        item+" removed successfully",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        floatingActionButton = findViewById(R.id.add_prod_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyAdsActivity.this, AddProduct.class));
            }
        });

        recyclerView = findViewById(R.id.grid_layout);
        layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new MyProdAdapter(mProducts, this, databaseReference);
        recyclerView.setAdapter(adapter);
    }


}
