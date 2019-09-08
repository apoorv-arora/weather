package sanshinkan.org.warrior.utils.views;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

import sanshinkan.org.warrior.utils.CommonLib;

/**
 * Created by apoorvarora on 04/10/16.
 */
public class TypefaceSpan extends MetricAffectingSpan {

    private Typeface mTypeface;
    private int mTextColor;
    private float mTextSize;

    /**
     * Load the {@link Typeface} and apply to a {@link Spannable}.
     */
    public TypefaceSpan(Context context, String typefaceName, int color, float size) {

        if(typefaceName.equals(CommonLib.FONT_BOLD))
            mTypeface = CommonLib.getTypeface(context, CommonLib.FONT_BOLD);
        else
            mTypeface = CommonLib.getTypeface(context, CommonLib.FONT_LIGHT);

        //mTypeface = sTypefaceCache.get(typefaceName);
        mTextColor = color;
        mTextSize = size;

    }

    @Override
    public void updateMeasureState(TextPaint p) {
        p.setTypeface(mTypeface);
        // Note: This flag is required for proper typeface rendering
        p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setTypeface(mTypeface);
        tp.setColor(mTextColor);
        tp.setTextSize(mTextSize);
        // Note: This flag is required for proper typeface rendering
        tp.setFlags(tp.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }
}