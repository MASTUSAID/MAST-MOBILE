package com.rmsi.android.mast.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.adapter.AttributeAdapter;
import com.rmsi.android.mast.db.DBController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.Option;
import com.rmsi.android.mast.util.CommonFunctions;

public class AddNonNaturalPersonActivity extends ActionBarActivity {

	Long featureId = 0L;
	List<Attribute> attribList;
	ListView listView;
	final Context context = this;
	AttributeAdapter adapterList;
	Button btnSave,btnBack;
	String FieldValue;	
	CommonFunctions cf = CommonFunctions.getInstance();
	int roleId=0;
	int groupId = 0;
	private List<Integer> errorList = new ArrayList<Integer>();
	long personCount;
	String serverFeatureId,backStr,viewPersonStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		//Initializing context in common functions in case of a crash
		try{CommonFunctions.getInstance().Initialize(getApplicationContext());}catch(Exception e){}
		cf.loadLocale(getApplicationContext());

		setContentView(R.layout.activity_add_non_natural_person);
		
		roleId=CommonFunctions.getRoleID();
		listView = (ListView) findViewById(R.id.list_view);
		btnSave=(Button) findViewById(R.id.btn_save);
		btnBack=(Button) findViewById(R.id.btn_back);
		//addPerson=(Button) findViewById(R.id.btn_addPerson);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);		
		toolbar.setTitle(R.string.title_activity_add_non_natural_person);
						
		if(toolbar!=null)
			setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);	
		DBController sqllite = new DBController(context);
		
		backStr=getResources().getString(R.string.back);
		viewPersonStr=getResources().getString(R.string.view_person);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) 
		{
			featureId = extras.getLong("featureid");
			serverFeatureId=extras.getString("serverFeaterID");
			String keyword="NonNatural";
			attribList = sqllite.getFeatureGenaralInfo(featureId,keyword,cf.getLocale());

			if(attribList.size()>0)
			{
				groupId = attribList.get(0).getGroupId();
			}

			sqllite.close();
		}
		String addPerson="";
		personCount=sqllite.getPersonCount(featureId);
		if(personCount!=0)
		{
			addPerson=getResources().getString(R.string.edit_Person);
			btnSave.setText(addPerson);
			
		}	
		else if(personCount==0)
		{
			addPerson=getResources().getString(R.string.AddPerson);
			btnSave.setText(addPerson);
			
		}	

		sqllite.close();

		try {
			adapterList = new AttributeAdapter(context, attribList,featureId);
		} 
		catch (Exception e) {

			e.printStackTrace();
		}
		listView.setAdapter(adapterList);
		
		if(roleId==2)  // Hardcoded Id for Role (1=Trusted Intermediary, 2=Adjudicator)
		{
			//btnSave.setEnabled(false);
			btnSave.setText(viewPersonStr);  //TODO  add in swahili
			btnBack.setText(backStr);

		}
		btnSave.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{             
				saveData();				
			}			
		});

		/*addPerson.setOnClickListener(new OnClickListener() 
		{			
			@Override
			public void onClick(View v) {

				Intent myIntent = new Intent(context,NonNaturalPersonListActivity.class);
				myIntent.putExtra("featureid", featureId);
				myIntent.putExtra("persontype", "nonNatural");
				startActivity(myIntent);

			}
		});*/

		btnBack.setOnClickListener(new OnClickListener() 
		{			
			@Override
			public void onClick(View v) {

				if(personCount!=0)
				{
					if(roleId==2){
						
						finish();
					}
					else{
					Intent myIntent  =  new Intent(context, DataSummaryActivity.class);
					myIntent.putExtra("featureid", featureId);
					myIntent.putExtra("Server_featureid", serverFeatureId);
					myIntent.putExtra("className", "PersonListActivity");
					startActivity(myIntent); 
					}
				}
				
				else if(personCount==0)
				{
					String add_person=getResources().getString(R.string.add_atlest_one_person);
					String warningStr=getResources().getString(R.string.warning);
					cf.showMessage(context,warningStr,add_person);	
				}
			}
		});

	}
	
	public void saveData() 
	{
		if(validate())
		{
			try {
				if(groupId==0)
				{
					groupId = cf.getGroupId();
				}
				DBController sqllite = new DBController(context);
				String keyword="NonNaturalPerson";
				boolean saveResult = sqllite.saveFormDataTemp(attribList,groupId,featureId,keyword);


				sqllite.close();
				if(saveResult)
				{
					Intent myIntent = new Intent(context,NonNaturalPersonListActivity.class);
					myIntent.putExtra("featureid", featureId);
					myIntent.putExtra("persontype", "nonNatural");
					startActivity(myIntent);
					
				}else{								
					Toast.makeText(context,R.string.unable_to_save_data, Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				cf.appLog("", e);e.printStackTrace();
				Toast.makeText(context,R.string.unable_to_save_data, Toast.LENGTH_SHORT).show();
			}
		}else{								
			Toast.makeText(context,R.string.fill_mandatory, Toast.LENGTH_SHORT).show();
		}
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
						attribList.get(i).setFieldValue(selecteditem.getOptionId().toString());			
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
}
