package com.rmsi.android.mast.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rmsi.android.mast.activity.R.string;
import com.rmsi.android.mast.adapter.AttributeAdapter;
import com.rmsi.android.mast.db.DBController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.Option;
import com.rmsi.android.mast.util.CommonFunctions;

/**
 * 
 * @author prashant.nigam
 * 
 */
public class CaptureAttributesActivity extends ActionBarActivity {
	ImageView personInfo, tenureInfo, multimedia, custom, propertyInfo;
	TextView propertyCount;
	List<Attribute> attribList;
	List<Attribute> attribFormValues;
	List<Option> optionList;
	ListView listView;
	final Context context = this;
	AttributeAdapter adapterList;

	String FieldValue;

	CommonFunctions cf = CommonFunctions.getInstance();
	SharedPreferences sharedpreferences;
	int groupId = 0;
	Long featureId = 0L;
	static String serverFeatureId = null;
	String personType;
	boolean flag = false;
	private List<Integer> errorList = new ArrayList<Integer>();
	DBController sqllite = new DBController(context);
	Spinner spinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initializing context in common functions in case of a crash
		try {
			CommonFunctions.getInstance().Initialize(getApplicationContext());
		} catch (Exception e) {
		}
		cf.loadLocale(getApplicationContext());

		setContentView(R.layout.activity_capture_attributes);

		TextView spatialunitValue = (TextView) findViewById(R.id.spatialunit_lbl);
		TextView projectnameValue = (TextView) findViewById(R.id.projectname_lbl);
		spinner = (Spinner) findViewById(R.id.spinner_person_type);
		// personType=spinner.getSelectedItem().toString();

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				if (pos == 0)
					personType = "Natural";
				else
					personType = "Non-Natural";
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				personType = "Natural";
			}
		});

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			featureId = extras.getLong("featureid");
			serverFeatureId = extras.getString("Server_featureid");

		}

		flag = sqllite.getFormValues(featureId);
		if (flag) {
			spinner.setEnabled(false);
		}

		projectnameValue.setText(projectnameValue.getText() + "   :  "
				+ sqllite.getProjectname());

		if (!TextUtils.isEmpty(serverFeatureId) && serverFeatureId != null) {
			spatialunitValue.setText("USIN" + "   :  "
					+ serverFeatureId.toString());
		} else {
			spatialunitValue.setText(spatialunitValue.getText() + "   :  "
					+ featureId.toString());
		}

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.title_activity_capture_attributes);
		if (toolbar != null)
			setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// DBController sqllite = new DBController(context);

		if (featureId == 0) {

			attribList = sqllite.getGeneralAttribute(cf.getLocale());
		} else {

			String keyword = "general";
			attribList = sqllite.getFeatureGenaralInfo(featureId, keyword,
					cf.getLocale());

			String personType = attribList.get(0).getPersonType();

			if (!TextUtils.isEmpty(personType)
					&& personType.equalsIgnoreCase("Natural")) {
				spinner.setSelection(0);
			} else if (!TextUtils.isEmpty(personType)
					&& personType.equalsIgnoreCase("Non-Natural")) {
				spinner.setSelection(1);
			}

			if (attribList.size() > 0) {
				groupId = attribList.get(0).getGroupId();
			}
		}
		sqllite.close();

		listView = (ListView) findViewById(R.id.list_view);
		try {
			adapterList = new AttributeAdapter(context, attribList);
		} catch (Exception e) {

			e.printStackTrace();
		}

		listView.setAdapter(adapterList);

		// propertyCount=(TextView) findViewById(R.id.propertyCount);
		personInfo = (ImageView) findViewById(R.id.btn_personlist);
		propertyInfo = (ImageView) findViewById(R.id.btn_propertyInfo);
		tenureInfo = (ImageView) findViewById(R.id.btn_tenureInfo);
		multimedia = (ImageView) findViewById(R.id.btn_addMultimedia);
		custom = (ImageView) findViewById(R.id.btn_addcustom);

		// For tooltip text

		View viewForTenureToolTip = tenureInfo;
		View viewForPersonToolTip = personInfo;
		View viewForMediaToolTip = multimedia;
		View viewForCustomToolTip = custom;
		View viewForPropertyDetailsToolTip = propertyInfo;

		String add_person = getResources().getString(R.string.AddPerson);
		String add_social_tenure = getResources().getString(
				R.string.AddSocialTenureInfo);
		String add_multimedia = getResources().getString(
				R.string.AddNewMultimedia);
		String add_custom_attrib = getResources().getString(
				R.string.add_custom_attributes);
		String add_property_details = getResources().getString(
				R.string.AddNewPropertyDetails);

		cf.setup(viewForPersonToolTip, add_person);
		cf.setup(viewForTenureToolTip, add_social_tenure);
		cf.setup(viewForMediaToolTip, add_multimedia);
		cf.setup(viewForCustomToolTip, add_custom_attrib);
		cf.setup(viewForPropertyDetailsToolTip, add_property_details);

		personInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (spinner.getSelectedItemPosition() == 0) {
					flag = sqllite.getFormValues(featureId);
					if (flag) {
						Intent myIntent = new Intent(context,
								PersonListActivity.class);
						myIntent.putExtra("featureid", featureId);
						myIntent.putExtra("persontype", "natural");
						myIntent.putExtra("serverFeaterID", serverFeatureId);

						startActivity(myIntent);
					} else {

						String msg = getResources().getString(
								string.save_genral_attrribute);
						String warning = getResources().getString(
								string.warning);
						cf.showMessage(context, warning, msg);
					}

				} else if (spinner.getSelectedItemPosition() == 1) {
					flag = sqllite.getFormValues(featureId);

					if (flag) {
						Intent myIntent = new Intent(context,
								AddNonNaturalPersonActivity.class);
						myIntent.putExtra("featureid", featureId);
						startActivity(myIntent);
					} else {

						String msg = getResources().getString(
								string.save_genral_attrribute);
						String warning = getResources().getString(
								string.warning);
						cf.showMessage(context, warning, msg);
					}

				}

			}
		});

		propertyInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				flag = sqllite.getFormValues(featureId);
				if (flag) {
					Intent myIntent = new Intent(context,
							AddGeneralPropertyActivity.class);
					myIntent.putExtra("featureid", featureId);
					startActivity(myIntent);
				} else {
					String msg = getResources().getString(
							string.save_genral_attrribute);
					String warning = getResources().getString(string.warning);
					cf.showMessage(context, warning, msg);
				}
			}
		});

		tenureInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				flag = sqllite.getFormValues(featureId);
				if (flag) {
					Intent myIntent = new Intent(context,
							SocialTenureListActivity.class);
					myIntent.putExtra("featureid", featureId);
					myIntent.putExtra("serverFeaterID", serverFeatureId);
					startActivity(myIntent);
				} else {

					String msg = getResources().getString(
							string.save_genral_attrribute);
					String warning = getResources().getString(string.warning);
					cf.showMessage(context, warning, msg);
				}
				/*
				 * View imageview=tenureInfo; showCheatSheet(imageview,
				 * "Tooltip for tenure");
				 */
			}
		});

		/*
		 * tenureInfo.setOnLongClickListener(new OnLongClickListener() {
		 * 
		 * @Override public boolean onLongClick(View v) {
		 * 
		 * 
		 * View imageview=tenureInfo; showCheatSheet(imageview,
		 * "Social Tenure List"); return true; } });
		 */

		multimedia.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				flag = sqllite.getFormValues(featureId);
				if (flag) {
					Intent myIntent = new Intent(context,
							MediaListActivity.class);
					myIntent.putExtra("featureid", featureId);
					myIntent.putExtra("serverFeaterID", serverFeatureId);
					startActivity(myIntent);
				} else {

					String msg = getResources().getString(
							string.save_genral_attrribute);
					String warning = getResources().getString(string.warning);
					cf.showMessage(context, warning, msg);
				}
			}
		});

		custom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				flag = sqllite.getFormValues(featureId);

				if (flag) {
					Intent myIntent = new Intent(context,
							AddCustomAttribActivity.class);
					myIntent.putExtra("featureid", featureId);
					startActivity(myIntent);
				} else {

					String msg = getResources().getString(
							string.save_genral_attrribute);
					String warning = getResources().getString(string.warning);
					cf.showMessage(context, warning, msg);
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.save, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_save) {
			saveData();
		}
		if (id == android.R.id.home) {
			finish();
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		updateCount();
		flag = sqllite.getFormValues(featureId);
		if (flag) {
			spinner.setEnabled(false);
		}
		super.onResume();
	}

	@SuppressLint("SimpleDateFormat")
	public void saveData() {
		if (validate()) {
			try {
				if (groupId == 0)
					groupId = cf.getGroupId();

				DBController sqllite = new DBController(context);

				boolean saveResult = sqllite.saveFormDataTemp(attribList,
						groupId, featureId, personType);
				sqllite.close();

				if (saveResult) {
					Toast toast = Toast.makeText(context, R.string.data_saved,
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				} else {
					Toast.makeText(context, R.string.unable_to_save_data,
							Toast.LENGTH_SHORT).show();
				}

			} catch (Exception e) {
				cf.appLog("", e);
				e.printStackTrace();
			}
		} else {
			Toast.makeText(context, R.string.fill_mandatory, Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void updateCount() {
		try {
			DBController sqllite = new DBController(context);
			List<Attribute> tmpList = sqllite.getTenureList(featureId, null);
			// List<Attribute> tmpList1 = sqllite.getPropertyList(featureId);
			List<Attribute> tmpList2 = sqllite.getPersonList(featureId);
			List<Attribute> tmpList3 = sqllite.getMediaList(featureId);
			sqllite.close();
			((TextView) findViewById(R.id.personCount)).setText(""
					+ tmpList2.size());
			// ((TextView)
			// findViewById(R.id.propertyCount)).setText(""+tmpList1.size());
			((TextView) findViewById(R.id.tenureCount)).setText(""
					+ tmpList.size());
			((TextView) findViewById(R.id.multimediaCount)).setText(""
					+ tmpList3.size());
		} catch (Exception e) {
			cf.appLog("", e);
			e.printStackTrace();
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
