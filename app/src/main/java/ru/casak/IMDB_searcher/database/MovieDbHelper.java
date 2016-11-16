package ru.casak.IMDB_searcher.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.casak.IMDB_searcher.database.MovieContract.*;

public class MovieDbHelper extends SQLiteOpenHelper {
    public static final String TAG = MovieDbHelper.class.getSimpleName();

    public static final String DATABASE_NAME = "movies.db";
    public static final Integer DATABASE_VERSION = 2;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_GENRE_TABLE = "CREATE TABLE " + GengeEntry.TABLE_NAME + " (" +
                GengeEntry._ID + " INTEGER PRIMARY KEY, " +
                GengeEntry.COLUMN_NAME + " TEXT NOT NULL" + ");";

        final String CREATE_COUNTRY_TABLE = "CREATE TABLE " + CountryEntry.TABLE_NAME + " (" +
                CountryEntry._ID + " INTEGER PRIMARY KEY, " +
                CountryEntry.COLUMN_ISO + " TEXT NOT NULL UNIQUE, " +
                CountryEntry.COLUMN_NAME + " TEXT NOT NULL" + ");";

        final String CREATE_COMPANY_TABLE = "CREATE TABLE " + CompanyEntry.TABLE_NAME + " (" +
                CompanyEntry._ID + " INTEGER PRIMARY KEY, " +
                CompanyEntry.COLUMN_DESCRIPTION + " TEXT, " +
                CompanyEntry.COLUMN_HEADQUARTERS + " TEXT, " +
                CompanyEntry.COLUMN_HOMEPAGE + " TEXT, " +
                CompanyEntry.COLUMN_LOGO_PATH + " TEXT, " +
                CompanyEntry.COLUMN_NAME + " TEXT NOT NULL" + ");";

        final String CREATE_LANGUAGE_TABLE = "CREATE TABLE " + SpokenLanguagesEntry.TABLE_NAME + " (" +
                SpokenLanguagesEntry._ID + " INTEGER PRIMARY KEY, " +
                SpokenLanguagesEntry.COLUMN_ISO + " TEXT NOT NULL UNIQUE, " +
                SpokenLanguagesEntry.COLUMN_NAME + " TEXT NOT NULL" + ");";

        final String CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                MovieEntry.COLUMN_ADULT + " TEXT, " +
                MovieEntry.COLUMN_BACKDROP_PATH + " TEXT, " +
                MovieEntry.COLUMN_BUDGET + " INTEGER, " +
                MovieEntry.COLUMN_HOMEPAGE + " TEXT, " +
                MovieEntry.COLUMN_IMDB_ID + " TEXT, " +
                MovieEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT, " +
                MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT, " +
                MovieEntry.COLUMN_POPULARITY + " TEXT, " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                MovieEntry.COLUMN_REVENUE + " INTEGER, " +
                MovieEntry.COLUMN_RUNTIME + " INTEGER, " +
                MovieEntry.COLUMN_STATUS + " TEXT, " +
                MovieEntry.COLUMN_TAGLINE + " TEXT, " +
                MovieEntry.COLUMN_TITLE + " TEXT, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL, " +
                MovieEntry.COLUMN_VOTE_COUNT + " INTEGER, " +
                "UNIQUE (" + MovieEntry.COLUMN_IMDB_ID + ") ON CONFLICT REPLACE);"; //// TODO: 06.09.2016 Read about ON CONFLICT

        final String CREATE_TOP_RATED_TABLE = "CREATE TABLE " + TopRatedEntry.TABLE_NAME + " (" +
                TopRatedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TopRatedEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL UNIQUE, " +
                TopRatedEntry.COLUMN_RATING + " REAL NOT NULL, " +
                "FOREIGN KEY (" + TopRatedEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + "(" + MovieEntry._ID + ")  " +
                ");";

        final String CREATE_UPCOMING_TABLE = "CREATE TABLE " + UpcomingEntry.TABLE_NAME + " (" +
                UpcomingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UpcomingEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL UNIQUE, " +
                UpcomingEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                "FOREIGN KEY (" + UpcomingEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + "(" + MovieEntry._ID + ")  " +
                ");";

        final String CREATE_FAVORITES_TABLE = "CREATE TABLE " + FavoritesEntry.TABLE_NAME + " (" +
                FavoritesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoritesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL UNIQUE, " +
                "FOREIGN KEY (" + FavoritesEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + "(" + MovieEntry._ID + ")  " +
                ");";

        final String CREATE_MOVIE_GENRE_JUNCTION_TABLE = "CREATE TABLE " + MovieGenreJunctionEntry.TABLE_NAME + " (" +
                MovieGenreJunctionEntry.COLUMN_FILM_ID + " INTEGER NOT NULL, " +
                MovieGenreJunctionEntry.COLUMN_GENRE_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + MovieGenreJunctionEntry.COLUMN_FILM_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + "(" + MovieEntry._ID + ")  " +
                "FOREIGN KEY (" + MovieGenreJunctionEntry.COLUMN_GENRE_ID + ") REFERENCES " +
                GengeEntry.TABLE_NAME + "(" + GengeEntry._ID + ")  " +
                ");";

        sqLiteDatabase.beginTransaction();
        sqLiteDatabase.execSQL(CREATE_GENRE_TABLE);
        sqLiteDatabase.execSQL(CREATE_COUNTRY_TABLE);
        sqLiteDatabase.execSQL(CREATE_COMPANY_TABLE);
        sqLiteDatabase.execSQL(CREATE_LANGUAGE_TABLE);
        sqLiteDatabase.execSQL(CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(CREATE_MOVIE_GENRE_JUNCTION_TABLE);
        sqLiteDatabase.execSQL(CREATE_TOP_RATED_TABLE);
        sqLiteDatabase.execSQL(CREATE_UPCOMING_TABLE);
        sqLiteDatabase.execSQL(CREATE_FAVORITES_TABLE);
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
    }
//TODO Implement onUpgrage
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
