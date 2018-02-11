package com.graphycode.farmconnectdemo.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.graphycode.farmconnectdemo.Adapters.CartAdapter;
import com.graphycode.farmconnectdemo.Adapters.ProductAdapter;
import com.graphycode.farmconnectdemo.Models.Products;
import com.graphycode.farmconnectdemo.R;

import java.util.ArrayList;

import static com.graphycode.farmconnectdemo.Fragments.HomeFragment.adapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    public static ArrayList<Products> mProducts = new ArrayList<>();
    public static CartAdapter adapter;

    //DatabaseReference mDatabase;


    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        /*ProductAdapter productAdapter = new ProductAdapter();
        String key = productAdapter.getProdKey();*/


       /* if (key != null){
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Products").child(key);
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Products products = dataSnapshot.getValue(Products.class);
                    mProducts.add(products);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }*/




        recyclerView = view.findViewById(R.id.cartRecyclerview);
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new CartAdapter(mProducts,this.getActivity());
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        return view;
    }

}
