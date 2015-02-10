package com.rosshambrick.fido;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fido {
    static final Gson gson = new Gson();

    static boolean LOGGING = false;

    public static Builder with(Context context) {
        return new Builder(context);
    }

    public static void enableLogging() {
        LOGGING = true;
    }

    public static class Builder {
        private static final String TAG = "Fido.Builder";
        private final Context context;
//        private File filesDir;

        public Builder(Context context) {
            this.context = context;
//            this.filesDir = context.getFilesDir();
        }

        private File getClassDirectory(Class tClass) {
            String folderName = tClass.getSimpleName().toLowerCase();
            return new File(context.getFilesDir(), folderName);
        }

        public Editor edit() {
            return new Editor(context);
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
            File classDirectory = getClassDirectory(tClass);
            Log.d(TAG, String.format("checking for presence of %s in %s", key, classDirectory));
            File file = new File(classDirectory, key);
            boolean exists = file.exists();
            Log.d(TAG, exists ? "file found" : "file not found");
            return exists;
        }

        public <T> List<T> fetchAll(Class<T> tClass) {
            List<T> list = new ArrayList<>();
            File classDirectory = getClassDirectory(tClass);

            Log.d(TAG, "fetching all files in " + classDirectory);
            File[] files = classDirectory.listFiles();
            if (files == null) {
                Log.d(TAG, "none found");
                return list;
            }

            Log.d(TAG, "found " + files.length + " files");
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

        public <T> T fetch(Class<T> tClass, String key) {
            File classDirectory = getClassDirectory(tClass);
            Log.d(TAG, "fetching " + key + " in " + classDirectory);
            File file = new File(classDirectory, key);
            if (!file.exists()) {
                Log.d(TAG, "not found");
                return null;
            }

            Log.d(TAG, "found " + key);
            try {
                FileReader fileReader = new FileReader(file);
//              JsonReader jsonReader = new JsonReader(fileReader);
                return gson.fromJson(fileReader, tClass);
            } catch (FileNotFoundException e) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }
    }

    public static class Editor {
        private static final String TAG = Fido.class.getSimpleName();

        private final Context context;
//        private Class<T> tClass;
//        private String key;
//        protected Object value;
        private Map<String, Class> removeQueue = new HashMap<>();
        private Map<String, Object> putQueue = new HashMap<>();

//        public Editor(Context context, Class<T> tClass) {
//            this.context = context;
//            this.tClass = tClass;
//        }

        public Editor(Context context) {
            this.context = context;
        }

        private File getClassDirectory(Class tClass) {
            String folderName = tClass.getSimpleName().toLowerCase();
            return new File(context.getFilesDir(), folderName);
        }

        public Editor put(String key, Object value) {
            return queuePut(key, value);
        }

//        public Editor put(String key, Collection<T> values) {
//            return queuePut(key, values.toArray());
//        }

//        public Editor put(String key, T[] values) {
//            return queuePut(key, values);
//        }

        private Editor queuePut(String key, Object value) {
            putQueue.put(key, value);
            return this;
        }

        public void commit() {
            for (String key : removeQueue.keySet()) {
                doRemove(key, removeQueue.get(key));
            }
            for (String key : putQueue.keySet()) {
                doPut(key, putQueue.get(key));
            }

        }

        private void doRemove(String key, Class tClass) {
            File classDirectory = getClassDirectory(tClass);
            if (key != null) {
                File file = new File(classDirectory, key);
                file.delete();
            } else {
                File[] files = classDirectory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                }
            }
        }

        private void doPut(String key, Object value) {
            Log.d(TAG, "putting " + key);

            FileWriter writer = null;
            try {
                String json = Fido.gson.toJson(value);

//                String folderName;
//                if (value.getClass().isArray()) {
//                    Object[] array = (Object[]) value;
//                    if (array.length == 0) {
//                        return; //empty array, nothing to do
//                    } else {
//                        folderName = array[0].getClass().getSimpleName().toLowerCase() + "[]";
//                    }
//                } else {
//                    folderName = value.getClass().getSimpleName().toLowerCase();
//                }

                File classDirectoryPath = getClassDirectory(value.getClass());
                classDirectoryPath.mkdirs();

                File file = new File(classDirectoryPath, key);

                if (Fido.LOGGING) Log.d(TAG, "PUT: " + file);
                if (Fido.LOGGING) Log.d(TAG, "PUT: " + json);

                writer = new FileWriter(file);
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

        public <T> Editor remove(Class<T> tClass, int id) {
            removeQueue.put(String.valueOf(id), tClass);
            return this;
        }
    }
}
