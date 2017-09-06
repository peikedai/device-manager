package com.detroitlabs.devicemanager.sync.tasks;

import android.Manifest;
import android.os.Build;

import com.detroitlabs.devicemanager.utils.DeviceUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import javax.inject.Inject;

import io.reactivex.SingleEmitter;
import io.reactivex.functions.Consumer;


public class GetSerialNumberTask extends AsyncTask<String> {

    private final RxPermissions rxPermissions;

    @Inject
    GetSerialNumberTask(RxPermissions rxPermissions) {
        this.rxPermissions = rxPermissions;
    }

    @Override
    protected void task(final SingleEmitter<String> emitter) {
        if (rxPermissions.isGranted(Manifest.permission.READ_PHONE_STATE)) {
            emitter.onSuccess(DeviceUtil.getSerialNumber());
        } else {
            rxPermissions
                    .request(Manifest.permission.READ_PHONE_STATE)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean granted) throws Exception {
                            if (granted) {
                                String serial;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    serial = Build.getSerial();
                                } else {
                                    serial = Build.SERIAL;
                                }
                                try {
                                    DeviceUtil.setSerialNumber(serial);
                                } catch (IllegalAccessException e) {
                                    emitter.onError(e);
                                }
                                emitter.onSuccess(serial);
                            } else {
                                emitter.onSuccess(Build.UNKNOWN);
                            }
                        }
                    });
        }
    }
}
