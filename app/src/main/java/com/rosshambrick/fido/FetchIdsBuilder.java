package com.rosshambrick.fido;

import android.content.Context;

import java.io.File;

import rx.Observable;
import rx.Subscriber;

public class FetchIdsBuilder<T> {
    private final Context context;
    private final Class<T> tClass;

    public FetchIdsBuilder(Context context, Class<T> tClass) {
        this.context = context;
        this.tClass = tClass;
    }

    public Observable<String[]> asObservable() {
        return Observable.create(new Observable.OnSubscribe<String[]>() {
            @Override
            public void call(Subscriber<? super String[]> subscriber) {
                try {
                    subscriber.onNext(asBlocking());
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });

    }

    public String[] asBlocking() {
        File filesDir = context.getFilesDir();
        File folderPath = new File(filesDir, tClass.getSimpleName().toLowerCase());
        File[] files = folderPath.listFiles();
        if (files != null) {
            String[] ids = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                ids[i] = files[i].getName();
            }
            return ids;
        } else {
            return new String[0];
        }
    }
}
