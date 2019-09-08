package sanshinkan.org.warrior.utils.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import sanshinkan.org.warrior.utils.CommonLib;

/**
 * Created by apoorvarora on 03/10/16.
 */
public class EditTextLight extends android.support.v7.widget.AppCompatEditText {
    public EditTextLight(Context context) {
        super(context);
        setTypeface(CommonLib.getTypeface(context, CommonLib.FONT_LIGHT));
    }

    public EditTextLight(Context context, AttributeSet attr) {
        super(context,attr);
        setTypeface(CommonLib.getTypeface(context, CommonLib.FONT_LIGHT));
    }

    public EditTextLight(Context context, AttributeSet attr, int i) {
        super(context,attr,i);
        setTypeface(CommonLib.getTypeface(context, CommonLib.FONT_LIGHT));
    }
}