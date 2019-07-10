package com.github.doomsower;

import android.content.Context;
import android.location.Location;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

import org.json.JSONException;
import org.json.JSONObject;

public class RNMetricaModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private boolean mAppMetricaActivated = false;

    public RNMetricaModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "RNMetrica";
    }

    @ReactMethod
    private void activate(ReadableMap configMap) {
        if (mAppMetricaActivated) {
            return;
        }
        final YandexMetricaConfig config = Utils.toConfig(configMap);

        final Context context = getReactApplicationContext();
        YandexMetrica.activate(context, config);
        YandexMetrica.reportAppOpen(getCurrentActivity());

        mAppMetricaActivated = true;
    }

    @ReactMethod
    private void reportReferralUrl(String url) {
        YandexMetrica.reportReferralUrl(url);
    }

    @ReactMethod
    private void reportEvent(String eventName, ReadableMap params, Callback errorCallback) {
        if (params != null) {
            try {
                JSONObject payload = MapUtils.toJSONObject(params);
                YandexMetrica.reportEvent(eventName, payload.toString());
            } catch (JSONException e) {
                errorCallback.invoke(e.getMessage());
            }
        } else {
            YandexMetrica.reportEvent(eventName);
        }
    }

    @ReactMethod
    private void reportError(String errorName, String errorReason, Callback errorCallback) {
        Throwable errorThrowable = null;
        try {
            errorThrowable = new Throwable(errorReason);
        } catch (Exception ignored) {
        }

        YandexMetrica.reportError(errorName, errorThrowable);
    }

    @ReactMethod
    private void setLocation(ReadableMap locationMap) {
        final Location location = Utils.toLocation(locationMap);
        YandexMetrica.setLocation(location);
    }

    @ReactMethod
    private void setLocationTracking(Boolean enabled) {
        YandexMetrica.setLocationTracking(enabled);
    }

    @Override
    public void onHostResume() {
        if (mAppMetricaActivated) {
            YandexMetrica.resumeSession(getCurrentActivity());
        }
    }

    @Override
    public void onHostPause() {
        if (mAppMetricaActivated) {
            YandexMetrica.pauseSession(getCurrentActivity());
        }
    }

    @Override
    public void onHostDestroy() {
    }
}
