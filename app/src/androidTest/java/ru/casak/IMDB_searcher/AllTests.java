package ru.casak.IMDB_searcher;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ru.casak.IMDB_searcher.providers.MovieDbHelperTest;

@RunWith(Suite.class)
@SuiteClasses({
        MovieDbHelperTest.class
})
public class AllTests {

}