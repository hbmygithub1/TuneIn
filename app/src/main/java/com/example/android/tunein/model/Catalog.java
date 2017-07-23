package com.example.android.tunein.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Catalog {

	@SerializedName("head")
	private Header mHeader;

	@SerializedName("body")
	private List<CatalogItem> mCatalogItems;

	public Header getHeader() {
		return mHeader;
	}

	public List<CatalogItem> getCatalogItems() {
		return mCatalogItems;
	}

	public void setCatalogItems(List<CatalogItem> catalogItems) {
		mCatalogItems = catalogItems;
	}

	public class Header {
		@SerializedName("title")
		private String mTitle;

		public String getTitle() {
			return mTitle;
		}
	}
}
