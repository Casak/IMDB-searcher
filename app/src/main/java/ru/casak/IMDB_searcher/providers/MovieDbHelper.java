package ru.casak.IMDB_searcher.providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.casak.IMDB_searcher.providers.MovieContract.*;

public class MovieDbHelper extends SQLiteOpenHelper {
    public static final String TAG = MovieDbHelper.class.getSimpleName();

    public static final String DATABASE_NAME = "movies.db";
    public static final Integer DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
//TODO Test it, faggot!
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_GENRE_TABLE = "CREATE TABLE" + GengeEntry.TABLE_NAME + " (" +
                GengeEntry._ID + "INTEGER PRIMARY KEY, " +
                GengeEntry.COLUMN_NAME + "TEXT NOT NULL" + ");";

        final String CREATE_COUNTRY_TABLE = "CREATE TABLE" + CountryEntry.TABLE_NAME + " (" +
                CountryEntry._ID + "INTEGER PRIMARY KEY, " +
                CountryEntry.COLUMN_ISO + "TEXT NOT NULL UNIQUE, " +
                CountryEntry.COLUMN_NAME + "TEXT NOT NULL" + ");";

        final String CREATE_COMPANY_TABLE = "CREATE TABLE" + CompanyEntry.TABLE_NAME + " (" +
                CompanyEntry._ID + "INTEGER PRIMARY KEY, " +
                CompanyEntry.COLUMN_DESCRIPTION + "TEXT, " +
                CompanyEntry.COLUMN_HEADQUARTERS + "TEXT, " +
                CompanyEntry.COLUMN_HOMEPAGE + "TEXT, " +
                CompanyEntry.COLUMN_LOGO_PATH + "TEXT, " +
                CompanyEntry.COLUMN_NAME + "TEXT NOT NULL" + ");";

        final String CREATE_LANGUAGE_TABLE = "CREATE TABLE" + SpokenLanguagesEntry.TABLE_NAME + " (" +
                SpokenLanguagesEntry._ID + "INTEGER PRIMARY KEY, " +
                SpokenLanguagesEntry.COLUMN_ISO + "TEXT NOT NULL UNIQUE, " +
                SpokenLanguagesEntry.COLUMN_NAME + "TEXT NOT NULL" + ");";

        final String CREATE_MOVIE_TABLE = "CREATE TABLE" + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + "INTEGER PRIMARY KEY, " +
                MovieEntry.COLUMN_ADULT + "TEXT, " +
                MovieEntry.COLUMN_BACKDROP_PATH + "TEXT, " +
                MovieEntry.COLUMN_BUDGET + "INTEGER, " +
                MovieEntry.COLUMN_GENRES + "INTEGER NOT NULL, " +
                MovieEntry.COLUMN_HOMEPAGE + "TEXT, " +
                MovieEntry.COLUMN_IMDB_ID + "TEXT, " +
                MovieEntry.COLUMN_ORIGINAL_LANGUAGE + "TEXT, " +
                MovieEntry.COLUMN_ORIGINAL_TITLE + "TEXT, " +
                MovieEntry.COLUMN_OVERVIEW + "TEXT, " +
                MovieEntry.COLUMN_POPULARITY + "TEXT, " +
                MovieEntry.COLUMN_POSTER_PATH + "TEXT, " +
                MovieEntry.COLUMN_COMPANIES + "INTEGER NOT NULL, " +
                MovieEntry.COLUMN_COUNTRIES + "INTEGER NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + "TEXT" +
                MovieEntry.COLUMN_REVENUE + "INTEGER, " +
                MovieEntry.COLUMN_RUNTIME + "INTEGER" +
                MovieEntry.COLUMN_SPOKEN_LANGUAGES + "INTEGER NOT NULL, " +
                MovieEntry.COLUMN_STATUS + "TEXT, " +
                MovieEntry.COLUMN_TAGLINE + "TEXT, " +
                MovieEntry.COLUMN_TITLE + "TEXT, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + "REAL, " +
                MovieEntry.COLUMN_VOTE_COUNT + "REAL" +
                "FOREIGN KEY (" + MovieEntry.COLUMN_GENRES + ") REFERENCES " +
                GengeEntry.TABLE_NAME + "( " + GengeEntry._ID + "),  " +
                "FOREIGN KEY (" + MovieEntry.COLUMN_COUNTRIES + ") REFERENCES " +
                CountryEntry.TABLE_NAME + "( " + CountryEntry._ID + "),  " +
                "FOREIGN KEY (" + MovieEntry.COLUMN_COMPANIES + ") REFERENCES " +
                CompanyEntry.TABLE_NAME + "( " + CompanyEntry._ID + "),  " +
                "FOREIGN KEY (" + MovieEntry.COLUMN_SPOKEN_LANGUAGES + ") REFERENCES " +
                SpokenLanguagesEntry.TABLE_NAME + "( " + SpokenLanguagesEntry._ID + "),  " +
                "UNIQUE (" + MovieEntry.COLUMN_IMDB_ID + ") ON CONFLICT REPLACE);"; //// TODO: 06.09.2016 Read about ON CONFLICT

        sqLiteDatabase.beginTransaction();
        sqLiteDatabase.execSQL(CREATE_GENRE_TABLE);
        sqLiteDatabase.execSQL(CREATE_COUNTRY_TABLE);
        sqLiteDatabase.execSQL(CREATE_COMPANY_TABLE);
        sqLiteDatabase.execSQL(CREATE_LANGUAGE_TABLE);
        sqLiteDatabase.execSQL(CREATE_MOVIE_TABLE);
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
    }
//TODO Implement onUpdrage
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
