package com.cpxiao.sudoku.mode;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.cpxiao.gamelib.mode.common.Sprite;

/**
 * @author cpxiao on 2017/10/25.
 */

public class SudokuSmallBlock extends Sprite {
    private int mNumberCountX, mNumberCountY;

    private static final int state_default = 1;
    private static final int state_show_value = 2;
    private static final int state_show_selected_value = 3;
    private int state;

    private int mValue;
    private int[] mSelectedValueArray;
    private int indexX, indexY;

    private boolean isSelected = false;

    protected SudokuSmallBlock(Build build) {
        super(build);
        mValue = build.value;
        if (mValue > 0) {
            state = state_default;
        }
        this.indexX = build.indexX;
        this.indexY = build.indexY;

        this.mNumberCountX = build.numberCountX;
        this.mNumberCountY = build.numberCountY;
        this.mSelectedValueArray = build.selectedNumberArray;
    }

    public void updateSelectedValueArray(int value) {
        int index = value - 1;
        mSelectedValueArray[index] = value;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setValue(int value) {
        mValue = value;
    }

    public int getValue() {
        return mValue;
    }

    public int getIndexX() {
        return indexX;
    }

    public int getIndexY() {
        return indexY;
    }

    @Override
    protected void beforeDraw(Canvas canvas, Paint paint) {
        super.beforeDraw(canvas, paint);
    }

    @Override
    public void onDraw(Canvas canvas, Paint paint) {
        super.onDraw(canvas, paint);
        paint.setColor(Color.GRAY);

        if (isSelected) {
            paint.setColor(Color.YELLOW);
        }
        canvas.drawRect(getSpriteRectF(), paint);

        paint.setColor(Color.BLACK);
        paint.setTextSize(0.6F * getWidth());
        if (state == state_default) {
            if (mValue > 0) {
                canvas.drawText("" + mValue, getCenterX(), getCenterY() + 0.4F * paint.getTextSize(), paint);
            }
        } else if (state == state_show_value) {
            if (mValue > 0) {
                canvas.drawText("" + mValue, getCenterX(), getCenterY() + 0.4F * paint.getTextSize(), paint);
            }
        } else if (state == state_show_selected_value) {
            paint.setTextSize(0.9F * Math.min(getWidth() / mNumberCountX, getHeight() / mNumberCountY));
            for (int i = 0; i < mNumberCountX * mNumberCountY; i++) {
                int indexX = i % mNumberCountX;
                int indexY = i / mNumberCountX;
                int value = mSelectedValueArray[i];
                if (value <= 0) {
                    continue;
                }
                String text = "" + value;
                float cX = getX() + (0.5F + indexX) * getWidth() / mNumberCountX;
                float cY = getY() + (0.5F + indexY) * getHeight() / mNumberCountY + 0.4F * paint.getTextSize();
                canvas.drawText(text, cX, cY, paint);
            }

        }
    }

    public boolean isDefault() {
        return state == state_default;
    }

    public void showValue() {
        state = state_show_value;
    }

    public void showSelectedNumber() {
        state = state_show_selected_value;
    }

    public void clearSelectedNumber() {
        for (int i = 0; i < mNumberCountX * mNumberCountY; i++) {
            mSelectedValueArray[i] = 0;
        }
    }

    public void clearNumber() {
        mValue = 0;
    }

    public static class Build extends Sprite.Build {
        private int value = 0;
        private int indexX, indexY;
        private int numberCountX, numberCountY;
        private int[] selectedNumberArray;


        public Build setValue(int value) {
            this.value = value;
            return this;
        }

        public Build setIndexX(int indexX) {
            this.indexX = indexX;
            return this;
        }

        public Build setIndexY(int indexY) {
            this.indexY = indexY;
            return this;
        }

        public Build setNumberCountX(int numberCountXX) {
            this.numberCountX = numberCountXX;
            return this;
        }

        public Build setNumberCountY(int numberCountYY) {
            this.numberCountY = numberCountYY;
            return this;
        }

        public Build setSelectedNumberArray(int[] selectedNumberArray) {
            this.selectedNumberArray = selectedNumberArray;
            return this;
        }

        public Sprite build() {
            return new SudokuSmallBlock(this);
        }
    }

}
