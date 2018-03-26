package com.cpxiao.gamelib.views;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 计时器控件。Timer、TimeTask、Handler、Message实现
 * 间隔为10毫秒时，一分钟误差为2.6秒
 * 改进：调用mTimer.scheduleAtFixedRate(mTimeTask, 10, 10);方法，可减少误差，两分钟误差1-2秒
 * 继续改进：将间隔改为29/31/33/37等随机毫秒，减少误差且可以模拟精确到毫秒
 *
 * @author cpxiao on 2017/6/6.
 * @version cpxiao on 2017/9/2.重构变量mTime
 */
public class TimeTextView extends AppCompatTextView {

    /**
     * 是否正在计时
     */
    private boolean isTiming = false;

    /**
     * 时间，单位毫秒
     */
    private long mTimeMillis = 0;

    /**
     * 间隔时间，单位毫秒
     */
    protected long mInterval = 37;
    private static final long mIntervalArray[] = {29, 31, 33, 37, 39, 41, 43, 47, 49};

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    setText(timeFormat(mTimeMillis));
            }
            return true;
        }
    });
    private Timer mTimer = new Timer();
    private TimerTask mTimeTask = null;

    /**
     * 计时
     *
     * @param context context
     */
    public TimeTextView(Context context) {
        super(context);
        init();
    }

    public TimeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setText(timeFormat(mTimeMillis));
        setGravity(Gravity.CENTER);
        mInterval = getRandomInterval();
        initTimeTask();
    }

    private void initTimeTask() {
        if (mTimeTask != null) {
            return;
        }
        mTimeTask = new TimerTask() {
            @Override
            public void run() {
                updateTime();
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        };
    }


    protected void updateTime() {
        if (isTiming) {
            mTimeMillis += mInterval;
        }
    }

    protected String timeFormat(long time) {
        double result = (double) time / 1000.0;
        DecimalFormat df = new DecimalFormat("0.0");
        return df.format(result) + "\"";
    }

    /**
     * 获取随机时间间隔
     *
     * @return int
     */
    private long getRandomInterval() {
        int index = ((int) (Math.random() * mIntervalArray.length)) % mIntervalArray.length;
        return mIntervalArray[index];
    }

    /**
     * 开始
     */
    public void start() {
        if (!isTiming) {
            if (mTimeTask == null) {
                initTimeTask();
            }
            mTimer.scheduleAtFixedRate(mTimeTask, mInterval, mInterval);
        }
        isTiming = true;
    }

    /**
     * 暂停
     */
    public void pause() {
        isTiming = false;
        //
        cancelTimeTask();
    }

    /**
     * 结束
     */
    public void destroy() {
        mTimer.cancel();
        mTimer = null;
        cancelTimeTask();
    }

    /**
     * 取消
     */
    protected void cancelTimeTask() {
        if (mTimeTask != null) {
            mTimeTask.cancel();
            mTimeTask = null;
        }
    }

    public void setTimeMillis(long timeMillis) {
        mTimeMillis = timeMillis;
    }

    public long getTimeMillis() {
        return mTimeMillis;
    }
}