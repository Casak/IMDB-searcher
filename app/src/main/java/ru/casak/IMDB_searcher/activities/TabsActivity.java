package ru.casak.IMDB_searcher.activities;

import android.animation.ArgbEvaluator;
import android.content.Context;
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
    private static final String TAG = "TabsActivity";
    private static Context context;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private static TypedArray ColorSetTop250Tab;
    private static TypedArray ColorSetComingSoonTab;
    private static TypedArray ColorSetFavoriteTab;
    private static TypedArray[] colorThemes;

    private ArgbEvaluator colorEvaluator = new ArgbEvaluator();
    private Window window;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        ColorSetTop250Tab = getResources().obtainTypedArray(R.array.colorSetTop250);
        ColorSetComingSoonTab = getResources().obtainTypedArray(R.array.colorSetComingSoon);
        ColorSetFavoriteTab = getResources().obtainTypedArray(R.array.colorSetFavorite);
        colorThemes = new TypedArray[] {ColorSetTop250Tab, ColorSetComingSoonTab, ColorSetFavoriteTab};
        window = getWindow();

        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new TabWithFragmentPagerAdapter(getSupportFragmentManager(),
                TabsActivity.this));
        viewPager.setOffscreenPageLimit(2);

        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

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
        });
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
}
