package com.cpxiao.sudoku.mode.extra;

/**
 * @author cpxiao on 2016/11/21.
 */
public final class Extra {

    public static final class Key {

        public static final String KEY_DIFFICULTY = "KEY_DIFFICULTY";

        public static final long KEY_BEST_SCORE_DEFAULT = 0;

        private static final String BEST_SCORE_FORMAT = "KEY_BEST_SCORE_%s_%s";


        public static final String KEY_SAVED_SUDOKU = "KEY_SAVED_SUDOKU";

        public static String getBestScoreKey(int gameMode, int difficulty) {
            return String.format(BEST_SCORE_FORMAT, gameMode, difficulty);
        }

        /**
         * 音效开关，默认开
         */
        public static final String SETTING_SOUND = "SETTING_SOUND";

        public static final boolean SETTING_SOUND_DEFAULT = true;
        /**
         * 音乐开关，默认开
         */
        public static final String SETTING_MUSIC = "SETTING_MUSIC";

        public static final boolean SETTING_MUSIC_DEFAULT = true;
    }

    public static final class Name {
        public static final String INTENT_EXTRA_IS_NEW_GAME = "INTENT_EXTRA_IS_NEW_GAME";
    }

}
