package sanshinkan.org.warrior.utils.views;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

import sanshinkan.org.warrior.utils.CommonLib;

/**
 * Created by apoorvarora on 03/10/16.
 */
public class EditTextRegular extends android.support.v7.widget.AppCompatEditText {
    public EditTextRegular(Context context) {
        super(context);
        setTypeface(CommonLib.getTypeface(context, CommonLib.FONT_REGULAR));
        setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.HINTING_ON);
    }

    public EditTextRegular(Context context, AttributeSet attr) {
        super(context,attr);
        setTypeface(CommonLib.getTypeface(context, CommonLib.FONT_REGULAR));
        setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.HINTING_ON);
    }

    public EditTextRegular(Context context, AttributeSet attr, int i) {
        super(context,attr,i);
        setTypeface(CommonLib.getTypeface(context, CommonLib.FONT_REGULAR));
        setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.HINTING_ON);
    }
}