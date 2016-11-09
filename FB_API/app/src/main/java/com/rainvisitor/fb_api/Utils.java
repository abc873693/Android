package com.rainvisitor.fb_api;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by Ray on 2016/11/8.
 */

public class Utils {



    public static Point getDisplayDimen(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }


    public static PendingIntent createSharePendingIntent(Context context, String content) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, content);
        sendIntent.setType("text/plain");
        return PendingIntent.getActivity(context, 0, sendIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }



    public static Drawable getSelectableItemBackgroundDrawable(Context context) {
        return ContextCompat.getDrawable(context, getSelectableItemBackgroundResource(context));
    }

    public static int getSelectableItemBackgroundResource(Context context) {
        int[] attrs = new int[]{R.attr.selectableItemBackground};
        TypedArray typedArray = context.obtainStyledAttributes(attrs);
        int resourceId = typedArray.getResourceId(0, 0);
        typedArray.recycle();
        return resourceId;
    }

    public static LayerDrawable getSelectableDrawable(Context context,
                                                      @DrawableRes int drawableResId) {
        return new LayerDrawable(new Drawable[]{ContextCompat.getDrawable(context, drawableResId),
                getSelectableItemBackgroundDrawable(context)});
    }

    /**
     * Sets the background for a view while preserving its current padding. If the background drawable
     * has its own padding, that padding will be added to the current padding.
     *
     * @param view               View to receive the new background.
     * @param backgroundDrawable Drawable to set as new background.
     */
    public static void setBackgroundAndKeepPadding(View view, Drawable backgroundDrawable) {
        Rect drawablePadding = new Rect();
        backgroundDrawable.getPadding(drawablePadding);
        int top = view.getPaddingTop() + drawablePadding.top;
        int left = view.getPaddingLeft() + drawablePadding.left;
        int right = view.getPaddingRight() + drawablePadding.right;
        int bottom = view.getPaddingBottom() + drawablePadding.bottom;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            //noinspection deprecation
            view.setBackgroundDrawable(backgroundDrawable);
        } else {
            view.setBackground(backgroundDrawable);
        }
        view.setPadding(left, top, right, bottom);
    }

    public static void dismissDialog(Dialog dialog) {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (IllegalArgumentException e) {
            // ignore
        }
    }

}
