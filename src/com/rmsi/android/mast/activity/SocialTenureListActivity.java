package com.rmsi.android.mast.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.rmsi.android.mast.activity.R.string;
import com.rmsi.android.mast.adapter.MediaListingAdapterTemp;
import com.rmsi.android.mast.db.DBController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.Option;
import com.rmsi.android.mast.util.CommonFunctions;

/**
 * 
 * @author prashant.nigam
 * 
 */
public class SocialTenureListActivity extends ActionBarActivity {

	Button addnewSocialTenure, back;
	Context context;
	ListView listView;
	List<Attribute> attribute = new ArrayList<Attribute>();
	MediaListingAdapterTemp adapter;
	Long featureId;
	List<Option> optionlist;
	CommonFunctions cf = CommonFunctions.getInstance();
	int roleId = 0;
	String serverFeatureId;
	boolean openAdd = false;

	private void deleteEntry(final int groupId) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder.setMessage(R.string.deleteEntryMsg);
		alertDialogBuilder.setPositiveButton(R.string.btn_ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						DBController sqllite = new DBController(context);
						String keyword = "tenure";
						boolean result = sqllite.deleteRecord(groupId, keyword);
						if (result) {
							refereshList();
						} else {
							String msg = getResources().getString(
									R.string.unable_delete);
							Toast.makeText(context, msg, Toast.LENGTH_SHORT)
									.show();
						}
					}
				});
		alertDialogBuilder.setNegativeButton(R.string.btn_cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initializing context in common functions in case of a crash
		try {
			CommonFunctions.getInstance().Initialize(getApplicationContext());
		} catch (Exception e) {
		}
		cf.loadLocale(getApplicationContext());

		setContentView(R.layout.activity_list);

		roleId = CommonFunctions.getRoleID();
		TextView spatialunitValue = (TextView) findViewById(R.id.spatialunit_lbl);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.SocialTenureInfo);
		if (toolbar != null)
			setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		context = this;

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			featureId = extras.getLong("featureid");
			serverFeatureId = extras.getString("serverFeaterID");
		}

		if (!TextUtils.isEmpty(serverFeatureId) && serverFeatureId != null) {
			spatialunitValue.setText("USIN" + "   :  "
					+ serverFeatureId.toString());
		} else {
			spatialunitValue.setText(spatialunitValue.getText() + "   :  "
					+ featureId.toString());
		}

		addnewSocialTenure = (Button) findViewById(R.id.btn_addNewPerson);
		back = (Button) findViewById(R.id.btn_backPersonList);
		listView = (ListView) findViewById(android.R.id.list);
		TextView emptyText = (TextView) findViewById(android.R.id.empty);
		listView.setEmptyView(emptyText);

		adapter = new MediaListingAdapterTemp(context, this, attribute,
				"socialTenurelist");
		listView.setAdapter(adapter);

		addnewSocialTenure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DBController sqllite = new DBController(context);
				optionlist = sqllite.getPersonForTenure(featureId);

				if (optionlist.size() == 0) {

					String warning_msg = getResources().getString(
							string.add_atlest_one_person);
					String warning = getResources().getString(string.warning);

					cf.showMessage(context, warning, warning_msg);

				} else {

					Intent myIntent = new Intent(context,
							AddSocialTenureActivity.class);
					myIntent.putExtra("groupid", 0);
					myIntent.putExtra("featureid", featureId);
					startActivity(myIntent);
				}
			}
		});

		if (roleId == 2) // Hardcoded Id for Role (1=Trusted Intermediary,
							// 2=Adjudicator)
		{
			addnewSocialTenure.setEnabled(false);
		} else if (roleId == 1) {
			openAdd = true;
		}

		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		refereshList();

		super.onResume();
	}

	private void refereshList() {
		attribute.clear();
		DBController sqllite = new DBController(context);
		attribute.addAll(sqllite.getTenureList(featureId, cf.getLocale()));
		sqllite.close();
		adapter.notifyDataSetChanged();

		if (openAdd && attribute.size() == 0) {
			openAdd = false;
			addnewSocialTenure.callOnClick();
		}
	}

	public void showPopup(View v, Object object) {
		PopupMenu popup = new PopupMenu(context, v);
		MenuInflater inflater = popup.getMenuInflater();
		if (roleId == 1) // Hardcoded Id for Adjudicator
		{
			inflater.inflate(R.menu.attribute_listing_options, popup.getMenu());

		} else {
			inflater.inflate(R.menu.attribute_listing_options_to_view_details,
					popup.getMenu());
		}

		int position = (Integer) object;
		final int groupId = attribute.get(position).getGroupId();
		final int personId = attribute.get(position).getPersonId();

		popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
				case R.id.edit_attributes:
					// Open attributes form to edit --------------
					Intent myIntent = new Intent(context,
							AddSocialTenureActivity.class);
					myIntent.putExtra("groupid", groupId);
					myIntent.putExtra("featureid", featureId);
					myIntent.putExtra("personid", personId);
					startActivity(myIntent);
					return true;
				case R.id.delete_entry:
					deleteEntry(groupId);
					return true;
				case R.id.view_attributes:
					// Open attributes form to view --------------
					Intent intent = new Intent(context,
							AddSocialTenureActivity.class);
					intent.putExtra("groupid", groupId);
					intent.putExtra("featureid", featureId);
					intent.putExtra("personid", personId);
					startActivity(intent);
					return true;

				default:
					return false;
				}
			}
		});
		popup.show();
	}
}
