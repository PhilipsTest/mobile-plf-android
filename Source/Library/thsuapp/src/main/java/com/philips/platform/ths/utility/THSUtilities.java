package com.philips.platform.ths.utility;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.StringRes;

import com.philips.platform.uid.drawable.FontIconDrawable;

public class THSUtilities {

    public static int getAttributeColor(Context context, int attribute) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[]{attribute});
        int color = Color.MAGENTA;
        if (typedArray != null) {
            color = typedArray.getColor(0, Color.WHITE);
            typedArray.recycle();
        }
        return color;
    }

    public static Drawable getGpsDrawableFromFontIcon(Context context, @StringRes int fontIconPath, int colorInt, int sizeInDp){
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/iconfont.ttf");
        FontIconDrawable fontIconDrawable = new FontIconDrawable(context,context.getString(fontIconPath),typeface);
        fontIconDrawable.sizeDp(sizeInDp);
        fontIconDrawable.color(colorInt);
        fontIconDrawable.setStyle(Paint.Style.FILL);
        return fontIconDrawable;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
