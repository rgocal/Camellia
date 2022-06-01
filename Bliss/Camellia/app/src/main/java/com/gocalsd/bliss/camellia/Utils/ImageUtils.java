package com.gocalsd.bliss.camellia.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.List;

public class ImageUtils {

    /**
     * Converts a drawable to a bitmap
     *
     * @param drawable a drawable
     * @return a bitmap
     */
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Iterates through each pixel in a Bitmap and determines
     * whether it has any transparent parts.
     *
     * @param bitmap a bitmap
     * @return true if any part of the bitmap is transparent
     */
    public static boolean hasTransparency(Bitmap bitmap) {
        for (int y = 0; y < bitmap.getWidth(); y++) {
            for (int x = 0; x < bitmap.getHeight(); x++) {
                if (Color.alpha(bitmap.getPixel(x, y)) < 255)
                    return true;
            }
        }

        return false;
    }

    /**
     * Removes the shadow (and any other transparent parts)
     * from a bitmap.
     *
     * @param bitmap the original bitmap
     * @return the bitmap with the shadow removed
     */
    public static Bitmap removeShadow(Bitmap bitmap) {
        if (!bitmap.isMutable())
            bitmap = bitmap.copy(bitmap.getConfig(), true);

        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        for (int i = 0; i < pixels.length; i++) {
            if (Color.alpha(pixels[i]) < 255)
                pixels[i] = Color.TRANSPARENT;
        }

        bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return bitmap;
    }

    public static Drawable scaleImage (Context context, Drawable image, float scaleFactor) {

        if ((image == null) || !(image instanceof BitmapDrawable)) {
            return image;
        }

        Bitmap b = ((BitmapDrawable)image).getBitmap();

        int sizeX = Math.round(image.getIntrinsicWidth() * scaleFactor);
        int sizeY = Math.round(image.getIntrinsicHeight() * scaleFactor);

        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, sizeX, sizeY, false);
        image = new BitmapDrawable(context.getResources(), bitmapResized);
        return image;
    }

    public static int getDrawableColor(Drawable drawable) {
        Bitmap iconBm = ImageUtils.drawableToBitmap(drawable);
        try {
            return ColorProvider.getDominantColor(iconBm);
        }catch (IllegalArgumentException e){
            return Color.WHITE;
        }
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    public Bitmap mergeThemAll(List<Bitmap> orderImagesList) {
        Bitmap result = null;
        if (orderImagesList != null && orderImagesList.size() > 0) {
            // if two images > increase the width only
            if (orderImagesList.size() == 2)
                result = Bitmap.createBitmap(orderImagesList.get(0).getWidth() * 2, orderImagesList.get(0).getHeight(), Bitmap.Config.ARGB_8888);
                // increase the width and height
            else if (orderImagesList.size() > 2)
                result = Bitmap.createBitmap(orderImagesList.get(0).getWidth() * 2, orderImagesList.get(0).getHeight() * 2, Bitmap.Config.ARGB_8888);
            else // don't increase anything
                result = Bitmap.createBitmap(orderImagesList.get(0).getWidth(), orderImagesList.get(0).getHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            for (int i = 0; i < orderImagesList.size(); i++) {
                canvas.drawBitmap(orderImagesList.get(i), orderImagesList.get(i).getWidth() * (i % 2), orderImagesList.get(i).getHeight() * (i / 2), paint);
            }
        } else {
            Log.e("MergeError", "Couldn't merge bitmaps");
        }
        return result;
    }

}
