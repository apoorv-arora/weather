package sanshinkan.org.warrior.utils.views;

import android.content.Context;
import android.util.AttributeSet;

import sanshinkan.org.warrior.utils.CommonLib;

/**
 * Created by apoorvarora on 03/10/16.
 */
public class EditTextBold extends android.support.v7.widget.AppCompatEditText {
    public EditTextBold(Context context) {
        super(context);
        setTypeface(CommonLib.getTypeface(context, CommonLib.FONT_BOLD));
    }

    public EditTextBold(Context context, AttributeSet attr) {
        super(context,attr);
        setTypeface(CommonLib.getTypeface(context, CommonLib.FONT_BOLD));
    }

    public EditTextBold(Context context, AttributeSet attr, int i) {
        super(context,attr,i);
        setTypeface(CommonLib.getTypeface(context, CommonLib.FONT_BOLD));
    }
}