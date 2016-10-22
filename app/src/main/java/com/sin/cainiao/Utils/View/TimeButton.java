package com.sin.cainiao.utils.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.sin.cainiao.fragment.login_register.RegisterFragment;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TimeButton extends Button implements View.OnClickListener {
    private long length = 60 * 1000;// 倒计时长度,这里给了默认60秒
    private String textAfter = "秒后重新获取";
    private String textBefore = "点击获取验证码";
    private final String TIME = "time";
    private final String CTIME = "ctime";
    private OnClickListener mOnclickListener;
    private Timer t;
    private TimerTask tt;
    private long time;
    private boolean start = false;
    Map<String, Long> map = new HashMap<>();

    public TimeButton(Context context) {
        super(context);
        setOnClickListener(this);

    }

    public TimeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        Context mContext = context;
        setOnClickListener(this);
    }

    @SuppressLint("HandlerLeak")
    private
    Handler han = new Handler() {
        public void handleMessage(android.os.Message msg) {
            TimeButton.this.setText(time / 1000 + textAfter);
            time -= 1000;
            if (time < 0) {
                TimeButton.this.setEnabled(true);
                TimeButton.this.setText(textBefore);
                clearTimer();
            }
        }
    };

    private void initTimer() {
        time = length;
        t = new Timer();
        tt = new TimerTask() {
            @Override
            public void run() {
                han.sendEmptyMessage(0x01);
            }
        };
    }

    private void clearTimer() {
        if (tt != null) {
            tt.cancel();
            tt = null;
        }
        if (t != null)
            t.cancel();
        t = null;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        if (l instanceof TimeButton) {
            super.setOnClickListener(l);
        } else
            this.mOnclickListener = l;
    }

    @Override
    public void onClick(View v) {
        if (mOnclickListener != null)
            mOnclickListener.onClick(v);

        if (start){
            initTimer();
            this.setText(time / 1000 + textAfter);
            this.setEnabled(false);
            t.schedule(tt, 0, 1000);
        }
    }

    public void setStart(Boolean start){
        this.start = start;
    }

    /**
     * 和activity的onDestroy()方法同步
     */
    public void onDestroy() {
        if (RegisterFragment.map == null)
            RegisterFragment.map = new HashMap<>();
        RegisterFragment.map.put(TIME, time);
        RegisterFragment.map.put(CTIME, System.currentTimeMillis());
        clearTimer();
    }

    /**
     * 和activity的onCreate()方法同步
     */
    public void onCreate(Bundle bundle) {
        if (RegisterFragment.map == null)
            return;
        if (RegisterFragment.map.size() <= 0)// 这里表示没有上次未完成的计时
            return;
        long time = System.currentTimeMillis() - RegisterFragment.map.get(CTIME)
                - RegisterFragment.map.get(TIME);
        RegisterFragment.map.clear();
        if (time <= 0){
            initTimer();
            this.time = Math.abs(time);
            t.schedule(tt, 0, 1000);
            this.setText(time + textAfter);
            this.setEnabled(false);
        }
    }

    /**
     * 设置计时时候显示的文本
     */
    public TimeButton setTextAfter(String text1) {
        this.textAfter = text1;
        return this;
    }

    /**
     * 设置点击之前的文本
     */
    public TimeButton setTextBefore(String text0) {
        this.textBefore = text0;
        this.setText(textBefore);
        return this;
    }

    /**
     * 设置到计时长度
     *
     * @param lenght 时间 默认毫秒
     * @return TimeButton button
     */
    public TimeButton setLenght(long lenght) {
        this.length = lenght;
        return this;
    }
}
