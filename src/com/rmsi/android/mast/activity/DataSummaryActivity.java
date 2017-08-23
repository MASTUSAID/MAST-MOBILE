package com.rmsi.android.mast.activity;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.rmsi.android.mast.db.DBController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.util.CommonFunctions;

public class DataSummaryActivity extends ActionBarActivity {

	Long featureId = 0L;
	String serverFeatureId = null;
	Context context = this;
	CommonFunctions cf = CommonFunctions.getInstance();
	TextView personCount, mediaCount, tenureCount, tenureRaltion, customStatus,
			nonNaturalCount, propertyStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initializing context in common functions in case of a crash
		try {
			CommonFunctions.getInstance().Initialize(getApplicationContext());
		} catch (Exception e) {
		}
		cf.loadLocale(getApplicationContext());

		setContentView(R.layout.activity_data_summary);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.title_activity_data_summary);
		personCount = (TextView) findViewById(R.id.TextView_countNaturalPerson);
		mediaCount = (TextView) findViewById(R.id.TextView_countMultimedia);
		tenureCount = (TextView) findViewById(R.id.TextView_countSocialTenure);
		tenureRaltion = (TextView) findViewById(R.id.TextView_tenureRelation);
		customStatus = (TextView) findViewById(R.id.TextView_custom);
		nonNaturalCount = (TextView) findViewById(R.id.TextView_nonNatural);
		propertyStatus = (TextView) findViewById(R.id.TextView_propertyStatus);

		if (toolbar != null)
			setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			featureId = extras.getLong("featureid");
			serverFeatureId = extras.getString("Server_featureid");
		}

		Button btn_edit = (Button) findViewById(R.id.edit_attributes);
		btn_edit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent myIntent = new Intent(context,
						CaptureAttributesActivity.class);
				myIntent.putExtra("featureid", featureId);
				myIntent.putExtra("Server_featureid", serverFeatureId);
				// myIntent.putExtra("flag",true);
				startActivity(myIntent);
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
		updateCount();
		super.onResume();
	}

	private void updateCount() {
		try {
			DBController sqllite = new DBController(context);
			List<Attribute> tmpList = sqllite.getTenureList(featureId, null);
			// List<Attribute> tmpList1 = sqllite.getPropertyList(featureId);
			List<Attribute> tmpList2 = sqllite.getPersonList(featureId);
			List<Attribute> tmpList3 = sqllite.getMediaList(featureId);
			boolean isNonNatural = sqllite.IsNonNaturalPerson(featureId);
			boolean isCustomValue = sqllite.IsCustomAttributeValue(featureId);
			boolean isCustomAttrib = sqllite.IsCustomAttribute();
			boolean ispropertyAtttrib = sqllite.IsPropertyAttribute();
			boolean isPropertyValue = sqllite.IsPropertyAttribValue(featureId);
			sqllite.close();

			if (isPropertyValue) {
				propertyStatus.setText(R.string.completed);
				propertyStatus.setTextColor(getResources().getColor(
						R.color.green)); // for green color

			} else {
				propertyStatus.setText(R.string.incomplete);
				propertyStatus.setTextColor(getResources()
						.getColor(R.color.red)); // red color
			}

			if (isNonNatural) {
				tenureRaltion.setText(R.string.nonnatural);
				nonNaturalCount.setText("1");
				nonNaturalCount.setTextColor(getResources().getColor(
						R.color.green));

			} else {

				tenureRaltion.setText(R.string.natural);
				nonNaturalCount.setText("0");
				nonNaturalCount.setTextColor(getResources().getColor(
						R.color.red));
			}

			if (isCustomAttrib) {
				if (isCustomValue) {

					customStatus.setText(R.string.completed);
					customStatus.setTextColor(getResources().getColor(
							R.color.green)); // for green color

				} else {
					customStatus.setText(R.string.incomplete);
					customStatus.setTextColor(getResources().getColor(
							R.color.red)); // red color
				}

			} else {
				customStatus.setText(R.string.not_defined);
				customStatus.setTextColor(getResources().getColor(R.color.red));
			}

			// Update color of the count

			if (tmpList.size() == 0) {
				tenureCount.setTextColor(getResources().getColor(R.color.red));
			} else {

				tenureCount
						.setTextColor(getResources().getColor(R.color.green));
			}

			if (tmpList2.size() == 0) {
				personCount.setTextColor(getResources().getColor(R.color.red));
			} else {

				personCount
						.setTextColor(getResources().getColor(R.color.green));
			}
			if (tmpList3.size() == 0) {
				mediaCount.setTextColor(getResources().getColor(R.color.red));
			} else {
				mediaCount.setTextColor(getResources().getColor(R.color.green));
			}

			// Update count

			personCount.setText("" + tmpList2.size());
			// ((TextView)
			// findViewById(R.id.propertyCount)).setText(""+tmpList1.size());
			tenureCount.setText("" + tmpList.size());
			mediaCount.setText("" + tmpList3.size());
		} catch (Exception e) {
			cf.appLog("", e);
			e.printStackTrace();
		}
	}
}
