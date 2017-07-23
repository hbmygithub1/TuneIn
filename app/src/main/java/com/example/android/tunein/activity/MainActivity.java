package com.example.android.tunein.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.tunein.R;
import com.example.android.tunein.WrappedAppContext;
import com.example.android.tunein.client.ApiClient;
import com.example.android.tunein.fragment.CustomDialogFragment;
import com.example.android.tunein.model.Catalog;
import com.example.android.tunein.model.CatalogItem;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

	private static final String TAG = MainActivity.class.getSimpleName();
	private static final String EXTRA_URL = "URL";

	@BindView(R.id.audio_detail_expand_listview)
	protected ExpandableListView mExpandableListView;

	@BindView(R.id.browse_list_empty_text_view)
	protected TextView mEmptyTextView;

	@BindView(R.id.browse_toolbar)
	protected Toolbar mToolbar;

	private ItemAdapter mAdapter;
	private CustomDialogFragment mDialogFragment;
	private String PROGRESS_DIALOG_FRAGMENT_TAG = "PROGRESS_DIALOG_FRAGMENT";
	private String mURL = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		setSupportActionBar(mToolbar);
		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		Intent intent = getIntent();
		if (intent.hasExtra(EXTRA_URL)) {
			mURL = intent.getStringExtra(EXTRA_URL);
		}

		mExpandableListView.setEmptyView(mEmptyTextView);

		mAdapter = new ItemAdapter();
		mExpandableListView.setAdapter(mAdapter);

		mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				CatalogItem item = (CatalogItem) parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
				if ((item.getType() != null) && item.getType().equals("audio")) {
					return true;
				}

				Intent intent = new Intent(MainActivity.this, MainActivity.class);
				intent.putExtra(EXTRA_URL, item.getURL());
				startActivity(intent);
				((WrappedAppContext) getApplication()).getClient().browseDetails(item.getURL());
				return true;
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		((WrappedAppContext) getApplication()).getBus().register(this);

		((WrappedAppContext) getApplication()).getClient().browseDetails(mURL);

		mDialogFragment = new CustomDialogFragment();
		mDialogFragment.show(getSupportFragmentManager(), PROGRESS_DIALOG_FRAGMENT_TAG);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mDialogFragment != null) {
			mDialogFragment.dismiss();
		}
		((WrappedAppContext) getApplication()).getBus().unregister(this);
	}

	@Subscribe
	public void onBrowseDetailAvailable(ApiClient.ApiResponse response) {
		Catalog catalog = response.mCatalog;

		if (response.mSuccess) {
			if (catalog.getHeader() != null) {
				getSupportActionBar().setTitle(catalog.getHeader().getTitle());

				if ((catalog.getHeader().getTitle() != null) && catalog.getHeader().getTitle().equals("Browse")) {
					getSupportActionBar().setDisplayHomeAsUpEnabled(false);
				} else {
					getSupportActionBar().setDisplayHomeAsUpEnabled(true);
				}
			}


			boolean noChildren = false;
			List<CatalogItem> items = catalog.getCatalogItems();
			for (CatalogItem item : items) {
				if (item.getChildren() == null) {
					noChildren = true;
					break;
				}
			}
			if (noChildren) {
				noChildren = false;
				List<CatalogItem> itemsWithChildren = new ArrayList<CatalogItem>();
				CatalogItem newDetail = new CatalogItem();
				newDetail.setText("");

				newDetail.setChildren(items);
				itemsWithChildren.add(newDetail);
				catalog.setCatalogItems(itemsWithChildren);
			}

			mAdapter.setItems(catalog.getCatalogItems());
		} else {
			if (mDialogFragment != null) {
				mDialogFragment.dismiss();
			}

			mEmptyTextView.setText(response.mThrowable.getMessage());
		}
	}

	private class ItemAdapter extends BaseExpandableListAdapter {
		private List<CatalogItem> mCatalogItems = new ArrayList<CatalogItem>();

		public void setItems(List<CatalogItem> catalogItems) {
			mCatalogItems = catalogItems;
			notifyDataSetChanged();
		}

		@Override
		public int getGroupCount() {
			return mCatalogItems.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return mCatalogItems.get(groupPosition).getChildren().size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return mCatalogItems.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return mCatalogItems.get(groupPosition).getChildren().get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

			CatalogItem catalogItem = (CatalogItem) getGroup(groupPosition);
			if ((catalogItem.getText() != null) && catalogItem.getText().isEmpty()) {
				convertView = new FrameLayout(getApplicationContext());
			} else {

				if (convertView == null) {
					LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					convertView = inflater.inflate(R.layout.list_item_browse_header, parent, false);
				}
				convertView.setOnClickListener(null);

				TextView textView = (TextView) convertView.findViewById(R.id.list_item_browse_header_text_view);
				textView.setText(catalogItem.getText());
			}

			ExpandableListView listView = (ExpandableListView) parent;
			listView.expandGroup(groupPosition);

			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.list_item_browse_children, parent, false);
			}
			TextView textView = (TextView) convertView.findViewById(R.id.item_browse_text_view);
			TextView subTextView = (TextView) convertView.findViewById(R.id.item_browse_subText_text_view);
			ImageView imageView = (ImageView) convertView.findViewById(R.id.item_browse_imageView);

			CatalogItem catalogItem = (CatalogItem) getChild(groupPosition, childPosition);

			if (catalogItem != null) {
				if ((catalogItem.getImageUrl() == null) || catalogItem.getImageUrl().isEmpty()) {
					mDialogFragment.dismiss();
					imageView.setVisibility(View.GONE);
				} else {
					imageView.setVisibility(View.VISIBLE);
					Picasso.with(MainActivity.this)
							.load(catalogItem.getImageUrl())
							.error(R.mipmap.ic_launcher)
							.into(imageView, new Callback() {
								@Override
								public void onSuccess() {
									mDialogFragment.dismiss();
								}

								@Override
								public void onError() {
									mDialogFragment.dismiss();
								}
							});
				}

				textView.setText(catalogItem.getText());
				if ((catalogItem.getSubText() == null) || catalogItem.getSubText().isEmpty()) {
					textView.setPadding(textView.getPaddingLeft(), 60, textView.getPaddingRight(), textView.getPaddingBottom());
				} else {
					textView.setPadding(textView.getPaddingLeft(), 20, textView.getPaddingRight(), textView.getPaddingBottom());
				}
				subTextView.setText(catalogItem.getSubText());
			}
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}
}
