package com.graphycode.farmconnectdemo.Fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.graphycode.farmconnectdemo.Activities.AddProduct;
import com.graphycode.farmconnectdemo.R;

/**
 * Created by kay on 12/7/17.
 */

public class LocationFragment extends DialogFragment {
    Button btn;
    ListView lv;
    SearchView sv;
    ArrayAdapter<CharSequence> adapter;

    String selectedLoc;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_location, null);

        getDialog().setTitle("Select Location");

        lv = rootView.findViewById(R.id.listView1);
        sv = rootView.findViewById(R.id.searchView1);
        btn = rootView.findViewById(R.id.dismiss);

        adapter = ArrayAdapter.createFromResource(getActivity(),R.array.cities ,android.R.layout.simple_list_item_1 );
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedLoc = lv.getItemAtPosition(position).toString();
                AddProduct.mLocation = selectedLoc;
                Toast.makeText(getActivity(), "Location Selected", Toast.LENGTH_SHORT).show();
            }
        });

        sv.setQueryHint("Search...");
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                setLocation(AddProduct.locationBtn);
            }
        });

        return rootView;
    }

    public void setLocation(Button b){
        b.setText(selectedLoc);
    }
}
