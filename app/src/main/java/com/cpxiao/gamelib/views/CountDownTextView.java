package com.cpxiao.gamelib.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import static android.R.attr.angle;

/**
 * 倒计时控件
 *
 * @author cpxiao on 2017/6/6.
 */
public class CountDownTextView extends TimeTextView {

    private boolean isTimeUp = false;//是否time up，防止多次回调timeUp()方法
    private OnTimeUpListener mOnTimeUpListener;
    private static final int[] mAttr = {angle};
    private static final int ATTR_ANDROID_ANGLE = 0;

    /**
     * 倒计时
     *
     * @param context context
     * @param time    time
     */
    public CountDownTextView(Context context, long time) {
        super(context);
        setTimeMillis(time);
        setText(timeFormat(time));
    }

    public CountDownTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, mAttr);
        long time = (long) typedArray.getFloat(ATTR_ANDROID_ANGLE, 0f);
        setTimeMillis(time);
        typedArray.recycle();

        setText(timeFormat(time));
    }

    /**
     * 重置时间
     *
     * @param time time
     */
    public void resetTime(long time) {
        pause();
        super.setTimeMillis(time);
        isTimeUp = false;
        setText(timeFormat(time));
    }

    @Override
    protected void updateTime() {
        long time = getTimeMillis() - mInterval;
        setTimeMillis(time);
    }

    @Override
    protected String timeFormat(long time) {
        if (time < 0) {
            time = 0;
            if (!isTimeUp && mOnTimeUpListener != null) {
                isTimeUp = true;
                mOnTimeUpListener.timeUp();
            }
            cancelTimeTask();
        }
        return super.timeFormat(time);
    }


    public void setOnTimeUpListener(OnTimeUpListener listener) {
        mOnTimeUpListener = listener;
    }

    public interface OnTimeUpListener {
        void timeUp();
    }
}