package ru.casak.IMDB_searcher.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.casak.IMDB_searcher.adapters.CardsAdapter;
import ru.casak.IMDB_searcher.database.DbUtils;
import ru.casak.IMDB_searcher.models.Movie;
import ru.casak.IMDB_searcher.models.MovieResults;
import ru.casak.IMDB_searcher.R;
import ru.casak.IMDB_searcher.network.TMDBRetrofit;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class Top250Fragment extends Fragment {
    private static final String TAG = Top250Fragment.class.getSimpleName();
    private static final int SPAN_COUNT = 2;
    private static final int PAGE_NUMBER = 1;
    private static final int MOVIES_PER_PAGE = 20;
    private final CardsAdapter cardsAdapter = new CardsAdapter();
    private RecyclerView mRecyclerView;
    private boolean loading = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadData(PAGE_NUMBER);
        Log.d(TAG, "onCreate finished");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final LinearLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);

        View rootView = inflater.inflate(R.layout.fragment_page, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(cardsAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        loading = false;
                        int page = (cardsAdapter.getItemCount() / MOVIES_PER_PAGE) + 1;
                        if (page <= 20) loadData(page);
                    }
                }
            }
        });
        Log.d(TAG, "onCreateView finished");
        return rootView;
    }

    private void loadData(final int page) {
        List<Movie> movies = DbUtils.getTopRatedMovies(page * MOVIES_PER_PAGE - MOVIES_PER_PAGE, page * MOVIES_PER_PAGE,
                getContext().getContentResolver());

        if (movies != null && movies.size() == MOVIES_PER_PAGE) {
            for (Movie movie : movies) {
                cardsAdapter.addMovie(movie);
                cardsAdapter.notifyItemRangeInserted(cardsAdapter.getMovieList().size() - 1, 1);
                loading = true;
            }
        } else {
            TMDBRetrofit
                    .getFilmServiceInstance()
                    .getTopRated(page, "en")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .flatMap(new Func1<MovieResults, Observable<Movie>>() {
                        @Override
                        public Observable<Movie> call(MovieResults movieResults) {
                            DbUtils.addTopRatedMovies(movieResults.getResults(),
                                    getContext().getContentResolver());
                            return Observable.from(movieResults.getResults());
                        }
                    })
                    .subscribe(new Subscriber<Movie>() {
                        @Override
                        public void onCompleted() {
                            loading = true;
                            Log.d(TAG, "onCompleted() ");
                        }

                        @Override
                        public void onError(Throwable e) {
                            loading = true;
                            Log.e(TAG, "onError(): " + e.getMessage());
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(Movie movie) {
                            cardsAdapter.addMovie(movie);
                            cardsAdapter.notifyItemRangeInserted(cardsAdapter.getMovieList().size() - 1, 1);
                            Log.d(TAG, "onNext: " + movie.getTitle());
                        }
                    });
        }
    }

}
