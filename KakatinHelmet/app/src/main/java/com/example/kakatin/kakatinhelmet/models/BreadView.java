package com.example.kakatin.kakatinhelmet.models;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Matti on 05/12/2015.
 */
public class BreadView extends TextView {

    public BreadView(Context context, AttributeSet attrs){
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(),
                "fonts/PoetsenOne-Regular.ttf"));
    }
}
