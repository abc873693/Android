package com.nail.tatproject.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nail.tatproject.MainActivity;
import com.nail.tatproject.R;

/**
 * Created by 70J on 2016/6/17.
 */
public class ShoppingStep3Fragment extends Fragment {

    private final String ARG_SECTION_NUMBER = "section_number";
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
