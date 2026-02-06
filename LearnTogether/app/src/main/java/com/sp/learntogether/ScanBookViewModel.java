package com.sp.learntogether;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.mlkit.vision.barcode.common.Barcode;
import com.sp.learntogether.data.AppDatabase;
import com.sp.learntogether.data.BookDao;
import com.sp.learntogether.io.DatabaseInteractor;
import com.sp.learntogether.models.Book;
import com.sp.learntogether.objects.BarcodeAnalyzer;
import com.sp.learntogether.objects.ScanWrapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ScanBookViewModel extends AndroidViewModel {

    private static final String TAG = "ScanBookViewModel";
    public BarcodeAnalyzer analyzer;
    private final MutableLiveData<Set<ScanWrapper>> scanned = new MutableLiveData<>(new HashSet<>());
    private final BookDao dao;

    public ScanBookViewModel(Application application) {
        super(application);
        analyzer = new BarcodeAnalyzer(
                this::scanBarcodes
        );
        dbIO = DatabaseInteractor.getInstance(application);
        dao = AppDatabase.getInstance(application)
                .bookDao();

    }

    public LiveData<Set<ScanWrapper>> getScanned() {
        return scanned;
    }

    public void scanBarcodes(Iterable<Barcode> barcodes) {
        boolean changed = false;
        for (Barcode barcode: barcodes) {
            assert scanned.getValue() != null;
            ScanWrapper sw = new ScanWrapper(barcode, me -> {
                if (scanned.getValue() != null) {
                    scanned.getValue().remove(me);
                }
            });
            if (sw.getIsbn() != null) {
                scanned.getValue().add(sw);
                changed = true;
            }
        }
//        Log.i(TAG, "scanBarcodes: " + Arrays.toString(scanned.getValue().toArray()));

        if (changed) {
            // More codes detected
            scanned.setValue(scanned.getValue());
        }
    }
    private DatabaseInteractor dbIO;
    private MutableLiveData<Book> displayedBook = new MutableLiveData<>();

    public LiveData<Book> getDisplayedBook() {
        return displayedBook;
    }

    public void addBook(Book book) throws IOException {
        Log.i(TAG, "addBook: Inserting book" + book.imagePath);
        if (book.imagePath != null && !book.imagePath.trim().isEmpty()) {
            dbIO.persistBookImage(getApplication(), book, this::insertBook);
        } else {
            insertBook(book);
        }

    }

    public void loadBook(String isbn) {
        dbIO.queryOpenLibrary(isbn, book -> {
            displayedBook.setValue(book);
        });
    }

    public void clearBook() {
        displayedBook.setValue(null);
    }

    private void insertBook(Book book1) {
        new Thread(() -> dao.insertOne(book1)).start();
    }
}