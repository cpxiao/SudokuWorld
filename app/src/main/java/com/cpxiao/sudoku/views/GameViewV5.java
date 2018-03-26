package com.cpxiao.sudoku.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.cpxiao.R;
import com.cpxiao.gamelib.mode.common.Sprite;
import com.cpxiao.gamelib.mode.common.SpriteControl;
import com.cpxiao.gamelib.views.BaseSurfaceView;
import com.cpxiao.sudoku.controller.SudokuFactory;
import com.cpxiao.sudoku.mode.NumberSelectorBlock;
import com.cpxiao.sudoku.mode.SudokuLine;
import com.cpxiao.sudoku.mode.SudokuSmallBlock;
import com.cpxiao.sudoku.mode.ToolBtn;
import com.cpxiao.sudoku.mode.extra.GameMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cpxiao on 2017/10/25.
 */

public class GameViewV5 extends BaseSurfaceView {

    private int mBigBlockCountX, mBigBlockCountY, mNumberCount;
    private float blockW, blockH;
    private RectF mTitleBarRectF = new RectF();
    private RectF mSudokuRectF = new RectF();
    private RectF mToolBarRectF = new RectF();
    private RectF mNumberSelectorToolRectF = new RectF();
    private ToolBtn mTipBtn;
    private ToolBtn mShowNumberCountBtn;
    private ToolBtn mShowSelectedNumberBtn;
    private ToolBtn mDeleteBtn;

    private List<SudokuLine> mSudokuLineList = new ArrayList<>();
    private List<NumberSelectorBlock> mNumberSelectorBlockList = new ArrayList<>();
    private List<SudokuSmallBlock> mSudokuSmallBlockList = new ArrayList<>();

    public GameViewV5(Context context) {
        super(context);
        init(null);
    }

    public GameViewV5(final Context context, Bundle bundle) {
        super(context);
        init(bundle);
    }

    private void init(Bundle bundle) {
        if (bundle == null) {
            mBigBlockCountX = 3;
            mBigBlockCountY = 3;
            return;
        }
        mBigBlockCountX = bundle.getInt(GameMode.BIG_BLOCK_COUNT_X, 3);
        mBigBlockCountY = bundle.getInt(GameMode.BIG_BLOCK_COUNT_Y, 3);

        mNumberCount = mBigBlockCountX * mBigBlockCountY;
    }


    @Override
    protected void initWidget() {
        initRectF();
        initButtons();

        initSudoku(mSudokuRectF);
        initNumberSelectorTool(mNumberSelectorToolRectF, mBigBlockCountY, mBigBlockCountX);

    }

    private void initButtons() {
        float btnW = 0.2F * mToolBarRectF.width();
        float btnH = 0.3F * mToolBarRectF.height();

        float left = mToolBarRectF.left + 0.2F * mToolBarRectF.width();
        float right = mToolBarRectF.right - 0.2F * mToolBarRectF.width();
        float top = mToolBarRectF.top + 0.25F * mToolBarRectF.height();
        float bottom = mToolBarRectF.bottom - 0.25F * mToolBarRectF.height();

        mTipBtn = (ToolBtn) new ToolBtn.Build()
                .setText(getContext().getString(R.string.tip))
                .setW(btnW)
                .setH(btnH)
                .centerTo(left, top)
                .build();
        mShowNumberCountBtn = (ToolBtn) new ToolBtn.Build()
                .setText(getContext().getString(R.string.show_count))
                .setW(btnW)
                .setH(btnH)
                .centerTo(left, bottom)
                .build();
        mShowSelectedNumberBtn = (ToolBtn) new ToolBtn.Build()
                .setText(getContext().getString(R.string.show_selected))
                .setW(btnW)
                .setH(btnH)
                .centerTo(right, top)
                .build();
        mDeleteBtn = (ToolBtn) new ToolBtn.Build()
                .setText(getContext().getString(R.string.delete))
                .setW(btnW)
                .setH(btnH)
                .centerTo(right, bottom)
                .build();
    }

    private void initRectF() {
        float density = Resources.getSystem().getDisplayMetrics().density;
        float titleBarHeight = 80 * density;
        mTitleBarRectF.set(0, 0, mViewWidth, titleBarHeight);

        float toolBarHeight = 150 * density;
        mToolBarRectF.set(0, mViewHeight - toolBarHeight, mViewWidth, mViewHeight);

        float sudokuHeight = mViewHeight - titleBarHeight - toolBarHeight;
        float sudokuWH = Math.min(mViewWidth, sudokuHeight);

        mSudokuRectF.left = 0.5F * (mViewWidth - sudokuWH);
        mSudokuRectF.top = titleBarHeight + 0.5F * (sudokuHeight - sudokuWH);
        mSudokuRectF.right = mSudokuRectF.left + sudokuWH;
        mSudokuRectF.bottom = mSudokuRectF.top + sudokuWH;

        blockW = mSudokuRectF.width() / mNumberCount;
        blockH = mSudokuRectF.height() / mNumberCount;

        initNumberSelectorRectF(mToolBarRectF);
    }

    private void initNumberSelectorRectF(RectF toolBarRectF) {
        float aspectRatio = 0.9F;
        float tmpNumberSelectorW = 0.5F * toolBarRectF.width() / mBigBlockCountY;
        float tmpNumberSelectorH = 0.9F * toolBarRectF.height() / mBigBlockCountX;

        float numberSelectorW = Math.min(tmpNumberSelectorW, tmpNumberSelectorH * aspectRatio);
        float numberSelectorH = Math.min(tmpNumberSelectorW / aspectRatio, tmpNumberSelectorH);

        float numberSelectorToolW = numberSelectorW * mBigBlockCountY;
        float numberSelectorToolH = numberSelectorH * mBigBlockCountX;
        mNumberSelectorToolRectF.left = 0.5F * (toolBarRectF.width() - numberSelectorToolW);
        mNumberSelectorToolRectF.top = toolBarRectF.top + 0.5F * (toolBarRectF.height() - numberSelectorToolH);
        mNumberSelectorToolRectF.right = mNumberSelectorToolRectF.left + numberSelectorToolW;
        mNumberSelectorToolRectF.bottom = mNumberSelectorToolRectF.top + numberSelectorToolH;
    }

    private void initSudoku(RectF sudokuRectF) {
        int[][] sudoku = new SudokuFactory(mBigBlockCountX, mBigBlockCountY).generateSudoku();

        initSudokuSmallBlock(sudokuRectF, sudoku);
        initSudokuLine(sudokuRectF);
    }

    private void initSudokuSmallBlock(final RectF sudokuRectF, int[][] sudoku) {
        if (sudokuRectF == null || sudoku == null) {
            return;
        }
        for (int y = 0; y < mNumberCount; y++) {
            for (int x = 0; x < mNumberCount; x++) {
                int value = sudoku[y][x];
                float cX = sudokuRectF.left + (0.5F + x) * blockW;
                float cY = sudokuRectF.top + (0.5F + y) * blockH;
                int countX = mBigBlockCountY;
                int countY = mBigBlockCountX;
                int[] selectedNumberArray = new int[countX * countY];
                //                selectedNumberArray[0][0] = value;
                SudokuSmallBlock block = (SudokuSmallBlock) new SudokuSmallBlock.Build()
                        .setValue(value)
                        .setNumberCountX(countX)
                        .setNumberCountY(countY)
                        .setSelectedNumberArray(selectedNumberArray)
                        .setIndexX(x)
                        .setIndexY(y)
                        .setW(blockW)
                        .setH(blockH)
                        .centerTo(cX, cY)
                        .build();
                //                block.setState(3);
                mSudokuSmallBlockList.add(block);
            }
        }
    }


    private void initSudokuLine(final RectF sudokuRectF) {
        if (sudokuRectF == null) {
            return;
        }
        float lineSize = Math.max(0.002F * sudokuRectF.width(), 2);
        float boldLineSize = 0.01F * sudokuRectF.width();


        //竖线
        float verticalLineW;
        final float verticalLineH = sudokuRectF.height() + boldLineSize;
        float verticalLineCY = sudokuRectF.centerY();
        for (int x = 0; x < mNumberCount + 1; x++) {
            if (x % mBigBlockCountY == 0) {
                verticalLineW = boldLineSize;
            } else {
                verticalLineW = lineSize;
            }
            SudokuLine line = (SudokuLine) new SudokuLine.Build()
                    .setW(verticalLineW)
                    .setH(verticalLineH)
                    .centerTo(sudokuRectF.left + x * blockW, verticalLineCY)
                    .build();
            mSudokuLineList.add(line);
        }

        //横线
        final float horizontalLineW = sudokuRectF.height() + boldLineSize;
        float horizontalLineH;
        float horizontalLineCX = sudokuRectF.centerX();
        for (int y = 0; y < mNumberCount + 1; y++) {
            if (y % mBigBlockCountX == 0) {
                horizontalLineH = boldLineSize;
            } else {
                horizontalLineH = lineSize;
            }
            SudokuLine line = (SudokuLine) new SudokuLine.Build()
                    .setW(horizontalLineW)
                    .setH(horizontalLineH)
                    .centerTo(horizontalLineCX, sudokuRectF.top + y * blockH)
                    .build();
            mSudokuLineList.add(line);
        }
    }

    private void initNumberSelectorTool(final RectF rectF, final int blockCountXX, final int blockCountYY) {
        if (rectF == null) {
            return;
        }
        float blockW = rectF.width() / blockCountXX;
        float blockH = rectF.height() / blockCountYY;
        for (int y = 0; y < blockCountYY; y++) {
            for (int x = 0; x < blockCountXX; x++) {
                int value = y * blockCountXX + x + 1;
                float cX = rectF.left + (0.5F + x) * blockW;
                float cY = rectF.top + (0.5F + y) * blockH;
                NumberSelectorBlock block = (NumberSelectorBlock) new NumberSelectorBlock.Build()
                        .setValue(value)
                        .setW(0.9F * blockW)
                        .setH(0.9F * blockH)
                        .centerTo(cX, cY)
                        .build();
                mNumberSelectorBlockList.add(block);
            }
        }
    }


    @Override
    public void drawCache() {
        if (DEBUG) {
            Log.d(TAG, "drawCache: .......");
        }
        for (Sprite sprite : mSudokuSmallBlockList) {
            sprite.draw(mCanvasCache, mPaint);
        }
        for (Sprite sprite : mSudokuLineList) {
            sprite.draw(mCanvasCache, mPaint);
        }
        for (Sprite sprite : mNumberSelectorBlockList) {
            sprite.draw(mCanvasCache, mPaint);
        }

        mTipBtn.draw(mCanvasCache, mPaint);
        mShowNumberCountBtn.draw(mCanvasCache, mPaint);
        mShowSelectedNumberBtn.draw(mCanvasCache, mPaint);
        mDeleteBtn.draw(mCanvasCache, mPaint);

    }

    private int mSelectedIndexX = -1, mSelectedIndexY = -1;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float eventX = event.getX();
        float eventY = event.getY();

        /*遍历数独区域，找到最后选择的方格*/
        for (SudokuSmallBlock block : mSudokuSmallBlockList) {
            block.setSelected(false);
            if (SpriteControl.isClicked(block, eventX, eventY)) {
                mSelectedIndexX = block.getIndexX();
                mSelectedIndexY = block.getIndexY();
            }
        }
        /*设置选中方格*/
        for (SudokuSmallBlock block : mSudokuSmallBlockList) {
            //            if (block.getIndexX() == mSelectedIndexX) {
            //                block.setSelected(true);
            //            }
            //            if (block.getIndexY() == mSelectedIndexY) {
            //                block.setSelected(true);
            //            }
            //            if (block.getIndexX() / mBigBlockCountY == mSelectedIndexX / mBigBlockCountY
            //                    && block.getIndexY() / mBigBlockCountX == mSelectedIndexY / mBigBlockCountX) {
            //                block.setSelected(true);
            //            }
            if (block.getIndexX() == mSelectedIndexX && block.getIndexY() == mSelectedIndexY) {
                block.setSelected(true);
                break;
            }
        }

        /*数字选择区域*/
        for (NumberSelectorBlock block : mNumberSelectorBlockList) {
            block.isSelected = false;
            if (SpriteControl.isClicked(block, eventX, eventY)) {
                block.isSelected = true;
                if (action == MotionEvent.ACTION_UP) {
                    //
                    SudokuSmallBlock sudokuSmallBlock = getBlock(mSelectedIndexX, mSelectedIndexY);
                    updateSudokuSmallBlock(sudokuSmallBlock, block.getValue());
                }
            }
        }




        /*点击提示按钮*/
        if (action == MotionEvent.ACTION_DOWN) {
            if (SpriteControl.isClicked(mTipBtn, eventX, eventY)) {
                mTipBtn.setSelected(!mTipBtn.isSelected());
                if (mTipBtn.isSelected()) {
                    showTip();
                } else {
                    hideTip();
                }
            }
        }
        /*点击计数按钮*/
        if (action == MotionEvent.ACTION_DOWN) {
            if (SpriteControl.isClicked(mShowNumberCountBtn, eventX, eventY)) {
                mShowNumberCountBtn.setSelected(!mShowNumberCountBtn.isSelected());
                if (mShowNumberCountBtn.isSelected()) {
                    showNumberCount();
                } else {
                    hideNumberCount();
                }
            }
        }
        /*点击标记按钮*/
        if (action == MotionEvent.ACTION_DOWN) {
            if (SpriteControl.isClicked(mShowSelectedNumberBtn, eventX, eventY)) {
                mShowSelectedNumberBtn.setSelected(!mShowSelectedNumberBtn.isSelected());
                if (mShowSelectedNumberBtn.isSelected()) {
                    showSelectedNumber();
                } else {
                    hideSelectedNumber();
                }
            }
        }

        /*点击删除按钮*/
        if (action == MotionEvent.ACTION_DOWN && SpriteControl.isClicked(mDeleteBtn, eventX, eventY)) {
            mDeleteBtn.setSelected(true);
            delete();
        }
        if (action == MotionEvent.ACTION_UP || !SpriteControl.isClicked(mDeleteBtn, eventX, eventY)) {
            mDeleteBtn.setSelected(false);
        }


        myDraw();
        //        postInvalidate();
        return true;


    }

    private void updateSudokuSmallBlock(SudokuSmallBlock block, int value) {
        if (block == null) {
            return;
        }
        if (mShowSelectedNumberBtn.isSelected()) {
            block.updateSelectedValueArray(value);
        } else {
            block.setValue(value);
        }
    }

    private void showTip() {

    }

    private void hideTip() {
        for (NumberSelectorBlock block : mNumberSelectorBlockList) {
            block.isTip = false;
        }
    }


    private void showNumberCount() {
        for (NumberSelectorBlock block : mNumberSelectorBlockList) {
            block.isShowNumberCount = true;
        }
    }

    private void hideNumberCount() {
        for (NumberSelectorBlock block : mNumberSelectorBlockList) {
            block.isShowNumberCount = false;
        }
    }

    private void showSelectedNumber() {
        SudokuSmallBlock block = getBlock(mSelectedIndexX, mSelectedIndexY);
        if (block == null) {
            return;
        }
        block.showSelectedNumber();
    }

    private void hideSelectedNumber() {
        SudokuSmallBlock block = getBlock(mSelectedIndexX, mSelectedIndexY);
        if (block == null) {
            return;
        }
        block.showValue();
    }


    private void delete() {
        SudokuSmallBlock block = getBlock(mSelectedIndexX, mSelectedIndexY);
        if (block == null) {
            return;
        }
        if (block.isDefault()) {
            //            return;
        }
        if (mShowSelectedNumberBtn.isSelected()) {
            //删除列表
            block.clearSelectedNumber();
        } else {
            //删除数字
            block.clearNumber();
        }
    }

    private SudokuSmallBlock getBlock(int indexX, int indexY) {
        for (SudokuSmallBlock block : mSudokuSmallBlockList) {
            if (block.getIndexX() == indexX && block.getIndexY() == indexY) {
                return block;
            }
        }
        return null;
    }
}
