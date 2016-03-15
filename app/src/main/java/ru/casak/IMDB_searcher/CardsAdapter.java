package ru.casak.IMDB_searcher;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import android.os.AsyncTask;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {
    private static final String TAG = "CardsAdapter";
    private static List<String> dataList = new ArrayList<String>();
    private static List<Drawable> posterList = new ArrayList<Drawable>();

    public CardsAdapter(String apiKey){
        try{
            for(MovieDb movie : new GetMoviesTask().execute(apiKey).get()){
                dataList.add(movie.getTitle());
                posterList.add(new GetPostersTask().execute(movie.getPosterPath()).get());
            }
        }
        catch(InterruptedException e){
            Log.d(TAG, "JSON request was interrupted");
        }
        catch(ExecutionException e){
            Log.d(TAG, "Computation threw an exception.");
        }
        catch(CancellationException e){
            Log.d(TAG, "Computation was cancelled.");
        }


    }

    @Override
    public int getItemCount(){
        return dataList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        holder.getTextView().setText(dataList.get(position));
        holder.getImageView().setImageDrawable(posterList.get(position));


        Log.d(TAG, "Element " + position + " set.");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View cardLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.film_card, null);
        return new ViewHolder(cardLayoutView);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textView;
        private final ImageView imageView;

        public ViewHolder(View view){
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });
            textView = (TextView)view.findViewById(R.id.title);
            imageView = (ImageView)view.findViewById(R.id.poster);

        }

        public TextView getTextView(){
            return textView;
        }
        public ImageView getImageView(){
            return imageView;
        }
    }

    private class GetMoviesTask extends AsyncTask <String, Integer, List<MovieDb>> {

        @Override
        protected List<MovieDb> doInBackground(String... apiKey) {
            TmdbApi mTmdbApi = new TmdbApi(apiKey[0]);
            Log.d(TAG, "GetMoviesTask: TmdbApi mTmdbApi = new TmdbApi(apiKey[0]); ----done");
            TmdbMovies mTmdbMovies = mTmdbApi.getMovies();
            Log.d(TAG, "GetMoviesTask: TmdbMovies mTmdbMovies = mTmdbApi.getMovies(); ----done");
            MovieResultsPage mMovieResultsPage = mTmdbMovies.getPopularMovies("en", 1);
            Log.d(TAG, "GetMoviesTask: MovieResultsPage mMovieResultsPage = mTmdbMovies.getPopularMovies(\"en\", 1); ----done");
            List<MovieDb> list = mMovieResultsPage.getResults();
            Log.d(TAG, "GetMoviesTask: List<MovieDb> list = mMovieResultsPage.getResults(); ----done");
            return list;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
        }

        @Override
        protected void onPostExecute(List<MovieDb> result) {
        }
    }

    private class GetPostersTask extends AsyncTask <String, Integer, Drawable> {

        @Override
        protected Drawable doInBackground(String... posterPath) {
            Drawable poster = null;
            try {
                Log.d(TAG, "GetPostersTask: posterPath = " + posterPath[0]);
                InputStream is = (InputStream) new URL("http://image.tmdb.org/t/p/w500" + posterPath[0]).getContent();
                Log.d(TAG, "GetPostersTask: InputStream is = (InputStream) new URL(\"http://image.tmdb.org/t/p/w500\" + posterPath[0]).getContent(); ---done");
                poster = Drawable.createFromStream(is, posterPath[0]);
                Log.d(TAG, "GetPostersTask: poster = Drawable.createFromStream(is, posterPath[0]); ---done");
            }
            catch (IOException e){
                Log.d(TAG, "GetPostersTask: doInBackground was interrupted");
            }
            return poster;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
        }

        @Override
        protected void onPostExecute(Drawable result) {
        }
    }
}
