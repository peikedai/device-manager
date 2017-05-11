package com.detroitlabs.devicemanager.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.detroitlabs.devicemanager.constants.Platform;
import com.detroitlabs.devicemanager.models.Device;
import com.facebook.device.yearclass.YearClass;
import com.jaredrummler.android.device.DeviceName;

import java.util.Locale;

public class DeviceUtil {

    public static boolean hasGetAccountsPermission(Context context) {
        return ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED;
    }

    public static void requestGetAccountsPermission(Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(activity,
                new String[]{android.Manifest.permission.GET_ACCOUNTS},
                requestCode);
    }

    public static Device readThisDevice(Context context) {
        Device device = new Device();
        device.platform = Platform.ANDROID;
        device.brandAndModel = getBrandAndModel();
        device.version = Build.VERSION.RELEASE;
        device.screenResolution = getResolution(context);
        device.screenSize = getSize(context);
        device.serialNumber = getSerialNumber();
        device.yearClass = getYearClass(context);
        device.isSamsung = getIsSamsung();
        return device;
    }

    public static String getSerialNumber() {
        return Build.SERIAL;
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

    private static String getBrandAndModel() {
        return DeviceName.getDeviceName();
    }

    private static String getYearClass(Context context) {
        return String.valueOf(YearClass.get(context));
    }

    private static String getIsSamsung() {
        return Build.MANUFACTURER.toLowerCase().contains("samsung") ? "Yes" : "No";
    }
}
