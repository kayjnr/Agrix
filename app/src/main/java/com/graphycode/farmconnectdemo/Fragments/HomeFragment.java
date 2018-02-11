package com.graphycode.farmconnectdemo.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.graphycode.farmconnectdemo.Activities.MainActivity;
import com.graphycode.farmconnectdemo.Adapters.ProductAdapter;
import com.graphycode.farmconnectdemo.Models.Products;
import com.graphycode.farmconnectdemo.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    public static ProductAdapter adapter;
    public  static ArrayList<Products> products;

    DatabaseReference mDatabase;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

       mDatabase = FirebaseDatabase.getInstance().getReference().child("Products");
        mDatabase.keepSynced(true);
        products = new ArrayList<>();

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Products mProducts = dataSnapshot.getValue(Products.class);
                mProducts.setProduct_Key(dataSnapshot.getKey());
                products.add(mProducts);

                /*Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                startActivity(intent);*/

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        recyclerView = view.findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new ProductAdapter(products,this.getActivity());
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.refreshList();
    }

    /* @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> firebaseRecyclerAdapter = new
                FirebaseRecyclerAdapter<Products, ProductViewHolder>(
                        Products.class,
                        R.layout.cardview_layout,
                        ProductViewHolder.class,
                        mDatabase

                ) {
                    @Override
                    protected void populateViewHolder(ProductViewHolder viewHolder, Products model, int position) {
                        viewHolder.setProdName(model.getProduct_Name());
                        viewHolder.setProdPrice(model.getPrice());
                        viewHolder.setProdQuantity(model.getQuantity());
                        viewHolder.setProdFarm(model.getFarm_Name());

                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public ProductViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setProdName(String n){
            TextView prod_name = mView.findViewById(R.id.prod_name);
            prod_name.setText(n);
        }
        public void setProdPrice(String p){
            TextView prod_price = mView.findViewById(R.id.prod_price);
            prod_price.setText(p);
        }
        public void setProdQuantity(String q){
            TextView quantity = mView.findViewById(R.id.quantity);
            quantity.setText(q);
        }
        public void setProdFarm(String f){
            TextView farm_name = mView.findViewById(R.id.farm_name);
            farm_name.setText(f);
        }

    }*/

}
