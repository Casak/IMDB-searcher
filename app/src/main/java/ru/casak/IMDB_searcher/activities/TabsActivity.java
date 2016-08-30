package ru.casak.IMDB_searcher.activities;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;

import ru.casak.IMDB_searcher.R;
import ru.casak.IMDB_searcher.adapters.TabWithFragmentPagerAdapter;

public class TabsActivity extends AppCompatActivity {
    private static final String TAG = TabsActivity.class.getSimpleName();

    private static Context context;
    private static TypedArray[] colorThemes;
    private Window window;
    private ArgbEvaluator colorEvaluator;

    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onStart() {
        super.onStart();

        context = getApplicationContext();
        window = getWindow();

        colorEvaluator = new ArgbEvaluator();

        Resources resources = getResources();
        colorThemes = new TypedArray[] {
                resources.obtainTypedArray(R.array.colorSetTop250),
                resources.obtainTypedArray(R.array.colorSetComingSoon),
                resources.obtainTypedArray(R.array.colorSetFavorite)};
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new TabWithFragmentPagerAdapter(getSupportFragmentManager(),
                TabsActivity.this));
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ColorChangeListener());

        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    public static Context getContext() {
        return TabsActivity.context;
    }

    private void changeColorTheme(TypedArray colorSet){
        changeInterfaceHeadColorTheme(
                colorSet.getColor(0, 0),
                colorSet.getColor(1, 0),
                colorSet.getColor(2, 0),
                colorSet.getColor(3, 0)
        );
    }

    private void changeInterfaceHeadColorTheme(Integer actionBarColor, Integer statusBarColor, Integer tabIndicatorColor, Integer backgroundColor) {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(actionBarColor));
        window.setStatusBarColor(statusBarColor);
        window.setNavigationBarColor(backgroundColor);
        tabLayout.setBackgroundColor(backgroundColor);
        tabLayout.setSelectedTabIndicatorColor(tabIndicatorColor);
    }

    class ColorChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            TypedArray currentColorSet = colorThemes[position];
            TypedArray nextColor = position < colorThemes.length - 1
                    ? colorThemes[position + 1]
                    : colorThemes[position];

            Integer[] evalColor = new Integer[4];
            for (int i = 0; i < 4; i++) {
                evalColor[i] = (Integer) colorEvaluator.evaluate(
                        positionOffset,
                        currentColorSet.getColor(i, 0),
                        nextColor.getColor(i, 0)
                );
            }

            changeInterfaceHeadColorTheme(evalColor[0], evalColor[1], evalColor[2], evalColor[3]);
            Log.d(TAG, "onPageScrolled() ----done");
        }

        @Override
        public void onPageSelected(int position) {
            changeColorTheme(colorThemes[position]);
            Log.d(TAG, "onPageSelected() ----done");
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            Log.d(TAG, "onPageScrollStateChanged() ----done");
        }
    }
}
