package com.cpxiao.sudoku.mode.extra;

import com.cpxiao.R;

/**
 * @author cpxiao on 2017/09/05.
 */

public final class GameMode {
    public static final String BIG_BLOCK_COUNT_X = "BIG_BLOCK_COUNT_X";
    public static final String BIG_BLOCK_COUNT_Y = "BIG_BLOCK_COUNT_Y";

    private static int[] mode4 = {R.string.btn_4_grid, 4, 2, 2};
    private static int[] mode6 = {R.string.btn_6_grid, 6, 2, 3};
    private static int[] mode9 = {R.string.btn_9_grid, 9, 3, 3};
    private static int[] mode12 = {R.string.btn_12_grid, 12, 3, 4};
    private static int[] mode16 = {R.string.btn_16_grid, 16, 4, 4};
    private static int[] mode20 = {R.string.btn_20_grid, 20, 4, 5};

    public static int[][] MODE_ARRAY = {mode4, mode6, mode9, mode12, mode16, mode20};
}
