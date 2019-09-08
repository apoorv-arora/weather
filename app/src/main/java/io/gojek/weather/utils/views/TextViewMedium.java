package io.gojek.weather.utils.views;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;

import io.gojek.weather.utils.CommonLib;

/**
 * Created by apoorvarora on 03/10/16.
 */
public class TextViewMedium extends android.support.v7.widget.AppCompatTextView {

    public TextViewMedium(Context context) {
        super(context);
        setTypeface(CommonLib.getTypeface(context, CommonLib.FONT_MEDIUM));
        setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.HINTING_ON);
    }

    public TextViewMedium(Context context, AttributeSet attr) {
        super(context,attr);
        setTypeface(CommonLib.getTypeface(context, CommonLib.FONT_MEDIUM));
        setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.HINTING_ON);
    }

    public TextViewMedium(Context context, AttributeSet attr, int i) {
        super(context,attr,i);
        setTypeface(CommonLib.getTypeface(context, CommonLib.FONT_MEDIUM));
        setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.HINTING_ON);
    }
}
