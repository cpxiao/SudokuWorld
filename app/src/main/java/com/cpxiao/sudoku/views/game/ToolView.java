package com.cpxiao.sudoku.views.game;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

import com.cpxiao.AppConfig;
import com.cpxiao.sudoku.imps.OnKeyBoardViewClickListener;
import com.cpxiao.sudoku.imps.OnToolViewClickListener;

import static android.view.View.MeasureSpec.EXACTLY;


/**
 * ToolView：包含待选数字及功能按钮（提示、显示、标记、删除）
 *
 * @author cpxiao on 2016/11/28.
 * @version 2017/3/16.
 */

public class ToolView extends ViewGroup {

    private static final boolean DEBUG = AppConfig.DEBUG;
    private static final String TAG = ToolView.class.getSimpleName();

    private float mViewW, mViewH;

    private int mCountX, mCountY;
    public BtnSelectedView mToolsView;
    private NumberSelectedView mKeyboard1LineView;

    private Rect mToolsViewRect;
    private Rect mKeyboard1LineViewRect;

    private ToolView(Context context) {
        this(context, null);
    }

    private ToolView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToolView(Context context, int countX, int countY) {
        this(context);
        if (countX <= 0 || countY <= 0) {
            if (DEBUG) {
                throw new IllegalArgumentException("error! countX <= 0 || countY <= 0");
            }
        }
        if (DEBUG) {
            Log.d(TAG, "ToolView: countX = " + countX + ", countY = " + countY);
        }
        this.mCountX = countX;
        this.mCountY = countY;
        initWidget(context);
    }

    private void initWidget(Context context) {
        mToolsView = new BtnSelectedView(context, mCountX * mCountY);
        mKeyboard1LineView = new NumberSelectedView(context, mCountX * mCountY);

        addView(mToolsView);
        addView(mKeyboard1LineView);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        mViewW = w;
        mViewH = h;
        initLayoutRect();
    }

    private void initLayoutRect() {
        //上下平分
        mToolsViewRect = new Rect();
        mKeyboard1LineViewRect = new Rect();

        mKeyboard1LineViewRect.left = 0;
        mKeyboard1LineViewRect.right = (int) mViewW;
        mKeyboard1LineViewRect.top = 0;
        mKeyboard1LineViewRect.bottom = (int) (mViewH / 2);

        mToolsViewRect.left = 0;
        mToolsViewRect.right = (int) (mViewW);
        mToolsViewRect.top = (int) (mViewH / 2);
        mToolsViewRect.bottom = (int) (mViewH);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMeasure = MeasureSpec.makeMeasureSpec((int) mViewW, EXACTLY);
        int heightMeasure = MeasureSpec.makeMeasureSpec((int) (mViewH / 2), MeasureSpec.EXACTLY);
        //
        mKeyboard1LineView.measure(widthMeasure, heightMeasure);
        mToolsView.measure(widthMeasure, heightMeasure);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mToolsView.layout(mToolsViewRect.left, mToolsViewRect.top, mToolsViewRect.right, mToolsViewRect.bottom);
        mKeyboard1LineView.layout(mKeyboard1LineViewRect.left, mKeyboard1LineViewRect.top, mKeyboard1LineViewRect.right, mKeyboard1LineViewRect.bottom);
    }


    public void setOnToolViewClickListener(OnToolViewClickListener onToolViewClickListener) {
        if (mToolsView == null) {
            if (DEBUG) {
                throw new NullPointerException("error! mToolsView == null");
            }
        } else {
            mToolsView.setOnToolViewClickListener(onToolViewClickListener);
        }
    }

    public void setOnKeyBoardViewClickListener(OnKeyBoardViewClickListener onKeyBoardViewClickListener) {
        if (mKeyboard1LineView == null) {
            if (DEBUG) {
                throw new NullPointerException("error! mKeyboard1LineView == null");
            }
        } else {
            mKeyboard1LineView.setOnKeyBoardViewClickListener(onKeyBoardViewClickListener);
        }
    }

}
