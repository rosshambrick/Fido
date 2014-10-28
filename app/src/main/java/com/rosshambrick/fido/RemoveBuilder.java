package com.rosshambrick.fido;

import android.content.Context;

import java.io.File;

public class RemoveBuilder<T> {
    private final Context context;
    private String key;
    private final Class<T> tClass;

    public RemoveBuilder(Context context, String key, Class<T> tClass) {
        this.context = context;
        this.key = key;
        this.tClass = tClass;
    }

    public RemoveBuilder(Context context, Class<T> tClass) {
        this.context = context;
        this.tClass = tClass;
    }

    public void blocking() {
        String folderName = tClass.getSimpleName().toLowerCase();
        File folderPath = new File(context.getFilesDir(), folderName);
        if (key != null) {
            File file = new File(folderPath, key);
            file.delete();
        } else {
            File[] files = folderPath.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
        }
    }
}
