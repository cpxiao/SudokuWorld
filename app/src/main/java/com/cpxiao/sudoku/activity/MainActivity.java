package com.cpxiao.sudoku.activity;

import android.os.Bundle;

import com.cpxiao.gamelib.activity.BaseZAdsActivity;
import com.cpxiao.gamelib.fragment.BaseFragment;
import com.cpxiao.sudoku.fragment.HomeFragment;
import com.cpxiao.zads.ZAdManager;
import com.cpxiao.zads.core.ZAdPosition;

public class MainActivity extends BaseZAdsActivity {

    @Override
    protected BaseFragment getFirstFragment() {
        return HomeFragment.newInstance(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ZAdManager.getInstance().init(getApplicationContext());
        loadAds();
    }

    private void loadAds() {
//        initAdMobAds50("ca-app-pub-4157365005379790/5817111468");
//        initAdMobAds50("ca-app-pub-4157365005379790/8481785667");
//        initFbAds50("225277207901222_352811625147779");
//        initFbAds50("225277207901222_225282477900695");
        loadZAds(ZAdPosition.POSITION_MAIN);
    }
}
