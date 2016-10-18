/*
 * Copyright (c) 2016 Federico Paolinelli.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.whiterabbit.pisabike.ui;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.util.LruCache;

import com.whiterabbit.pisabike.R;


public class MapMarkerFactory {
    private LruCache<String, Bitmap> mMemoryCache;

    public MapMarkerFactory() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {

                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public Bitmap getMapMarker(long freeBikes, long allBikes, Context context, boolean selected) {
        String cacheKey = String.valueOf(freeBikes) + "|" + String.valueOf(allBikes)
                                                    + (selected ? "Y" : "N");

        Bitmap res = mMemoryCache.get(cacheKey);
        if (res != null) {
            return res;
        }

        Resources resources = context.getResources();
        float scale = resources.getDisplayMetrics().density;


        Bitmap originalBitmap = selected ?
                BitmapFactory.decodeResource(resources, R.drawable.marker_selcted) :
                BitmapFactory.decodeResource(resources, R.drawable.marker);

        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        int width = (int) (36 * scale);
        int height = width;
        Bitmap bitmap = originalBitmap.copy(conf, true);

        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setColor(Color.WHITE);
        paint.setTextSize((int) (14 * scale));
        paint.setShadowLayer(1f, 0f, 1f, Color.BLACK);

        // draw text to the Canvas center
        Rect bounds = new Rect();
        String text = String.valueOf(freeBikes);
        paint.getTextBounds(text, 0, text.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width()) / 2;
        int y = (height + bounds.height())/2;


        canvas.drawText(text, x, y, paint);

        int tick = (int) (4 * scale);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Paint arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            arcPaint.setColor(Color.GREEN);
            arcPaint.setStrokeWidth(2 * scale);
            arcPaint.setStyle(Paint.Style.STROKE);
            final RectF oval = new RectF();
            oval.set(4 * scale + tick, tick, width + 4 * scale - tick, width - tick);
            float ratio = (float) freeBikes / allBikes;
            canvas.drawArc(oval,
                            90, 360 * ratio, false, arcPaint);
        }

        mMemoryCache.put(cacheKey, bitmap);

        return bitmap;
    }

    public Bitmap getSelectedMapMarker(long freeBikes, long allBikes, Context context) {
        return getMapMarker(freeBikes, allBikes, context, true);
    }

    public Bitmap getNotSelectedMapMarker(long freeBikes, long allBikes, Context context) {
        return getMapMarker(freeBikes, allBikes, context, false);
    }

}
