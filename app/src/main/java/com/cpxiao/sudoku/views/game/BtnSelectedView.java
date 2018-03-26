package com.cpxiao.sudoku.views.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.cpxiao.R;
import com.cpxiao.AppConfig;
import com.cpxiao.sudoku.imps.OnToolViewClickListener;

/**
 * 功能按钮（提示、显示、标记、删除）
 *
 * @author cpxiao on 2017/3/16.
 */

public class BtnSelectedView extends View {

    private static final boolean DEBUG = AppConfig.DEBUG;
    private static final String TAG = BtnSelectedView.class.getSimpleName();

    private int mNumberCount = 4;

    private float mBtnTextSize;

    private Btn[] mBtnArray;

    private OnToolViewClickListener mOnToolViewClickListener;

    /**
     * 画笔
     */
    private static final Paint mBackgroundPaint = new Paint();
    private static final Paint mSelectedPaint = new Paint();
    private static final Paint mNumberPaint = new Paint();
    private static final Paint mSignPaint = new Paint();

    static {
        mBackgroundPaint.setAntiAlias(true);//抗锯齿

        mSelectedPaint.setAntiAlias(true);//抗锯齿

        mNumberPaint.setAntiAlias(true);//抗锯齿
        mNumberPaint.setStyle(Paint.Style.STROKE);
        mNumberPaint.setTextAlign(Paint.Align.CENTER);

        mSignPaint.setAntiAlias(true);//抗锯齿
        mSignPaint.setStyle(Paint.Style.STROKE);
        mSignPaint.setTextAlign(Paint.Align.CENTER);
    }

    private BtnSelectedView(Context context) {
        this(context, null);
    }

    private BtnSelectedView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BtnSelectedView(Context context, int numberCount) {
        this(context);
        if (numberCount <= 0) {
            if (DEBUG) {
                throw new IllegalArgumentException("error! countX <= 0 || countY <= 0");
            }
        }
        this.mNumberCount = numberCount;
        this.mNumberCount = 4;

    }

    private void initPaints() {

        mBackgroundPaint.setColor(ContextCompat.getColor(getContext(), R.color.sudoku_keyboard_btn_bg));

        mSelectedPaint.setColor(ContextCompat.getColor(getContext(), R.color.sudoku_keyboard_btn_bg_selected));

        mNumberPaint.setColor(ContextCompat.getColor(getContext(), R.color.sudoku_keyboard_number));
        mNumberPaint.setTextSize(mBtnTextSize);

        mSignPaint.setColor(ContextCompat.getColor(getContext(), R.color.sudoku_keyboard_sign));
        mSignPaint.setTextSize(mBtnTextSize);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        float btnOuterW = 1f * w / mNumberCount;
        float btnOuterH = h;

        float aspectRatio = 1.618F;//宽高比
        float btnRatio = 0.95F;
        float btnInnerW = btnRatio * Math.min(btnOuterW, aspectRatio * btnOuterH);
        float btnInnerH = btnRatio * Math.min(btnOuterW / aspectRatio, btnOuterH);

        mBtnTextSize = 0.3F * Math.min(btnOuterW, btnOuterH);
        if (DEBUG) {
            Log.d(TAG, "onSizeChanged: mBtnTextSize = " + mBtnTextSize);
        }

        float signPositionDelta = 0.2F * Math.min(btnInnerW, btnInnerH);

        mBtnArray = new Btn[mNumberCount];
        for (int i = 0; i < mNumberCount; i++) {
            RectF outRectF = new RectF();
            outRectF.left = i * btnOuterW;
            outRectF.right = outRectF.left + btnOuterW;
            outRectF.top = 0;
            outRectF.bottom = outRectF.top + btnOuterH;

            RectF innerRectF = new RectF();
            innerRectF.left = (outRectF.left + outRectF.right) / 2 - btnInnerW / 2;
            innerRectF.right = innerRectF.left + btnInnerW;
            innerRectF.top = (outRectF.top + outRectF.bottom) / 2 - btnInnerH / 2;
            innerRectF.bottom = innerRectF.top + btnInnerH;

            mBtnArray[i] = new Btn(btnOuterW, btnOuterH, btnInnerW, btnInnerH, 0);
            mBtnArray[i].number = i;
            mBtnArray[i].mSignCX = innerRectF.right - signPositionDelta;
            mBtnArray[i].mSignCY = innerRectF.top + signPositionDelta;
            mBtnArray[i].mNumberOuterRect = outRectF;
            mBtnArray[i].mNumberInnerRect = innerRectF;
        }

        initPaints();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (DEBUG) {
            Log.d(TAG, "onDraw: ......");
        }
        super.onDraw(canvas);

        for (int i = 0; i < mNumberCount; i++) {
            mBtnArray[i].draw(canvas);
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        for (int i = 0; i < mNumberCount; i++) {
            if (mBtnArray[i].isSelected(eventX, eventY)) {
                if (mOnToolViewClickListener != null) {
                    if (i == 3) {
                        mOnToolViewClickListener.onDeleteNumberClick();
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mBtnArray[i].mIsSelected = false;
                }
            }
        }
        invalidate();
        //        return super.onTouchEvent(event);
        return true;
    }

    public void setOnToolViewClickListener(OnToolViewClickListener listener) {
        mOnToolViewClickListener = listener;
    }

    private class Btn {
        int number;
        private RectF mNumberInnerRect;
        private RectF mNumberOuterRect;
        private float mBtnOuterW, mBtnOuterH;
        private float mBtnInnerW, mBtnInnerH;
        private float mSignCX, mSignCY, mSignR;
        private boolean mIsSelected = false;

        private Btn(float mBtnOuterW, float mBtnOuterH, float mBtnInnerW, float mBtnInnerH, float mSignR) {
            this.mBtnOuterW = mBtnOuterW;
            this.mBtnOuterH = mBtnOuterH;
            this.mBtnInnerW = mBtnInnerW;
            this.mBtnInnerH = mBtnInnerH;
            this.mSignR = mSignR;
        }

        private boolean isSelected(float x, float y) {
            mIsSelected = (x >= mNumberInnerRect.left && x <= mNumberInnerRect.right &&
                    y >= mNumberInnerRect.top && y <= mNumberInnerRect.bottom);
            return mIsSelected;
        }

        private void draw(Canvas canvas) {
            //只绘制删除按钮
            if (number != 3) {
                return;
            }

            //            drawOuter(canvas);
            if (mIsSelected) {
                drawInner(canvas, mSelectedPaint);
            } else {
                drawInner(canvas, mBackgroundPaint);
            }
            drawNumber(canvas);
            drawSign(canvas);
        }

        private void drawOuter(Canvas canvas) {
            canvas.drawRoundRect(mNumberOuterRect, mBtnOuterW / 10, mBtnOuterH / 10, mBackgroundPaint);
        }

        private void drawInner(Canvas canvas, Paint paint) {
            canvas.drawRoundRect(mNumberInnerRect, mBtnInnerW / 10, mBtnInnerH / 10, paint);
        }

        private void drawNumber(Canvas canvas) {

            Paint.FontMetrics numFm = mNumberPaint.getFontMetrics();
            float numFmX = mBtnInnerW / 2;
            float numFmY = mBtnInnerH / 2 - (numFm.ascent + numFm.descent) / 2;

            float numX = mNumberInnerRect.left + numFmX;
            float numY = mNumberInnerRect.top + numFmY;


            String numText;
            if (number == 0) {
                numText = getContext().getString(R.string.tools_btn_tips);
            } else if (number == 1) {
                numText = getContext().getString(R.string.tools_btn_show);
            } else if (number == 2) {
                numText = getContext().getString(R.string.tools_btn_sign);
            } else if (number == 3) {
                numText = getContext().getString(R.string.tools_btn_delete);
            } else {
                if (DEBUG) {
                    throw new IllegalArgumentException("number error! number = " + number);
                }
                numText = "";
            }
            canvas.drawText(numText, numX, numY, mNumberPaint);
        }

        private void drawSign(Canvas canvas) {
            canvas.drawCircle(mSignCX, mSignCY, mSignR, mSignPaint);
        }

    }
}
