package ru.casak.IMDB_searcher.providers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.test.InstrumentationRegistry;
import android.test.RenamingDelegatingContext;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import org.mockito.Mock;

import java.util.HashSet;

public class MovieDbHelperTest {

    @Mock
    private static Context mContext;
    @Mock
    private static JSONObject mMovieJSON;

    private static SQLiteOpenHelper mSQLiteOpenHelper;

    @BeforeClass
    public static void setUp() throws JSONException {
        mContext =  new RenamingDelegatingContext(InstrumentationRegistry.getTargetContext(), "test_");
        mSQLiteOpenHelper = new MovieDbHelper(mContext);
        //mMovieJSON = new JSONObject("{   \"adult\": false,   \"backdrop_path\": \"/hNFMawyNDWZKKHU4GYCBz1krsRM.jpg\",   \"belongs_to_collection\": null,   \"budget\": 63000000,   \"genres\": [     {       \"id\": 18,       \"name\": \"Drama\"     }   ],   \"homepage\": \"\",   \"id\": 550,   \"imdb_id\": \"tt0137523\",   \"original_language\": \"en\",   \"original_title\": \"Fight Club\",   \"overview\": \"A ticking-time-bomb insomniac and a slippery soap salesman channel primal male aggression into a shocking new form of therapy. Their concept catches on, with underground \\\"fight clubs\\\" forming in every town, until an eccentric gets in the way and ignites an out-of-control spiral toward oblivion.\",   \"popularity\": 2.50307202280779,   \"poster_path\": \"/2lECpi35Hnbpa4y46JX0aY3AWTy.jpg\",   \"production_companies\": [     {       \"name\": \"20th Century Fox\",       \"id\": 25     },     {       \"name\": \"Fox 2000 Pictures\",       \"id\": 711     },     {       \"name\": \"Regency Enterprises\",       \"id\": 508     }   ],   \"production_countries\": [     {       \"iso_3166_1\": \"DE\",       \"name\": \"Germany\"     },     {       \"iso_3166_1\": \"US\",       \"name\": \"United States of America\"     }   ],   \"release_date\": \"1999-10-14\",   \"revenue\": 100853753,   \"runtime\": 139,   \"spoken_languages\": [     {       \"iso_639_1\": \"en\",       \"name\": \"English\"     }   ],   \"status\": \"Released\",   \"tagline\": \"How much can you know about yourself if you've never been in a fight?\",   \"title\": \"Fight Club\",   \"video\": false,   \"vote_average\": 7.7,   \"vote_count\": 3185 }");
    }

    @Test
    public void createTables() {
        final HashSet<String> tableNames = new HashSet<>();
        tableNames.add(MovieContract.MovieEntry.TABLE_NAME);
        tableNames.add(MovieContract.GengeEntry.TABLE_NAME);
        tableNames.add(MovieContract.CompanyEntry.TABLE_NAME);
        tableNames.add(MovieContract.CountryEntry.TABLE_NAME);
        tableNames.add(MovieContract.SpokenLanguagesEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
        SQLiteDatabase testDB = mSQLiteOpenHelper.getWritableDatabase();
        assertTrue("Error: Database is closed", testDB.isOpen());

        Cursor cursor = testDB.rawQuery("SELECT name FROM sqlite_master where type = 'table'", null);
        assertTrue("Error: Database is not created correctly", cursor.moveToFirst());

        do{
            tableNames.remove(cursor.getString(0));
        } while(cursor.moveToNext());
        assertTrue("Error: Database does not contain one or several tables", tableNames.isEmpty());
    }

    @Test
    public void checkMovieTableColumns (){
        final HashSet<String> columns = new HashSet<>();
        columns.add(MovieContract.MovieEntry.COLUMN_ADULT);
        columns.add(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH);
        columns.add(MovieContract.MovieEntry.COLUMN_BUDGET);
        columns.add(MovieContract.MovieEntry.COLUMN_COMPANIES);
        columns.add(MovieContract.MovieEntry.COLUMN_COUNTRIES);
        columns.add(MovieContract.MovieEntry.COLUMN_GENRES);
        columns.add(MovieContract.MovieEntry.COLUMN_HOMEPAGE);
        columns.add(MovieContract.MovieEntry.COLUMN_IMDB_ID);
        columns.add(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE);
        columns.add(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
        columns.add(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        columns.add(MovieContract.MovieEntry.COLUMN_POPULARITY);
        columns.add(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        columns.add(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        columns.add(MovieContract.MovieEntry.COLUMN_REVENUE);
        columns.add(MovieContract.MovieEntry.COLUMN_RUNTIME);
        columns.add(MovieContract.MovieEntry.COLUMN_SPOKEN_LANGUAGES);
        columns.add(MovieContract.MovieEntry.COLUMN_STATUS);
        columns.add(MovieContract.MovieEntry.COLUMN_TAGLINE);
        columns.add(MovieContract.MovieEntry.COLUMN_TITLE);
        columns.add(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        columns.add(MovieContract.MovieEntry.COLUMN_VOTE_COUNT);

        SQLiteDatabase testDb = mSQLiteOpenHelper.getReadableDatabase();

        Cursor cursor = testDb.rawQuery("PRAGMA table_info("+MovieContract.MovieEntry.TABLE_NAME+")", null);
        assertTrue("Error: There is no info for table " + MovieContract.MovieEntry.TABLE_NAME, cursor.moveToFirst());

        int columnNameIndex = cursor.getColumnIndex("name");
        do{
            columns.remove(cursor.getString(columnNameIndex));
        }
        while(cursor.moveToNext());
        assertTrue("Error: Movie table does not contain one or more column", columns.isEmpty());
    }
}
