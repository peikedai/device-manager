package com.detroitlabs.devicemanager.di;


import android.app.Application;
import android.arch.persistence.room.Room;

import com.detroitlabs.devicemanager.db.DeviceDao;
import com.detroitlabs.devicemanager.db.DeviceDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {
    @Provides
    @Singleton
    DeviceDatabase providesDeviceDatabase(Application application) {
        return Room.databaseBuilder(application, DeviceDatabase.class, "device.db")
                .fallbackToDestructiveMigration().build();
    }

    @Provides
    @Singleton
    DeviceDao providesDeviceDao(DeviceDatabase db) {
        return db.deviceDao();
    }
}
