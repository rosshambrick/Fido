package com.rosshambrick.fido;

import android.content.Context;

import com.google.gson.Gson;

import java.util.Collection;

public class Fido {
    static final Gson gson = new Gson();

    public static final boolean LOGGING = false;

    public static Builder with(Context context) {
        return new Builder(context);
    }

    public static class Builder {

        private final Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public PutBuilder put(String key, Object value) {
            return new PutBuilder(context, key, value);
        }

        public PutBuilder put(String key, Collection values) {
            return new PutBuilder(context, key, values.toArray());
        }

        public PutBuilder put(String key, Object[] values) {
            return new PutBuilder(context, key, values);
        }

        public FetchBuilder fetch(String key) {
            return new FetchBuilder(context, key);
        }

        public <T> FetchIdsBuilder<T> fetchIds(final Class<T> tClass) {
            return new FetchIdsBuilder<T>(context, tClass);
        }

        public <T> RemoveBuilder<T> remove(String key, Class<T> tClass) {
            return new RemoveBuilder<T>(context, key, tClass);
        }

        public <T> RemoveBuilder remove(Class<T> tClass) {
            return new RemoveBuilder<T>(context, tClass);
        }
    }
}
