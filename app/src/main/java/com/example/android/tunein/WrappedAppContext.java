package com.example.android.tunein;

import android.content.Context;

import com.example.android.tunein.client.ApiClient;
import com.squareup.otto.Bus;

public interface WrappedAppContext {
	Bus getBus();
	Context getContext();
	ApiClient getClient();
}
