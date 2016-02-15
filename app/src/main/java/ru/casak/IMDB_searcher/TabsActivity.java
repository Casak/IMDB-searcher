package ru.casak.IMDB_searcher;

import android.animation.ArgbEvaluator;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

public class TabsActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private static TypedArray ColorSetTop250Tab;
    private static TypedArray ColorSetComingSoonTab;
    private static TypedArray ColorSetFavoriteTab;
    private static TypedArray[] colorThemes;

    private ArgbEvaluator colorEvaluator = new ArgbEvaluator();
    private Window window;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager(),
                TabsActivity.this));

        ColorSetTop250Tab = getResources().obtainTypedArray(R.array.colorSetTop250);
        ColorSetComingSoonTab = getResources().obtainTypedArray(R.array.colorSetComingSoon);
        ColorSetFavoriteTab = getResources().obtainTypedArray(R.array.colorSetFavorite);
        colorThemes = new TypedArray[] {ColorSetTop250Tab, ColorSetComingSoonTab, ColorSetFavoriteTab};



        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        window = getWindow();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                TypedArray currentColorSet = colorThemes[position];
                TypedArray nextColor = position < colorThemes.length - 1
                        ? colorThemes[position + 1]
                        : colorThemes[position];

                Integer[] a = new Integer[4];
                for ( int i = 0; i < 4 ; i++ ) {
                    a[i] = (Integer) colorEvaluator.evaluate(
                            positionOffset,
                            currentColorSet.getColor(i, 0),
                            nextColor.getColor(i, 0)
                    );
                }

                changeInterfaceHeadColorTheme(a[0], a[1], a[2], a[3]);
            }

            @Override
            public void onPageSelected(int position) {
                changeColorTheme(colorThemes[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
