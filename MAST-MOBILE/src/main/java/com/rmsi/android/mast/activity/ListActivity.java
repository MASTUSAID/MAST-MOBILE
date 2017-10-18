package com.rmsi.android.mast.activity;

import android.view.View;

/**
 * Interface for all activities showing lists with options button
 */

public interface ListActivity {
    /**
     * Shows popup menu for options button
     * @param v View component to bind popup displaying
     * @param position Item position in the underlying source list
     */
    void showPopup(View v, int position);
}
