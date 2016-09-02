package ru.casak.IMDB_searcher.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.casak.IMDB_searcher.adapters.CardsAdapter;
import ru.casak.IMDB_searcher.models.Movie;
import ru.casak.IMDB_searcher.R;


public class PageFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    public static PageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page, container, false);
        final RecyclerView mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView);
        final LinearLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //mRecyclerView.setAdapter(new CardsAdapter(getContext().getResources().getString(R.string.ApiKey)));
        mRecyclerView.setAdapter(new CardsAdapter(new ArrayList<Movie>()));
        return rootView;
    }
}