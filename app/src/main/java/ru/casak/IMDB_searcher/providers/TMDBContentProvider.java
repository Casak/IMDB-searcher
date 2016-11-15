package ru.casak.IMDB_searcher.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import ru.casak.IMDB_searcher.database.MovieContract;
import ru.casak.IMDB_searcher.database.MovieDbHelper;

public class TMDBContentProvider extends ContentProvider {
    private static final String TAG = TMDBContentProvider.class.getSimpleName();

    public static final String AUTHORITY = MovieContract.CONTENT_AUTHORITY;

    static final int MOVIE = 1;
    static final int MOVIES = 11;
    static final int LANGUAGE = 2;
    static final int LANGUAGES = 22;
    static final int COMPANY = 3;
    static final int COMPANIES = 33;
    static final int COUNTRY = 4;
    static final int COUNTRIES = 44;
    static final int GENRE = 5;
    static final int GENRES = 55;
    static final int MOVIE_GENRES = 66;
    static final int TOP_RATED = 250;
    static final int TOP_RATED_ENTRY = 255;
    static final int UPCOMING = 500;
    static final int UPCOMING_ENTRY = 505;
    static final int FAVORITE = 100;
    static final int FAVORITES = 1000;

    private static final SQLiteQueryBuilder sMovieQueryBuilder;
    private static final SQLiteQueryBuilder sTopRatedQueryBuilder;
    private static final SQLiteQueryBuilder sUpcomingQueryBuilder;
    private static final SQLiteQueryBuilder sFavoritesQueryBuilder;
    private static final SQLiteQueryBuilder sGenresQueryBuilder;

    static {
        sMovieQueryBuilder = new SQLiteQueryBuilder();
        sTopRatedQueryBuilder = new SQLiteQueryBuilder();
        sUpcomingQueryBuilder = new SQLiteQueryBuilder();
        sFavoritesQueryBuilder = new SQLiteQueryBuilder();
        sGenresQueryBuilder = new SQLiteQueryBuilder();

        sMovieQueryBuilder.setTables(MovieContract.MovieEntry.TABLE_NAME
        );

        sTopRatedQueryBuilder.setTables(
                MovieContract.TopRatedEntry.TABLE_NAME +
                        " INNER JOIN " +
                        MovieContract.MovieEntry.TABLE_NAME + " ON " +
                        MovieContract.TopRatedEntry.TABLE_NAME + "." +
                        MovieContract.TopRatedEntry.COLUMN_MOVIE_ID + " = " +
                        MovieContract.MovieEntry.TABLE_NAME + "." +
                        MovieContract.MovieEntry._ID);

        sUpcomingQueryBuilder.setTables(
                MovieContract.UpcomingEntry.TABLE_NAME +
                        " INNER JOIN " +
                        MovieContract.MovieEntry.TABLE_NAME + " ON " +
                        MovieContract.UpcomingEntry.TABLE_NAME + "." +
                        MovieContract.UpcomingEntry.COLUMN_MOVIE_ID + " = " +
                        MovieContract.MovieEntry.TABLE_NAME + "." +
                        MovieContract.MovieEntry._ID);

        sFavoritesQueryBuilder.setTables(
                MovieContract.FavoritesEntry.TABLE_NAME +
                        " INNER JOIN " +
                        MovieContract.MovieEntry.TABLE_NAME + " ON " +
                        MovieContract.FavoritesEntry.TABLE_NAME + "." +
                        MovieContract.FavoritesEntry.COLUMN_MOVIE_ID + " = " +
                        MovieContract.MovieEntry.TABLE_NAME + "." +
                        MovieContract.MovieEntry._ID);

        sGenresQueryBuilder.setTables(
                MovieContract.GengeEntry.TABLE_NAME);
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
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case MOVIES:
                return sMovieQueryBuilder.query(db,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            case MOVIE:
                selection = MovieContract.MovieEntry.TABLE_NAME + "." +
                        MovieContract.MovieEntry._ID + " = ?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                return sMovieQueryBuilder.query(db,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            case TOP_RATED:
                return sTopRatedQueryBuilder.query(db,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            case TOP_RATED_ENTRY:
                selection = MovieContract.TopRatedEntry.TABLE_NAME + "." +
                        MovieContract.TopRatedEntry._ID + " = ?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                return sTopRatedQueryBuilder.query(db,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            case UPCOMING:
                return sUpcomingQueryBuilder.query(db,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            case UPCOMING_ENTRY:
                selection = MovieContract.UpcomingEntry.TABLE_NAME + "." +
                        MovieContract.UpcomingEntry._ID + " = ?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                return sUpcomingQueryBuilder.query(db,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            case FAVORITES:
                return sFavoritesQueryBuilder.query(db,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            case GENRES:
                return sGenresQueryBuilder.query(db,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            case MOVIE_GENRES:
                return sGenresQueryBuilder.query(db,
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
        switch (match) {
            case MOVIES:
                return MovieContract.MovieEntry.CONTENT_DIR_TYPE;
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case TOP_RATED:
                return MovieContract.TopRatedEntry.CONTENT_DIR_TYPE;
            case TOP_RATED_ENTRY:
                return MovieContract.TopRatedEntry.CONTENT_ITEM_TYPE;
            case UPCOMING:
                return MovieContract.UpcomingEntry.CONTENT_DIR_TYPE;
            case UPCOMING_ENTRY:
                return MovieContract.UpcomingEntry.CONTENT_ITEM_TYPE;
            case FAVORITES:
                return MovieContract.FavoritesEntry.CONTENT_DIR_TYPE;
            case FAVORITE:
                return MovieContract.FavoritesEntry.CONTENT_ITEM_TYPE;
            case COMPANIES:
                return MovieContract.CompanyEntry.CONTENT_DIR_TYPE;
            case COMPANY:
                return MovieContract.CompanyEntry.CONTENT_ITEM_TYPE;
            case COUNTRIES:
                return MovieContract.CountryEntry.CONTENT_DIR_TYPE;
            case COUNTRY:
                return MovieContract.CountryEntry.CONTENT_ITEM_TYPE;
            case LANGUAGES:
                return MovieContract.SpokenLanguagesEntry.CONTENT_DIR_TYPE;
            case LANGUAGE:
                return MovieContract.SpokenLanguagesEntry.CONTENT_ITEM_TYPE;
            case MOVIE_GENRES:
                return MovieContract.MovieGenreJunctionEntry.CONTENT_DIR_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri result;
        long _id;

        switch (match) {
            case MOVIE:
                _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    result = ContentUris.withAppendedId(uri, _id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case FAVORITE:
                _id = db.insert(MovieContract.FavoritesEntry.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    result = ContentUris.withAppendedId(uri, _id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case GENRE:
                _id = db.insert(MovieContract.GengeEntry.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    result = ContentUris.withAppendedId(uri, _id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case COMPANY:
                _id = db.insert(MovieContract.CompanyEntry.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    result = ContentUris.withAppendedId(uri, _id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case COUNTRY:
                _id = db.insert(MovieContract.CountryEntry.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    result = ContentUris.withAppendedId(uri, _id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case LANGUAGE:
                _id = db.insert(MovieContract.SpokenLanguagesEntry.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    result = ContentUris.withAppendedId(uri, _id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return result;
    }
    //TODO Implement all cases
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int returnCount = 0;

        switch (match) {
            case GENRES:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.GengeEntry.TABLE_NAME, null, value);
                        if (_id > 0)
                            returnCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case MOVIE_GENRES:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieGenreJunctionEntry.TABLE_NAME,
                                null,
                                value);
                        if (_id > 0)
                            returnCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case TOP_RATED:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.TopRatedEntry.TABLE_NAME,
                                null,
                                value);
                        if (_id > 0)
                            returnCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case UPCOMING:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.UpcomingEntry.TABLE_NAME,
                                null,
                                value);
                        if (_id > 0)
                            returnCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case LANGUAGES:
                throw new android.database.SQLException("Failed to insert row into " + uri);
            case COUNTRIES:
                throw new android.database.SQLException("Failed to insert row into " + uri);
            case COMPANIES:
                throw new android.database.SQLException("Failed to insert row into " + uri);
            case FAVORITES:
                throw new android.database.SQLException("Failed to insert row into " + uri);
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    static UriMatcher buildUriMatcher() {
        UriMatcher result = new UriMatcher(UriMatcher.NO_MATCH);

        result.addURI(AUTHORITY, MovieContract.PATH_MOVIES, MOVIES);
        result.addURI(AUTHORITY, MovieContract.PATH_MOVIES + "/*", MOVIE);

        result.addURI(AUTHORITY, MovieContract.PATH_LANGUAGE, LANGUAGE);
        result.addURI(AUTHORITY, MovieContract.PATH_LANGUAGES, LANGUAGES);

        result.addURI(AUTHORITY, MovieContract.PATH_COMPANY, COMPANY);
        result.addURI(AUTHORITY, MovieContract.PATH_COMPANIES, COMPANIES);

        result.addURI(AUTHORITY, MovieContract.PATH_COUNTRY, COUNTRY);
        result.addURI(AUTHORITY, MovieContract.PATH_COUNTRIES, COUNTRIES);

        result.addURI(AUTHORITY, MovieContract.PATH_GENRE, GENRE);
        result.addURI(AUTHORITY, MovieContract.PATH_GENRES, GENRES);

        result.addURI(AUTHORITY, MovieContract.PATH_MOVIE_GENRES, MOVIE_GENRES);

        result.addURI(AUTHORITY, MovieContract.PATH_TOP_RATED, TOP_RATED);
        result.addURI(AUTHORITY, MovieContract.PATH_TOP_RATED + "/*", TOP_RATED_ENTRY);

        result.addURI(AUTHORITY, MovieContract.PATH_UPCOMING, UPCOMING);
        result.addURI(AUTHORITY, MovieContract.PATH_UPCOMING + "/*", UPCOMING_ENTRY);

        result.addURI(AUTHORITY, MovieContract.PATH_FAVORITES, FAVORITES);
        result.addURI(AUTHORITY, MovieContract.PATH_FAVORITE, FAVORITE);

        return result;
    }
}
