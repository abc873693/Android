package com.nail.tatproject.Fragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.nail.tatproject.R;

import java.util.HashMap;

/**
 * Created by 70J on 2016/6/17.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class FirstFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private SwipeRefreshLayout laySwipe;
    private SliderLayout mDemoSlider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("FirstFragment", "onCreate");
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //導入Tab分頁的Fragment Layout
        Log.e("FirstFragment", "onCreateView");
        View view =  inflater.inflate(R.layout.fragment_first, container, false);
        laySwipe = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_layout);
        laySwipe.setOnRefreshListener(this);
        mDemoSlider = (SliderLayout)view.findViewById(R.id.slider);

        HashMap<String,Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("Hannibal", R.drawable.hannibal);
        file_maps.put("Big Bang Theory", R.drawable.bigbang);
        file_maps.put("House of Cards", R.drawable.house);
        file_maps.put("Game of Thrones", R.drawable.game_of_thrones);

        for (String name : file_maps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(this.getContext());
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);

            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("FirstFragment", "onResume");
        // 資料連接

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("FirstFragment", "onPause");

    }

    @Override
    public void onRefresh() {
        laySwipe.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                laySwipe.setRefreshing(false);
                Toast.makeText(FirstFragment.this.getContext(), "Refresh done!", Toast.LENGTH_SHORT).show();
            }
        }, 3000);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this.getContext(),slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
    }
}
