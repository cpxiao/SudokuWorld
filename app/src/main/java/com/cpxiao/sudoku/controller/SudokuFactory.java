package com.cpxiao.sudoku.controller;

import android.text.TextUtils;
import android.util.Log;

import com.cpxiao.AppConfig;
import com.cpxiao.sudoku.mode.extra.Difficulty;

import java.util.Random;

/**
 * 数独工厂
 *
 * @author cpxiao on 2016/3/1.
 */
public class SudokuFactory {

    private static final boolean DEBUG = AppConfig.DEBUG;
    private static final String TAG = SudokuFactory.class.getSimpleName();

    /**
     * 横向大方块数量及纵向大方块数量
     * 以下数独，mBigBlockCountX = 2, mBigBlockCountY = 3
     * 123|456
     * 456|123
     * --- ---
     * 234|561
     * 561|234
     * --- ---
     * 345|612
     * 612|345
     */
    private int mBigBlockCountX, mBigBlockCountY;

    /**
     * 数字数量，mNumberCount =  mBigBlockCountX * mBigBlockCountY;
     */
    private int mNumberCount;

    /**
     * 用于生成数独库的数组
     */
    private int mInitArray[];
    private Random mRandom = new Random();

    private SudokuFactory() {

    }

    public SudokuFactory(int bigBlockCountX, int bigBlockCountY) {
        if (bigBlockCountX <= 0 || bigBlockCountY <= 0) {
            if (DEBUG) {
                throw new IllegalArgumentException("bigBlockCountX or bigBlockCountY error!");
            }
        }
        mBigBlockCountX = bigBlockCountX;
        mBigBlockCountY = bigBlockCountY;
        mNumberCount = bigBlockCountX * bigBlockCountY;
        mInitArray = new int[mNumberCount];
    }

    /**
     * 随机生成数独库
     *
     * @return 数独库
     */
    public int[][] generateSudoku() {
        int[][] mSudoku = new int[mNumberCount][mNumberCount];
        mSudoku = randomSudoku(mSudoku);
        return mSudoku;
    }

    /**
     * 根据数独库字符串生成数独库
     *
     * @param sudoku 数独库字符串
     * @return 数独库
     */
    public int[][] generateSudoku(String sudoku) {
        int[][] mSudoku = new int[mNumberCount][mNumberCount];
        if (TextUtils.isEmpty(sudoku)) {
            return generateSudoku();
        }
        String[] tmp = sudoku.split(",");
        if (tmp.length != mNumberCount * mNumberCount) {
            return generateSudoku();
        }
        int x, y;
        for (int i = 0; i < tmp.length; i++) {
            x = i % mNumberCount;
            y = i / mNumberCount;
            mSudoku[y][x] = Integer.valueOf(tmp[i]);
        }
        return mSudoku;
    }

    /**
     * 检查合法性，判断是否是y行x列的数组
     *
     * @param sudoku 数独库
     * @param x      x
     * @return boolean true 合法；false 不合法
     */
    public boolean checkSudokuLegitimacy(int[][] sudoku, int x, int y) {
        return x > 0 && y > 0 && (sudoku != null && sudoku.length == y && sudoku[0].length == x);
    }

    /**
     * 根据游戏难度保留游戏初始值
     *
     * @param sudoku         sudoku
     * @param gameDifficulty 游戏难度
     */
    public int[][] keepInitNumbersByDifficulty(int[][] sudoku, int gameDifficulty) {
        if (!checkSudokuLegitimacy(sudoku, mNumberCount, mNumberCount)) {
            if (DEBUG) {
                throw new IllegalAccessError("sudoku error");
            }
            return sudoku;
        }
        int initNumbers = Difficulty.getInitNumbersByDifficulty(gameDifficulty, mNumberCount);
        int tmp, count = 0, i, j, id;
        int tmpArray[] = new int[mNumberCount * mNumberCount];
        for (i = 0; i < tmpArray.length; i++) {
            tmpArray[i] = i;
        }
        int delNumbers = mNumberCount * mNumberCount - initNumbers;
        while (count < delNumbers) {
            id = mRandom.nextInt(1000 + count * count * delNumbers * 10) % tmpArray.length;
            tmp = tmpArray[id];
            i = tmp / mNumberCount;
            j = tmp % mNumberCount;
            sudoku[i][j] = 0;
            count++;
            tmpArray = removeNumberFromArray(tmpArray, tmp);
        }
        return sudoku;
    }


    /**
     * 随机生成数独库
     */
    private int[][] randomSudoku(int[][] mSudoku) {
        mInitArray = createRandomArray(mNumberCount);
        int array[];
        for (int i = 0; i < mBigBlockCountY; i++) {
            for (int j = 0; j < mBigBlockCountX; j++) {
                for (int k = 0; k < mNumberCount; k++) {
                    array = arrayDisplace(mInitArray, i + j * mBigBlockCountY);
                    mSudoku[i * mBigBlockCountX + j][k] = array[k];
                }
            }
        }

        int index = 0;
        while (index <= mNumberCount) {
            mSudoku = changeRow(mSudoku);
            mSudoku = changeRows(mSudoku);
            mSudoku = changeColumn(mSudoku);
            mSudoku = changeColumns(mSudoku);
            mSudoku = changeNumbers(mSudoku);
            index++;
        }
        return mSudoku;
    }

    /**
     * 数组位移，比如{1,2,3,4,5,6,7,8,9}移动1位则返回{2,3,4,5,6,7,8,9,1}
     *
     * @param array 初始数组
     * @param n     位移位数
     * @return 新数组
     */
    private int[] arrayDisplace(int[] array, int n) {
        if (n <= 0 || n >= mNumberCount) {
            return array;
        }
        int[] resultArray = new int[mNumberCount];
        for (int i = 0; i < mNumberCount; i++) {
            resultArray[i] = array[(i + n) % mNumberCount];
        }
        return resultArray;
    }

    /**
     * 删除数组中值为num的数据
     *
     * @param array 数组
     * @param num   待删除数字
     * @return 新数组
     */
    private int[] removeNumberFromArray(int[] array, int num) {
        int count = 0;
        for (int anArray : array) {
            if (anArray != num) {
                count++;
            }
        }
        int resultArray[] = new int[count];
        count = 0;
        for (int anArray : array) {
            if (anArray != num) {
                resultArray[count++] = anArray;
            }
        }

        return resultArray;
    }

    /**
     * 获得长度为length的无重复数字的随机数组
     *
     * @param length 数组长度
     */
    private int[] createRandomArray(int length) {
        int array[] = new int[length];
        for (int i = 0; i < length; i++) {
            array[i] = i + 1;
        }
        int resultArray[] = new int[length];
        for (int i = 0; i < length; i++) {
            int t = mRandom.nextInt(array.length);
            resultArray[i] = array[t];
            array[t] = 0;
            array = removeNumberFromArray(array, 0);
        }
        return resultArray;
    }

    /**
     * 在局部横区域中随机交换两行
     *
     * @param array 数独库
     * @return 新数独库
     */
    private int[][] changeRow(int[][] array) {
        int tmp = mRandom.nextInt(mBigBlockCountY);
        int row1 = tmp * mBigBlockCountX + mRandom.nextInt(mBigBlockCountX);
        int row2 = tmp * mBigBlockCountX + mRandom.nextInt(mBigBlockCountX);
        int[][] resultSudoku = new int[mNumberCount][mNumberCount];
        for (int y = 0; y < mNumberCount; y++) {
            for (int x = 0; x < mNumberCount; x++) {
                if (y == row1) {
                    resultSudoku[row2][x] = array[y][x];
                } else if (y == row2) {
                    resultSudoku[row1][x] = array[y][x];
                } else {
                    resultSudoku[y][x] = array[y][x];
                }
            }
        }
        return resultSudoku;
    }

    /**
     * 在局部竖区域中随机交换两列
     *
     * @param array 数独库
     * @return 新数独库
     */
    private int[][] changeColumn(int[][] array) {
        int tmp = mRandom.nextInt(mBigBlockCountX);
        int column1 = tmp * mBigBlockCountY + mRandom.nextInt(mBigBlockCountY);
        int column2 = tmp * mBigBlockCountY + mRandom.nextInt(mBigBlockCountY);
        int[][] resultSudoku = new int[mNumberCount][mNumberCount];
        for (int y = 0; y < mNumberCount; y++) {
            for (int x = 0; x < mNumberCount; x++) {
                if (x == column1) {
                    resultSudoku[y][column2] = array[y][x];
                } else if (x == column2) {
                    resultSudoku[y][column1] = array[y][x];
                } else {
                    resultSudoku[y][x] = array[y][x];
                }
            }
        }
        return resultSudoku;
    }

    /**
     * 随机交换两个横区域
     *
     * @param array 数独库
     * @return 新数独库
     */
    private int[][] changeRows(int[][] array) {
        int rows1 = mRandom.nextInt(mBigBlockCountY);
        int rows2 = mRandom.nextInt(mBigBlockCountY);
        int[][] resultSudoku = new int[mNumberCount][mNumberCount];
        for (int y = 0; y < mNumberCount; y++) {
            for (int x = 0; x < mNumberCount; x++) {
                if (y / mBigBlockCountX == rows1) {
                    resultSudoku[y][x] = array[rows2 * mBigBlockCountX + y % mBigBlockCountX][x];
                } else if (y / mBigBlockCountX == rows2) {
                    resultSudoku[y][x] = array[rows1 * mBigBlockCountX + y % mBigBlockCountX][x];
                } else {
                    resultSudoku[y][x] = array[y][x];
                }
            }
        }
        return resultSudoku;
    }

    /**
     * 随机交换两个列区域
     *
     * @param array 数独库
     * @return 新数独库
     */
    private int[][] changeColumns(int[][] array) {
        int columns1 = mRandom.nextInt(mBigBlockCountX);
        int columns2 = mRandom.nextInt(mBigBlockCountX);
        int[][] resultSudoku = new int[mNumberCount][mNumberCount];
        for (int y = 0; y < mNumberCount; y++) {
            for (int x = 0; x < mNumberCount; x++) {
                if (x / mBigBlockCountY == columns1) {
                    resultSudoku[y][x] = array[y][columns2 * mBigBlockCountY + x % mBigBlockCountY];
                } else if (x / mBigBlockCountY == columns2) {
                    resultSudoku[y][x] = array[y][columns1 * mBigBlockCountY + x % mBigBlockCountY];
                } else {
                    resultSudoku[y][x] = array[y][x];
                }
            }
        }
        return resultSudoku;
    }


    /**
     * 随机对换数字
     *
     * @param array 数组
     * @return 新数组
     */
    private int[][] changeNumbers(int[][] array) {
        int num1 = mRandom.nextInt(mNumberCount) + 1;
        int num2 = mRandom.nextInt(mNumberCount) + 1;
        return changeNumbers(array, num1, num2);
    }

    /**
     * 调换指定两个数字
     *
     * @param array 数组
     * @param num1  数字1
     * @param num2  数字2
     * @return 新数组
     */
    private int[][] changeNumbers(int[][] array, int num1, int num2) {
        for (int y = 0; y < array.length; y++) {
            for (int x = 0; x < array.length; x++) {
                if (array[y][x] == num1) {
                    array[y][x] = num2;
                } else if (array[y][x] == num2) {
                    array[y][x] = num1;
                }
            }
        }
        return array;
    }


    /**
     * 将非0的数据全部更新成类型为numberType的值，用于初始化数独库默认值
     *
     * @param numberType 数字类型
     */
    public int[][] updateSudokuByNumberType(int[][] mSudoku, int numberType) {
        for (int y = 0; y < mNumberCount; y++) {
            for (int x = 0; x < mNumberCount; x++) {
                if (mSudoku[y][x] % (mNumberCount + 1) != 0) {
                    mSudoku[y][x] = mSudoku[y][x] % (mNumberCount + 1) + mNumberCount * mNumberCount * numberType;
                }
            }
        }
        return mSudoku;
    }

    /**
     * debug 打印数独库
     */
    public void showSudoku(int[][] mSudoku, int lengthX, int lengthY) {

        if (DEBUG) {
            if (mSudoku == null || mSudoku.length != lengthY || mSudoku[0].length != lengthX) {
                return;
            }

            Log.d(TAG, "--------------------------------------------");
            for (int y = 0; y < lengthY; y++) {
                String tmp = "";
                for (int x = 0; x < lengthX; x++) {
                    //                    tmp = tmp + mSudoku[y][x] % (mNumberCount * mNumberCount) + ":" + mSudoku[y][x] / (mNumberCount * mNumberCount) + ",";
                    tmp = tmp + mSudoku[y][x] % (mNumberCount * mNumberCount) + ",";
                }
                Log.d(TAG, tmp);
            }
            Log.d(TAG, "-----------------");
            for (int y = 0; y < lengthY; y++) {
                String tmp = "";
                for (int x = 0; x < lengthX; x++) {
                    //                    tmp = tmp + mSudoku[y][x] % (mNumberCount * mNumberCount) + ":" + mSudoku[y][x] / (mNumberCount * mNumberCount) + ",";
                    tmp = tmp + mSudoku[y][x] / (mNumberCount * mNumberCount) + ",";
                }
                Log.d(TAG, tmp);
            }
            Log.d(TAG, "--------------------------------------------");
        }
    }
}
