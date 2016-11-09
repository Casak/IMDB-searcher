package ru.casak.IMDB_searcher.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.graphics.Palette;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import ru.casak.IMDB_searcher.database.DbUtils;
import ru.casak.IMDB_searcher.models.*;
import ru.casak.IMDB_searcher.R;
import ru.casak.IMDB_searcher.network.TMDBRetrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FilmActivity extends AppCompatActivity {
    private static final String TAG = FilmActivity.class.getSimpleName();
    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w780";
    private static final String EXTRA_IMAGE = "ru.casak.IMDB_searcher.extraImage";
    private int id;
    private String posterPath;
    private ImageView imageView;
    private TextView title;
    private TextView overview;
    private TextView director;
    private TextView runtime;
    private TextView releaseDate;
    private TextView voteAverage;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide transition = new Slide();
            transition.excludeTarget(android.R.id.statusBarBackground, true);
            getWindow().setEnterTransition(transition);
            getWindow().setReturnTransition(transition);
        }

        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        posterPath = intent.getStringExtra("poster_path");

        Log.d(TAG, "GetExtra: " + id);
        setContentView(R.layout.activity_film_detail);

        ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), EXTRA_IMAGE);
        supportPostponeEnterTransition();


        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        imageView = (ImageView) findViewById(R.id.image);
        title = (TextView) findViewById(R.id.film_title);
        overview = (TextView) findViewById(R.id.overview);
        director = (TextView) findViewById(R.id.director);
        runtime = (TextView) findViewById(R.id.runtime);
        releaseDate = (TextView) findViewById(R.id.release_date);
        voteAverage = (TextView) findViewById(R.id.vote_average);

        Picasso picasso = Picasso.with(getApplicationContext());
        picasso.setIndicatorsEnabled(true);
        picasso
                .load(BASE_IMAGE_URL + IMAGE_SIZE + posterPath)
                .placeholder(R.drawable.progress_spinner)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                            public void onGenerated(Palette palette) {
                                applyPalette(palette);
                            }
                        });
                    }

                    @Override
                    public void onError() {
                        Log.e(TAG, "Picasso onError(): " + posterPath);
                    }
                });

        Movie movie = DbUtils.getMovie(id, getContentResolver());
        if (movie != null) {
            setViews(movie);
        } else {
            TMDBRetrofit
                    .getFilmServiceInstance()
                    .getMovie(id, "en")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<Movie>() {
                        @Override
                        public void onCompleted() {

                            Log.d(TAG, "onCompleted() ");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "onError(): " + e.getMessage());
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(Movie movie) {
                            DbUtils.addMovieIfNotExist(movie.getId(), getContentResolver());
                            setViews(movie);
                            Log.d(TAG, "onNext(): " + movie.getTitle());
                        }
                    });
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(TAG, "onOptionsItemSelected(): android.R.id.home");
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void applyPalette(Palette palette) {
        int primaryDark = getResources().getColor(R.color.colorPrimaryDark);
        int primary = getResources().getColor(R.color.colorPrimary);
        collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
        collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));
        updateBackground((FloatingActionButton) findViewById(R.id.fab), palette);
        supportStartPostponedEnterTransition();
    }

    private void updateBackground(FloatingActionButton fab, Palette palette) {
        int lightVibrantColor = palette.getLightVibrantColor(getResources().getColor(android.R.color.white));
        int vibrantColor = palette.getVibrantColor(getResources().getColor(R.color.colorAccent));

        fab.setRippleColor(lightVibrantColor);
        fab.setBackgroundTintList(ColorStateList.valueOf(vibrantColor));
    }

    public void setViews(Movie movie) {
        final Resources resources = getApplicationContext().getResources();
        if (movie != null) {
            String titleText = movie.getTitle();
            String overviewText = movie.getOverview();
            Integer runTime = movie.getRuntime();
            String release = movie.getReleaseDate();
            Double vote = movie.getVoteAverage();

            collapsingToolbarLayout.setTitle(titleText);
            title.setText(String.format(resources.getString(R.string.film_title), titleText));
            overview.setText(String.format(resources.getString(R.string.film_overview), overviewText));
            runtime.setText(String.format(resources.getString(R.string.film_runtime), runTime));
            releaseDate.setText(String.format(resources.getString(R.string.film_release_date), release));
            voteAverage.setText(String.format(resources.getString(R.string.film_vote_average), vote));
        }
    }
}
