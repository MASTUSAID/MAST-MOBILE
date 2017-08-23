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
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rmsi.android.mast.adapter.AttributeAdapter;
import com.rmsi.android.mast.adapter.SpinnerAdapter;
import com.rmsi.android.mast.db.DBController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.Option;
import com.rmsi.android.mast.util.CommonFunctions;

/**
 * @author prashant.nigam
 */
public class AddSocialTenureActivity extends ActionBarActivity {

	List<Attribute> attribList;
	List<Option> optionlist;

	ListView listView;
	final Context context = this;
	AttributeAdapter adapterList;
	Button btnSave, addMore, btnCancel;
	String FieldValue;
	CommonFunctions cf = CommonFunctions.getInstance();
	int groupId = 0;
	long featureId = 0;
	int personId;
	Spinner spinnerForPerson;
	SpinnerAdapter spinnerAdapterList;
	List<String> spinnerArray;
	long person_Id = 0;
	String selectedOptionName;
	private List<Integer> errorList = new ArrayList<Integer>();
	int role = 0;
	boolean isNonNatural = false;
	TextView selectPersonLbl, selectTenure;
	RadioGroup radioSelectTenure;
	String keyword = "SOCIAL_TENURE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initializing context in common functions in case of a crash
		try {
			CommonFunctions.getInstance().Initialize(getApplicationContext());
		} catch (Exception e) {
		}
		cf.loadLocale(getApplicationContext());

		setContentView(R.layout.activity_social_tenure_information);

		role = CommonFunctions.getRoleID();
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.AddSocialTenureInfo);
		if (toolbar != null)
			setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		btnSave = (Button) findViewById(R.id.btn_save);
		spinnerForPerson = (Spinner) findViewById(R.id.spinner_person_type);
		selectPersonLbl = (TextView) findViewById(R.id.select_person_lbl);
		selectTenure = (TextView) findViewById(R.id.select_tenure_lbl);
		radioSelectTenure = (RadioGroup) findViewById(R.id.radio_selectTenure);
		spinnerForPerson.setVisibility(View.GONE);
		// radioSelectTenure.setVisibility(View.GONE);
		selectPersonLbl.setVisibility(View.GONE);

		radioSelectTenure
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
						case R.id.radio_multiple:
							spinnerForPerson.setVisibility(View.GONE);
							selectPersonLbl.setVisibility(View.GONE);
							break;
						case R.id.radio_single:
							spinnerForPerson.setVisibility(View.VISIBLE);
							selectPersonLbl.setVisibility(View.VISIBLE);
							break;
						}
					}
				});

		if (role == 2) // Hardcoded Id for Role (1=Trusted Intermediary,
						// 2=Adjudicator)
		{
			spinnerForPerson.setEnabled(false);
			btnSave.setEnabled(false);
			selectTenure.setVisibility(View.GONE);
			radioSelectTenure.setVisibility(View.GONE);

		}

		btnCancel = (Button) findViewById(R.id.btn_cancel);

		DBController sqllite = new DBController(context);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			int gId = extras.getInt("groupid");
			featureId = extras.getLong("featureid");
			personId = extras.getInt("personid");

			if (gId != 0) {
				attribList = sqllite.getFormDataByGroupId(gId, cf.getLocale());
				optionlist = sqllite.getPersonForTenure(featureId);
				groupId = gId;
			} else {
				attribList = sqllite.getTenureAttribute(
						sqllite.getReadableDatabase(), cf.getLocale());
				optionlist = sqllite.getPersonForTenure(featureId);

			}
		}

		isNonNatural = sqllite.IsNonNaturalPerson(featureId);
		if (isNonNatural) {
			spinnerForPerson.setVisibility(View.GONE);
			selectPersonLbl.setVisibility(View.GONE);
			selectTenure.setVisibility(View.GONE);
			radioSelectTenure.setVisibility(View.GONE);
		}

		if (personId != 0) {
			selectTenure.setVisibility(View.GONE);
			radioSelectTenure.setVisibility(View.GONE);
		}

		sqllite.close();

		listView = (ListView) findViewById(R.id.list_view);
		try {
			adapterList = new AttributeAdapter(context, attribList);
			spinnerAdapterList = new SpinnerAdapter(context,
					android.R.layout.simple_spinner_item, optionlist);
			spinnerAdapterList
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerForPerson.setAdapter(spinnerAdapterList);

			if (groupId != 0) {
				for (int i = 0; i < optionlist.size(); i++) {
					if (optionlist.get(i).getOptionId() == personId) {
						spinnerForPerson.setSelection(i);
						// spinnerForPerson.setSelection(spinnerAdapterList.getPosition(i));

					}
				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		listView.setAdapter(adapterList);

		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveData();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return true;
	}

	public void saveData() {
		if (validate()) {
			try {
				if (groupId == 0) // NEW INSERT
				{
					int selectedtenureType = radioSelectTenure
							.getCheckedRadioButtonId();

					boolean saveResult = false;
					DBController sqllite = new DBController(context);

					if (selectedtenureType == R.id.radio_multiple) {
						for (int i = 0; i < optionlist.size(); i++) {
							person_Id = optionlist.get(i).getOptionId();
							groupId = cf.getGroupId();
							saveResult = sqllite.saveSocialTenureFormData(
									attribList, groupId, featureId, keyword,
									person_Id);
						}
						sqllite.close();
						if (saveResult) {
							Toast toast = Toast.makeText(context,
									R.string.data_saved, Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
							finish();
						} else {
							Toast.makeText(context,
									R.string.unable_to_save_data,
									Toast.LENGTH_SHORT).show();
						}
					} else if (selectedtenureType == R.id.radio_single) {
						if (groupId == 0) {
							groupId = cf.getGroupId();
						}
						Option selecteditem = (Option) spinnerForPerson
								.getSelectedItem();
						person_Id = selecteditem.getOptionId();
						saveResult = sqllite.saveSocialTenureFormData(
								attribList, groupId, featureId, keyword,
								person_Id);

						sqllite.close();
						if (saveResult) {
							Toast toast = Toast.makeText(context,
									R.string.data_saved, Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
							finish();
						} else {
							Toast.makeText(context,
									R.string.unable_to_save_data,
									Toast.LENGTH_SHORT).show();
						}
					}
				} else // EDIT CASE
				{
					DBController sqllite = new DBController(context);
					boolean saveResult = sqllite.saveSocialTenureFormData(
							attribList, groupId, featureId, keyword, personId);
					sqllite.close();
					if (saveResult) {
						Toast toast = Toast.makeText(context,
								R.string.data_saved, Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						finish();
					} else {
						Toast.makeText(context, R.string.unable_to_save_data,
								Toast.LENGTH_SHORT).show();
					}
				}
			} catch (Exception e) {
				cf.appLog("", e);
				e.printStackTrace();
				Toast.makeText(context, R.string.unable_to_save_data,
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(context, R.string.fill_mandatory, Toast.LENGTH_SHORT)
					.show();
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
					} else if (!value.isEmpty()) {
						attribList.get(i).setFieldValue(value);
					} else {
						attribList.get(i).setFieldValue(null);
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
					} else if (!value.isEmpty()) {
						attribList.get(i).setFieldValue(value);
					} else {
						attribList.get(i).setFieldValue(null);
					}
				} else if (hasvalidation.equalsIgnoreCase("true")) {
					isValid = false;
					errorList.add(item.getAttributeid());
					attribList.get(i).setFieldValue(null);
				}
			} else if (item.getControlType() == 3) {
				if (item.getView() != null) // No Validation as boolean has only
											// Yes OR No
				{
					Spinner spinner = (Spinner) item.getView();
					String selecteditem = (String) spinner.getSelectedItem();
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
					} else if (!value.isEmpty()) {
						attribList.get(i).setFieldValue(value);
					} else {
						attribList.get(i).setFieldValue(null);
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
					} else if (selecteditem.getOptionId() != 0) {
						attribList.get(i).setFieldValue(
								selecteditem.getOptionId().toString());
					} else {
						attribList.get(i).setFieldValue(null);
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
