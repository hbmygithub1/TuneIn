package com.example.android.tunein;

import android.app.Application;
import android.content.Context;

import com.example.android.tunein.client.ApiClient;
import com.squareup.otto.Bus;

public class App extends Application implements WrappedAppContext {

	private Bus mBus;
	private ApiClient mTuneInClient;

	@Override
	public void onCreate() {
		super.onCreate();
		mBus = new Bus();
		mTuneInClient = new ApiClient(this);
	}

	@Override
	public Bus getBus() {
		return mBus;
	}

	@Override
	public Context getContext() {
		return getApplicationContext();
	}

	@Override
	public ApiClient getClient() {
		return mTuneInClient;
	}
}
