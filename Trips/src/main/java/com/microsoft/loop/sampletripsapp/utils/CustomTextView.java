package com.microsoft.loop.sampletripsapp.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import java.io.File;

public class CustomTextView extends TextView {

    private  String fontName = "";
    private final String fontNameFileType = ".ttf";
    private final String namespace = "https://schemas.android.com/apk/res/com.microsoft.loop.sampletripsapp.utils";

    public CustomTextView(Context context) {
        super(context);
        init(context, null);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        fontName = attributeSet.getAttributeValue(namespace, "fontName");

        AssetManager assets = context.getAssets();
        final Typeface font = Typeface.createFromAsset(assets, fontName + fontNameFileType);
        this.setTypeface(font);
    }
}