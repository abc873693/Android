package com.nail.tatproject;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.nail.tatproject.Fragment.ShoppingStep1Fragment;
import com.nail.tatproject.Fragment.ShoppingStep2Fragment;
import com.nail.tatproject.Fragment.ShoppingStep3Fragment;
import com.nail.tatproject.Menu.CollectionFragment;
import com.nail.tatproject.Menu.HomeFragment;
import com.nail.tatproject.Menu.MemberFragment;
import com.nail.tatproject.Menu.SearchFragment;
import com.nail.tatproject.Menu.ShoppingFragment;
import com.nail.tatproject.SQLite.TATDB;

import java.util.ArrayList;

/**
 * Created by 70J on 2016/9/30.
 */
public class TATApplication extends Application {
    public SharedPreferences sharedPref;   // 計錄參數
    public int ShoppingStep = 0;
    public ArrayList<Fragment> PageList, ShoppingList;
    public TATDB tatdb;
    @Override
    public void onCreate() {
        super.onCreate();
        tatdb = new TATDB(getApplicationContext());
        PageList = new ArrayList<>();
        PageList.add(AddFragment(1));
        PageList.add(AddFragment(2));
        PageList.add(AddFragment(3));
        PageList.add(AddFragment(4));
        PageList.add(AddFragment(5));
        ShoppingList = new ArrayList<>();
        ShoppingList.add(new ShoppingStep1Fragment());
        ShoppingList.add(new ShoppingStep2Fragment());
        ShoppingList.add(new ShoppingStep3Fragment());
    }

    Fragment AddFragment(int position)
    {
        final String ARG_SECTION_NUMBER = "section_number";
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, position);
        switch (position - 1) {
            case 0:
                HomeFragment homeFragment = new HomeFragment();
                homeFragment.setArguments(args);
                return homeFragment;
            case 1:
                CollectionFragment collectionFragment = new CollectionFragment();
                collectionFragment.setArguments(args);
                return collectionFragment;
            case 2:
                ShoppingFragment shoppingFragment = new ShoppingFragment();
                shoppingFragment.setArguments(args);
                return shoppingFragment;
            case 3:
                SearchFragment searchFragment = new SearchFragment();
                searchFragment.setArguments(args);
                return searchFragment;
            case 4:
                MemberFragment memberFragment = new MemberFragment();
                memberFragment.setArguments(args);
                return memberFragment;
            default:
                return null;
        }
    }
}
