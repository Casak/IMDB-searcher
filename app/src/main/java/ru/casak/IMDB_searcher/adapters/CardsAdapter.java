package ru.casak.IMDB_searcher.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.squareup.picasso.Picasso;

import ru.casak.IMDB_searcher.models.Movie;
import ru.casak.IMDB_searcher.R;
import ru.casak.IMDB_searcher.activities.FilmActivity;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {
    private static final String TAG = "CardsAdapter";
    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w780";
    private List<Movie> movieList = new ArrayList<Movie>();

    public CardsAdapter(List<Movie> movieList) {
        this.movieList = movieList;
    }

    public List<Movie> getMovieList(){
        return movieList;
    }

    @Override
    public int getItemCount(){
        return movieList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){
        final Context context = holder.getImageView().getContext();

        holder.getImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FilmActivity.class);
                intent.putExtra("id", movieList.get(position).getId());
                intent.putExtra("poster_path", movieList.get(position).getPosterPath());
                context.startActivity(intent);
                Log.d(TAG, "Element " + position + " clicked.");
            }
        });

        if ( movieList.size() != 0 ) {
            holder.getTextView().setText(movieList.get(position).getTitle());
            Picasso picasso = Picasso.with(context);
            picasso.setIndicatorsEnabled(true);
            picasso
                    .load(BASE_IMAGE_URL + IMAGE_SIZE + movieList.get(position).getPosterPath())
                    .placeholder(R.drawable.progress_spinner)
                    .into(holder.getImageView());
            Log.d(TAG, "Element " + position + " set with: " + movieList.get(position).getTitle());
        }
        else
            Log.d(TAG, "No elements was set");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View cardLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.film_card, parent, false);
        return new ViewHolder(cardLayoutView);
    }



    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textView;
        private final ImageView imageView;

        public ViewHolder(View view){
            super(view);

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
}
