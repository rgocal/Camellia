package com.gocalsd.bliss.camellia.Icons;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;

import com.gocalsd.bliss.camellia.Utils.ImageUtils;
import com.gocalsd.bliss.camellia.Utils.PathUtils;

public class AdaptiveIconBitmap {
    public static final int PATH_CIRCLE = 0;
    public static final int PATH_SQUIRCLE = 1;
    public static final int PATH_ROUNDED_SQUARE = 2;
    public static final int PATH_SQUARE = 3;
    public static final int PATH_TEARDROP = 4;
    public static final int PATH_VESSEL = 5;
    public static final int PATH_TAPERED_RECT = 6;
    public static final int PATH_PEBBLE = 7;
    public static final int PATH_HEART = 8;
    public static final int PATH_CYLINDER = 9;
    public static final int PATH_SHIELD = 10;
    public static final int PATH_LEMON = 11;

    public static final String TITLE_CIRCLE = "Circle";
    public static final String TITLE_SQUIRCLE = "Squircle";
    public static final String TITLE_ROUNDED_SQUARE = "Rounded Square";
    public static final String TITLE_SQUARE = "Square";
    public static final String TITLE_TEARDROP = "Teardrop";
    public static final String TITLE_VESSEL = "Vessel";
    public static final String TITLE_INVADER = "Invader";
    public static final String TITLE_PEBBLE = "Pebble";
    public static final String TITLE_HEART = "Heart";
    public static final String TITLE_CYCLINDER = "Cylinder";
    public static final String TITLE_SHIELD = "Shield";
    public static final String TITLE_LEMON = "Lemon";


    Drawable fgDrawable, bgDrawable;
    Bitmap scaledBgBitmap, scaledFgBitmap, bgBitmap, fgBitmap;
    Path path, scaledPath;
    Rect pathSize;
    Paint paint;

    private double scale = 1.0;
    private int size;
    private float fgScale = 1;
    private float offsetX, offsetY;

    public AdaptiveIconBitmap() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);

        setScale(0.6);
        setPath(PATH_CIRCLE);

        size = 256;
    }

    public AdaptiveIconBitmap setForeground(Drawable drawable) {
        fgDrawable = drawable;
        return this;
    }

    public AdaptiveIconBitmap setBackground(Drawable drawable) {
        bgDrawable = drawable;
        return this;
    }

    public AdaptiveIconBitmap setDrawables(Drawable fgDrawableData, Drawable bgDrawableData) {
        fgDrawable = fgDrawableData;
        bgDrawable = bgDrawableData;
        return this;
    }

    @TargetApi(26)
    public AdaptiveIconBitmap setDrawable(AdaptiveIconDrawable drawable) {
        fgDrawable = drawable.getForeground();
        bgDrawable = drawable.getBackground();
        return this;
    }

    public AdaptiveIconBitmap setScale(double scaleData) {
        scale = scaleData;
        return this;
    }

    public AdaptiveIconBitmap setSize(int newSize) {
        size = newSize;
        return this;
    }

    public Bitmap getFgBitmap() {
        if (fgBitmap == null)
            fgBitmap = ImageUtils.drawableToBitmap(fgDrawable);
        return fgBitmap;
    }

    public Bitmap getBgBitmap() {
        if (bgBitmap == null)
            bgBitmap = ImageUtils.drawableToBitmap(bgDrawable);
        return bgBitmap;
    }

    public AdaptiveIconBitmap setPath(int pathType) {
        path = new Path();
        pathSize = new Rect(0, 0, 50, 50);
        switch (pathType) {
            case PATH_CIRCLE:
                path.arcTo(new RectF(pathSize), 0, 359);
                path.close();
                break;
            case PATH_SQUIRCLE:
                setPath("M 50,0 C 10,0 0,10 0,50 C 0,90 10,100 50,100 C 90,100 100,90 100,50 C 100,10 90,0 50,0 Z");
                break;
            case PATH_ROUNDED_SQUARE:
                setPath("M 50,0 L 70,0 A 30,30,0,0 1 100,30 L 100,70 A 30,30,0,0 1 70,100 L 30,100 A 30,30,0,0 1 0,70 L 0,30 A 30,30,0,0 1 30,0 z");
                break;
            case PATH_SQUARE:
                path.lineTo(0, 50);
                path.lineTo(50, 50);
                path.lineTo(50, 0);
                path.lineTo(0, 0);
                path.close();
                break;
            case PATH_TEARDROP:
                setPath("M 50,0 A 50,50,0,0 1 100,50 L 100,85 A 15,15,0,0 1 85,100 L 50,100 A 50,50,0,0 1 50,0 z");
                break;
            case PATH_VESSEL:
                setPath("M 12.97,0 C 8.41,0 4.14,2.55 2.21,6.68 -1.03,13.61 -0.71,21.78 3.16,28.46 4.89,31.46 4.89,35.2 3.16,38.2 -1.05,45.48 -1.05,54.52 3.16,61.8 4.89,64.8 4.89,68.54 3.16,71.54 -0.71,78.22 -1.03,86.39 2.21,93.32 4.14,97.45 8.41,100 12.97,100 21.38,100 78.62,100 87.03,100 91.59,100 95.85,97.45 97.79,93.32 101.02,86.39 100.71,78.22 96.84,71.54 95.1,68.54 95.1,64.8 96.84,61.8 101.05,54.52 101.05,45.48 96.84,38.2 95.1,35.2 95.1,31.46 96.84,28.46 100.71,21.78 101.02,13.61 97.79,6.68 95.85,2.55 91.59,0 87.03,0 78.62,0 21.38,0 12.97,0 Z");
                break;
            case PATH_TAPERED_RECT:
                path.lineTo(15, 50);
                path.lineTo(50, 50);
                path.lineTo(50, 15);
                path.close();
                break;
            case PATH_PEBBLE:
                setPath("M M 55,0 C 25,0 0,25 0,50 0,78 28,100 55,100 85,100 100,85 100,58 100,30 86,0 55,0 Z");
                break;
            case PATH_HEART:
                setPath("M 50,20 C 45,0 30,0 25,0 20,0 0,5 0,34 0,72 40,97 50,100 60,97 100,72 100,34 100,5 80,0 75,0 70,0 55,0 50,20 Z");
                break;
            case PATH_CYLINDER:
                setPath("M50,0A50,30 0,0,1 100,30V70A50,30 0,0,1 0,70V30A50,30 0,0,1 50,0z");
                break;
            case PATH_SHIELD:
                setPath("m6.6146,13.2292a6.6146,6.6146 0,0 0,6.6146 -6.6146v-5.3645c0,-0.6925 -0.5576,-1.25 -1.2501,-1.25L6.6146,-0 1.2501,-0C0.5576,0 0,0.5575 0,1.25v5.3645A6.6146,6.6146 0,0 0,6.6146 13.2292Z");
                break;
            case PATH_LEMON:
                setPath("M1.2501,0C0.5576,0 0,0.5576 0,1.2501L0,6.6146A6.6146,6.6146 135,0 0,6.6146 13.2292L11.9791,13.2292C12.6716,13.2292 13.2292,12.6716 13.2292,11.9791L13.2292,6.6146A6.6146,6.6146 45,0 0,6.6146 0L1.2501,0z");
                break;
        }
        return this;
    }

    public Path getPath(){
        return path;
    }

    public AdaptiveIconBitmap setPath(String pathData) {
        path = PathUtils.createPathFromPathData(pathData);
        pathSize = new Rect(0, 0, 100, 100);
        return this;
    }

    private Boolean isPrepared() {
        if (path != null && pathSize != null) {
            return true;
        }
        return false;
    }

    private Boolean isScaled(int width, int height) {
        return scaledBgBitmap != null && (getFgBitmap() == null || scaledFgBitmap != null) && scaledPath != null;
    }

    private Path getScaledPath(Path origPath, Rect origRect, int width, int height) {
        Rect newRect = new Rect(0, 0, width, height);
        int origWidth = origRect.right - origRect.left;
        int origHeight = origRect.bottom - origRect.top;

        Matrix matrix = new Matrix();
        matrix.postScale((float) (newRect.right - newRect.left) / origWidth, (float) (newRect.bottom - newRect.top) / origHeight);

        Path newPath = new Path();
        origPath.transform(matrix, newPath);
        return newPath;
    }

    private Bitmap getScaledBitmap(Bitmap bitmap, int width, int height) {
        if (scale <= 1)
            return ThumbnailUtils.extractThumbnail(bitmap, (int) ((2 - scale) * width), (int) ((2 - scale) * height));
        else if (bitmap.getWidth() > 1 && bitmap.getHeight() > 1) {
            int widthMargin = (int) ((scale - 1) * width);
            int heightMargin = (int) ((scale - 1) * height);

            if (widthMargin > 0 && heightMargin > 0) {
                Bitmap source = ThumbnailUtils.extractThumbnail(bitmap, (int) ((2 - scale) * width), (int) ((2 - scale) * height));
                int dWidth = width + widthMargin;
                int dHeight = height + heightMargin;
                bitmap = Bitmap.createBitmap(dWidth, dHeight, bitmap.getConfig());
                Canvas canvas = new Canvas(bitmap);
                canvas.drawBitmap(source, (dWidth - source.getWidth()) / 2, (dHeight - source.getHeight()) / 2, new Paint());
                return bitmap;
            }
        } else if (bitmap.getWidth() > 0 && bitmap.getHeight() > 0)
            return ThumbnailUtils.extractThumbnail(bitmap, width, height);

        return null;
    }

    public Bitmap render() {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        if (isPrepared()) {
            if (!isScaled(canvas.getWidth(), canvas.getHeight())) {
                scaledPath = getScaledPath(path, pathSize, size, size);
                if (getBgBitmap() != null) {
                    scaledBgBitmap = getScaledBitmap(getBgBitmap(), size, size);
                    scaledFgBitmap = getScaledBitmap(getFgBitmap(), size, size);
                } else if (getFgBitmap() != null)
                    scaledFgBitmap = ThumbnailUtils.extractThumbnail(getFgBitmap(), size, size);
            }

            if (scaledBgBitmap != null) {
                float dx = size * offsetX * 0.066f;
                float dy = size * offsetY * 0.066f;
                if (scaledBgBitmap.getWidth() > size && scaledBgBitmap.getHeight() > size)
                    canvas.scale(2 - ((fgScale + 1) / 2), 2 - ((fgScale + 1) / 2), size / 2, size / 2);
                else {
                    dx = 0;
                    dy = 0;
                }

                float marginX = (scaledBgBitmap.getWidth() - size) / 2;
                float marginY = (scaledBgBitmap.getHeight() - size) / 2;
                canvas.drawBitmap(scaledBgBitmap, dx - marginX, dy - marginY, paint);
            }

            if (scaledFgBitmap != null) {
                canvas.scale(2 - fgScale, 2 - fgScale, size / 2, size / 2);
                float dx = ((size - scaledFgBitmap.getWidth()) / 2) + (size * offsetX * 0.188f);
                float dy = ((size - scaledFgBitmap.getHeight()) / 2) + (size * offsetY * 0.188f);
                canvas.drawBitmap(scaledFgBitmap, dx, dy, paint);
                canvas.scale(fgScale + 1, fgScale + 1, size / 2, size / 2);
            }

            Paint bitmapPaint = new Paint();
            bitmapPaint.setAntiAlias(true);
            bitmapPaint.setShader(new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
            Bitmap iconBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            Canvas iconCanvas = new Canvas(iconBitmap);
            iconCanvas.drawPath(scaledPath, bitmapPaint);

            return iconBitmap;
        }
        return null;
    }
}