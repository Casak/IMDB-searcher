package ru.casak.IMDB_searcher.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Casak on 28.03.2016.
 */
public class MovieResults {
    private Integer page;
    private List<Movie> results  = new ArrayList<Movie>();

    public Integer getPage(){
        return page;
    }


    public void setPage(Integer page) {
        this.page = page;
    }

    public List<Movie> getResults(){
        return results;
    }


    public void setResults(List<Movie> results) {
        this.results = results;
    }

}
