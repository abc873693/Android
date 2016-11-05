package com.rainvisitor.fb_api;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class PhotoActivity extends AppCompatActivity {
    private ArrayList<String> photos_url = new ArrayList<>();
    private ViewPager mViewPager;
    private MessagesPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        String json = "", url = "";
        if (getIntent().getExtras() != null) {
            json = getIntent().getExtras().getString("image_json");
            url = getIntent().getExtras().getString("url");
        }
        photos_url.add(url);
        try {
            if (!json.equals("")) {
                JSONObject json_data = new JSONObject(json);
                JSONArray jsonArray = json_data.getJSONArray("data");
                for (int i = 1; i <= jsonArray.length() - 1; i++) {
                    JSONObject dataObject = jsonArray.getJSONObject(i);
                    String tmp = dataObject.getJSONObject("media").getJSONObject("image").optString("src");
                    Log.d("url", tmp);
                    photos_url.add(tmp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAdapter = new MessagesPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                int width = page.getWidth();
                int i = mViewPager.getCurrentItem();
                TextView textView = (TextView) findViewById(R.id.position);
                textView.setText(i + 1 + "/" + photos_url.size());
            }
        });
    }

    private class MessagesPagerAdapter extends FragmentPagerAdapter {

        public MessagesPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new ImageFragment().newInstance(photos_url.get(position));
        }

        @Override
        public int getCount() {
            return photos_url.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return photos_url.get(position);
        }
    }
}
