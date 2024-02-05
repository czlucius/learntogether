package com.sp.learntogether.data;

import androidx.recyclerview.widget.DiffUtil;

import com.sp.learntogether.models.Book;

import java.util.List;

public class BookDiffCallback extends DiffUtil.Callback {
    private final List<Book> oldBooks;
    private final List<Book> newBooks;


    public BookDiffCallback(List<Book> oldBooks, List<Book> newBooks) {
        this.oldBooks = oldBooks;
        this.newBooks = newBooks;
    }

    @Override
    public int getOldListSize() {
        return oldBooks.size();
    }

    @Override
    public int getNewListSize() {
        return newBooks.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldBooks.get(oldItemPosition).isbn == newBooks.get(newItemPosition).isbn;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldBooks.get(oldItemPosition).equals(newBooks.get(newItemPosition));
    }

}
