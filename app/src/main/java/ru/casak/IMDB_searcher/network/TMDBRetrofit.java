package ru.casak.IMDB_searcher.network;

import android.util.Log;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.casak.IMDB_searcher.R;
import ru.casak.IMDB_searcher.activities.TabsActivity;
import ru.casak.IMDB_searcher.services.FilmService;

/**
 * Created by Casak on 01.04.2016.
 */
public class TMDBRetrofit {
    private static final String TAG = "TMDBRetrofit";
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static Retrofit retrofitInstance = null;
    private static FilmService serviceInstance = null;

    public static Retrofit getRetrofitInstance(){
        if(retrofitInstance == null) return retrofitInstance = createRetrofitInstance();
        else return retrofitInstance;
    }

    public static FilmService getFilmServiceInstance(){
        if(retrofitInstance == null) getRetrofitInstance();
        if(serviceInstance == null) return serviceInstance = retrofitInstance.create(FilmService.class);
        else return serviceInstance;
    }

    private static Retrofit createRetrofitInstance(){
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalUrl = original.url();

                HttpUrl url = originalUrl.newBuilder()
                        .addQueryParameter("api_key", TabsActivity.getContext().getResources().getString(R.string.api_key))
                        .build();

                Request.Builder requestBuilder = original.newBuilder()
                        .url(url)
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                Log.d(TAG, "intercept(): built request:" + request.toString());
                return chain.proceed(request);
            }
        });

        retrofitInstance = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client.build())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofitInstance;
    }
}
