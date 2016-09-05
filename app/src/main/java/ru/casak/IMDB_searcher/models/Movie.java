package ru.casak.IMDB_searcher.models;

import android.os.Parcel;

import java.util.Date;
import java.util.List;

public class Movie{
    private Integer id;
    private String poster_path;
    private String title;
    private String overview;
    private List<Genre> genres;
    private Date release_date;
    private Integer runtime;
    private Double vote_average;

    public Movie(){}

    public Movie(Parcel parcel){
        id = parcel.readInt();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Genre> getGenre() {
        return genres;
    }

    public void setGenre(List<Genre> genres) {
        this.genres = genres;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Date getReleaseDate() {
        return release_date;
    }

    public void setReleaseDate(Date releaseDate) {
        this.release_date = releaseDate;
    }

    public Double getVoteAverage() {
        return vote_average;
    }

    public void setVoteAverage(Double voteAverage) {
        this.vote_average = voteAverage;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }
}

class Genre{
    public Integer id;
    public String name;
}
