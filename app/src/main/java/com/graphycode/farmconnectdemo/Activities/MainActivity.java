package com.graphycode.farmconnectdemo.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.graphycode.farmconnectdemo.Adapters.SectionPagerAdapter;
import com.graphycode.farmconnectdemo.Fragments.CartFragment;
import com.graphycode.farmconnectdemo.Fragments.HomeFragment;
import com.graphycode.farmconnectdemo.Models.Products;
import com.graphycode.farmconnectdemo.R;

import java.util.ArrayList;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity {
    ViewPager viewPager;
    TabLayout tabLayout;
    SearchView searchView;
    Spinner spinner;
    AppCompatImageView search_img, back_img, account_img, refreshImg;
    String SelectedItem;
    ArrayList<Products> CategorizedList;

    public static boolean IsLoggedIn = true;
    private static  int Splash_Time_Out = 2000;

    ProgressDialog mProgress;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    Handler mHandler;

    String[] spinnerData = {"All", "Tuber", "Fruit", "Grain", "Vegetable", "Cereal"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
               if (IsLoggedIn){
                   if (firebaseAuth.getCurrentUser() == null){
                       Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                       loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                       startActivity(loginIntent);
                   }
               }
                IsLoggedIn = true;
            }
        };

        mProgress = new ProgressDialog(this);

        setUpViewPager();
        SpinnerController();
        ShowSearchBar();
        ShowAppBar();
        RefreshList();
        SearchFilter();
        LaunchAccountActivity();

        /*Intent intent = getIntent();
        finish();
        startActivity(intent);*/

    }

    @Override
    protected void onStart() {
        super.onStart();
       if (IsLoggedIn){
            mAuth.addAuthStateListener(mAuthListener);
        }
    }

    public void setUpViewPager() {
        SectionPagerAdapter sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        sectionPagerAdapter.AddFragment(new HomeFragment());
        sectionPagerAdapter.AddFragment(new CartFragment());
        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#008975"));
        tabLayout.getTabAt(0).setIcon(getResources().getDrawable(R.drawable.ic_home_black_24dp));
        tabLayout.getTabAt(1).setIcon(getResources().getDrawable(R.drawable.ic_shopping_cart_black_24dp));
    }

    public void ShowSearchBar(){
        search_img = (AppCompatImageView) findViewById(R.id.search_ic);
        search_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CardView cardView = (CardView) findViewById(R.id.search_layout);
                cardView.setVisibility(View.VISIBLE);
                AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
                appBarLayout.setVisibility(View.GONE);
            }
        });
    }

    public void ShowAppBar(){
        back_img = (AppCompatImageView) findViewById(R.id.back_arrow);
        back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
                appBarLayout.setVisibility(View.VISIBLE);
                CardView cardView = (CardView) findViewById(R.id.search_layout);
                cardView.setVisibility(View.GONE);
            }
        });
    }

    public void SpinnerController(){
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> catadapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerData);
        catadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(catadapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CategorizedList = new ArrayList<>();
                SelectedItem = spinner.getSelectedItem().toString();
                if (SelectedItem.equalsIgnoreCase(spinnerData[0])){
                    ListIterator<Products> listIterator = HomeFragment.products.listIterator(HomeFragment.products.size());
                    while (listIterator.hasPrevious()){
                        Products products = listIterator.previous();
                        CategorizedList.add(products);
                    }
                }else if (SelectedItem.equalsIgnoreCase(spinnerData[1])){
                    ListIterator<Products> listIterator = HomeFragment.products.listIterator(HomeFragment.products.size());
                    while (listIterator.hasPrevious()){
                        Products product = listIterator.previous();
                        String ProdCategory = product.getCategory();
                        if (ProdCategory.equalsIgnoreCase(SelectedItem)){
                            CategorizedList.add(product);
                        }
                    }
                }else if (SelectedItem.equalsIgnoreCase(spinnerData[2])){
                    ListIterator<Products> listIterator = HomeFragment.products.listIterator(HomeFragment.products.size());
                    while (listIterator.hasPrevious()){
                        Products product = listIterator.previous();
                        String ProdCategory = product.getCategory();
                        if (ProdCategory.equalsIgnoreCase(SelectedItem)){
                            CategorizedList.add(product);
                        }
                    }
                }else if (SelectedItem.equalsIgnoreCase(spinnerData[3])){
                    ListIterator<Products> listIterator = HomeFragment.products.listIterator(HomeFragment.products.size());
                    while (listIterator.hasPrevious()){
                        Products product = listIterator.previous();
                        String ProdCategory = product.getCategory();
                        if (ProdCategory.equalsIgnoreCase(SelectedItem)){
                            CategorizedList.add(product);
                        }
                    }
                }else if (SelectedItem.equalsIgnoreCase(spinnerData[4])){
                    ListIterator<Products> listIterator = HomeFragment.products.listIterator(HomeFragment.products.size());
                    while (listIterator.hasPrevious()){
                        Products product = listIterator.previous();
                        String ProdCategory = product.getCategory();
                        if (ProdCategory.equalsIgnoreCase(SelectedItem)){
                            CategorizedList.add(product);
                        }
                    }
                }else if (SelectedItem.equalsIgnoreCase(spinnerData[5])){
                    ListIterator<Products> listIterator = HomeFragment.products.listIterator(HomeFragment.products.size());
                    while (listIterator.hasPrevious()){
                        Products product = listIterator.previous();
                        String ProdCategory = product.getCategory();
                        if (ProdCategory.equalsIgnoreCase(SelectedItem)){
                            CategorizedList.add(product);
                        }
                    }
                }
                HomeFragment.adapter.setFilter(CategorizedList);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void SearchFilter(){
        searchView = (SearchView) findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText.toLowerCase();
                ArrayList<Products> filteredList = new ArrayList<>();
                if (SelectedItem.equalsIgnoreCase(spinnerData[0])){
                    for (Products product : HomeFragment.products){
                        String ProdName = product.getProduct_Name().toLowerCase();
                        if (ProdName.contains(newText)){
                            filteredList.add(product);
                        }
                    }
                }else if (SelectedItem.equalsIgnoreCase(spinnerData[1])){
                    for (Products product : CategorizedList){
                        String ProdName = product.getProduct_Name().toLowerCase();
                        if (ProdName.contains(newText)){
                            filteredList.add(product);
                        }
                    }
                }else if (SelectedItem.equalsIgnoreCase(spinnerData[2])){
                    for (Products product : CategorizedList){
                        String ProdName = product.getProduct_Name().toLowerCase();
                        if (ProdName.contains(newText)){
                            filteredList.add(product);
                        }
                    }
                }else if (SelectedItem.equalsIgnoreCase(spinnerData[3])){
                    for (Products product : CategorizedList){
                        String ProdName = product.getProduct_Name().toLowerCase();
                        if (ProdName.contains(newText)){
                            filteredList.add(product);
                        }
                    }
                }else if (SelectedItem.equalsIgnoreCase(spinnerData[4])){
                    for (Products product : CategorizedList){
                        String ProdName = product.getProduct_Name().toLowerCase();
                        if (ProdName.contains(newText)){
                            filteredList.add(product);
                        }
                    }
                }else if (SelectedItem.equalsIgnoreCase(spinnerData[5])){
                    for (Products product : CategorizedList){
                        String ProdName = product.getProduct_Name().toLowerCase();
                        if (ProdName.contains(newText)){
                            filteredList.add(product);
                        }
                    }
                }
                HomeFragment.adapter.setFilter(filteredList);
                return true;
            }
        });
    }

    public void LaunchAccountActivity(){
        account_img = findViewById(R.id.account_ic);
            account_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (IsLoggedIn){
                        startActivity(new Intent(MainActivity.this, AccountActivity.class));
                    }else {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                }
            });
    }

    public void RefreshList(){
        refreshImg = findViewById(R.id.refresh_ic);
        refreshImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.setMessage("refreshing..");
                mProgress.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        /*Intent intent = getIntent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        startActivity(intent);*/
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                        mProgress.dismiss();
                    }
                }, Splash_Time_Out);
            }
        });

    }

}
