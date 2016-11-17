package ru.casak.IMDB_searcher.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import ru.casak.IMDB_searcher.models.Genre;
import ru.casak.IMDB_searcher.models.Movie;
import ru.casak.IMDB_searcher.network.TMDBRetrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DbUtils {
    private static final String TAG = DbUtils.class.getSimpleName();

    public static void addMovie(Movie movie, final ContentResolver resolver) {
        final Uri uri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, movie.getId());
        addMovieGenres(movie, resolver);
        ContentValues values = createMovieContentValues(movie);
        resolver.insert(uri, values);
    }

    public static void addMovieIfNotExist(Integer id, final ContentResolver resolver) {
        Movie movie = DbUtils.getMovie(id, resolver);
        if (movie == null) {
            TMDBRetrofit
                    .getFilmServiceInstance()
                    .getMovie(id, "en")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<Movie>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(Movie movie) {
                            addMovie(movie, resolver);
                            Log.d(TAG, "Inserted: " + movie.getTitle());
                        }
                    });
        }
    }

    @Nullable
    public static Movie getMovie(Integer id, ContentResolver resolver) {
        final Uri movieWithId = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id);
        Movie result = null;
        Cursor cursor = resolver.query(movieWithId,
                null,
                null,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            result = createMovieFromCursor(cursor);
            cursor.close();
        }
        return result;
    }

    @Nullable
    public static Movie getUpcomingMovie(Integer id, ContentResolver resolver) {
        final Uri movieWithId = ContentUris.withAppendedId(MovieContract.UpcomingEntry.CONTENT_URI, id);
        Movie result = null;
        Cursor cursor = resolver.query(movieWithId,
                null,
                null,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            result = createMovieFromCursor(cursor);
            cursor.close();
        }
        return result;
    }

    @Nullable
    public static Movie getTopRatedMovie(Integer id, ContentResolver resolver) {
        final Uri movieWithId = ContentUris.withAppendedId(MovieContract.TopRatedEntry.CONTENT_URI, id);
        Movie result = null;
        Cursor cursor = resolver.query(movieWithId,
                null,
                null,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            result = createMovieFromCursor(cursor);
            cursor.close();
        }
        return result;
    }

    public static void addTopRatedMovies(List<Movie> movies, final ContentResolver resolver) {
        List<ContentValues> topRatedValues = new ArrayList<>();
        for (Movie movie : movies) {
            if (getTopRatedMovie(movie.getId(), resolver) == null) {
                DbUtils.addMovieIfNotExist(movie.getId(), resolver);

                ContentValues value = new ContentValues();
                value.put(MovieContract.TopRatedEntry.COLUMN_MOVIE_ID, movie.getId());
                value.put(MovieContract.TopRatedEntry.COLUMN_RATING, movie.getVoteAverage());
                topRatedValues.add(value);
            }
        }

        int valuesNumber = topRatedValues.size();

        switch (valuesNumber) {
            case 0:
                break;
            case 1:
                ContentValues value = topRatedValues.get(0);
                int movieId = Integer.parseInt(value
                        .get(MovieContract.TopRatedEntry.COLUMN_MOVIE_ID)
                        .toString());
                final Uri uri = ContentUris.withAppendedId(MovieContract.TopRatedEntry.CONTENT_URI,
                        movieId);
                resolver.insert(uri, value);
                break;
            default:
                resolver.bulkInsert(MovieContract.TopRatedEntry.CONTENT_URI,
                        topRatedValues.toArray(new ContentValues[topRatedValues.size()]));
        }
    }

    @Nullable
    public static List<Movie> getTopRatedMovies(Integer start, Integer end, ContentResolver resolver) {
        List<Movie> result = new ArrayList<>();
        String sortOrder = MovieContract.TopRatedEntry.COLUMN_RATING + " DESC LIMIT " + (end - start)
                + " OFFSET " + start;
        Cursor cursor = resolver.query(MovieContract.TopRatedEntry.CONTENT_URI,
                null,
                null,
                null,
                sortOrder);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int movieId = cursor.getInt(0);
                Movie movie = getMovie(movieId, resolver);
                if (movie != null)
                    result.add(movie);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return result.size() == 0 ? null : result;
    }

    public static void addUpcomingMovies(List<Movie> movies, final ContentResolver resolver) {
        List<ContentValues> upcomingValues = new ArrayList<>();

        for (Movie movie : movies) {
            if (getUpcomingMovie(movie.getId(), resolver) == null) {
                DbUtils.addMovieIfNotExist(movie.getId(), resolver);

                ContentValues value = new ContentValues();
                value.put(MovieContract.UpcomingEntry.COLUMN_MOVIE_ID, movie.getId());
                value.put(MovieContract.UpcomingEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
                upcomingValues.add(value);
            }
        }

        int valuesNumber = upcomingValues.size();

        switch (valuesNumber) {
            case 0:
                break;
            case 1:
                ContentValues value = upcomingValues.get(0);
                int movieId = Integer.parseInt(value
                        .get(MovieContract.UpcomingEntry.COLUMN_MOVIE_ID)
                        .toString());
                final Uri uri = ContentUris.withAppendedId(MovieContract.UpcomingEntry.CONTENT_URI,
                        movieId);
                resolver.insert(uri, value);
                break;
            default:
                resolver.bulkInsert(MovieContract.UpcomingEntry.CONTENT_URI,
                        upcomingValues.toArray(new ContentValues[upcomingValues.size()]));
        }
    }

    @Nullable
    public static List<Movie> getUpcomingMovies(Integer start, Integer end, ContentResolver resolver) {
        List<Movie> result = new ArrayList<>();
        String dateToday = new SimpleDateFormat("yyyy-MM-dd")
                .format(Calendar.getInstance().getTime());

        String selection = MovieContract.UpcomingEntry.COLUMN_RELEASE_DATE + " >= ?" ;
        String[] selectionArgs = new String[]{dateToday};
        String sortOrder = MovieContract.UpcomingEntry.COLUMN_RELEASE_DATE + " ASC LIMIT "
                + (end - start) + " OFFSET " + start;
        Cursor cursor = resolver.query(MovieContract.UpcomingEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                sortOrder);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int movieId = cursor.getInt(1);
                Movie movie = getMovie(movieId, resolver);
                if (movie != null)
                    result.add(movie);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return result.size() == 0 ? null : result;
    }

    public static boolean addGenresIfNotExist(List<Genre> genres, ContentResolver resolver) {
        if (genres == null)
            return false;

        int countInserted = 0;
        String[] projection = new String[]{"_id"};
        StringBuilder selection = new StringBuilder();
        String[] selectionArgs = new String[genres.size()];

        Map<Integer, ContentValues> values = new HashMap<>();

        for (int i = 0; i < genres.size(); i++) {
            selection.append(MovieContract.GengeEntry.TABLE_NAME + "." + MovieContract.GengeEntry._ID + " = ? OR ");
            Genre genre = genres.get(i);
            selectionArgs[i] = genre.getId().toString();

            ContentValues value = new ContentValues();
            value.put(MovieContract.GengeEntry._ID, genre.getId());
            value.put(MovieContract.GengeEntry.COLUMN_NAME, genre.getName());

            values.put(genre.getId(), value);
        }

        if (selection.length() != 0)
            selection.delete(selection.length() - 3, selection.length());

        Cursor cursor = resolver.query(MovieContract.GengeEntry.CONTENT_URI,
                projection,
                selection.toString(),
                selectionArgs,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                values.remove(cursor.getInt(0));
            } while (cursor.moveToNext());
            cursor.close();
        }

        if (values.size() > 0)
            countInserted = resolver.bulkInsert(MovieContract.GengeEntry.CONTENT_URI,
                    values.values().toArray(new ContentValues[values.size()]));

        return values.size() == countInserted;
    }

    public static boolean addMovieGenres(Movie movie, ContentResolver resolver) {
        int insertedCount = 0;
        if (addGenresIfNotExist(movie.getGenres(), resolver)) {
            HashSet<ContentValues> values = new HashSet<>();
            for (Genre genre : movie.getGenres()) {
                ContentValues value = new ContentValues();
                value.put(MovieContract.MovieGenreJunctionEntry.COLUMN_FILM_ID, movie.getId());
                value.put(MovieContract.MovieGenreJunctionEntry.COLUMN_GENRE_ID, genre.getId());
                values.add(value);
            }
            insertedCount = resolver.bulkInsert(MovieContract.MovieGenreJunctionEntry.CONTENT_URI,
                    values.toArray(new ContentValues[movie.getGenres().size()]));
        }
        return insertedCount == movie.getGenres().size();
    }

    private static Movie createMovieFromCursor(Cursor cursor) {
        return new Movie(
                cursor.getInt(0),
                Boolean.parseBoolean(cursor.getString(1)),
                cursor.getString(2),
                cursor.getInt(3),
                null,//TODO get genres
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getString(7),
                cursor.getString(8),
                cursor.getString(9),
                cursor.getString(10),
                null,
                null,
                cursor.getString(11),
                cursor.getInt(12),
                cursor.getInt(13),
                null,
                cursor.getString(14),
                cursor.getString(15),
                cursor.getString(16),
                cursor.getDouble(17),
                cursor.getLong(18)
        );
    }

    private static ContentValues createMovieContentValues(Movie movie) {
        ContentValues result = new ContentValues();

        result.put(MovieContract.MovieEntry._ID, movie.getId());
        result.put(MovieContract.MovieEntry.COLUMN_ADULT, movie.isAdult());
        result.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
        result.put(MovieContract.MovieEntry.COLUMN_BUDGET, movie.getBudget());

        result.put(MovieContract.MovieEntry.COLUMN_HOMEPAGE, movie.getHomepage());
        result.put(MovieContract.MovieEntry.COLUMN_IMDB_ID, movie.getImdbID());
        result.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, movie.getOriginalLanguage());
        result.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
        result.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        result.put(MovieContract.MovieEntry.COLUMN_POPULARITY, movie.getPopularity());
        result.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());

        result.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        result.put(MovieContract.MovieEntry.COLUMN_REVENUE, movie.getRevenue());
        result.put(MovieContract.MovieEntry.COLUMN_RUNTIME, movie.getRuntime());

        result.put(MovieContract.MovieEntry.COLUMN_STATUS, movie.getStatus());
        result.put(MovieContract.MovieEntry.COLUMN_TAGLINE, movie.getTagline());
        result.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        result.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        result.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, movie.getVoteCount());

        return result;
    }
}
