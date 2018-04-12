//package com.rmsi.android.mast.util;
//
//import android.app.Activity;
//import android.graphics.Rect;
//import android.os.Build;
//import android.view.View;
//import android.view.ViewTreeObserver;
//import android.view.inputmethod.InputMethodManager;
//import com.mikepenz.materialize.util.UIUtils;
///**
// * Created by Ambar.Srivastava on 2/7/2018.
// */
//
//public class RitalinLayout {
//        private View decorView;
//        private View contentView;
//        private float initialDpDiff = -1;
//
//        public RitalinLayout(Activity act, View contentView) {
//            this.decorView = act.getWindow().getDecorView();
//            this.contentView = contentView;
//
//            //only required on newer android versions. it was working on API level 19
//            if (Build.VERSION.SDK_INT >= 19) {
//                decorView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
//            }
//        }
//
//        public void enable() {
//            if (Build.VERSION.SDK_INT >= 19) {
//                decorView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
//            }
//        }
//
//        public void disable() {
//            if (Build.VERSION.SDK_INT >= 19) {
//                decorView.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
//            }
//        }
//
//
//        //a small helper to allow showing the editText focus
//        ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                Rect r = new Rect();
//                //r will be populated with the coordinates of your view that area still visible.
//                decorView.getWindowVisibleDisplayFrame(r);
//
//                //get the height diff as dp
//                float heightDiffDp = UIUtils.convertPixelsToDp(decorView.getRootView().getHeight() - (r.bottom - r.top), decorView.getContext());
//
//                //set the initialDpDiff at the beginning. (on my phone this was 73dp)
//                if (initialDpDiff == -1) {
//                    initialDpDiff = heightDiffDp;
//                }
//
//                //if it could be a keyboard add the padding to the view
//                if (heightDiffDp - initialDpDiff > 100) { // if more than 100 pixels, its probably a keyboard...
//                    //check if the padding is 0 (if yes set the padding for the keyboard)
//                    if (contentView.getPaddingBottom() == 0) {
//                        //set the padding of the contentView for the keyboard
//                        contentView.setPadding(0, 0, 0, (int) UIUtils.convertDpToPixel((heightDiffDp - initialDpDiff), decorView.getContext()));
//                    }
//                } else {
//                    //check if the padding is != 0 (if yes reset the padding)
//                    if (contentView.getPaddingBottom() != 0) {
//                        //reset the padding of the contentView
//                        contentView.setPadding(0, 0, 0, 0);
//                    }
//                }
//            }
//        };
//
//
//        /**
//         * Helper to hide the keyboard
//         *
//         * @param act
//         */
//        public static void hideKeyboard(Activity act) {
//            if (act != null && act.getCurrentFocus() != null) {
//                InputMethodManager inputMethodManager = (InputMethodManager) act.getSystemService(Activity.INPUT_METHOD_SERVICE);
//                inputMethodManager.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(), 0);
//            }
//        }