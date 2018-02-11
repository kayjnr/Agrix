package com.graphycode.farmconnectdemo.Adapters;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.graphycode.farmconnectdemo.Activities.MainActivity;
import com.graphycode.farmconnectdemo.Activities.ProductInfoActivity;
import com.graphycode.farmconnectdemo.Models.Products;
import com.graphycode.farmconnectdemo.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kay on 12/9/17.
 */

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    ArrayList<Products> productArrayList = new ArrayList<>();
    Context context;
    SectionPagerAdapter sectionPagerAdapter;

    public CartAdapter(ArrayList<Products> productArrayList, Context context) {
        this.productArrayList = productArrayList;
        this.context = context;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        CartAdapter.CartViewHolder cartViewHolder = new CartAdapter.CartViewHolder(view);
        return cartViewHolder;
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        Picasso.with(context).load(productArrayList.get(position).getImage()).into(holder.prodImg);
        holder.prodName.setText(productArrayList.get(position).getProduct_Name());
        holder.farmName.setText(productArrayList.get(position).getFarm_Name());
        holder.quantity.setText(productArrayList.get(position).getQuantity());
        holder.price.setText(productArrayList.get(position).getPrice());
        holder.imgRemoveCart.setOnClickListener(RemoveListener(position));
        holder.container.setOnClickListener(onClickListener(position));

    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    public void setCartItems(ArrayList<Products> cartList){
        productArrayList.addAll(cartList);
        notifyDataSetChanged();
    }

    public View.OnClickListener RemoveListener(final int position){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productArrayList.remove(position);
                notifyDataSetChanged();
                Toast.makeText(context, "Item removed",Toast.LENGTH_SHORT).show();
            }
        };
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

    public static class CartViewHolder extends RecyclerView.ViewHolder{

        ImageView prodImg, imgRemoveCart;
        TextView prodName, farmName,quantity, price;
        View container;

        public CartViewHolder(View itemView) {
            super(itemView);
            prodImg = itemView.findViewById(R.id.prodImg);
            prodName = itemView.findViewById(R.id.prodName);
            farmName = itemView.findViewById(R.id.farmName);
            quantity = itemView.findViewById(R.id.prodQuantity);
            price = itemView.findViewById(R.id.prodPrice);
            container = itemView.findViewById(R.id.prod_img_layout);

            imgRemoveCart = itemView.findViewById(R.id.remove_cart);
        }
    }
}
