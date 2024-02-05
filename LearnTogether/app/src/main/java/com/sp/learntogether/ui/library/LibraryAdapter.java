package com.sp.learntogether.ui.library;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.sp.learntogether.data.AppDatabase;
import com.sp.learntogether.data.BookDao;
import com.sp.learntogether.data.BookDiffCallback;
import com.sp.learntogether.databinding.SingleBookBinding;
import com.sp.learntogether.models.Book;

import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.VH> {

    private List<Book> books;
    private BookDao dao;

    public LibraryAdapter(List<Book> books) {
        this.books = books;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        SingleBookBinding binding = SingleBookBinding.inflate(inflater, parent, false);
        VH vh = new VH(binding);
        if (dao == null) {
            dao = AppDatabase.getInstance(context).bookDao();
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Context context = holder.itemView.getContext();
        SingleBookBinding binding = holder.binding;
        Book book = books.get(position);
        binding.setName(book.title);
        binding.setDesc(book.description);
        binding.setIsbn(book.isbn);
        binding.bookImage.setImageURI(
                Uri.parse(book.imagePath)
        );
        binding.itemShare.setOnClickListener(v -> {
            book.share(context);
        });
        binding.bookDelete.setOnClickListener(v -> {
            new Thread(() -> {

                dao.deleteOne(book);
            }).start();
        });
    }
    public void updateLibrary(List<Book> newBooks) {
        BookDiffCallback callback = new BookDiffCallback(books, newBooks);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        books = newBooks;
        result.dispatchUpdatesTo(this);
    }


    @Override
    public int getItemCount() {
        return books.size();
    }

    public static class VH extends RecyclerView.ViewHolder {

        private final SingleBookBinding binding;
        public VH(@NonNull SingleBookBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }
}
