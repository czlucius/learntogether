package com.sp.learntogether.io;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Facade pattern for interacting with files through content resolvers.
 */
public class FileInteractor {
    private byte[] data;

    public FileInteractor(@NonNull Context context, @NonNull Uri uri) throws IOException, NullPointerException {

        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ) {
            if (inputStream == null) {
                throw new NullPointerException("File does not exist at " + uri);
            }
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                baos.write(buf, 0, len);
            }
            data = baos.toByteArray();
        }
    }

    public boolean saveToFile(@NonNull File file) {
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(readAllBytes());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public byte[] readAllBytes()  {
        return data;
    }
}
