package ru.casak.IMDB_searcher.adapters;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.casak.IMDB_searcher.R;
import ru.casak.IMDB_searcher.fragments.ComingSoonFragment;
import ru.casak.IMDB_searcher.fragments.PageFragment;
import ru.casak.IMDB_searcher.fragments.Top250Fragment;

public class TabWithFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    final int TOP250_POSITION = 0;
    final int COMING_SOON_POSITION = 1;
    final int FAVORITES_POSITION = 2;

    private String tabTitles[];


    public TabWithFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
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
            case TOP250_POSITION:
                return new Top250Fragment();
            case COMING_SOON_POSITION:
                return new ComingSoonFragment();
            case FAVORITES_POSITION:
                return PageFragment.newInstance(position + 1);
            default:
                return PageFragment.newInstance(position + 1);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}