package com.rmsi.android.mast.activity;

import java.util.ArrayList;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
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
public class AddSocialTenureActivity extends ActionBarActivity 
{

	List<Attribute> attribList;
	List<Option> optionlist;

	ListView listView;
	final Context context = this;
	AttributeAdapter	adapterList;
	Button btnSave,addMore,btnCancel;
	String FieldValue;
	CommonFunctions cf = CommonFunctions.getInstance();
	int groupId = 0;
	long featureId=0;
	int personId;
	Spinner spinnerForPerson;
	SpinnerAdapter spinnerAdapterList;
	List<String> spinnerArray;
	long person_Id=0;
	String selectedOptionName;
	private List<Integer> errorList = new ArrayList<Integer>();
	int role=0;
	boolean isNonNatural=false;
	TextView selectPersonLbl,selectTenure;
	RadioGroup radioSelectTenure;
	String keyword="SOCIAL_TENURE";
	Spinner spinnerResidentValue;
	static String serverFeatureId=null;
	String warningStr,infoSingleOccupantStr,infoMultipleJointStr,infoMultipleTeneancyStr,infoTenancyInProbateStr,infoGuardianMinorStr,infoStr;
	String You_have_selected,yesStr,noStr,msgStr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		//Initializing context in common functions in case of a crash
		try{CommonFunctions.getInstance().Initialize(getApplicationContext());}catch(Exception e){}
		cf.loadLocale(getApplicationContext());

		setContentView(R.layout.activity_social_tenure_information);

		listView = (ListView)findViewById(R.id.list_view);
		role = CommonFunctions.getRoleID();		
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.AddSocialTenureInfo);
		if(toolbar!=null)
			setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		btnSave=(Button) findViewById(R.id.btn_save);
		
		
		
		final DBController sqllite = new DBController(context);
		warningStr=getResources().getString(R.string.warning);
		infoStr=getResources().getString(R.string.info);
		infoSingleOccupantStr=getResources().getString(R.string.infoSingleOccupantStr);
		infoMultipleJointStr=getResources().getString(R.string.infoMultipleJointStr);
		infoMultipleTeneancyStr=getResources().getString(R.string.infoMultipleTeneancyStr);
		infoTenancyInProbateStr=getResources().getString(R.string.infoTenancyInProbateStr);
		infoGuardianMinorStr=getResources().getString(R.string.infoGuardianMinorStr);
		You_have_selected=getResources().getString(R.string.You_have_selected);
        yesStr=getResources().getString(R.string.yes);
        noStr=getResources().getString(R.string.no);
		
		
		
		
		
		btnCancel=(Button) findViewById(R.id.btn_cancel);

		
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) 
		{
			int gId = extras.getInt("groupid");
			featureId = extras.getLong("featureid");
			personId=extras.getInt("personid");
			serverFeatureId=extras.getString("Server_featureid");
			
			
				String keyword="SocialTenure";
				attribList = sqllite.getFeatureGenaralInfo(featureId,keyword,cf.getLocale());
				groupId = gId;
				if(attribList.size()>0)
				{
					groupId = attribList.get(0).getGroupId();
				}
				sqllite.close();
		}
		
		

		
		listView = (ListView) findViewById(R.id.list_view);
		try {
			adapterList = new AttributeAdapter(context, attribList,featureId);
			

		} 
		catch (Exception e) {

			e.printStackTrace();
		}

		listView.setAdapter(adapterList);


		btnSave.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				saveData();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() 
		{			
			@Override
			public void onClick(View arg0) {
				finish();				
			}
		});
	}


	public void saveData() 
	{
		if(validate())
		{
			try {
				if(groupId==0)	// NEW INSERT
				{
					//int selectedtenureType=radioSelectTenure.getCheckedRadioButtonId();

					boolean saveResult = false;
					DBController sqllite = new DBController(context);

						groupId = cf.getGroupId();
					//Option selecteditem = (Option) spinnerForPerson.getSelectedItem();
					person_Id = 0;
					saveResult = sqllite.saveAttributeData(attribList,groupId,featureId,keyword);
					long tenureId=0L;
					
					if(saveResult)
					{	
						tenureId=sqllite.getTenureTypeId(featureId);
						String personFormType="PERSON";
						Intent	myIntent = null;
						if(tenureId==129l)
						{
							personFormType="PERSON";   
							myIntent = new Intent(context, Individual_ExistingListActivity.class);
						}
						else if(tenureId==130l)
						{
							personFormType="PERSON";
							myIntent = new Intent(context, PersonListActivity.class);
						}	
						else if(tenureId==131l)
						{
							personFormType="EXISTING";
							myIntent = new Intent(context, Individual_ExistingListActivity.class);
						}
										   
						myIntent.putExtra("featureid", featureId);
						myIntent.putExtra("serverFeaterID",serverFeatureId);
						myIntent.putExtra("PersonFormType",personFormType);
						
						startActivity(myIntent);
						sqllite.close();
						
						
					}else{			
						Toast.makeText(context,R.string.unable_to_save_data, Toast.LENGTH_SHORT).show();
					}
				
						}
				else // EDIT CASE
				{
					DBController sqllite = new DBController(context);
					boolean saveResult = sqllite.saveAttributeData(attribList,groupId,featureId,keyword);
					
					long tenureId=0L;
					if(saveResult)
					{	
						tenureId=sqllite.getTenureTypeId(featureId);
						String personFormType="PERSON";
						Intent	myIntent = null;
						if(tenureId==129l)
						{
							personFormType="PERSON";
							myIntent = new Intent(context, Individual_ExistingListActivity.class);
						}
						else if(tenureId==130l)
						{
							personFormType="PERSON";
							myIntent = new Intent(context, PersonListActivity.class);
						}	
						else if(tenureId==131l)
						{
							personFormType="EXISTING";
							myIntent = new Intent(context, Individual_ExistingListActivity.class);
						}
												   
						myIntent.putExtra("featureid", featureId);
						myIntent.putExtra("serverFeaterID",serverFeatureId);
						myIntent.putExtra("PersonFormType",personFormType);
						
						startActivity(myIntent);
						sqllite.close();
						
						
					}
					else{			
						Toast.makeText(context,R.string.unable_to_save_data, Toast.LENGTH_SHORT).show();
					}
				}
			} catch (Exception e) {
				cf.appLog("", e);e.printStackTrace();
				Toast.makeText(context,R.string.unable_to_save_data, Toast.LENGTH_SHORT).show();
			}
		}
		else{	    	 			
			Toast.makeText(context,R.string.fill_mandatory, Toast.LENGTH_SHORT).show();
		}
	}
 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		finish();
		return true;
	}
	
	@Override
	protected void onResume() 
	{
				
				refereshList();

		super.onResume();
	}


	public boolean validate()
	{
		boolean isValid = true;
		errorList.clear();
		for (int i = 0; i < adapterList.getCount(); i++) 
		{
			Attribute item = (Attribute) adapterList.getItem(i);
			String value = ""; 
			String	hasvalidation = attribList.get(i).getValidation();
			if (item.getControlType() == 1) {
				if (item.getView() != null) 
				{
					// edit text
					EditText editText = (EditText) item.getView();
					value = editText.getText().toString();
					if(hasvalidation.equalsIgnoreCase("true") && value.isEmpty())
					{	
						isValid = false;
						errorList.add(item.getAttributeid());
						attribList.get(i).setFieldValue(null);
					}
					else if(!value.isEmpty()){						
						attribList.get(i).setFieldValue(value);						
					}else{						
						attribList.get(i).setFieldValue(null);						
					}
				}else if(hasvalidation.equalsIgnoreCase("true")){	
					isValid = false;
					errorList.add(item.getAttributeid());
					attribList.get(i).setFieldValue(null);
				}
			}
			else if (item.getControlType() == 2) {
				if (item.getView() != null) 
				{
					// edit text
					TextView textview = (TextView) item.getView();
					value = textview.getText().toString();
					if(hasvalidation.equalsIgnoreCase("true") && value.isEmpty())
					{	
						isValid = false;
						errorList.add(item.getAttributeid());
						attribList.get(i).setFieldValue(null);
					}else if(!value.isEmpty()){						
						attribList.get(i).setFieldValue(value);						
					}else{						
						attribList.get(i).setFieldValue(null);						
					}
				}else if(hasvalidation.equalsIgnoreCase("true")){	
					isValid = false;
					errorList.add(item.getAttributeid());
					attribList.get(i).setFieldValue(null);
				}
			}
			else if (item.getControlType() == 3) {
				if (item.getView() != null) // No Validation as boolean has only Yes OR No
				{
					Spinner spinner = (Spinner) item.getView();
					String selecteditem = (String) spinner.getSelectedItem();
					attribList.get(i).setFieldValue(selecteditem);
				}else if(hasvalidation.equalsIgnoreCase("true")){	
					isValid = false;
					errorList.add(item.getAttributeid());
					attribList.get(i).setFieldValue(null);
				}

			}
			else if (item.getControlType() == 4) {
				if (item.getView() != null) 
				{
					// edit text(Numeric)
					EditText editText = (EditText) item.getView();
					value = editText.getText().toString();

					if(hasvalidation.equalsIgnoreCase("true") && value.isEmpty())
					{
						isValid = false;
						errorList.add(item.getAttributeid());	
						attribList.get(i).setFieldValue(null);
					}else if(!value.isEmpty()){						
						attribList.get(i).setFieldValue(value);						
					}else{
						attribList.get(i).setFieldValue(null);
					}
				}else if(hasvalidation.equalsIgnoreCase("true")){	
					isValid = false;
					errorList.add(item.getAttributeid());
					attribList.get(i).setFieldValue(null);
				}
			}

			else if (item.getControlType() == 5) {
				if (item.getView() != null) 
				{
					// drop down spinner
					Spinner spinner = (Spinner) item.getView();
					Option selecteditem = (Option) spinner.getSelectedItem();

					if (hasvalidation.equalsIgnoreCase("true") && selecteditem.getOptionId() == 0) {
						isValid = false;
						errorList.add(item.getAttributeid());
						attribList.get(i).setFieldValue(null);
					}else if(selecteditem.getOptionId() != 0){	
						if(role==1)  // Hardcoded Id for Role (1=Trusted Intermediary, 2=Adjudicator)
						  {
						attribList.get(i).setFieldValue(selecteditem.getOptionId().toString());
								
						  }
						else{
						attribList.get(i).setFieldValue(selecteditem.getOptionId().toString());	
						}	
					}
					else{
						attribList.get(i).setFieldValue(null);
					}
				}else if(hasvalidation.equalsIgnoreCase("true")){	
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

	
	private void refereshList()
	{
		adapterList.notifyDataSetChanged();
	}
	
}
