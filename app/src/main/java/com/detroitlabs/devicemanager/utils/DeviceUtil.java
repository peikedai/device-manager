package com.detroitlabs.devicemanager.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.BatteryManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.detroitlabs.devicemanager.constants.Platform;
import com.detroitlabs.devicemanager.db.Device;
import com.facebook.device.yearclass.YearClass;
import com.jaredrummler.android.device.DeviceName;

import java.util.Locale;

public class DeviceUtil {

    public static Device readThisDevice(Context context) {
        Device device = new Device();
        device.platform = Platform.ANDROID;
        device.brandAndModel = getBrandAndModel();
        device.version = Build.VERSION.RELEASE;
        device.screenResolution = getResolution(context);
        device.screenSize = getSize(context);
        device.serialNumber = getLocalSerialNumber(context);
        device.yearClass = getYearClass(context);
        device.isSamsung = getIsSamsung();
        return device;
    }

    public static String getSerialNumber() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Build.getSerial();
        } else {
            return Build.SERIAL;
        }
    }

    public static String getLocalSerialNumber(Context context) {
        int value = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
        if (value == PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Build.getSerial();
        } else {
            return Build.SERIAL;
        }
    }

    public static double getBatteryLevel(Context context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        double percent = level / (double) scale;
        return Math.round(percent * 100.0) / 100.0;
    }

    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    public static String getBrandAndModel() {
        return DeviceName.getDeviceName();
    }

    private static String getSize(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getRealMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        double wi = (double) width / (double) dm.xdpi;
        double hi = (double) height / (double) dm.ydpi;
        double x = Math.pow(wi, 2);
        double y = Math.pow(hi, 2);
        double screenInches = Math.sqrt(x + y);
        return String.format(Locale.getDefault(), "%.1f in", screenInches);
    }

    private static String getResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        int width = size.x;
        int height = size.y;
        return width + " × " + height + " px";
    }

    private static String getYearClass(Context context) {
        return String.valueOf(YearClass.get(context));
    }

    private static String getIsSamsung() {
        return Build.MANUFACTURER.toLowerCase().contains("samsung") ? "Yes" : "No";
    }
}
