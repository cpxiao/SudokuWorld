package com.cpxiao.sudoku.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cpxiao.R;
import com.cpxiao.androidutils.library.utils.PreferencesUtils;
import com.cpxiao.gamelib.fragment.BaseZAdsFragment;
import com.cpxiao.sudoku.mode.extra.Difficulty;
import com.cpxiao.sudoku.mode.extra.Extra;
import com.cpxiao.zads.core.ZAdPosition;

/**
 * @author cpxiao on 2017/09/05.
 */

public class SettingsFragment extends BaseZAdsFragment implements View.OnClickListener {
    private TextView mDifficulty;
    private TextView mSound;
    private TextView mMusic;

    public static SettingsFragment newInstance(Bundle bundle) {
        SettingsFragment fragment = new SettingsFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        loadZAds(ZAdPosition.POSITION_SETTINGS);

        Context context = getHoldingActivity();

        view.findViewById(R.id.layout_difficulty).setOnClickListener(this);
        view.findViewById(R.id.layout_sound).setOnClickListener(this);
        view.findViewById(R.id.layout_music).setOnClickListener(this);
        view.findViewById(R.id.btn_ok).setOnClickListener(this);

        mDifficulty = (TextView) view.findViewById(R.id.tv_difficulty);
        mSound = (TextView) view.findViewById(R.id.tv_sound);
        mMusic = (TextView) view.findViewById(R.id.tv_music);

        int difficulty = PreferencesUtils.getInt(context, Extra.Key.KEY_DIFFICULTY, Difficulty.DIFFICULTY_DEFAULT[1]);
        int index = Difficulty.getArrayIndex(difficulty);
        String msg = getString(Difficulty.DIFFICULTY_ARRAY[index][0]);
        mDifficulty.setText(msg);

        boolean isSoundOn = PreferencesUtils.getBoolean(context, Extra.Key.SETTING_SOUND, Extra.Key.SETTING_SOUND_DEFAULT);
        if (isSoundOn) {
            mSound.setText(R.string.settings_on);
        } else {
            mSound.setText(R.string.settings_off);
        }

        boolean isMusicOn = PreferencesUtils.getBoolean(context, Extra.Key.SETTING_MUSIC, Extra.Key.SETTING_MUSIC_DEFAULT);
        if (isMusicOn) {
            mMusic.setText(R.string.settings_on);
        } else {
            mMusic.setText(R.string.settings_off);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_settings;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Context context = getHoldingActivity();
        if (id == R.id.layout_difficulty) {
            int difficulty = PreferencesUtils.getInt(context, Extra.Key.KEY_DIFFICULTY, Difficulty.DIFFICULTY_DEFAULT[1]);
            int nextIndex = Difficulty.getArrayNextIndex(difficulty);
            String msg = getString(Difficulty.DIFFICULTY_ARRAY[nextIndex][0]);
            mDifficulty.setText(msg);
            PreferencesUtils.putInt(context, Extra.Key.KEY_DIFFICULTY, Difficulty.DIFFICULTY_ARRAY[nextIndex][1]);
        } else if (id == R.id.layout_sound) {
            boolean isSoundOn = PreferencesUtils.getBoolean(context, Extra.Key.SETTING_SOUND, Extra.Key.SETTING_SOUND_DEFAULT);
            if (isSoundOn) {
                PreferencesUtils.putBoolean(context, Extra.Key.SETTING_SOUND, false);
                mSound.setText(R.string.settings_off);
            } else {
                PreferencesUtils.putBoolean(context, Extra.Key.SETTING_SOUND, true);
                mSound.setText(R.string.settings_on);
            }
        } else if (id == R.id.layout_music) {
            boolean isMusicOn = PreferencesUtils.getBoolean(context, Extra.Key.SETTING_MUSIC, Extra.Key.SETTING_MUSIC_DEFAULT);
            if (isMusicOn) {
                PreferencesUtils.putBoolean(context, Extra.Key.SETTING_MUSIC, false);
                mMusic.setText(R.string.settings_off);
            } else {
                PreferencesUtils.putBoolean(context, Extra.Key.SETTING_MUSIC, true);
                mMusic.setText(R.string.settings_on);
            }
        } else if (id == R.id.btn_ok) {
            removeFragment();
        }
    }


}
