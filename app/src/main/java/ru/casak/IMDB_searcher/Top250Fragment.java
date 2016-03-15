package ru.casak.IMDB_searcher;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Top250Fragment extends Fragment {
    private static final String TAG = "Top250Fragment";
    private static final int SPAN_COUNT = 2;
    private boolean loading = true;
    private int previousTotal = 0;
    private int visibleThreshold = 4;
    private int firstVisibleItem, visibleItemCount, totalItemCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page, container, false);
        final RecyclerView mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView);
        final LinearLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(new CardsAdapter(getContext().getResources().getString(R.string.ApiKey)));
        Log.d(TAG, "onCreateView finished");
        return rootView;
    }

}
