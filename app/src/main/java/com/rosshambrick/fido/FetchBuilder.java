package com.rosshambrick.fido;

import android.content.Context;
import android.util.Log;

import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class FetchBuilder {
    private static final String TAG = Fido.class.getSimpleName();

    private final Context context;
    private final String key;
    private boolean fromAssets;

    public FetchBuilder(Context context, String key) {
        this.context = context;
        this.key = key;
    }

    public FetchBuilder fromAssets() {
        this.fromAssets = true;
        return this;
    }

    public <T> T as(Class<T> tClass) {
        JsonReader jsonReader = getReader(tClass);
        if (jsonReader != null) {
            return Fido.gson.fromJson(jsonReader, tClass);
        } else {
            return null;
        }
    }

    public <T> Observable<T> asObservable(final Class<T> tClass) {
        return Observable
                .create(new Observable.OnSubscribe<T>() {
                    @Override
                    public void call(Subscriber<? super T> subscriber) {
                        try {
                            T data = as(tClass);
                            if (data != null) {
                                subscriber.onNext(data);
                            }
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    protected <T> JsonReader getReader(Class<T> tClass) {
        InputStreamReader reader;
        if (fromAssets) {
            if (Fido.LOGGING) Log.d(TAG, "GET: assets/" + key);

            try {
                InputStream stream = context.getAssets().open(key);
                reader = new InputStreamReader(stream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            File filesDir = context.getFilesDir();
            String folderName = tClass.getSimpleName().toLowerCase();
            String relativePath = new File(folderName, key).getPath();
            File file = new File(filesDir, relativePath);

            if (Fido.LOGGING) Log.d(TAG, "GET: " + file.getPath());

            try {
                reader = new FileReader(file);
            } catch (FileNotFoundException e1) {
                return null;
            }
        }

        return new JsonReader(reader);
    }
}

