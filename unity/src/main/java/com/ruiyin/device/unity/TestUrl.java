package com.ruiyin.device.unity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestUrl {
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

    public void getUrl(String url) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse(url));
        getActivity().startActivity(intent);
    }
}
