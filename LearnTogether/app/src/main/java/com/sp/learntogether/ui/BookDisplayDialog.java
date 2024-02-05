package com.sp.learntogether.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sp.learntogether.data.AppDatabase;
import com.sp.learntogether.data.BookDao;
import com.sp.learntogether.databinding.DialogBookBinding;
import com.sp.learntogether.models.Book;

import java.util.function.Consumer;

public class BookDisplayDialog extends BottomSheetDialog {
    private final Consumer<Book> addListener;
    private Book book;
    private BookDao dao;
    public BookDisplayDialog(@NonNull Context context, @NonNull Book book, Consumer<Book> addListener) {
        super(context);
        this.book = book;
        this.dao = AppDatabase.getInstance(context).bookDao();
        this.addListener = addListener;
    }
    private DialogBookBinding binding;

    private static final String TAG = "BookDisplayDialog";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.i(TAG, "onCreate: Book received has URL " + book.bookUrl);
        binding.setBook(book);
        binding.bookUrl.setOnClickListener(v -> {
            WebView wv = new WebView(getContext());
            wv.loadUrl(book.bookUrl);
            BottomSheetDialog bsd = new BottomSheetDialog(getContext());
            bsd.setContentView(wv);
            bsd.show();
        });
        binding.addBook.setOnClickListener(v -> {
            addListener.accept(book);
            Toast.makeText(getContext(), "Book has been added", Toast.LENGTH_SHORT).show();
            dismiss();

        });
    }
}
