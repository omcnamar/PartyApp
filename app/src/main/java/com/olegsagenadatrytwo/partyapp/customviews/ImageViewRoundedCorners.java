package com.olegsagenadatrytwo.partyapp.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by Admin on 9/21/2017.
 */

public class ImageViewRoundedCorners extends android.support.v7.widget.AppCompatImageView {

    public static float radius = 18.0f;

    public ImageViewRoundedCorners(Context context) {
        super(context);
    }

    public ImageViewRoundedCorners(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewRoundedCorners(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //float radius = 36.0f;
        Path clipPath = new Path();
        RectF rect = new RectF(0, 0, this.getWidth(), this.getHeight());
        clipPath.addRoundRect(rect, radius, radius, Path.Direction.CW);
        canvas.clipPath(clipPath);
        super.onDraw(canvas);
    }
}
