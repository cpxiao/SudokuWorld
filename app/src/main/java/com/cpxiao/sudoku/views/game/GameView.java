package com.cpxiao.sudoku.views.game;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

import com.cpxiao.AppConfig;
import com.cpxiao.androidutils.library.utils.PreferencesUtils;
import com.cpxiao.sudoku.controller.GameController;
import com.cpxiao.sudoku.controller.NumberType;
import com.cpxiao.sudoku.controller.SudokuFactory;
import com.cpxiao.sudoku.imps.OnGameListener;
import com.cpxiao.sudoku.imps.OnKeyBoardViewClickListener;
import com.cpxiao.sudoku.imps.OnToolViewClickListener;
import com.cpxiao.sudoku.mode.extra.Difficulty;
import com.cpxiao.sudoku.mode.extra.Extra;
import com.cpxiao.sudoku.mode.extra.GameMode;

/**
 * @author cpxiao on 2016/11/28.
 */

public class GameView extends ViewGroup {
    protected static final boolean DEBUG = AppConfig.DEBUG;
    protected static final String TAG = GameView.class.getSimpleName();

    private int mBigBlockCountX, mBigBlockCountY;

    private SudokuView mSudokuView;
    private ToolView mToolView;

    private int mSudokuViewW, mSudokuViewH;
    private int mToolViewW, mToolViewH;

    private Rect mSudokuViewRect;
    private Rect mToolViewRect;

    private OnGameListener mOnGameListener;

    public GameView(Context context) {
        super(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameView(final Context context, Bundle bundle) {
        super(context);

        mBigBlockCountX = bundle.getInt(GameMode.BIG_BLOCK_COUNT_X, 3);
        mBigBlockCountY = bundle.getInt(GameMode.BIG_BLOCK_COUNT_Y, 3);
        int gameDifficulty = bundle.getInt(Extra.Key.KEY_DIFFICULTY, Difficulty.DIFFICULTY_DEFAULT[1]);
        boolean isNewGame = bundle.getBoolean(Extra.Name.INTENT_EXTRA_IS_NEW_GAME, true);

        mSudokuView = new SudokuView(context, SudokuView.SCALE_TYPE_CENTER);
        SudokuFactory sudokuFactory = new SudokuFactory(mBigBlockCountX, mBigBlockCountY);
        GameController gameController = new GameController(mBigBlockCountX, mBigBlockCountY);
        int[][] sudoku = null;
        if (!isNewGame) {
            String sudokuString = PreferencesUtils.getString(context, Extra.Key.KEY_SAVED_SUDOKU, "");
            sudoku = sudokuFactory.generateSudoku(sudokuString);
        }
        // 校验数独库，若非完整数独库则重新生成
        if (!sudokuFactory.checkSudokuLegitimacy(sudoku, mBigBlockCountX, mBigBlockCountY)) {
            sudoku = sudokuFactory.generateSudoku();
            sudoku = sudokuFactory.keepInitNumbersByDifficulty(sudoku, gameDifficulty);
            sudoku = sudokuFactory.updateSudokuByNumberType(sudoku, NumberType.INITIAL_VALUE);
        }
        if (DEBUG) {
            sudokuFactory.showSudoku(sudoku, mBigBlockCountX * mBigBlockCountY, mBigBlockCountX * mBigBlockCountY);
        }
        gameController.setSudoku(sudoku);

        mSudokuView.setGameController(gameController);
        mSudokuView.setOnGameListener(new OnGameListener() {
            @Override
            public void onGameSuccess() {
                if (mOnGameListener != null) {
                    mToolView.setOnToolViewClickListener(null);
                    mToolView.setOnKeyBoardViewClickListener(null);
                    mOnGameListener.onGameSuccess();
                }
            }

        });

        int countXX = mBigBlockCountY;
        int countYY = mBigBlockCountX;
        mToolView = new ToolView(context, countXX, countYY);
        mToolView.setOnToolViewClickListener(mOnToolViewClickListener);
        mToolView.setOnKeyBoardViewClickListener(mOnKeyBoardViewClickListener);

        addView(mSudokuView);
        addView(mToolView);

    }

    public void setOnGameListener(OnGameListener listener) {
        mOnGameListener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        mSudokuViewW = w;
        //        mSudokuViewH = (int) (h / (mBigBlockCountY + 1f) * mBigBlockCountY);
        mSudokuViewH = (int) (h * 0.75f);// 修改为固定比例，使ToolView在各模式下大小一致

        mToolViewW = w;
        mToolViewH = h - mSudokuViewH;

        mSudokuViewRect = new Rect();
        mSudokuViewRect.left = 0;
        mSudokuViewRect.right = w;
        mSudokuViewRect.top = 0;
        mSudokuViewRect.bottom = mSudokuViewH;

        mToolViewRect = new Rect();
        mToolViewRect.left = 0;
        mToolViewRect.right = w;
        mToolViewRect.bottom = h;
        mToolViewRect.top = mToolViewRect.bottom - mToolViewH;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int sudokuViewW = MeasureSpec.makeMeasureSpec(mSudokuViewW, MeasureSpec.EXACTLY);
        int sudokuViewH = MeasureSpec.makeMeasureSpec(mSudokuViewH, MeasureSpec.EXACTLY);
        mSudokuView.measure(sudokuViewW, sudokuViewH);

        int toolViewW = MeasureSpec.makeMeasureSpec(mToolViewW, MeasureSpec.EXACTLY);
        int toolViewH = MeasureSpec.makeMeasureSpec(mToolViewH, MeasureSpec.EXACTLY);
        mToolView.measure(toolViewW, toolViewH);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mSudokuView.layout(mSudokuViewRect.left, mSudokuViewRect.top, mSudokuViewRect.right, mSudokuViewRect.bottom);
        mToolView.layout(mToolViewRect.left, mToolViewRect.top, mToolViewRect.right, mToolViewRect.bottom);

    }

    private OnKeyBoardViewClickListener mOnKeyBoardViewClickListener = new OnKeyBoardViewClickListener() {
        @Override
        public void onNumberClick(int number) {
            if (DEBUG) {
                Log.d(TAG, "onNumberClick: number = " + number);
            }

            mSudokuView.setNumber(number);
        }
    };

    private OnToolViewClickListener mOnToolViewClickListener = new OnToolViewClickListener() {
        @Override
        public void onDeleteNumberClick() {
            if (DEBUG) {
                Log.d(TAG, "onDeleteNumberClick: ");
            }
            mSudokuView.deleteNumber();
        }

        @Override
        public void onDeleteSignNumberClick() {
            if (DEBUG) {
                Log.d(TAG, "onDeleteSignNumberClick: ");
            }

            mSudokuView.deleteSignNumber();
        }

        @Override
        public void onSignSelected() {
            if (DEBUG) {
                Log.d(TAG, "onSignSelected: ");
            }

        }

        @Override
        public void onSignUnSelected() {
            if (DEBUG) {
                Log.d(TAG, "onSignUnSelected: ");
            }

        }

        @Override
        public void onShowTip() {
            if (DEBUG) {
                Log.d(TAG, "onShowTip: ");
            }

        }

        @Override
        public void onHideTip() {
            if (DEBUG) {
                Log.d(TAG, "onHideTip: ");
            }

        }

        @Override
        public void onShowCount() {
            if (DEBUG) {
                Log.d(TAG, "onShowCount: ");
            }

        }

        @Override
        public void onHideCount() {
            if (DEBUG) {
                Log.d(TAG, "onHideCount: ");
            }

        }

    };
}
