package com.cpxiao.sudoku.controller;

import com.cpxiao.AppConfig;

/**
 * GameController
 *
 * @author cpxiao on 2016/3/6.
 */
public class GameController {

    private static final boolean DEBUG = AppConfig.DEBUG;
    private static final String TAG = GameController.class.getSimpleName();

    /**
     * 游戏类型：2、3、4、5...
     */
    public int mBigBlockCountX;
    /**
     * 游戏类型：2、3、4、5...
     */
    public int mBigBlockCountY;

    /**
     * 游戏数字个数：mBigBlockCountX * mBigBlockCountY
     */
    public int mNumberCount;

    /**
     * 数独库
     */
    private int mSudoku[][];

    /**
     * 数独库每个位置用户设置的待选数字数组
     */
    private int mSudokuArray[][][];

    public GameController(int bigBlockCountX, int bigBlockCountY) {
        init(bigBlockCountX, bigBlockCountY);
    }

    private void init(int bigBlockCountX, int bigBlockCountY) {
        mBigBlockCountX = bigBlockCountX;
        mBigBlockCountY = bigBlockCountY;
        mNumberCount = bigBlockCountX * bigBlockCountY;
        mSudoku = new int[mNumberCount][mNumberCount];
        mSudokuArray = new int[mNumberCount][mNumberCount][mNumberCount];

    }

    public void setSudoku(int[][] sudoku) {
        mSudoku = sudoku;
    }

    public int[][] getSudoku() {
        return mSudoku;
    }

    /**
     * 根据坐标(x,y)获取方格中数字
     */
    private int getTile(int x, int y) {
        if (!checkSelectedXY(x, y)) {
            if (DEBUG) {
                throw new IllegalAccessError("x or y error! x = " + x + ", y = " + y);
            }
            return -1;
        }
        return mSudoku[y][x] % (mNumberCount * mNumberCount);
    }

    /**
     * 判断坐标(x, y)是否在数独库数字区域内
     *
     * @param x x
     * @param y y
     * @return boolean.true:在数独库数字区域内
     */
    public boolean checkSelectedXY(int x, int y) {
        return x >= 0 && x < mNumberCount && y >= 0 && y < mNumberCount;
    }

    /**
     * @param x x
     * @param y y
     * @return boolean
     */
    public boolean isInitialValue(int x, int y) {
        if (x < 0 || x >= mNumberCount || y < 0 || y >= mNumberCount) {
            return false;
        }
        int type = mSudoku[y][x] / (mNumberCount * mNumberCount);
        return type == NumberType.INITIAL_VALUE;
    }

    /**
     * @param x x
     * @param y y
     * @return int
     */
    public int getNumberType(int x, int y) {
        if (x < 0 || x >= mNumberCount || y < 0 || y >= mNumberCount) {
            return -1;
        }
        int type = mSudoku[y][x] / (mNumberCount * mNumberCount);
        if (type == NumberType.INITIAL_VALUE) {
            return NumberType.INITIAL_VALUE;
        }
        return NumberType.DEFAULT_VALUE;
    }

    /**
     * 根据坐标(x,y)获得方格中显示字符
     */
    public String getTileString(int x, int y) {
        int value = getTile(x, y);
        if (value == 0) {
            return "";
        } else {
            return String.valueOf(value);
        }
    }

    /**
     * 获取某个位置用户设置的待选数字列表
     * 对应位置存放对应数值，比如9个数的数独，
     * 若没设置，则返回{0,0,0,0,0,0,0,0,0};
     * 若设置135，则返回{1,0,3,0,5,0,0,0,0}
     *
     * @param x 坐标x
     * @param y 坐标y
     * @return 长度为mGameNumber的数组
     */
    public int[] getNumberArray(int x, int y) {
        return null;
    }

    /**
     * 删除为0的数据
     */
    private int[] removeZero(int[] array) {
        int count = 0;
        for (int anArray : array) {
            if (anArray != 0) {
                count++;
            }
        }
        int array1[] = new int[count];
        count = 0;
        for (int anArray : array) {
            if (anArray != 0) {
                array1[count++] = anArray;
            }
        }
        return array1;
    }


    /**
     * 更新数独库,当选定区域不是游戏初始值，则更新该区域值
     *
     * @param x      x
     * @param y      y
     * @param num_xy num
     * @return int[][]
     */
    public int[][] resetSudoku(int x, int y, int num_xy) {
        if (x < 0 || x >= mNumberCount || y < 0 || y >= mNumberCount) {
            return mSudoku;
        }
        if (getNumberType(x, y) != NumberType.INITIAL_VALUE) {
            mSudoku[y][x] = num_xy;
        }

        return mSudoku;
    }

    /**
     * 当选定区域不是游戏初始值，删除该值
     *
     * @param x x
     * @param y y
     * @return int[][]
     */
    public int[][] deleteNumber(int x, int y) {
        if (x < 0 || x >= mNumberCount || y < 0 || y >= mNumberCount) {
            return mSudoku;
        }
        if (getNumberType(x, y) != NumberType.INITIAL_VALUE) {
            mSudoku[y][x] = 0;
        }

        return mSudoku;
    }

    /**
     * 当选定区域不是游戏初始值，删除标记数组
     *
     * @param x x
     * @param y y
     * @return int[][]
     */
    public int[][] deleteSignNumber(int x, int y) {
        if (x < 0 || x >= mNumberCount || y < 0 || y >= mNumberCount) {
            return mSudoku;
        }
        if (getNumberType(x, y) != NumberType.INITIAL_VALUE) {
            if (mSudokuArray[y][x] != null) {
                for (int i = 0; i < mSudokuArray[y][x].length; i++) {
                    mSudokuArray[y][x][i] = 0;
                }
            }
        }

        return mSudoku;
    }

    /**
     * 判断游戏是否成功 false:unsuccessful,true:success)
     *
     * @return boolean
     */
    public boolean isSuccess() {
        int c1[] = new int[mNumberCount];
        //判断所有行是否成功
        for (int y = 0; y < mNumberCount; y++) {
            c1 = new int[mNumberCount];
            for (int x = 0; x < mNumberCount; x++) {
                int t = getTile(x, y);
                if (t != 0) {
                    c1[t - 1] = t;
                } else {
                    return false;
                }
            }
            c1 = removeZero(c1);
            if (c1.length != mNumberCount) {
                return false;
            }
        }
        //判断所有列是否成功
        for (int x = 0; x < mNumberCount; x++) {
            c1 = new int[mNumberCount];
            for (int y = 0; y < mNumberCount; y++) {
                int t = getTile(x, y);
                if (t != 0) {
                    c1[t - 1] = t;
                } else {
                    return false;
                }
            }
            c1 = removeZero(c1);
            if (c1.length != mNumberCount) {
                return false;
            }
        }
        //判断所有小区域是否成功
        for (int i = 0; i < mBigBlockCountY; i++) {
            for (int j = 0; j < mBigBlockCountX; j++) {
                for (int x = i * mBigBlockCountY; x < (i + 1) * mBigBlockCountX; x++) {
                    for (int y = j * mBigBlockCountX; y < (j + 1) * mBigBlockCountY; y++) {
                        int t = getTile(x, y);
                        if (t != 0) {
                            c1[t - 1] = t;
                        } else {
                            return false;
                        }
                    }
                }
                c1 = removeZero(c1);
                if (c1.length != mNumberCount) {
                    return false;
                }
            }
        }

        return true;
    }


}
