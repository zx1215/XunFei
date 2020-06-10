package com.ruiyin.device.unity;

import android.app.Application;
import android.content.Context;
import com.iflytek.cloud.SpeechUtility;

public class App extends Application {
    public void onCreate() {
        SpeechUtility.createUtility((Context)this, "appid=" + getString(R.string.app_id));
        super.onCreate();
    }
}
