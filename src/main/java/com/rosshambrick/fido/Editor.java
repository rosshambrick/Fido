package com.rosshambrick.fido;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Editor<T> {
    private static final String TAG = Fido.class.getSimpleName();

    private final Context context;
    private Class<T> tClass;
    private String id;
    protected Object value;
    private List<String> removes = new ArrayList<>();
    private List<Pair<String, Object>> puts = new ArrayList<>();

    public Editor(Context context, Class<T> tClass) {
        this.context = context;
        this.tClass = tClass;
    }

    public Editor put(String key, Object value) {
        return putInternal(key, value);
    }

    public Editor put(String key, Collection values) {
        return putInternal(key, values.toArray());
    }

    public Editor put(String key, Object[] values) {
        return putInternal(key, values);
    }

    private Editor putInternal(String key, Object value) {
        puts.add(new Pair<>(key, value));
        this.id = key;
        this.value = value;
        return this;
    }

    public void commit() {
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

    public Editor<T> remove(int id) {
        removes.add(String.valueOf(id));
        return this;
    }

//    public Observable async() {
//        return Async.start(new Func0<Void>() {
//            @Override
//            public Void call() {
//                commit();
//                return null;
//            }
//        });
//    }
}
