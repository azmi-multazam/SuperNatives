package cyrilpillai.supernatives.application.di;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import cyrilpillai.supernatives.BuildConfig;
import cyrilpillai.supernatives.MyObjectBox;
import cyrilpillai.supernatives.utils.Constants;
import cyrilpillai.supernatives.utils.network.ApiService;
import dagger.Module;
import dagger.Provides;
import io.objectbox.BoxStore;
import io.objectbox.android.Admin;
import io.objectbox.android.AndroidObjectBrowser;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by cyrilpillai on 11-11-2017.
 */

@Module
public class AppModule {

    @Provides
    @Singleton
    Context providesContext(Application application) {
        return application;
    }

    @Provides
    @Singleton
    ApiService providesApiService(Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }

    @Provides
    @Singleton
    Retrofit providesRetrofit(GsonConverterFactory gsonConverterFactory,
                              OkHttpClient okHttpClient) {
        return new Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .addConverterFactory(gsonConverterFactory)
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    OkHttpClient providesOkHttpClient(Cache cache) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);

        client.addNetworkInterceptor(new StethoInterceptor());

        return client.build();
    }

    @Provides
    @Singleton
    Cache providesOkhttpCache(Context context) {
        int cacheSize = 10 * 1024 * 1024; // 10 MB
        return new Cache(context.getCacheDir(), cacheSize);
    }

    @Provides
    @Singleton
    Gson providesGson() {
        return new Gson();
    }

    @Provides
    @Singleton
    GsonConverterFactory providesGsonConverterFactory() {
        return GsonConverterFactory.create();
    }

    @Provides
    @Singleton
    BoxStore providesBoxStore(Context context) {
        BoxStore boxStore = MyObjectBox.builder().androidContext(context).build();
        if (BuildConfig.DEBUG) {
            //new AndroidObjectBrowser(boxStore).start(context);
            boolean started = new Admin(boxStore).start(context);
            Log.i("ObjectBoxAdmin", "Started: " + started);
        }
        return boxStore;
    }
}