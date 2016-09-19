package ru.casak.IMDB_searcher.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;

import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import ru.casak.IMDB_searcher.models.Genre;
import ru.casak.IMDB_searcher.models.Movie;

public class DbUtilsTest {

    //@Mock
    private static Context mContext;

    //@Mock
    private static ContentResolver mContentResolver;

    private static Movie movie;
    private static List<Genre> genres;

    @BeforeClass
    public static void setUp() {
        InstrumentationRegistry.getTargetContext().deleteDatabase(MovieDbHelper.DATABASE_NAME);
        Genre dramaGenre = new Genre();
        dramaGenre.setId(18);
        dramaGenre.setName("Drama");
        genres = new LinkedList<>();
        genres.add(dramaGenre);

        movie = new Movie(
                550,
                false,
                "/hNFMawyNDWZKKHU4GYCBz1krsRM.jpg",
                63000000,
                genres,
                null,
                "tt0137523",
                "en",
                "Fight Club",
                "A ticking-time-bomb insomniac and a slippery soap salesman channel primal male aggression into a shocking new form of therapy. Their concept catches on, with underground \"fight clubs\" forming in every town, until an eccentric gets in the way and ignites an out-of-control spiral toward oblivion.",
                "2.50307202280779",
                "/2lECpi35Hnbpa4y46JX0aY3AWTy.jpg",
                null,
                null,
                "1999-10-14",
                100853753,
                139,
                null,
                "Released",
                "How much can you know about yourself if you've never been in a fight?",
                "Fight Club",
                7.7d,
                3185L);

        mContext = InstrumentationRegistry.getTargetContext();
        mContentResolver = mContext.getContentResolver();
    }

    @Test
    public void shouldAddMovieToMovieTable(){
        Cursor cursor =  mContentResolver.query(ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, 550),
                null,
                MovieContract.MovieEntry.TABLE_NAME + "." +  MovieContract.MovieEntry._ID + " = ?",
                new String[]{"550"},
                null);

        assertFalse("Error: Database already contains this movie", cursor.moveToFirst());

        cursor.close();

        DbUtils.addMovie(movie, mContentResolver);

        cursor =  mContentResolver.query(ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, 550),
                null,
                MovieContract.MovieEntry.TABLE_NAME + "." +  MovieContract.MovieEntry._ID + " = ?",
                new String[]{"550"},
                null);

        assertTrue("Error: Can`t receive Movie from resolver", cursor.moveToFirst());

        int id = cursor.getInt(0);
        cursor.close();

        assertTrue("Error: There is no movie with id 550 in database", id == 550);

        cursor = mContentResolver.query(MovieContract.GengeEntry.CONTENT_URI,
                null,
                MovieContract.GengeEntry.TABLE_NAME + "." +  MovieContract.GengeEntry._ID + " = ?",
                new String[]{"18"},
                null);

        assertTrue("Error: Can`t receive Genre from resolver", cursor.moveToFirst());

        id = cursor.getInt(0);
        cursor.close();

        assertTrue("Error: There is no genre with id 18 in database", id == 18);

    }

}
