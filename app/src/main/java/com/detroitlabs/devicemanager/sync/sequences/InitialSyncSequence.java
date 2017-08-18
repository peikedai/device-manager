package com.detroitlabs.devicemanager.sync.sequences;


import android.util.Log;

import com.detroitlabs.devicemanager.sync.SignInResult;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

public final class InitialSyncSequence extends AsyncTaskSequence<Boolean> {
    private static final String TAG = InitialSyncSequence.class.getName();

    private final TestDeviceAutoSignInSequence autoSignInSequence;

    private final RegisterAndSyncDbSequence registerAndSyncDbSequence;

    @Inject
    public InitialSyncSequence(RegisterAndSyncDbSequence registerAndSyncDbSequence,
                               TestDeviceAutoSignInSequence autoSignInSequence) {
        this.registerAndSyncDbSequence = registerAndSyncDbSequence;
        this.autoSignInSequence = autoSignInSequence;
    }

    @Override
    public Single<Boolean> run() {
        return autoSignInSequence.run()
                .flatMap(registerAndSync());
    }

    public Observable<String> status() {
        return Observable.merge(statusSubject, registerAndSyncDbSequence.status(), autoSignInSequence.status());
    }

    private Function<SignInResult, Single<Boolean>> registerAndSync() {
        return new Function<SignInResult, Single<Boolean>>() {
            @Override
            public Single<Boolean> apply(@NonNull SignInResult result) throws Exception {
                if (result.isSuccess()) {
                    Log.d(TAG, "authentication successful. start register and sync");
                    updateStatus("Authentication successful!");
                    return registerAndSyncDbSequence.run();
                } else {
                    return Single.just(false);
                }
            }
        };
    }
}
