package com.graphycode.farmconnectdemo.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.graphycode.farmconnectdemo.Activities.MainActivity;
import com.graphycode.farmconnectdemo.Fragments.CartFragment;
import com.graphycode.farmconnectdemo.Models.Products;
import com.graphycode.farmconnectdemo.Activities.ProductInfoActivity;
import com.graphycode.farmconnectdemo.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kay on 10/27/17.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    ArrayList<Products>productArrayList;
    Context context;
    private String prodKey;
    DatabaseReference mDatabase;
    ArrayList<Products>cartItems;

    public ProductAdapter() {
    }

    public ProductAdapter(ArrayList<Products> productArrayList, Context context) {
        this.productArrayList = productArrayList;
        this.context = context;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_layout, parent, false);
        ProductViewHolder productViewHolder = new ProductViewHolder(view);
        return productViewHolder;
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Picasso.with(context).load(productArrayList.get(position).getImage()).into(holder.prod_img);
        holder.prod_name.setText(productArrayList.get(position).getProduct_Name());
        holder.farm_name.setText(productArrayList.get(position).getFarm_Name());
        holder.quantity.setText(productArrayList.get(position).getQuantity());
        holder.price.setText(productArrayList.get(position).getPrice());
        holder.prod_date.setText(productArrayList.get(position).getDateCreated());

        holder.container.setOnClickListener(onClickListener(position));
        holder.imgAddCart.setOnClickListener(AddToCartClickListener(position));

    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    public void setFilter(ArrayList<Products> filterList){
        productArrayList = new ArrayList<>();
        productArrayList.addAll(filterList);
        notifyDataSetChanged();
    }

    public void refreshList(){
        this.notifyDataSetChanged();
    }

    public View.OnClickListener onClickListener(final int position){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s  = productArrayList.get(position).getProduct_Key();
                String t = productArrayList.get(position).getProduct_Name();
                String l = productArrayList.get(position).getLocation();
                String i = productArrayList.get(position).getUserID();
                //Toast.makeText(context, s, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, ProductInfoActivity.class);
                intent.putExtra("key", s);
                intent.putExtra("title",t);
                intent.putExtra("loc", l);
                intent.putExtra("id", i);
                context.startActivity(intent);
            }
        };
    }

    public View.OnClickListener AddToCartClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProdKey(productArrayList.get(position).getProduct_Key());

                mDatabase = FirebaseDatabase.getInstance().getReference().
                        child("Products").child(getProdKey());
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Products products = dataSnapshot.getValue(Products.class);
                        products.setProduct_Key(getProdKey());
                        cartItems = new ArrayList<>();
                        cartItems.add(products);
                        CartFragment.adapter.setCartItems(cartItems);
                        Toast.makeText(context, "Item added to cart",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        };
    }

    public String getProdKey() {
        return prodKey;
    }

    public void setProdKey(String prodKey) {
        this.prodKey = prodKey;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView prod_img, imgAddCart;
        TextView prod_name, farm_name,quantity, price, prod_date;
        View container;

        public ProductViewHolder(final View itemView) {
            super(itemView);

            prod_img = itemView.findViewById(R.id.prod_img);
            prod_name = itemView.findViewById(R.id.prod_name);
            farm_name = itemView.findViewById(R.id.farm_name);
            quantity = itemView.findViewById(R.id.quantity);
            price = itemView.findViewById(R.id.prod_price);
            prod_date = itemView.findViewById(R.id.prod_date);

            container = itemView.findViewById(R.id.prod_img_layout);
            imgAddCart = itemView.findViewById(R.id.add_cart);


        }

    }
}
