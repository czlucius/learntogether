package com.sp.learntogether.ui.library;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.sp.learntogether.data.AppDatabase;
import com.sp.learntogether.data.BookDao;
import com.sp.learntogether.models.Book;

import java.util.List;

public class LibraryViewModel extends AndroidViewModel {
    private BookDao dao;

    public LibraryViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        dao = db.bookDao();
    }

    public List<Book> getAllBooks() {
        return dao.getAll();
    }

    public LiveData<List<Book>> allBooksLive() {
        return dao.getAllLive();
    }
}
