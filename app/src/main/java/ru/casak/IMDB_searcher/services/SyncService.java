package ru.casak.IMDB_searcher.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import ru.casak.IMDB_searcher.adapters.SyncAdapter;

public class SyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();

    private static SyncAdapter mSyncAdapter;


    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock){
            if(mSyncAdapter == null){
                mSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mSyncAdapter.getSyncAdapterBinder();
    }
}
