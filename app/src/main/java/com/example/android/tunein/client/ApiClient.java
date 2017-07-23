package com.example.android.tunein.client;

import android.net.Uri;
import android.util.Log;

import com.example.android.tunein.R;
import com.example.android.tunein.WrappedAppContext;
import com.example.android.tunein.client.audio.Endpoints;
import com.example.android.tunein.model.Catalog;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
	private static final String TAG = ApiClient.class.getSimpleName();
	private WrappedAppContext mContext;
	private static final String TUNE_IN_URL = "http://opml.radiotime.com";
	private Endpoints mHttpClient = getHttpClient();

	public ApiClient(WrappedAppContext context) {
		mContext = context;
	}

	public void browseDetails(String uri) {
		String path = "";
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("render", "json");

		if ((uri != null) && uri.isEmpty()) {
			path = "/Browse.ashx";
		} else {
			Uri parsedUri = Uri.parse(uri);
			path = parsedUri.getPath();
			for (String queryKey : parsedUri.getQueryParameterNames()) {
				data.put(queryKey, parsedUri.getQueryParameter(queryKey));
			}
		}
		sendBrowseRequest(data, path);
	}

	public void sendBrowseRequest(HashMap<String, String> data, String path) {

		mHttpClient.browse(path, data).enqueue(new Callback<Catalog>() {
			@Override
			public void onResponse(Call<Catalog> call, Response<Catalog> response) {
				Log.d(TAG,"url " + call.request().url() + " message " + response.message() + " is success " + response.isSuccessful() + " code " + response.code());

				if (response.body() != null) {
					Catalog catalog = response.body();
					mContext.getBus().post(new ApiResponse(catalog));
				} else {
					mContext.getBus().post(new ApiResponse(new Throwable(mContext.getContext().getString(R.string.error_data))));
				}
			}

			@Override
			public void onFailure(Call<Catalog> call, Throwable t) {
				mContext.getBus().post(new ApiResponse(t));
			}
		});
	}

	protected Endpoints getHttpClient() {
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(TUNE_IN_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.build();

		return retrofit.create(Endpoints.class);
	}

	public static class ApiResponse {
		public final boolean mSuccess;
		public final Catalog mCatalog;
		public final Throwable mThrowable;

		public ApiResponse(Catalog catalog) {
			mCatalog = catalog;
			mSuccess = true;
			mThrowable = null;
		}

		public ApiResponse(Throwable e){
			mCatalog = null;
			mSuccess = false;
			mThrowable = e;
		}
	}
}
