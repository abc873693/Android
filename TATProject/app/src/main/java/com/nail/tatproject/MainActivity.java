package com.nail.tatproject;

import android.app.SearchManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private TATApplication Global; // 全域變數
    private int currentIndex;
    private Fragment mCurrentFrgment;
    private Toolbar toolbar;
    private MenuItem menuSearchItem;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Global = (TATApplication) getApplicationContext();
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Global.ShoppingStep > 0) {
                    Global.ShoppingStep -= 1;
                    mCurrentFrgment.getChildFragmentManager().beginTransaction().replace(R.id.shopping_container, Global.ShoppingList.get(Global.ShoppingStep)).commit();
                }
            }
        });
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // --------------------------------------------------------------------------------------------------------------------------------------------------

        RadioGroup rg = (RadioGroup) this.findViewById(R.id.radioGroup);
        RadioButton rb = (RadioButton) findViewById(R.id.rd_home);
        rb.setCompoundDrawables(null, tintDrawable(rb.getCompoundDrawables()[1], getResources().getColorStateList(R.color.style_bottom_menu)), null, null);
        rb = (RadioButton) findViewById(R.id.rd_collection);
        rb.setCompoundDrawables(null, tintDrawable(rb.getCompoundDrawables()[1], getResources().getColorStateList(R.color.style_bottom_menu)), null, null);
        rb = (RadioButton) findViewById(R.id.rd_shopping);
        rb.setCompoundDrawables(null, tintDrawable(rb.getCompoundDrawables()[1], getResources().getColorStateList(R.color.style_bottom_menu)), null, null);
        rb = (RadioButton) findViewById(R.id.rd_search);
        rb.setCompoundDrawables(null, tintDrawable(rb.getCompoundDrawables()[1], getResources().getColorStateList(R.color.style_bottom_menu)), null, null);
        rb = (RadioButton) findViewById(R.id.rd_member);
        rb.setCompoundDrawables(null, tintDrawable(rb.getCompoundDrawables()[1], getResources().getColorStateList(R.color.style_bottom_menu)), null, null);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rd_home:
                        changeTab(0);
                        break;
                    case R.id.rd_collection:
                        changeTab(1);
                        break;
                    case R.id.rd_shopping:
                        changeTab(2);
                        break;
                    case R.id.rd_search:
                        changeTab(3);
                        break;
                    case R.id.rd_member:
                        changeTab(4);
                        break;
                    default:
                        break;
                }
            }
        });
        initFragment(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("CurrentIndex", currentIndex);
        for (int i = 0; i < Global.PageList.size(); i++) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(Global.PageList.get(i).getClass().getName());
            if (null != fragment) {
                getSupportFragmentManager().putFragment(outState, "Fragment" + i, fragment);
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        menuSearchItem = menu.findItem(R.id.action_search);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menuSearchItem.getActionView();
        // 這邊讓icon可以還原到搜尋的icon
        searchView.setIconifiedByDefault(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case android.R.id.home:
                return true;
            case R.id.action_search:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            ((RadioButton) findViewById(R.id.rd_home)).setChecked(true);
        } else {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            for (int i = 0; i < Global.PageList.size(); i++) {
                Fragment fragment = getSupportFragmentManager().getFragment(savedInstanceState, "Fragment" + i);
                if (null != fragment) {
                    if (!fragment.isAdded()) {
                        ft.add(R.id.container, fragment, fragment.getClass().getName());
                    }
                    ft.hide(fragment);
                }
            }
            ft.commit();
            switch (savedInstanceState.getInt("CurrentIndex")) {
                case 0:
                    ((RadioButton) findViewById(R.id.rd_home)).setChecked(true);
                    break;
                case 1:
                    ((RadioButton) findViewById(R.id.rd_collection)).setChecked(true);
                    break;
                case 2:
                    ((RadioButton) findViewById(R.id.rd_shopping)).setChecked(true);
                    break;
                case 3:
                    ((RadioButton) findViewById(R.id.rd_search)).setChecked(true);
                    break;
                case 4:
                    ((RadioButton) findViewById(R.id.rd_member)).setChecked(true);
                    break;
                default:
                    break;
            }
        }
    }

    private void changeTab(int index) {
        switch (index) {
            case 0:
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setHomeButtonEnabled(false);
                toggle.setDrawerIndicatorEnabled(true);
                if(menuSearchItem != null)
                    menuSearchItem.setVisible(false);
                break;
            case 2:
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                toggle.setDrawerIndicatorEnabled(false);
                if(menuSearchItem != null)
                    menuSearchItem.setVisible(false);
                break;
            case 3:
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setHomeButtonEnabled(false);
                toggle.setDrawerIndicatorEnabled(false);
                if(menuSearchItem != null)
                    menuSearchItem.setVisible(true);
                break;
            default:
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setHomeButtonEnabled(false);
                toggle.setDrawerIndicatorEnabled(false);
                if(menuSearchItem != null)
                    menuSearchItem.setVisible(false);
                break;
        }
        Global.ShoppingStep = 0;
        currentIndex = index;

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //判断当前的Fragment是否为空，不为空则隐藏
        if (null != mCurrentFrgment) {
            ft.hide(mCurrentFrgment);
        }
        //先根据Tag从FragmentTransaction事物获取之前添加的Fragment
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(Global.PageList.get(currentIndex).getClass().getName());

        if (null == fragment) {
            fragment = Global.PageList.get(index);
            ft.add(R.id.container, fragment, fragment.getClass().getName());
        } else {
            ft.show(fragment);
        }
        ft.commit();

        mCurrentFrgment = fragment;
    }

    public static Drawable tintDrawable(Drawable drawable, ColorStateList colors) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, colors);
        return wrappedDrawable;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            if(Global.ShoppingStep > 0) {
                Global.ShoppingStep -= 1;
                mCurrentFrgment.getChildFragmentManager().beginTransaction().replace(R.id.shopping_container, Global.ShoppingList.get(Global.ShoppingStep)).commit();
            }
        }
        return false;
    }
}
