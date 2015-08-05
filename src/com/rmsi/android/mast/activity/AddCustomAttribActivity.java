package com.rmsi.android.mast.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rmsi.android.mast.adapter.AttributeAdapter;
import com.rmsi.android.mast.db.DBController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.Option;
import com.rmsi.android.mast.util.CommonFunctions;

public class AddCustomAttribActivity extends ActionBarActivity {

	Long featureId = 0L;
	List<Attribute> attribList;
	ListView listView;
	final Context context = this;
	AttributeAdapter adapterList;
	Button btnSave, btnBack;
	String FieldValue;
	CommonFunctions cf = CommonFunctions.getInstance();
	int groupId = 0;
	private List<Integer> errorList = new ArrayList<Integer>();
	int roleId = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initializing context in common functions in case of a crash
		try {
			CommonFunctions.getInstance().Initialize(getApplicationContext());
		} catch (Exception e) {
		}
		cf.loadLocale(getApplicationContext());

		setContentView(R.layout.activity_add_property_info);

		roleId = CommonFunctions.getRoleID();
		ListView listView = (ListView) findViewById(android.R.id.list);
		TextView emptyText = (TextView) findViewById(android.R.id.empty);
		listView.setEmptyView(emptyText);

		btnSave = (Button) findViewById(R.id.btn_save);
		btnBack = (Button) findViewById(R.id.btn_cancel);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.add_custom_attributes);

		if (toolbar != null)
			setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		DBController sqllite = new DBController(context);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			featureId = extras.getLong("featureid");

			String keyword = "custom";
			attribList = sqllite.getFeatureGenaralInfo(featureId, keyword,
					cf.getLocale());

			if (attribList.size() > 0) {
				groupId = attribList.get(0).getGroupId();
			} else {
				findViewById(R.id.btn_container).setVisibility(View.GONE);
			}
			sqllite.close();
		}

		sqllite.close();

		try {
			adapterList = new AttributeAdapter(context, attribList);
		} catch (Exception e) {

			e.printStackTrace();
		}
		listView.setAdapter(adapterList);

		if (roleId == 2) // Hardcoded Id for Role (1=Trusted Intermediary,
							// 2=Adjudicator)
		{
			btnSave.setEnabled(false);

		}
		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (saveData()) {
					String savedMsg = getResources().getString(
							R.string.data_saved);
					Toast toast = Toast.makeText(context, savedMsg,
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					finish();
				} else {
					String fillMandatory = getResources().getString(
							R.string.fill_mandatory);
					Toast.makeText(context, fillMandatory, Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		btnBack.setOnClickListener(new OnClickListener() {
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

	public boolean saveData() {
		if (validate()) {

			try {

				for (int i = 0; i < adapterList.getCount(); i++) {

					Attribute item = (Attribute) adapterList.getItem(i);

					if (item.getView() != null) {

						if (item.getControlType() == 1) {
							EditText editText = (EditText) item.getView();
							FieldValue = editText.getText().toString();
							if (!FieldValue.isEmpty()) {
								attribList.get(i).setFieldValue(FieldValue);
							}

						}

						else if (item.getControlType() == 2) // Date
						{
							TextView textviewvalue = (TextView) item.getView();
							FieldValue = textviewvalue.getText().toString();
							if (!FieldValue.isEmpty()) {
								attribList.get(i).setFieldValue(FieldValue);
							}
						} else if (item.getControlType() == 3) // Boolean
						{
							Spinner spinner = (Spinner) item.getView();
							String selecteditem = (String) spinner
									.getSelectedItem();
							attribList.get(i).setFieldValue(selecteditem);
						} else if (item.getControlType() == 5) {
							Spinner spinner = (Spinner) item.getView();
							Option selecteditem = (Option) spinner
									.getSelectedItem();
							attribList.get(i).setFieldValue(
									selecteditem.getOptionId().toString());
						} else if (item.getControlType() == 4) {
							EditText editText = (EditText) item.getView();
							FieldValue = editText.getText().toString();
							if (!FieldValue.isEmpty()) {
								attribList.get(i).setFieldValue(FieldValue);
							}
						}
					}

				}

				if (groupId == 0) {
					groupId = cf.getGroupId();
				}
				DBController sqllite = new DBController(context);
				String keyword = "custom";
				boolean saveResult = sqllite.saveFormDataTemp(attribList,
						groupId, featureId, keyword);

				sqllite.close();
				if (saveResult) {
					adapterList.notifyDataSetChanged();
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				cf.appLog("", e);
				e.printStackTrace();
				return false;
			}
		} else {

			String fillMandatory = getResources().getString(
					R.string.fill_mandatory);
			Toast.makeText(context, fillMandatory, Toast.LENGTH_SHORT).show();
			return false;
		}

	}

	public boolean validate() {
		boolean isValid = true;
		errorList.clear();
		for (int i = 0; i < adapterList.getCount(); i++) {
			Attribute item = (Attribute) adapterList.getItem(i);
			String value = "";
			String hasvalidation = attribList.get(i).getValidation();
			if (item.getControlType() == 1) {
				if (item.getView() != null) {
					// edit text
					EditText editText = (EditText) item.getView();
					value = editText.getText().toString();
					if (hasvalidation.equalsIgnoreCase("true")
							&& value.isEmpty()) {
						isValid = false;
						errorList.add(item.getAttributeid());
						attribList.get(i).setFieldValue(null);
					} else {
						attribList.get(i).setFieldValue(value);
					}
				} else if (hasvalidation.equalsIgnoreCase("true")) {
					isValid = false;
					errorList.add(item.getAttributeid());
					attribList.get(i).setFieldValue(null);
				}
			} else if (item.getControlType() == 2) {
				if (item.getView() != null) {
					// edit text
					TextView textview = (TextView) item.getView();
					value = textview.getText().toString();
					if (hasvalidation.equalsIgnoreCase("true")
							&& value.isEmpty()) {
						isValid = false;
						errorList.add(item.getAttributeid());
						attribList.get(i).setFieldValue(null);
					} else {
						attribList.get(i).setFieldValue(value);
					}
				} else if (hasvalidation.equalsIgnoreCase("true")) {
					isValid = false;
					errorList.add(item.getAttributeid());
					attribList.get(i).setFieldValue(null);
				}
			} else if (item.getControlType() == 3) {
				if (item.getView() != null) {
					Spinner spinner = (Spinner) item.getView();
					String selecteditem = (String) spinner.getSelectedItem();
					/*
					 * if (hasvalidation.equalsIgnoreCase("true")) { isValid =
					 * false; errorList.add(item.getAttributeid());
					 * attribList.get(i).setFieldValue(null); }else{
					 * attribList.get(i).setFieldValue(selecteditem); }
					 */
					// TODO validation for mandatory
					attribList.get(i).setFieldValue(selecteditem);
				} else if (hasvalidation.equalsIgnoreCase("true")) {
					isValid = false;
					errorList.add(item.getAttributeid());
					attribList.get(i).setFieldValue(null);
				}

			} else if (item.getControlType() == 4) {
				if (item.getView() != null) {
					// edit text(Numeric)
					EditText editText = (EditText) item.getView();
					value = editText.getText().toString();

					if (hasvalidation.equalsIgnoreCase("true")
							&& value.isEmpty()) {
						isValid = false;
						errorList.add(item.getAttributeid());
						attribList.get(i).setFieldValue(null);
					} else {
						attribList.get(i).setFieldValue(value);
					}
				} else if (hasvalidation.equalsIgnoreCase("true")) {
					isValid = false;
					errorList.add(item.getAttributeid());
					attribList.get(i).setFieldValue(null);
				}
			}

			else if (item.getControlType() == 5) {
				if (item.getView() != null) {
					// drop down spinner
					Spinner spinner = (Spinner) item.getView();
					Option selecteditem = (Option) spinner.getSelectedItem();

					if (hasvalidation.equalsIgnoreCase("true")
							&& selecteditem.getOptionId() == 0) {
						isValid = false;
						errorList.add(item.getAttributeid());
						attribList.get(i).setFieldValue(null);
					} else {
						attribList.get(i).setFieldValue(
								selecteditem.getOptionId().toString());
					}
				} else if (hasvalidation.equalsIgnoreCase("true")) {
					isValid = false;
					errorList.add(item.getAttributeid());
					attribList.get(i).setFieldValue(null);
				}
			}
		}
		adapterList.setErrorList(errorList);

		if (!isValid) {
			adapterList.notifyDataSetChanged();
		}
		return isValid;
	}
}
