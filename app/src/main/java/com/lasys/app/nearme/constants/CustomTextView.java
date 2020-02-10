package com.lasys.app.nearme.constants;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import static com.lasys.app.nearme.intrface.AppConstants.FONT_STYLE;

public class CustomTextView  extends android.support.v7.widget.AppCompatTextView
{
    public CustomTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public CustomTextView(Context context) {
        super(context);
        init();
    }

    public void init()
    {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), FONT_STYLE);
        setTypeface(tf ,1);

    }
}
