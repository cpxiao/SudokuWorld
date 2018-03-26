package com.cpxiao.sudoku.views.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.cpxiao.R;
import com.cpxiao.androidutils.library.utils.PreferencesUtils;
import com.cpxiao.AppConfig;
import com.cpxiao.sudoku.controller.GameController;
import com.cpxiao.sudoku.controller.NumberType;
import com.cpxiao.sudoku.imps.OnGameListener;
import com.cpxiao.sudoku.mode.extra.Extra;

/**
 * SudokuView
 *
 * @author cpxiao on 2016/3/6.
 */
public class SudokuView extends View {

    private static final boolean DEBUG = AppConfig.DEBUG;
    private final String TAG = getClass().getSimpleName();

    private boolean isSuccess = false;
    public int mSelectedX = -1;
    public int mSelectedY = -1;
    /**
     * 居中或者fit_xy
     */
    public static final int SCALE_TYPE_CENTER = 1;
    public static final int SCALE_TYPE_FIT_XY = 2;
    private int mScaleType = SCALE_TYPE_FIT_XY;

    /**
     * 游戏控制器
     */
    private GameController mGameController;

    /**
     * 方块宽高
     */
    private float mLBlockW;
    private float mLBlockH;
    private float mMBlockW;
    private float mMBlockH;
    private float mSBlockW;
    private float mSBlockH;
    /**
     * padding值
     */
    private float mPaddingLR;
    private float mPaddingTB;
    /**
     * 数字size及粗线width
     */
    private static final float NUMBER_TEXT_SIZE_PERCENT = 0.618f;//占比
    private float mNumberLength;
    private float mSmallNumberLength;
    private float mBoldLineWidth;

    /**
     * 画笔
     */
    private static final Paint mBackgroundPaint = new Paint();
    private static final Paint mLinePaint = new Paint();
    private static final Paint mInitialNumberPaint = new Paint();
    private static final Paint mNumberPaint = new Paint();
    private static final Paint mSmallNumberPaint = new Paint();

    private OnGameListener mOnGameListener;

    /**
     * canvas抗锯齿
     */
    private PaintFlagsDrawFilter mPaintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);


    static {
        mBackgroundPaint.setAntiAlias(true);//抗锯齿

        mLinePaint.setAntiAlias(true);//抗锯齿
        mLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mLinePaint.setStrokeWidth(2.0f);

        mInitialNumberPaint.setAntiAlias(true);//抗锯齿
        mInitialNumberPaint.setStyle(Paint.Style.STROKE);
        mInitialNumberPaint.setTextAlign(Paint.Align.CENTER);

        mNumberPaint.setAntiAlias(true);//抗锯齿
        mNumberPaint.setStyle(Paint.Style.STROKE);
        mNumberPaint.setTextAlign(Paint.Align.CENTER);

        mSmallNumberPaint.setAntiAlias(true);//抗锯齿
        mSmallNumberPaint.setStyle(Paint.Style.STROKE);
        mSmallNumberPaint.setTextAlign(Paint.Align.CENTER);
    }

    public SudokuView(Context context) {
        this(context, SCALE_TYPE_FIT_XY);
    }

    public SudokuView(Context context, int scaleType) {
        super(context);
        mScaleType = scaleType;
    }

    public void setGameController(GameController controller) {
        mGameController = controller;
    }


    public void setOnGameListener(OnGameListener listener) {
        mOnGameListener = listener;
    }

    private void initPaints() {

        mLinePaint.setColor(ContextCompat.getColor(getContext(), R.color.sudoku_line));

        mInitialNumberPaint.setColor(ContextCompat.getColor(getContext(), R.color.sudoku_number_initial));
        mInitialNumberPaint.setTextSize(NUMBER_TEXT_SIZE_PERCENT * mNumberLength);

        mNumberPaint.setColor(ContextCompat.getColor(getContext(), R.color.sudoku_number_setup));
        mNumberPaint.setTextSize(NUMBER_TEXT_SIZE_PERCENT * mNumberLength);

        mSmallNumberPaint.setColor(ContextCompat.getColor(getContext(), R.color.sudoku_number_setup));
        mSmallNumberPaint.setTextSize(NUMBER_TEXT_SIZE_PERCENT * mSmallNumberLength);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        if (mGameController != null) {
            if (mScaleType == SCALE_TYPE_FIT_XY) {
                mLBlockW = w;
                mLBlockH = h;
            } else if (mScaleType == SCALE_TYPE_CENTER) {
                mLBlockW = mLBlockH = Math.min(w, h);
            }
            mMBlockW = mLBlockW / mGameController.mBigBlockCountX;
            mMBlockH = mLBlockH / mGameController.mBigBlockCountY;
            mSBlockW = mMBlockW / mGameController.mBigBlockCountY;
            mSBlockH = mMBlockH / mGameController.mBigBlockCountX;

            mNumberLength = Math.min(mSBlockW, mSBlockH);
            mSmallNumberLength = Math.min(mSBlockW / mGameController.mBigBlockCountY, mSBlockH / mGameController.mBigBlockCountX);
            mBoldLineWidth = Math.max(2.0f, mLBlockW / 250);

            mPaddingLR = ((float) w - mLBlockW) / 2;
            mPaddingTB = ((float) h - mLBlockH) / 2;
        }
        if (DEBUG) {
            Log.d(TAG, "W = " + w + ", H = " + h);
            Log.d(TAG, "mLBlockW = " + mLBlockW + ",mLBlockH = " + mLBlockH);
            Log.d(TAG, "mMBlockW = " + mMBlockW + ",mMBlockH = " + mMBlockH);
            Log.d(TAG, "mSBlockW = " + mSBlockW + ",mSBlockH = " + mSBlockH);
        }
        initPaints();
        super.onSizeChanged(w, h, oldW, oldH);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //抗锯齿
        canvas.setDrawFilter(mPaintFlagsDrawFilter);

        drawBackground(canvas);

        drawNumbers(canvas);

    }

    /**
     * 绘制背景
     *
     * @param canvas canvas
     */
    private void drawBackground(Canvas canvas) {
        if (mGameController == null) {
            return;
        }
        //        /* 绘制背景色(分区块绘制)*/
        //        for (int i = 0; i < mGameController.mBigBlockCountX; ++i) {
        //            for (int j = 0; j < mGameController.mBigBlockCountY; ++j) {
        //                float l = mPaddingLR + mSBlockW * i * mGameController.mBigBlockCountY;
        //                float t = mPaddingTB + mSBlockH * j * mGameController.mBigBlockCountX;
        //                float r = l + mMBlockW;
        //                float b = t + mMBlockH;
        //                if ((i + j) % 2 == 0) {
        //                    mBackgroundPaint.setColor(ContextCompat.getColor(getContext(), R.color.sudoku_background_gray));
        //                } else {
        //                    mBackgroundPaint.setColor(ContextCompat.getColor(getContext(), R.color.sudoku_background_white));
        //                }
        //                canvas.drawRect(l, t, r, b, mBackgroundPaint);
        //            }
        //        }

        /* 绘制背景色(每个数字的背景)*/
        int indexSelectedX = mSelectedX / mGameController.mBigBlockCountY;
        int indexSelectedY = mSelectedY / mGameController.mBigBlockCountX;
        for (int x = 0; x < mGameController.mNumberCount; ++x) {
            for (int y = 0; y < mGameController.mNumberCount; ++y) {
                float l = mPaddingLR + mSBlockW * x;
                float t = mPaddingTB + mSBlockH * y;
                float r = l + mSBlockW;
                float b = t + mSBlockH;
                if (mGameController.isInitialValue(x, y)) {
                    mBackgroundPaint.setColor(ContextCompat.getColor(getContext(), R.color.sudoku_bg_initial));
                } else {
                    mBackgroundPaint.setColor(ContextCompat.getColor(getContext(), R.color.sudoku_bg_setup));
                }
                canvas.drawRect(l, t, r, b, mBackgroundPaint);

                /* 绘制背景色(选中数字的背景)*/
                if (mGameController.checkSelectedXY(mSelectedX, mSelectedY)) {
                    int indexX = x / mGameController.mBigBlockCountY;
                    int indexY = y / mGameController.mBigBlockCountX;
                    if (x == mSelectedX) {
                        mBackgroundPaint.setColor(ContextCompat.getColor(getContext(), R.color.sudoku_bg_selected));
                        canvas.drawRect(l, t, r, b, mBackgroundPaint);
                    }
                    if (y == mSelectedY) {
                        mBackgroundPaint.setColor(ContextCompat.getColor(getContext(), R.color.sudoku_bg_selected));
                        canvas.drawRect(l, t, r, b, mBackgroundPaint);
                    }
                    if (indexX == indexSelectedX && indexY == indexSelectedY) {
                        mBackgroundPaint.setColor(ContextCompat.getColor(getContext(), R.color.sudoku_bg_selected));
                        canvas.drawRect(l, t, r, b, mBackgroundPaint);
                    }
                }
            }
        }


        /* 横线(细线及粗线,最外测的粗线特殊处理)*/
        for (int i = 0; i <= mGameController.mNumberCount; ++i) {
            float startX = mPaddingLR + 0f;
            float endX = startX + mSBlockW * mGameController.mNumberCount;
            float Y = mPaddingTB + mSBlockH * i;
            if (i == 0) {
                canvas.drawRect(startX, Y, endX, Y + 2 * mBoldLineWidth, mLinePaint);
            } else if (i == mGameController.mNumberCount) {
                canvas.drawRect(startX, Y - 2 * mBoldLineWidth, endX, Y, mLinePaint);
            } else if (i % mGameController.mBigBlockCountX == 0) {
                canvas.drawRect(startX, Y - mBoldLineWidth, endX, Y + mBoldLineWidth, mLinePaint);
            } else {
                canvas.drawLine(startX, Y, endX, Y, mLinePaint);
            }
        }
        /*竖线(细线及粗线,最外测的粗线特殊处理)*/
        for (int i = 0; i <= mGameController.mNumberCount; ++i) {
            float startY = mPaddingTB + 0f;
            float endY = startY + mSBlockH * mGameController.mNumberCount;
            float X = mPaddingLR + mSBlockW * i;
            if (i == 0) {
                canvas.drawRect(X, startY, X + 2 * mBoldLineWidth, endY, mLinePaint);
            } else if (i == mGameController.mNumberCount) {
                canvas.drawRect(X - 2 * mBoldLineWidth, startY, X, endY, mLinePaint);
            } else if (i % mGameController.mBigBlockCountY == 0) {
                canvas.drawRect(X - mBoldLineWidth, startY, X + mBoldLineWidth, endY, mLinePaint);
            } else {
                canvas.drawLine(X, startY, X, endY, mLinePaint);
            }
        }
    }

    /**
     * @param canvas canvas
     */
    private void drawNumbers(Canvas canvas) {
        if (mGameController == null) {
            return;
        }
        Paint.FontMetrics numFm = mNumberPaint.getFontMetrics();
        float numFmX = mSBlockW / 2;
        //        float numFmY = mSBlockH / 2 - (numFm.ascent - numFm.descent) / 2;
        float numFmY = mSBlockH / 2 - (numFm.ascent + numFm.descent) / 2;

        Paint.FontMetrics sNumFx = mSmallNumberPaint.getFontMetrics();
        float sNumFmX = mSBlockW / mGameController.mBigBlockCountY / 2;
        float sNumFmY = mSBlockH / mGameController.mBigBlockCountX / 2 - (sNumFx.ascent + sNumFx.descent) / 2;


        //填充数字
        for (int i = 0; i < mGameController.mNumberCount; ++i) {
            for (int j = 0; j < mGameController.mNumberCount; ++j) {
                int type = mGameController.getNumberType(i, j);
                String numText = mGameController.getTileString(i, j);
                int[] numArray = mGameController.getNumberArray(i, j);
                float numL = mPaddingLR + mSBlockW * i;
                float numR = mPaddingTB + mSBlockH * j;
                float numX = numL + numFmX;
                float numY = numR + numFmY;

                if (type == NumberType.INITIAL_VALUE) {
                    //游戏初始值
                    canvas.drawText(numText, numX, numY, mInitialNumberPaint);
                } else if (type == NumberType.DEFAULT_VALUE) {
                    if (!TextUtils.isEmpty(numText)) {
                        //若大数字非空，填充大数字
                        canvas.drawText(numText, numX, numY, mNumberPaint);
                    } else {
                        //填充数字列表
                        if (numArray != null && numArray.length == mGameController.mNumberCount) {
                            for (int k = 0; k < numArray.length; ++k) {
                                String sNumText;
                                if (numArray[k] != k + 1) {
                                    sNumText = "";
                                } else {
                                    sNumText = String.valueOf(k + 1);
                                }
                                int indexX = k % mGameController.mBigBlockCountY;
                                int indexY = k / mGameController.mBigBlockCountY;
                                float sNumX = numL + sNumFmX + mSBlockW / mGameController.mBigBlockCountY * indexX;
                                float sNumY = numR + sNumFmY + mSBlockH / mGameController.mBigBlockCountX * indexY;
                                canvas.drawText(sNumText, sNumX, sNumY, mSmallNumberPaint);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isSuccess) {
            return true;
        }

        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            return super.onTouchEvent(event);
        }

        mSelectedX = (int) ((event.getX() - mPaddingLR) / mNumberLength);
        mSelectedY = (int) ((event.getY() - mPaddingTB) / mSBlockH);
        if (DEBUG) {
            Log.d(TAG, "onTouchEvent: mSelectedX = " + mSelectedX);
            Log.d(TAG, "onTouchEvent: mSelectedY = " + mSelectedY);
        }

        if (!mGameController.checkSelectedXY(mSelectedX, mSelectedY)) {
            mSelectedX = -1;
            mSelectedY = -1;
            return true;
        }

        invalidate();
        return true;
    }

    public void setNumber(int number) {
        if (!mGameController.checkSelectedXY(mSelectedX, mSelectedY)) {
            return;
        }
        mGameController.resetSudoku(mSelectedX, mSelectedY, number);
        invalidate();
        if (mOnGameListener != null && mGameController.isSuccess()) {
            isSuccess = true;
            mOnGameListener.onGameSuccess();
        }
    }

    public void deleteNumber() {
        if (!mGameController.checkSelectedXY(mSelectedX, mSelectedY)) {
            return;
        }
        mGameController.deleteNumber(mSelectedX, mSelectedY);
        invalidate();
    }

    public void deleteSignNumber() {
        if (!mGameController.checkSelectedXY(mSelectedX, mSelectedY)) {
            return;
        }
        mGameController.deleteSignNumber(mSelectedX, mSelectedY);
        invalidate();
    }

    /**
     * 保存
     */
    public void save() {
        int[][] sudoku = mGameController.getSudoku();
        StringBuilder tmp = new StringBuilder();
        for (int i = 0; i < mGameController.mNumberCount; ++i) {
            for (int j = 0; j < mGameController.mNumberCount; ++j) {
                if (i == mGameController.mNumberCount && j == mGameController.mNumberCount) {
                    tmp.append(sudoku[i][j]);
                } else {
                    tmp.append(sudoku[i][j]).append(",");
                }
            }
        }
        PreferencesUtils.putString(getContext(), Extra.Key.KEY_SAVED_SUDOKU, tmp.toString());
    }


}
