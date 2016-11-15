package ru.casak.IMDB_searcher.services;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import ru.casak.IMDB_searcher.adapters.SyncAdapter;
import ru.casak.IMDB_searcher.providers.TMDBContentProvider;

public class SyncService extends Service {
    private static final Integer SYNC_FREQUENCY = 60*60*24; //24 hours
    public static final String ACCOUNT_TYPE = "ru.casak.IMDB_searcher.account";

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

    public static void enableSync(Context context) {
        Account account = AuthenticatorService.getAccount(ACCOUNT_TYPE);
        AccountManager mAccountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        if(mAccountManager.addAccountExplicitly(account, null, null)) {
            String authority = TMDBContentProvider.AUTHORITY;
            ContentResolver.setIsSyncable(account, authority, 1);
            ContentResolver.setSyncAutomatically(account, authority, true);
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), SYNC_FREQUENCY);
        }
        startSync(account);
    }

    public static void startSync(Account account){
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(account, TMDBContentProvider.AUTHORITY, settingsBundle);
    }
}
