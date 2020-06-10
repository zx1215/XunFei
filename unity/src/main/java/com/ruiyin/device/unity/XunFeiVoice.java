package com.ruiyin.device.unity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class XunFeiVoice {
    private SpeechRecognizer mIat;

    private HashMap<String, String> mIatResults = new LinkedHashMap<>();

    private String result;

    private Toast mToast;

    private String mEngineType = "cloud";

    private boolean mTranslateEnable = false;

    private StringBuffer buffer = new StringBuffer();

    private int ret;

    JSONObject jsonObject = new JSONObject();

    private Activity _unityActivity;

    Activity getActivity() {
        if (null == this._unityActivity)
            try {
                Class<?> classtype = Class.forName("com.unity3d.player.UnityPlayer");
                Activity activity = (Activity)classtype.getDeclaredField("currentActivity").get(classtype);
                this._unityActivity = activity;
            } catch (ClassNotFoundException classNotFoundException) {

            } catch (IllegalAccessException illegalAccessException) {

            } catch (NoSuchFieldException noSuchFieldException) {}
        return this._unityActivity;
    }

    boolean AndroidCallUnity(String gameObjectName, String functionName, String args) {
        try {
            Class<?> classtype = Class.forName("com.unity3d.player.UnityPlayer");
            Method method = classtype.getMethod("UnitySendMessage", new Class[] { String.class, String.class, String.class });
            method.invoke(classtype, new Object[] { gameObjectName, functionName, args });
            return true;
        } catch (ClassNotFoundException classNotFoundException) {

        } catch (NoSuchMethodException noSuchMethodException) {

        } catch (IllegalAccessException illegalAccessException) {

        } catch (InvocationTargetException invocationTargetException) {}
        return false;
    }

    public void InitXunFei() {
        this.mToast = Toast.makeText((Context)getActivity(), "", Toast.LENGTH_LONG);
        SpeechUtility.createUtility((Context)getActivity(), "appid=5d7eee93");
        this.mIat = SpeechRecognizer.createRecognizer((Context)getActivity(), null);
    }

    public void CancelXunFei() {
        if (null != this.mIat)
            this.mIat.cancel();
    }

    public void XunFeiVoiceReadWrite(String str) {
        if ("0".equals(str)) {
            start();
        } else if ("1".equals(str)) {
            stop();
        }
    }

    public void start() {
        this.buffer.setLength(0);
        this.mIatResults.clear();
        setParam();
        this.ret = this.mIat.startListening(this.mRecognizerListener);
        if (this.ret != 0);
    }

    public void stop() {
        this.mIat.stopListening();
    }

    private RecognizerListener mRecognizerListener = new RecognizerListener() {
        public void onBeginOfSpeech() {}

        public void onError(SpeechError error) {
            try {
                //来了 老弟
                XunFeiVoice.this.jsonObject.put("result", "FAILED");
                XunFeiVoice.this.jsonObject.put("msg", "Error");
                XunFeiVoice.this.AndroidCallUnity("Driver", "UnityMethod", XunFeiVoice.this.jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void onEndOfSpeech() {}

        public void onResult(RecognizerResult results, boolean isLast) {
            XunFeiVoice.this.printResult(results);
        }

        public void onVolumeChanged(int volume, byte[] data) {}

        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {}
    };

    private void printResult(RecognizerResult results) {
        String text = parseIatResult(results.getResultString());
        String sn = null;
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.mIatResults.put(sn, text);
        StringBuffer resultBuffer = new StringBuffer();
        for (String key : this.mIatResults.keySet())
            resultBuffer.append(this.mIatResults.get(key));
        this.result = resultBuffer.toString();
        try {
            this.jsonObject.put("result", "SUCCESS");
            this.jsonObject.put("msg", this.result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidCallUnity("Driver", "UnityMethod", this.jsonObject.toString());
    }

    public void setParam() {
        this.mIat.setParameter("params", null);
        this.mIat.setParameter("engine_type", this.mEngineType);
        this.mIat.setParameter("result_type", "json");
        this.mIat.setParameter("language", "zh_cn");
        this.mIat.setParameter("accent", "mandarin");
        this.mIat.setParameter("vad_bos", "4000");
        this.mIat.setParameter("vad_eos", "1000");
        this.mIat.setParameter("asr_ptt", "1");
    }

    public String parseIatResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);
            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                ret.append(obj.getString("w"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
    }
}
