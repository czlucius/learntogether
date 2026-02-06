package com.sp.learntogether.io;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sp.learntogether.astraDBHelper;
import com.sp.learntogether.models.Book;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

import kotlin.jvm.functions.Function0;

public class DatabaseInteractor {

    RequestQueue queue;

    private Context appCtx;
    private DatabaseInteractor(@NonNull Context context) {
        queue = Volley.newRequestQueue(context.getApplicationContext());
        this.appCtx = context.getApplicationContext();
    }

    private static DatabaseInteractor instance;

    public static DatabaseInteractor getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseInteractor(context.getApplicationContext());
        }
        return instance;
    }


    public void getRow(Consumer<JSONObject> responseHandler, String base, String rowId) {
        get(responseHandler, base + rowId);
    }
    public void get(Consumer<JSONObject> responseHandler, String url) {
        AtomicInteger volleyResponseStatus = new AtomicInteger(-1);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            if (volleyResponseStatus.get() == 200) {
                responseHandler.accept(response);
            }
        }, error -> {
            error.printStackTrace();
            Log.e("OnErrorResponse", Arrays.toString(error.networkResponse.data));
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return astraDBHelper.getHeader();
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                volleyResponseStatus.set(response.statusCode);
                return super.parseNetworkResponse(response);
            }
        };
        queue.add(jsonObjectRequest);
    }


    public void persistBookImage(Context context, Book book, Consumer<Book> callback) throws IOException {
        File file = new File(context.getFilesDir(), book.isbn + ".png");
        boolean created = file.createNewFile();
        Log.i(TAG, "persistBookImage: " + book.imagePath);
        ImageRequest req = new ImageRequest(book.imagePath, bitmap -> {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                book.imagePath = file.getPath();
                callback.accept(book);

            } catch (IOException e) {
                throw new RuntimeException(e); // File should be created if not this path will never be taken!
            }
        }, 600, 600, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888, error -> {
            error.printStackTrace();
            Log.e(TAG, "fromNetworkResponse: " + Arrays.toString(error.networkResponse.data));
        });
        queue.add(req);

    }

    public void queryOpenLibrary(String isbn, Consumer<Book> handler) {
        String url = "https://openlibrary.org/api/books?jscmd=details&format=json&bibkeys=ISBN:" + isbn;
        JsonObjectRequest jsor = new JsonObjectRequest(url, response -> {
            try {
                Book book = Book.fromNetworkResponseDoNotPersist(isbn, response);
                handler.accept(book);
            } catch (JSONException | IOException e) {
                throw new RuntimeException(e);
            }
        }, err -> {
            handler.accept(null);
            err.printStackTrace();
            Log.e(TAG, "queryOpenLibrary: error: " + Arrays.toString(err.networkResponse.data));
        });
        queue.add(jsor);
    }

    public void post(JSONObject request, String url, Response.Listener<JSONObject> responseHandler, Response.ErrorListener errorListener) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, request, responseHandler, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return astraDBHelper.getHeader();
            }

        };
        queue.add(jsonObjectRequest);
    }

    private static final String TAG = "DatabaseInteractor";

    public void addToList(String url, String columnName, String value, Response.Listener<JSONObject> responseHandler) {
        get(jsor -> {
            JSONObject jsonObject;
            try {
                jsonObject = jsor.getJSONArray("data").getJSONObject(0);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            JSONArray list;
            JSONObject obj = new JSONObject();
            try {
                list = jsonObject.getJSONArray(columnName);
            } catch (JSONException e) {
                e.printStackTrace();
                list = new JSONArray();
            }

            list.put(value);
            try {
                obj.put(columnName, list);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH, url, obj, responseHandler, e -> {
                e.printStackTrace();
                Log.e(TAG, "addToList: " + Arrays.toString(e.networkResponse.data));
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return astraDBHelper.getHeader();
                }
            };
            queue.add(jsonObjectRequest);
        }, url);

    }
}

