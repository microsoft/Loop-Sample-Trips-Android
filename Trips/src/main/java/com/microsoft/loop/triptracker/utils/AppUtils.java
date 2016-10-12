package com.microsoft.loop.triptracker.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;

/**
 * Created on 6/19/16.
 */
public class AppUtils {

    public static void applyFontToMenuItem(Context context, MenuItem mi, String fontName) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), fontName + ".ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }
}
