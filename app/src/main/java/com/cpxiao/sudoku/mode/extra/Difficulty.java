package com.cpxiao.sudoku.mode.extra;

import com.cpxiao.AppConfig;
import com.cpxiao.R;

import java.util.Random;

/**
 * @author cpxiao on 2017/09/05.
 */

public final class Difficulty {
    private static final boolean DEBUG = AppConfig.DEBUG;

    private static final Random RANDOM = new Random();

    private static final int[] DIFFICULTY_EASY = {R.string.easy, 2, 60};
    private static final int[] DIFFICULTY_NORMAL = {R.string.normal, 3, 50};
    private static final int[] DIFFICULTY_HARD = {R.string.hard, 4, 40};
    private static final int[] DIFFICULTY_INSANE = {R.string.insane, 5, 30};
    public static final int[] DIFFICULTY_DEFAULT = DIFFICULTY_NORMAL;

    public static final int[][] DIFFICULTY_ARRAY = {
            DIFFICULTY_EASY,
            DIFFICULTY_NORMAL,
            DIFFICULTY_HARD,
            DIFFICULTY_INSANE,
    };

    /**
     * 根据难度获取保留数字的个数
     *
     * @param difficulty 游戏难度
     */
    public static int getInitNumbersByDifficulty(int difficulty, int numberCount) {
        int index = getArrayIndex(difficulty);
        double tmp = 0.65;
        if (index >= 0) {
            tmp = 0.01 * DIFFICULTY_ARRAY[index][2];
        }
        int result = (int) (tmp * numberCount * numberCount + 0.5F * RANDOM.nextInt(numberCount));

        if (DEBUG) {
            //            result = numberCount * numberCount - 1;
        }
        return result;
    }


    public static int getArrayIndex(int gameDifficulty) {
        int index = -1;
        for (int i = 0; i < Difficulty.DIFFICULTY_ARRAY.length; i++) {
            if (gameDifficulty == Difficulty.DIFFICULTY_ARRAY[i][1]) {
                index = i;
                break;
            }
        }
        if (DEBUG) {
            if (index == -1) {
                throw new IllegalArgumentException("index == -1");
            }
        }
        return index;
    }

    public static int getArrayNextIndex(int gameDifficulty) {
        int index = getArrayIndex(gameDifficulty);
        return (index + 1) % DIFFICULTY_ARRAY.length;
    }


}