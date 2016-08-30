package ru.casak.IMDB_searcher.fragments;


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

import ru.casak.IMDB_searcher.adapters.CardsAdapter;
import ru.casak.IMDB_searcher.services.FilmService;
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
    private static final String TAG = "Top250Fragment";
    private static final int SPAN_COUNT = 2;
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
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

        Observable<MovieResults> observable = filmService.getTopRated(page, "en");
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



/*public CardsAdapter(String apiKey, final Context context){
        this.apiKey = apiKey;
        this.context = context;

        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalUrl = original.url();

                HttpUrl url = originalUrl.newBuilder()
                        .addQueryParameter("api_key", apiKey)
                        .build();

                Request.Builder requestBuilder = original.newBuilder()
                        .url(url)
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                Log.d(TAG, "intercept(): builded request:" + request.toString());
                return chain.proceed(request);
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client.build())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FilmService filmService = retrofit.create(FilmService.class);

        Observable<MovieResults> observable = filmService.getTopRated(1, "en");
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
                    .doOnNext(new Action1<Movie>() {
                        @Override
                        public void call(Movie movie) {
                            loadImage(context, movie.getBackdrop_path(), );
                        }
                    })

                    .subscribe(new Subscriber<Movie>() {
                        @Override
                        public void onCompleted() {
                            Log.d(TAG, "onCompleted() ");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(TAG, "onError(): " + e.getMessage());
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(Movie movie) {

                            Log.d(TAG, "Result: " + movie.getBackdrop_path() + " \n\t " + movie.getTitle());
                        }
                    });
        }
        catch (NetworkOnMainThreadException e ){
            Log.d(TAG, "Caught:");
            e.printStackTrace();
        }
    }*/
