package ru.casak.IMDB_searcher.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

public class TMDBContentProvider extends ContentProvider {
    private static final String TAG = TMDBContentProvider.class.getSimpleName();

    private static final String AUTHORITY = MovieContract.CONTENT_AUTHORITY;

    static final int MOVIE = 1;
    static final int MOVIES = 99;

    private static final SQLiteQueryBuilder sMovieQueryBuilder;
    static {
        sMovieQueryBuilder = new SQLiteQueryBuilder();

        //movie INNER JOIN genre, country, company, language ON movie.genres = genre._id,
        //movie.countries = country._id, movie.companies = company._id,
        //movie.spoken_languages = language._id
        sMovieQueryBuilder.setTables(MovieContract.MovieEntry.TABLE_NAME +
                " INNER JOIN " +
                MovieContract.GengeEntry.TABLE_NAME + ", " +
                MovieContract.CountryEntry.TABLE_NAME + ", " +
                MovieContract.CompanyEntry.TABLE_NAME + ", " +
                MovieContract.SpokenLanguagesEntry.TABLE_NAME +
                " ON " +
                MovieContract.MovieEntry.TABLE_NAME + "." +
                MovieContract.MovieEntry.COLUMN_GENRES + " = " +
                MovieContract.GengeEntry.TABLE_NAME + "." +
                MovieContract.GengeEntry._ID +
                ", " +
                MovieContract.MovieEntry.TABLE_NAME + "." +
                MovieContract.MovieEntry.COLUMN_COUNTRIES + " = " +
                MovieContract.CountryEntry.TABLE_NAME + "." +
                MovieContract.CountryEntry._ID +
                ", " +
                MovieContract.MovieEntry.TABLE_NAME + "." +
                MovieContract.MovieEntry.COLUMN_COMPANIES + " = " +
                MovieContract.CompanyEntry.TABLE_NAME + "." +
                MovieContract.CompanyEntry._ID +
                ", " +
                MovieContract.MovieEntry.TABLE_NAME + "." +
                MovieContract.MovieEntry.COLUMN_SPOKEN_LANGUAGES + " = " +
                MovieContract.SpokenLanguagesEntry.TABLE_NAME + "." +
                MovieContract.SpokenLanguagesEntry._ID);
    }

    private static UriMatcher uriMatcher = buildUriMatcher();

    private MovieDbHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor cursor;

        switch (uriMatcher.match(uri)){
            case MOVIES:
                return sMovieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            case MOVIE:
                selection = MovieContract.MovieEntry.TABLE_NAME + "." +
                        MovieContract.MovieEntry._ID + " = ?";
                selectionArgs = new String[]{ uri.getLastPathSegment() };
                return sMovieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match){
            case MOVIES:
                return MovieContract.MovieEntry.CONTENT_DIR_TYPE;
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    static UriMatcher buildUriMatcher(){
        UriMatcher result = new UriMatcher(UriMatcher.NO_MATCH);

        result.addURI(AUTHORITY, MovieContract.PATH_MOVIES, MOVIES);
        result.addURI(AUTHORITY, MovieContract.PATH_MOVIE + "/#", MOVIE);

        return result;
    }
}
