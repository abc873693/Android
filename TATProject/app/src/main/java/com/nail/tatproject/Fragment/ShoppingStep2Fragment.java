package com.nail.tatproject.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nail.tatproject.MainActivity;
import com.nail.tatproject.R;

/**
 * Created by 70J on 2016/6/17.
 */

public class ShoppingStep2Fragment extends Fragment {
    private TextView other_option;
    private TextView product_total,products_total,products_ship,products_all;
    private final String ARG_SECTION_NUMBER = "section_number";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("ShoppingStep2Fragment", "onCreate");
        setRetainInstance(true);
    }

    @Override
    public void onHiddenChanged(boolean hidd) {
        if (hidd) {
            onPause();
        } else {
            onResume();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //導入Tab分頁的Fragment Layout
        Log.e("ShoppingStep2Fragment", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_step2, container, false);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        product_total=(TextView) view.findViewById(R.id.product_total);
        products_total = (TextView)view.findViewById(R.id.products_total);
        products_ship = (TextView)view.findViewById(R.id.products_ship);
        products_all = (TextView)view.findViewById(R.id.products_all);
        other_option = (TextView)view.findViewById(R.id.other_option);
        other_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "你已經按了其他選項", Toast.LENGTH_LONG).show();
            }
        });
        SharedPreferences data = getActivity().getSharedPreferences("data",0);
        String sum = data.getString("products_sum", null);
        String discount = data.getString("products_discount", null);
        String count = data.getString("products_count", null);
        int total = (Integer.valueOf(sum) - Integer.valueOf(discount));
        product_total.setText("共" + count+ "項商品");
        products_total.setText("$" + String.format("%,d",total));
        products_ship.setText("$" + String.format("%,d",500));
        products_all.setText("$" + String.format("%,d", total + 500));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("ShoppingStep2Fragment", "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("ShoppingStep2Fragment", "onPause");

    }
}
