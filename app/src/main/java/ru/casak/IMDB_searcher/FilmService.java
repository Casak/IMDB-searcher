package ru.casak.IMDB_searcher;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Casak on 25.03.2016.
 */
public interface FilmService {
    @GET("movie/top_rated")
    Observable<MovieResults> getTopRated(@Query("page") Integer page,
                                         @Query("language") String language);

    @GET("movie/upcoming")
    Observable<MovieResults> getUpcoming(@Query("page") Integer page,
                                         @Query("language") String language);

    @GET("movie/{id}")
    Observable<Movie> getMovie(@Path("id") Integer id,
                                         @Query("language") String language);



}