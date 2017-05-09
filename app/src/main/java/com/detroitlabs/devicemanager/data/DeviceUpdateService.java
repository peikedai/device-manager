package com.detroitlabs.devicemanager.data;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.detroitlabs.devicemanager.utils.DeviceUtil;
import com.google.firebase.database.FirebaseDatabase;

import static com.detroitlabs.devicemanager.data.DatabaseContract.TABLE_DEVICES;

public class DeviceUpdateService extends IntentService {

    private static final String TAG = DeviceUpdateService.class.getSimpleName();
    private static final String ACTION = TAG + ".ACTION";
    private static final String EXTRA = TAG + ".EXTRA";
    private static final String ACTION_CHECK_IN = ACTION + ".CHECK_IN";
    private static final String ACTION_CHECK_OUT = ACTION + ".CHECK_OUT";
    private static final String ACTION_REQUEST = ACTION + ".REQUEST";
    private static final String EXTRA_SERIAL_NUMBER = EXTRA + ".SERIAL_NUMBER";
    private static final String EXTRA_CHECKED_OUT_BY = EXTRA + ".CHECKED_OUT_BY";
    private static final String EXTRA_REQUESTED_BY = EXTRA + ".REQUESTED_BY";

    public static void checkInDevice(Context context) {
        Intent intent = new Intent(context, DeviceUpdateService.class);
        intent.setAction(ACTION_CHECK_IN);
        intent.putExtra(EXTRA_SERIAL_NUMBER, DeviceUtil.getSerialNumber());
        context.startService(intent);
    }

    public static void checkOutDevice(Context context, String checkedOutBy) {
        Intent intent = new Intent(context, DeviceUpdateService.class);
        intent.setAction(ACTION_CHECK_OUT);
        intent.putExtra(EXTRA_SERIAL_NUMBER, DeviceUtil.getSerialNumber());
        intent.putExtra(EXTRA_CHECKED_OUT_BY, checkedOutBy);
        context.startService(intent);
    }

    public static void requestDevice(Context context, String serialNumber, String requestBy) {
        Intent intent = new Intent(context, DeviceUpdateService.class);
        intent.setAction(ACTION_REQUEST);
        intent.putExtra(EXTRA_SERIAL_NUMBER, serialNumber);
        intent.putExtra(EXTRA_REQUESTED_BY, requestBy);
        context.startService(intent);
    }

    public DeviceUpdateService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String serialNumber = intent.getStringExtra(EXTRA_SERIAL_NUMBER);
        if (ACTION_CHECK_IN.equals(intent.getAction())) {
            performCheckIn(serialNumber);
        } else if (ACTION_CHECK_OUT.equals(intent.getAction())) {
            String checkedOutBy = intent.getStringExtra(EXTRA_CHECKED_OUT_BY);
            performCheckOut(serialNumber, checkedOutBy);
        } else if (ACTION_REQUEST.equals(intent.getAction())) {
            String requestedBy = intent.getStringExtra(EXTRA_REQUESTED_BY);
            performRequest(serialNumber, requestedBy);
        }
    }

    private void performCheckIn(String serialNumber) {
        FirebaseDatabase.getInstance().getReference()
                .child(TABLE_DEVICES)
                .child(serialNumber)
                .child(DatabaseContract.DeviceColumns.CHECKED_OUT_BY)
                .setValue("");
    }

    private void performCheckOut(String serialNumber, String checkedOutBy) {
        FirebaseDatabase.getInstance().getReference()
                .child(TABLE_DEVICES)
                .child(serialNumber)
                .child(DatabaseContract.DeviceColumns.CHECKED_OUT_BY)
                .setValue(checkedOutBy);
    }

    private void performRequest(String serialNumber, String requestedBy) {
        FirebaseDatabase.getInstance().getReference()
                .child(TABLE_DEVICES)
                .child(serialNumber)
                .child(DatabaseContract.DeviceColumns.REQUESTED_BY)
                .setValue(requestedBy);
    }
}
