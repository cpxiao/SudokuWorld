package com.cpxiao.sudoku.mode;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.cpxiao.gamelib.mode.common.Sprite;

/**
 * @author cpxiao on 2017/10/25.
 */

public class SudokuLine extends Sprite {

    private int mColor;

    protected SudokuLine(Build build) {
        super(build);
        this.mColor = build.color;
    }

    @Override
    public void onDraw(Canvas canvas, Paint paint) {
        super.onDraw(canvas, paint);
        paint.setColor(mColor);
        canvas.drawRect(getSpriteRectF(), paint);
    }

    public static class Build extends Sprite.Build {

        private int color = Color.BLACK;

        public Build setColor(int color) {
            this.color = color;
            return this;
        }

        public Sprite build() {
            return new SudokuLine(this);
        }
    }

}
