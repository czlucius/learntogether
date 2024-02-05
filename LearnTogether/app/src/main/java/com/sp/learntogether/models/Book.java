package com.sp.learntogether.models;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.android.volley.toolbox.ImageRequest;
import com.sp.learntogether.R;
import com.sp.learntogether.data.BookDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

@Entity(tableName = "book")
public class Book {


//    @PrimaryKey(autoGenerate = true)
//    public int uid;

    @ColumnInfo
    public String title;
    @ColumnInfo
    public String description;
    @PrimaryKey
    @NonNull
    public String isbn;
    @ColumnInfo
    public String imagePath;

    /**
     * Must make sure it is in {app data}/books
     */
    @ColumnInfo
    public String bookUrl;

    public Book(String title, String description, @NonNull String  isbn, String imagePath, String bookUrl) {
        this.title = title;
        this.description = description;
        this.isbn = isbn;
        this.imagePath = imagePath;
        this.bookUrl = bookUrl;
    }

//    public static Book createBook(Context context, String title, String description, long isbn, Uri imageUri) throws IOException {
//        FileInteractor interactor = new FileInteractor(context, imageUri);
//        interactor.saveToFile()
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(isbn, book.isbn) && title.equals(book.title) && description.equals(book.description) && Objects.equals(imagePath, book.imagePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, isbn, imagePath);
    }


    public void share(Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        String text = context.getString(R.string.book_share_template, title, description, bookUrl);
        intent.putExtra(Intent.EXTRA_TEXT, text);

        if (imagePath != null) {
            File file = new File(imagePath);
            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file, title + ".png");
            Log.i(TAG, "share: " + imagePath);
            intent.setType("image/*");

            intent.putExtra(Intent.EXTRA_STREAM, uri);

        }
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_with_your_friends, title)));
    }

    private static final String TAG = "Book";
    public static void fromNetworkResponse(Context context, String isbn, JSONObject obj, Consumer<Book> callback) throws JSONException, IOException {
        JSONArray arr = obj.names();
        if (arr != null && arr.length() > 0) {
            String key1 = arr.getString(0);
            JSONObject a = obj.getJSONObject(key1);
            Log.i(TAG, "fromNetworkResponse: " + a.toString());
            JSONObject details = a.getJSONObject("details");
            String name = details.getString("full_title");
            String desc = details.getString("subtitle");
            String bookUrl = details.getString("info_url");
            String imageUrl = details.getString("thumbnail_url").replace("-S.", "-L.");
            File file = new File(context.getFilesDir(), UUID.fromString(isbn).toString());
            boolean created = file.createNewFile();
            if (created) {

                try (FileOutputStream fos = new FileOutputStream(file)) {
                    ImageRequest req = new ImageRequest(imageUrl, bitmap -> {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                        callback.accept(new Book(
                                name, desc, isbn, file.getPath(), bookUrl
                        ));
                    }, 400, 400, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888, error -> {
                        error.printStackTrace();
                        Log.e(TAG, "fromNetworkResponse: " + Arrays.toString(error.networkResponse.data));
                    });
                }
            } else {
                callback.accept(new Book(
                        name, desc, isbn, file.getPath(), bookUrl
                ));
            }
        }
    }




    public static Book fromNetworkResponseDoNotPersist(String isbn, JSONObject obj) throws JSONException, IOException {
        JSONArray arr = obj.names();
        if (arr != null && arr.length() > 0) {
            String key1 = arr.getString(0);
            JSONObject a = obj.getJSONObject(key1);
            Log.i(TAG, "fromNetworkResponse: " + a.toString());
            JSONObject details = a.getJSONObject("details");
            String name = !details.optString("full_title").equals("") ? details.optString("full_title") :details.optString("title");
            String desc = details.optString("subtitle");
            String bookUrl = !a.optString("info_url").equals("") ? a.optString("info_url") : a.optString("preview_url");
            String imageUrl = a.optString("thumbnail_url").replace("-S.", "-L.");
            Log.i(TAG, "fromNetworkResponseDoNotPersist: " + name + ", " + desc + ", " + bookUrl + ", " + imageUrl );
            return new Book(
                    name,
                    desc,
                    isbn,
                    imageUrl,
                    bookUrl
            );
        } else {
            return null;
        }
    }
}
