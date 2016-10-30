package com.nail.tatproject.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nail.tatproject.MainActivity;
import com.nail.tatproject.R;

/**
 * Created by 70J on 2016/6/17.
 */
public class ShoppingStep3Fragment extends Fragment {
    private LinearLayout show_product,list_product;
    private TextView shrink , final_total;
    private final String ARG_SECTION_NUMBER = "section_number";
    private String count;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("ShoppingStep3Fragment", "onCreate");
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
        Log.e("ShoppingStep3Fragment", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_step3, container, false);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        show_product=(LinearLayout) view.findViewById(R.id.show_product);
        list_product=(LinearLayout) view.findViewById(R.id.list_porduct);
        shrink=(TextView) view.findViewById(R.id.shrink) ;
        final_total = (TextView)view.findViewById(R.id.final_total);
        SharedPreferences data = getActivity().getSharedPreferences("data",0);
        count = data.getString("products_count", null);
        int SUM = Integer.valueOf(data.getString("products_sum", null))-Integer.valueOf(data.getString("products_discount", null)) +Integer.valueOf(data.getString("products_ship", null));
        final_total.setText("$" + String.format("%,d",SUM));
        shrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( list_product.getVisibility()==View.VISIBLE) {
                    list_product.setVisibility(View.GONE);
                    shrink.setText("總計 " + count + " 項產品  ▽");
                }
                else {
                    list_product.setVisibility(View.VISIBLE);
                    shrink.setText("總計 " + count + " 項產品  △");
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("ShoppingStep3Fragment", "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("ShoppingStep3Fragment", "onPause");

    }
}
