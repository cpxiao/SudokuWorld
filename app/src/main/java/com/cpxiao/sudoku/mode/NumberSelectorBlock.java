package com.cpxiao.sudoku.mode;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.cpxiao.gamelib.mode.common.Sprite;

/**
 * @author cpxiao on 2017/10/25.
 */

public class NumberSelectorBlock extends Sprite {

    private int mValue;
    private int mColor;

    private int numberCount = 3;
    public boolean isShowNumberCount = false;

    public boolean isTip = true;
    public boolean isSelected = false;

    protected NumberSelectorBlock(Build build) {
        super(build);
        this.mValue = build.value;
        this.mColor = build.color;
    }

    public int getValue() {
        return mValue;
    }

    @Override
    public void onDraw(Canvas canvas, Paint paint) {
        super.onDraw(canvas, paint);
        paint.setColor(mColor);
        canvas.drawRect(getSpriteRectF(), paint);

        paint.setColor(Color.BLACK);
        paint.setTextSize(0.6F * getWidth());
        canvas.drawText("" + mValue, getCenterX(), getCenterY() + 0.4F * paint.getTextSize(), paint);

        if (isShowNumberCount) {
            paint.setTextSize(0.6F * 0.3F * getHeight());
            canvas.drawText("( " + numberCount + " )", getCenterX(), getCenterY() + 0.36F * getHeight() + 0.4F * paint.getTextSize(), paint);
        }

    }

    public static class Build extends Sprite.Build {

        private int value;
        private int color = Color.GREEN;

        public Build setValue(int value) {
            this.value = value;
            return this;
        }

        public Build setColor(int color) {
            this.color = color;
            return this;
        }

        public Sprite build() {
            return new NumberSelectorBlock(this);
        }
    }

}
