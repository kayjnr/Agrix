package com.graphycode.farmconnectdemo.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.graphycode.farmconnectdemo.Fragments.CartFragment;
import com.graphycode.farmconnectdemo.Models.Products;
import com.graphycode.farmconnectdemo.Models.User;
import com.graphycode.farmconnectdemo.R;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ProductInfoActivity extends AppCompatActivity {
    ImageView banner, call;
    FloatingActionButton fab;
    TextView farmName, price, quantity, location , dec, phone;
    String title, s, l,i, audUri, vidUri;
    LinearLayout mapLink, aud_desc, vid_desc;

    DatabaseReference mdatabase;
    DatabaseReference mUsers;
    ArrayList<Products>cartItems;

    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);

        mdatabase = FirebaseDatabase.getInstance().getReference().child("Products");
        mdatabase.keepSynced(true);
        mUsers = FirebaseDatabase.getInstance().getReference().child("User");
        //mUsers.keepSynced(true);
        mProgress = new ProgressDialog(this);

        xMLToJava();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle != null) {
            s = bundle.getString("key");
            title = bundle.getString("title");
            l = bundle.getString("loc");
            i = bundle.getString("id");

            DatabaseReference childRef = mdatabase.child(s);
            childRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Products products = dataSnapshot.getValue(Products.class);
                    farmName.setText(products.getFarm_Name());
                    price.setText(products.getPrice());
                    quantity.setText(products.getQuantity());
                    dec.setText(products.getDescription());
                    location.setText(products.getLocation());
                    Picasso.with(ProductInfoActivity.this).load(products.getImage()).into(banner);
                    audUri = products.getAudio();
                    vidUri = products.getVideo();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            DatabaseReference mUser = mUsers.child(i);
            mUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    phone.setText(user.getPhone());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            aud_desc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (audUri != null){
                        mProgress.setMessage("Processing..");
                        mProgress.show();
                        Intent play = new Intent(android.content.Intent.ACTION_VIEW);
                        Uri file = Uri.parse(audUri);
                        play.setDataAndType(file, "audio/mp3");
                        mProgress.dismiss();
                        startActivity(play);
                    }else {
                        Toast.makeText(ProductInfoActivity.this,
                                "Audio Unavailable", Toast.LENGTH_LONG).show();
                    }
                }
            });

            vid_desc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (vidUri !=null){
                        mProgress.setMessage("Processing..");
                        mProgress.show();
                        Intent play = new Intent(Intent.ACTION_VIEW);
                        Uri file = Uri.parse(vidUri);
                        play.setDataAndType(file, "video/*");
                        mProgress.dismiss();
                        startActivity(play);
                    }else {
                        Toast.makeText(ProductInfoActivity.this,
                                "Video Unavailable", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        mapLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = null;
                try {
                    uri = Uri.parse("geo:0,0?q="+ URLEncoder.encode(l, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(phone.getText())){
                    Intent call = new Intent(Intent.ACTION_DIAL,
                            Uri.fromParts("tel", phone.getText().toString(),null));
                    if (call.resolveActivity(getPackageManager()) != null){
                        startActivity(call);
                    }
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference Ref = mdatabase.child(s);
                Ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Products products = dataSnapshot.getValue(Products.class);
                        products.setProduct_Key(s);
                        cartItems = new ArrayList<>();
                        cartItems.add(products);
                        CartFragment.adapter.setCartItems(cartItems);
                        Toast.makeText(ProductInfoActivity.this, "Item added to cart",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


        final android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapse_toolbar);
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        final ImageView imageView = findViewById(R.id.banner);
        collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        collapsingToolbarLayout.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    public void xMLToJava(){
        banner = findViewById(R.id.banner);
        farmName = findViewById(R.id.farm_name);
        price = findViewById(R.id.prod_price);
        quantity = findViewById(R.id.prod_quantity);
        location = findViewById(R.id.txt_loc);
        dec = findViewById(R.id.desc);
        phone = findViewById(R.id.txt_phone);
        call = findViewById(R.id.call);
        fab = findViewById(R.id.add_cart_fab);

        mapLink = findViewById(R.id.map_link);
        aud_desc = findViewById(R.id.aud_desc_layout);
        vid_desc = findViewById(R.id.vid_desc_layout);


    }
}
