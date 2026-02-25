package com.example.mytest;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

public class circle implements Transformation {

    @Override
    public Bitmap transform(Bitmap source) {
        // Ensure the source bitmap is not null
        if (source == null) return null;

        // Calculate the radius to make the transformation circular
        int size = Math.min(source.getWidth(), source.getHeight()) ;
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        // Create a new bitmap with a transparent background
        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        // Create a new bitmap with a circular shape
        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());
        // Create a canvas and draw a circle on it
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);
        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);

        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circle";
    }
}
