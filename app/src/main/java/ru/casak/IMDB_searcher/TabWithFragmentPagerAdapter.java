package ru.casak.IMDB_searcher;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabWithFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private Context context;
    private String tabTitles[];


    public TabWithFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        tabTitles = new String[] {
                context.getResources().getString(R.string.top250_tab_title),
                context.getResources().getString(R.string.coming_soon_tab_title),
                context.getResources().getString(R.string.favorite_tab_title) };
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0: return new Top250Fragment();
            case 1: return new ComingSoonFragment();
            default: return PageFragment.newInstance(position + 1);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}