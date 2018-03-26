package com.cpxiao.sudoku.mode;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.cpxiao.androidutils.library.utils.PaintUtil;
import com.cpxiao.gamelib.mode.common.Sprite;

/**
 * @author cpxiao on 2017/10/25.
 */

public class ToolBtn extends Sprite {

    private int mColorBg;
    private int mColorText;
    private String mText;
    private boolean isSelected = false;

    protected ToolBtn(Build build) {
        super(build);
        this.mColorBg = build.colorBg;
        this.mColorText = build.colorText;
        this.mText = build.text;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public void onDraw(Canvas canvas, Paint paint) {
        super.onDraw(canvas, paint);
        paint.setColor(mColorBg);
        if (isSelected) {
            paint.setColor(Color.YELLOW);
        }
        canvas.drawRect(getSpriteRectF(), paint);

        paint.setColor(mColorText);
        paint.setTextSize(0.5F * getWidth());
        paint.setTextSize(PaintUtil.calculateTextSize(mText, getWidth()));
        canvas.drawText(mText, getCenterX(), getCenterY() + 0.4F * paint.getTextSize(), paint);
    }

    public static class Build extends Sprite.Build {

        private int colorBg = Color.BLACK;
        private int colorText = Color.WHITE;
        private String text = "";

        public Build setColorBg(int color) {
            this.colorBg = color;
            return this;
        }

        public Build setColorText(int color) {
            this.colorText = color;
            return this;
        }

        public Build setText(String text) {
            this.text = text;
            return this;
        }

        public Sprite build() {
            return new ToolBtn(this);
        }
    }

}
