package com.nail.tatproject.Menu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nail.tatproject.R;
import com.nail.tatproject.SQLite.TATDB;
import com.nail.tatproject.TATApplication;

/**
 * Created by 70J on 2016/6/17.
 */
public class ShoppingFragment extends Fragment {

    private final String ARG_SECTION_NUMBER = "section_number";
    private TATApplication Global; // 全域變數

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("ShoppingFragment", "onCreate" + getArguments().getInt(ARG_SECTION_NUMBER));
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
        Log.e("ShoppingFragment", "onCreateView" + getArguments().getInt(ARG_SECTION_NUMBER));
        View view = inflater.inflate(R.layout.fragment_shopping, container, false);
        Global = (TATApplication) getActivity().getApplicationContext();
        if (!Global.ShoppingList.get(Global.ShoppingStep).isAdded()) {
            getChildFragmentManager().beginTransaction().add(R.id.shopping_container, Global.ShoppingList.get(Global.ShoppingStep)).commit();
        }
        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences data = getActivity().getSharedPreferences("data", 0);
                String paymentType = data.getString("paymentType", "null").toString();
                if (Global.tatdb.getCount(TATDB.Shopping_TABLE_NAME) == 0 && Global.ShoppingStep == 0) {
                    Toast.makeText(getActivity(), "購物車是空的", Toast.LENGTH_LONG).show();
                } else if (Global.ShoppingStep == 1 && paymentType.equals("null")) {
                    Toast.makeText(getActivity(), "請選擇付款方式", Toast.LENGTH_LONG).show();
                } else if (Global.ShoppingStep < 2) {
                    Global.ShoppingStep += 1;
                    if (Global.ShoppingStep == 2)
                        view.findViewById(R.id.button).setVisibility(View.GONE);
                    getChildFragmentManager().beginTransaction().replace(R.id.shopping_container, Global.ShoppingList.get(Global.ShoppingStep)).commit();
                }
            }
        });
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("ShoppingFragment", "onResume" + getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("ShoppingFragment", "onPause" + getArguments().getInt(ARG_SECTION_NUMBER));

    }
}
