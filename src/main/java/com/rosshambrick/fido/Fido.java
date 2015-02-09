package com.rosshambrick.fido;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Fido {
    static final Gson gson = new Gson();

    public static final boolean LOGGING = false;

    public static Builder with(Context context) {
        return new Builder(context);
    }

    public static class Builder {
        private static final String TAG = "Fido.Builder";
        private final Context context;
        private File filesDir;

        public Builder(Context context) {
            this.context = context;
            this.filesDir = context.getFilesDir();
        }

        public <T> Editor edit(Class<T> tClass) {
            return new Editor<>(context, tClass);
        }

        public FetchBuilder fetch(String key) {
            return new FetchBuilder(context, key);
        }

        public FetchBuilder fetch(int key) {
            return fetch(String.valueOf(key));
        }

        public <T> FetchIdsBuilder<T> fetchIds(final Class<T> tClass) {
            return new FetchIdsBuilder<T>(context, tClass);
        }

        public <T> boolean contains(Class<T> tClass, String key) {
            String classFolder = getClassFolder(tClass);
            String keyFilePath = new File(classFolder, key).getPath();
            File file = new File(filesDir, keyFilePath);
            return file.exists();
        }

        public <T> List<T> fetchAll(Class<T> tClass) {
            List<T> list = new ArrayList<>();
            String classFolder = getClassFolder(tClass);
            File[] files = new File(classFolder).listFiles();
            for (File file : files) {
                try {
                    FileReader fileReader = new FileReader(file);
//                    JsonReader jsonReader = new JsonReader(fileReader);
                    T object = gson.fromJson(fileReader, tClass);
                    list.add(object);
                } catch (FileNotFoundException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
            return list;
        }

        private <T> String getClassFolder(Class<T> tClass) {
            return tClass.getSimpleName().toLowerCase();
        }
    }
}
