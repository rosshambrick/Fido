package com.rosshambrick.fido;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import rx.Observable;
import rx.functions.Func0;
import rx.util.async.Async;

public class PutBuilder {
    private static final String TAG = Fido.class.getSimpleName();

    private final Context context;
    private final String id;
    protected final Object value;

    public PutBuilder(Context context, String id, Object value) {
        this.context = context;
        this.id = id;
        this.value = value;
    }

    public void blocking() {
        FileWriter writer = null;
        try {
            String json = Fido.gson.toJson(this.value);

            String folderName;
            if (value.getClass().isArray()) {
                Object[] array = (Object[]) value;
                if (array.length == 0) {
                    return; //empty array, nothing to do
                } else {
                    folderName = array[0].getClass().getSimpleName().toLowerCase() + "[]";
                }
            } else {
                folderName = value.getClass().getSimpleName().toLowerCase();
            }

            File typeDirectory = new File(context.getFilesDir(), folderName);
            typeDirectory.mkdirs();

            File fullPath = new File(typeDirectory, id);

            if (Fido.LOGGING) Log.d(TAG, "PUT: " + fullPath);
            if (Fido.LOGGING) Log.d(TAG, "PUT: " + json);

            writer = new FileWriter(fullPath);
            writer.write(json);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (writer != null) writer.close();
            } catch (IOException e) {
                //nothing
            }
        }
    }

    public Observable async() {
        return Async.start(new Func0<Void>() {
            @Override
            public Void call() {
                blocking();
                return null;
            }
        });
    }
}
