package com.nail.tatproject.Menu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nail.tatproject.R;

/**
 * Created by 70J on 2016/6/17.
 */
public class CollectionFragment extends Fragment {

    private final String ARG_SECTION_NUMBER = "section_number";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("ShoppingStep1Fragment", "onCreate" + getArguments().getInt(ARG_SECTION_NUMBER));
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
        Log.e("CollectionFragment", "onCreateView" + getArguments().getInt(ARG_SECTION_NUMBER));
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        ((TextView) view.findViewById(R.id.textView2)).setText("" + getArguments().getInt(ARG_SECTION_NUMBER));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("CollectionFragment", "onResume" + getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("CollectionFragment", "onPause" + getArguments().getInt(ARG_SECTION_NUMBER));

    }
}
