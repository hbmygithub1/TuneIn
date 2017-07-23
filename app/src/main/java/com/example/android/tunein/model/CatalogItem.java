package com.example.android.tunein.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CatalogItem {

	@SerializedName("key")
	private String mKey;

	@SerializedName("text")
	private String mText;

	@SerializedName("subtext")
	private String mSubText;

	@SerializedName("image")
	private String mImageUrl;

	@SerializedName("type")
	private String mType;

	@SerializedName("URL")
	private String mURL;

	@SerializedName("children")
	private List<CatalogItem> mChildren;

	public String getKey() {
		return mKey;
	}

	public String getText() {
		return mText;
	}

	public void setText(String text) {
		mText = text;
	}

	public String getType() {
		return mType;
	}

	public String getURL() {
		return mURL;
	}

	public List<CatalogItem> getChildren() {
		return mChildren;
	}

	public void setChildren(List<CatalogItem> children) {
		mChildren = children;
	}

	public String getSubText() {
		return mSubText;
	}

	public String getImageUrl() {
		return mImageUrl;
	}
}
