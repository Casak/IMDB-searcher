package ru.casak.IMDB_searcher;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ru.casak.IMDB_searcher.database.DbUtilsTest;
import ru.casak.IMDB_searcher.database.MovieDbHelperTest;

@RunWith(Suite.class)
@SuiteClasses({
        MovieDbHelperTest.class,
        DbUtilsTest.class
})
public class AllTests {

}