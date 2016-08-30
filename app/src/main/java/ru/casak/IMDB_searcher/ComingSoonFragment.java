package ru.casak.IMDB_searcher;


import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ComingSoonFragment extends Fragment {
    private static final String TAG = "ComingSoonFragment";
    private static final int SPAN_COUNT = 2;
    private CardsAdapter cardsAdapter = new CardsAdapter(new ArrayList<Movie>());
    private RecyclerView mRecyclerView;
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        loadData(1);
        Log.d(TAG, "onCreate finished");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page, container, false);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView);
        final LinearLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(cardsAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        loading = false;
                        int page = (cardsAdapter.getItemCount() / 20) + 1;
                        if (page <= 20) loadData(page);
                    }
                }
            }
        });
        Log.d(TAG, "onCreateView finished");
        return rootView;
    }

    private void loadData(int page){
        FilmService filmService = TMDBRetrofit.getFilmServiceInstance();

        Observable<MovieResults> observable = filmService.getUpcoming(page, "en");
        try {
            observable
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .flatMap(new Func1<MovieResults, Observable<Movie>>() {
                        @Override
                        public Observable<Movie> call(MovieResults movieResults) {
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
                            cardsAdapter.getMovieList().add(movie);

                            cardsAdapter.notifyItemRangeInserted(cardsAdapter.getMovieList().size() - 1, 1);
                            Log.d(TAG, "onNext: " + movie.getTitle());
                        }
                    });
        }
        catch (NetworkOnMainThreadException e ){
            Log.d(TAG, "Caught:");
            e.printStackTrace();
        }
    }

}