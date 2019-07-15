package com.github.doomsower;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.react.bridge.ActivityEventListener;
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

import javax.annotation.Nullable;

public class RNMetricaModule extends ReactContextBaseJavaModule implements LifecycleEventListener, ActivityEventListener {
    private final static String TAG = RNMetricaModule.class.getCanonicalName();

    private boolean mAppMetricaActivated = false;

    public RNMetricaModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.getReactApplicationContext().addLifecycleEventListener(this);
        this.getReactApplicationContext().addActivityEventListener(this);
    }

    @Override
    public String getName() {
        return "RNMetrica";
    }

    @ReactMethod
    private void activate(ReadableMap configMap) {
        if (mAppMetricaActivated) {
            Log.d(TAG, "already activated");
            return;
        }
        final YandexMetricaConfig config = Utils.toConfig(configMap);

        final Context context = getReactApplicationContext();
        YandexMetrica.activate(context, config);
        YandexMetrica.reportAppOpen(getCurrentActivity());

        Log.d(TAG, "activated");
        mAppMetricaActivated = true;
    }

    @ReactMethod
    private void handleOpenURL(@NonNull String url) {
        YandexMetrica.reportAppOpen(getCurrentActivity());
    }

    @ReactMethod
    private void reportReferralUrl(@NonNull String url) {
        YandexMetrica.reportReferralUrl(url);
    }

    @ReactMethod
    private void reportEvent(@NonNull String eventName, @Nullable ReadableMap params, @Nullable Callback errorCallback) {
        if (params != null) {
            try {
                JSONObject payload = MapUtils.toJSONObject(params);
                YandexMetrica.reportEvent(eventName, payload.toString());
            } catch (JSONException e) {
                if (errorCallback != null) {
                    errorCallback.invoke(e.getMessage());
                }
            }
        } else {
            YandexMetrica.reportEvent(eventName);
        }
    }

    @ReactMethod
    private void reportError(@NonNull String errorName, @Nullable String errorReason, @Nullable Callback errorCallback) {
        Throwable errorThrowable = null;
        try {
            errorThrowable = new Throwable(errorReason);
        } catch (Exception ignored) {
        }

        YandexMetrica.reportError(errorName, errorThrowable);
    }

    @ReactMethod
    private void setProfileID(@Nullable String profileID) {
        YandexMetrica.setUserProfileID(profileID);
    }

    @ReactMethod
    private void setLocation(@Nullable ReadableMap locationMap) {
        final Location location = Utils.toLocation(locationMap);
        YandexMetrica.setLocation(location);
    }

    @ReactMethod
    private void setLocationTracking(@NonNull Boolean enabled) {
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

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onNewIntent(Intent intent) {
        YandexMetrica.reportAppOpen(getCurrentActivity());
    }
}
