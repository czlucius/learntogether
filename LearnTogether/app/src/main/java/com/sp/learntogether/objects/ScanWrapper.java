package com.sp.learntogether.objects;


import android.util.Log;
import android.view.View;

import com.google.android.material.behavior.SwipeDismissBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.google.mlkit.vision.barcode.common.Barcode;

import java.util.Objects;
import java.util.function.Consumer;

public class ScanWrapper {
    private static final String TAG = "ScanWrapper";


    private Snackbar snack;
    private final Barcode code;
    private boolean scanned = false;
    private DismissListener dismissListener;
     public ScanWrapper(Barcode barcode, DismissListener dismissListener) {
        this.code = barcode;
        this.dismissListener = dismissListener;
    }

    public Barcode getCode() {
        return code;
    }

    public String getIsbn() {
        if (code.getValueType() == Barcode.TYPE_ISBN) {
            return code.getDisplayValue();
        } else {
            return null;
        }
    }

    boolean isValidIsbn(String isbn) {
        int sum = 0;
        if(isbn.length() == 10) {
            for(int i = 0; i < 10; i++) {
                sum += i * isbn.charAt(i); //asuming this is 0..9, not '0'..'9'
            }

            if (isbn.charAt(9) == sum % 11) return true;
        } else if(isbn.length() == 13) {

            for(int i = 0; i < 12; i++) {
                if(i % 2 == 0) {
                    sum += isbn.charAt(i); //asuming this is 0..9, not '0'..'9'
                } else {
                    sum += isbn.charAt(i) * 3;
                }
            }

            if(isbn.charAt(12) == 10 - (sum % 10)) return true;
        }

        return false;
    }


    public void release() {
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scanned = false;

         }).start();


    }

    public void display(View view, Consumer<String> onClick) {
        if (!scanned) {
            String isbn = getIsbn();

            Log.i(TAG, "display: " + scanned);
            if (isbn != null) {
                snack = Snackbar.make(view, "Detected " + isbn, Snackbar.LENGTH_INDEFINITE);
//                snack.setBehavior(new SwipeDismissBehavior<>())
                snack.setAction("Check info", v -> {
                    onClick.accept(getIsbn());
                });
                snack.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        dismissListener.destroy(ScanWrapper.this);
                    }
                });

                snack.show();

            }
            scanned = true;

            // Add to history
//            HistoryDatabase.insertCode(ctx, code);
        }
    }

    public void dismissDialog() {
        if (snack != null) {
            snack.dismiss();
        }
        snack = null; // Prevent references to old dialog.
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScanWrapper that = (ScanWrapper) o;
        Log.i(TAG, "equals: " + getIsbn() + " " + that.getIsbn());
        if (that.getIsbn() == null || getIsbn() == null) {
            return false;
        }
        return getIsbn().equals(that.getIsbn());
        // Cannot compare scanned variable, because in this case, a new object is set by default to scanned, and comparison by HashSet will always return false.
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIsbn());
    }
    public interface DismissListener {
        void destroy(ScanWrapper instance);
    }

}