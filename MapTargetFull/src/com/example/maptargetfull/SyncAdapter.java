package com.example.maptargetfull;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
	
	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		// TODO Auto-generated constructor stub
	}
	
	public SyncAdapter(Context context, boolean autoInitialize,
			boolean allowParallelSyncs) {
		super(context, autoInitialize, allowParallelSyncs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		
		
	}

}
