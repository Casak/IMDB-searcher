package ru.casak.IMDB_searcher.adapters;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.annotation.TargetApi;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import ru.casak.IMDB_searcher.database.DbUtils;
import ru.casak.IMDB_searcher.models.Movie;
import ru.casak.IMDB_searcher.models.MovieResults;
import ru.casak.IMDB_searcher.network.TMDBRetrofit;
import ru.casak.IMDB_searcher.services.FilmService;
import rx.Observable;
import rx.functions.Func1;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = SyncAdapter.class.getSimpleName();
    private static final Integer TOTAL_PAGE_COUNT = 13;

    private final FilmService service = TMDBRetrofit.getFilmServiceInstance();

    private ContentResolver mContentResolver;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String authority, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.i(TAG, "Beginning network synchronization");

        ConnectivityManager mConnectivityManager = (ConnectivityManager) getContext().
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();

        if (networkInfo == null && !networkInfo.isConnectedOrConnecting())
            return;

        Observable<List<Movie>> topRated = getTopRatedEntries();
        Observable<List<Movie>> upcoming = getUpcomingEntries();
        topRated.concatMap(new AddTopRatedToDataBaseFunc(mContentResolver)).subscribe();
        upcoming.concatMap(new AddUpcomingToDataBaseFunc(mContentResolver)).subscribe();

        Log.i(TAG, "Ending network synchronization");
    }

    private Observable<List<Movie>> getUpcomingEntries() {
        final Observable<List<Movie>> result = Observable.empty();
        for (int page = 1; page < TOTAL_PAGE_COUNT; page++)
            result.mergeWith(getUpcomingPage(page));
        return result;
    }

    private Observable<List<Movie>> getUpcomingPage(Integer page) {
        return service
                .getUpcoming(page, "en")
                .concatMap(new EmitMoviesFromMovieList())
                .buffer(20);
    }

    private Observable<List<Movie>> getTopRatedEntries() {
        final Observable<List<Movie>> result = Observable.empty();
        for (int page = 1; page < TOTAL_PAGE_COUNT; page++)
            result.mergeWith(getTopRatedPage(page));
        return result;
    }

    private Observable<List<Movie>> getTopRatedPage(Integer page) {
        return service
                .getTopRated(page, "en")
                .concatMap(new EmitMoviesFromMovieList())
                .buffer(20);
    }


    private static class EmitMoviesFromMovieList implements Func1<MovieResults, Observable<Movie>> {
        @Override
        public Observable<Movie> call(MovieResults movieResults) {
            return Observable.from(movieResults.getResults());
        }
    }

    private static class AddTopRatedToDataBaseFunc implements Func1<List<Movie>, Observable<?>> {
        private ContentResolver mContentResolver;

        AddTopRatedToDataBaseFunc(ContentResolver resolver) {
            mContentResolver = resolver;
        }

        @Override
        public Observable<?> call(List<Movie> movies) {
            DbUtils.addTopRatedMovies(movies, mContentResolver, 0);
            return Observable.from(movies);
        }
    }

    private static class AddUpcomingToDataBaseFunc implements Func1<List<Movie>, Observable<?>> {
        private ContentResolver mContentResolver;

        AddUpcomingToDataBaseFunc(ContentResolver resolver) {
            mContentResolver = resolver;
        }

        @Override
        public Observable<?> call(List<Movie> movies) {
            DbUtils.addUpcomingMovies(movies, mContentResolver);
            return Observable.from(movies);
        }
    }
}
