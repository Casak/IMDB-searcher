package ru.casak.IMDB_searcher.activities;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;

import java.lang.ref.WeakReference;

import ru.casak.IMDB_searcher.R;
import ru.casak.IMDB_searcher.adapters.TabWithFragmentPagerAdapter;

public class TabsActivity extends AppCompatActivity {
    private static final String TAG = TabsActivity.class.getSimpleName();

    private static WeakReference<Context> mContextReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContextReference = new WeakReference<>(getApplicationContext());

        Resources resources = getResources();
        TypedArray[] colors = new TypedArray[] {
                resources.obtainTypedArray(R.array.colorSetTop250),
                resources.obtainTypedArray(R.array.colorSetComingSoon),
                resources.obtainTypedArray(R.array.colorSetFavorite)};

        setContentView(R.layout.activity_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);

        ColorChangeListener listener = new ColorChangeListener(colors, getWindow(), tabLayout);

        viewPager.setAdapter(new TabWithFragmentPagerAdapter(getSupportFragmentManager(),
                TabsActivity.this));
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(listener);

        tabLayout.setupWithViewPager(viewPager);
    }

    public static Context getContext() {
        return mContextReference.get();
    }

    class ColorChangeListener implements ViewPager.OnPageChangeListener {
        private TypedArray[] colors;
        private ArgbEvaluator colorEvaluator;
        private Window window;
        private TabLayout tabLayout;

        public ColorChangeListener(TypedArray[] colors, Window window, TabLayout tabLayout){
            this.colors = colors;
            this.window = window;
            this.tabLayout = tabLayout;
            colorEvaluator = new ArgbEvaluator();
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            TypedArray currentColorSet = colors[position];
            TypedArray nextColor = position < colors.length - 1
                    ? colors[position + 1]
                    : colors[position];

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
            changeColorTheme(colors[position]);
            Log.d(TAG, "onPageSelected() ----done");
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            Log.d(TAG, "onPageScrollStateChanged() ----done");
        }

        private void changeColorTheme(TypedArray colorSet) {
            changeInterfaceHeadColorTheme(
                    colorSet.getColor(0, 0),
                    colorSet.getColor(1, 0),
                    colorSet.getColor(2, 0),
                    colorSet.getColor(3, 0)
            );
        }

        private void changeInterfaceHeadColorTheme(Integer actionBarColor, Integer statusBarColor,
                                                   Integer tabIndicatorColor, Integer backgroundColor) {

            try{
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(actionBarColor));
            } catch (NullPointerException e){
                e.printStackTrace();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(statusBarColor);
                window.setNavigationBarColor(backgroundColor);
            }
            tabLayout.setBackgroundColor(backgroundColor);
            tabLayout.setSelectedTabIndicatorColor(tabIndicatorColor);
        }
    }
}
