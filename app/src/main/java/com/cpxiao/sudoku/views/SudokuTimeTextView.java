package com.cpxiao.sudoku.views;

import android.content.Context;
import android.util.AttributeSet;

import com.cpxiao.gamelib.views.TimeTextView;

import java.text.DecimalFormat;

/**
 * @author cpxiao on 2017/9/5.
 */
public class SudokuTimeTextView extends TimeTextView {

    public SudokuTimeTextView(Context context) {
        super(context);
    }

    public SudokuTimeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected String timeFormat(long time) {
        int hour = (int) (time / 3600000);
        int min = (int) ((time / 60000) % 60);
        int second = (int) ((time / 1000) % 60);
        DecimalFormat df = new DecimalFormat("00");
        return df.format(hour) + ":" + df.format(min) + ":" + df.format(second);
    }


}