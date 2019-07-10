package com.github.doomsower;

import android.location.Location;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.yandex.metrica.PreloadInfo;
import com.yandex.metrica.YandexMetricaConfig;

public class Utils {
    public static YandexMetricaConfig toConfig(ReadableMap configMap) {
        final String apiKey = configMap.getString("apiKey");
        final YandexMetricaConfig.Builder builder = YandexMetricaConfig.newConfigBuilder(apiKey);

        if (configMap.hasKey("handleFirstActivationAsUpdate")) {
            builder.handleFirstActivationAsUpdate(configMap.getBoolean("handleFirstActivationAsUpdate"));
        }
        if (configMap.hasKey("locationTracking")) {
            builder.withLocationTracking(configMap.getBoolean("locationTracking"));
        }
        if (configMap.hasKey("sessionTimeout")) {
            builder.withSessionTimeout(configMap.getInt("sessionTimeout"));
        }
        if (configMap.hasKey("crashReporting")) {
            builder.withCrashReporting(configMap.getBoolean("crashReporting"));
        }
        if (configMap.hasKey("nativeCrashReporting")) {
            builder.withNativeCrashReporting(configMap.getBoolean("nativeCrashReporting"));
        }
        if (configMap.hasKey("appVersion")) {
            builder.withAppVersion(configMap.getString("appVersion"));
        }
        if (configMap.hasKey("logs") && configMap.getBoolean("logs")) {
            builder.withLogs();
        }
        if (configMap.hasKey("installedAppCollecting")) {
            builder.withInstalledAppCollecting(configMap.getBoolean("installedAppCollecting"));
        }
        if (configMap.hasKey("location")) {
            final Location location = toLocation(configMap.getMap("location"));
            builder.withLocation(location);
        }
        if (configMap.hasKey("preloadInfo")) {
            final ReadableMap preloadInfoMap = configMap.getMap("preloadInfo");
            final PreloadInfo.Builder infoBuilder = PreloadInfo.newBuilder(preloadInfoMap.getString("trackingId"));
            final ReadableMap additionalInfoMap = preloadInfoMap.getMap("additionalInfo");
            if (additionalInfoMap != null) {
                for (ReadableMapKeySetIterator keyIterator = additionalInfoMap.keySetIterator(); keyIterator.hasNextKey(); ) {
                    final String key = keyIterator.nextKey();
                    final String value = additionalInfoMap.getString(key);
                    infoBuilder.setAdditionalParams(key, value);
                }
            }
            builder.withPreloadInfo(infoBuilder.build());
        }

        return builder.build();
    }

    public static Location toLocation(ReadableMap locationMap) {
        final Location location = new Location("Custom");

        if (locationMap.hasKey("latitude")) {
            location.setLatitude(locationMap.getDouble("latitude"));
        }
        if (locationMap.hasKey("longitude")) {
            location.setLongitude(locationMap.getDouble("longitude"));
        }
        if (locationMap.hasKey("altitude")) {
            location.setAltitude(locationMap.getDouble("altitude"));
        }
        if (locationMap.hasKey("accuracy")) {
            location.setAccuracy((float) locationMap.getDouble("accuracy"));
        }
        if (locationMap.hasKey("course")) {
            location.setBearing((float) locationMap.getDouble("course"));
        }
        if (locationMap.hasKey("speed")) {
            location.setSpeed((float) locationMap.getDouble("speed"));
        }
        if (locationMap.hasKey("timestamp")) {
            location.setTime(locationMap.getInt("timestamp"));
        }

        return location;
    }
}
