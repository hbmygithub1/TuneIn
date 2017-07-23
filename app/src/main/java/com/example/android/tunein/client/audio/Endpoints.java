package com.example.android.tunein.client.audio;

import com.example.android.tunein.model.Catalog;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface Endpoints {
	@GET("/{browsePath}")
	Call<Catalog> browse(
			@Path("browsePath") String path,
			@QueryMap Map<String, String> options);
}
