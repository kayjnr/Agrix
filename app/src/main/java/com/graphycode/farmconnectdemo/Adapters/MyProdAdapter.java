package com.graphycode.farmconnectdemo.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.graphycode.farmconnectdemo.Activities.EditProduct;
import com.graphycode.farmconnectdemo.Models.Products;
import com.graphycode.farmconnectdemo.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kay on 11/13/17.
 */

public class MyProdAdapter extends RecyclerView.Adapter<MyProdAdapter.MyProductViewHolder> {
    ArrayList<Products> MyproductArrayList;
    Context context;

    DatabaseReference mDatabase;

    public MyProdAdapter(ArrayList<Products> myproductArrayList, Context context, DatabaseReference mDatabase) {
        this.MyproductArrayList = myproductArrayList;
        this.context = context;
        this.mDatabase= mDatabase;
    }

    @Override
    public MyProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
        MyProductViewHolder myProductViewHolder = new MyProductViewHolder(view);
        return myProductViewHolder;
    }

    @Override
    public void onBindViewHolder(MyProductViewHolder holder, int position) {
        Picasso.with(context).load(MyproductArrayList.get(position).getImage()).into(holder.prod_img);
        holder.prod_name.setText(MyproductArrayList.get(position).getProduct_Name());
        holder.farm_name.setText(MyproductArrayList.get(position).getFarm_Name());
        holder.quantity.setText(MyproductArrayList.get(position).getQuantity());
        holder.price.setText(MyproductArrayList.get(position).getPrice());
        holder.date.setText(MyproductArrayList.get(position).getDateCreated());

        holder.popup_menu.setOnClickListener(onClickListener(position));

    }

    @Override
    public int getItemCount() {
        return MyproductArrayList.size();
    }

    public View.OnClickListener onClickListener(final int position){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context,v);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.remove:
                                DatabaseReference ref = mDatabase.
                                        child(MyproductArrayList.get(position).getProduct_Key());
                                ref.removeValue();
                                break;
                            case R.id.edit:
                                String k = MyproductArrayList.get(position).getProduct_Key();
                                Intent intent = new Intent(context, EditProduct.class);
                                intent.putExtra("key", k);
                                context.startActivity(intent);

                                //Toast.makeText(context, "Item edited", Toast.LENGTH_LONG).show();
                                break;
                            default:
                                Toast.makeText(context, "Nothing Better", Toast.LENGTH_LONG).show();
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        };
    }

    public static class MyProductViewHolder extends RecyclerView.ViewHolder {

        ImageView prod_img, popup_menu;
        TextView prod_name, farm_name,quantity, price, date;

        public MyProductViewHolder(final View itemView) {
            super(itemView);

            prod_img = itemView.findViewById(R.id.prod_img);
            prod_name = itemView.findViewById(R.id.prod_name);
            farm_name = itemView.findViewById(R.id.farm_name);
            quantity = itemView.findViewById(R.id.quantity);
            price = itemView.findViewById(R.id.prod_price);
            date = itemView.findViewById(R.id.date1);

            popup_menu = itemView.findViewById(R.id.ic_more);
        }

    }
}
