package com.cpxiao.sudoku.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cpxiao.R;
import com.cpxiao.gamelib.fragment.BaseZAdsFragment;
import com.cpxiao.zads.core.ZAdPosition;

/**
 * @author cpxiao on 2017/09/05.
 */

public class BestScoreFragment extends BaseZAdsFragment {


    public static BestScoreFragment newInstance(Bundle bundle) {
        BestScoreFragment fragment = new BestScoreFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        loadZAds(ZAdPosition.POSITION_BEST_SCORE);

        Button button = (Button) view.findViewById(R.id.dialog_btn_ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFragment();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_best_score;
    }
}
