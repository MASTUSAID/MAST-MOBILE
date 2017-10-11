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
public class CaptureAttributesActivity extends ActionBarActivity 
{
	ImageView personInfo,tenureInfo,multimedia,custom,propertyInfo;
	TextView propertyCount;
	List<Attribute> attribList;
	List<Attribute> attribFormValues;
	List<Option> optionList;
	ListView listView;
	final Context context = this;
	AttributeAdapter	adapterList;
	String FieldValue;
	CommonFunctions cf = CommonFunctions.getInstance();
	SharedPreferences sharedpreferences;
	int groupId =0;
	Long featureId=0L,witnessId_1=0L,witnessId_2;
	static String serverFeatureId=null;
	String personType="Select an option",hamletName_Id,witness_1,witness_2;
    boolean flag=false,generalAttributeSaved=false;
    private List<Integer> errorList = new ArrayList<Integer>();
    DBController sqllite = new DBController(context);
    Spinner spinnerPersonType,spinnerHamlet,spinnerWitness1,spinnerWitness2;
    Option selecteditem;
    String msg="";
    String warningStr,infoSingleOccupantStr,infoMultipleJointStr,infoMultipleTeneancyStr,infoTenancyInProbateStr,infoGuardianMinorStr,infoStr;
    String You_have_selected,yesStr,noStr,backStr;
    int roleId=0;
    Option tenureType;
    long tenureTypeId=0l;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		//Initializing context in common functions in case of a crash
		try{CommonFunctions.getInstance().Initialize(getApplicationContext());}catch(Exception e){}
		cf.loadLocale(getApplicationContext());

		setContentView(R.layout.activity_capture_attributes);
		roleId=CommonFunctions.getRoleID(); 

		TextView spatialunitValue = (TextView) findViewById(R.id.spatialunit_lbl);
		TextView projectnameValue = (TextView) findViewById(R.id.projectname_lbl);
		//TextView VillageName = (TextView) findViewById(R.id.villageName_lbl);
		TextView communeValue = (TextView) findViewById(R.id.communeLbl);
		
	
		listView = (ListView)findViewById(R.id.list_view);
		warningStr=getResources().getString(string.warning);
		infoStr=getResources().getString(string.info);
		infoSingleOccupantStr=getResources().getString(string.infoSingleOccupantStr);
		infoMultipleJointStr=getResources().getString(string.infoMultipleJointStr);
		infoMultipleTeneancyStr=getResources().getString(string.infoMultipleTeneancyStr);
		infoTenancyInProbateStr=getResources().getString(string.infoTenancyInProbateStr);
		infoGuardianMinorStr=getResources().getString(string.infoGuardianMinorStr);
		You_have_selected=getResources().getString(string.You_have_selected);
        yesStr=getResources().getString(string.yes);
        noStr=getResources().getString(string.no);


    	if(featureId==0)
		{

			attribList = sqllite.getGeneralAttribute(cf.getLocale());
		}


		Bundle extras = getIntent().getExtras();
		if (extras != null)
		{
			featureId = extras.getLong("featureid");
		   serverFeatureId=extras.getString("Server_featureid");
		   String keyword="general";
			attribList = sqllite.getFeatureGenaralInfo(featureId,keyword,cf.getLocale());


			if(attribList.size()>0)
			{
				groupId = attribList.get(0).getGroupId();
			}

		}

		try {
			adapterList = new AttributeAdapter(context, attribList,featureId);
			listView.setAdapter(adapterList);
		}
		catch (Exception e) {

			e.printStackTrace();
		}



		projectnameValue.setText(projectnameValue.getText()+" "+sqllite.getProjectname());
		communeValue.setText(communeValue.getText()+" "+sqllite.getCommune());



		//VillageName.setText(VillageName.getText()+" "+sqllite.villageName());

		if(!TextUtils.isEmpty(serverFeatureId) && serverFeatureId !=null)
		{
		spatialunitValue.setText("USIN"+" "+serverFeatureId.toString());
		}
		else
		{
		spatialunitValue.setText(spatialunitValue.getText()+" "+featureId.toString());
		}

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(string.title_activity_capture_attributes);
		if(toolbar!=null)
			setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);



		//propertyCount=(TextView) findViewById(R.id.propertyCount);
		personInfo=(ImageView) findViewById(R.id.btn_personlist);
		propertyInfo=(ImageView) findViewById(R.id.btn_propertyInfo);
		tenureInfo=(ImageView) findViewById(R.id.btn_tenureInfo);
		multimedia=(ImageView) findViewById(R.id.btn_addMultimedia);
		custom=(ImageView) findViewById(R.id.btn_addcustom);



		//For tooltip text

		View viewForTenureToolTip=tenureInfo;
		View viewForPersonToolTip=personInfo;
		View viewForMediaToolTip=multimedia;
		View viewForCustomToolTip=custom;
		View viewForPropertyDetailsToolTip=propertyInfo;


		String add_person=getResources().getString(string.AddPerson);
		String add_social_tenure=getResources().getString(string.AddSocialTenureInfo);
		String add_multimedia=getResources().getString(string.AddNewMultimedia);
		String add_custom_attrib=getResources().getString(string.add_custom_attributes);
		String add_property_details=getResources().getString(string.AddNewPropertyDetails);

		cf.setup(viewForPersonToolTip, add_person);
		cf.setup(viewForTenureToolTip,add_social_tenure);
		cf.setup(viewForMediaToolTip,add_multimedia);
		cf.setup(viewForCustomToolTip,add_custom_attrib);
		cf.setup(viewForPropertyDetailsToolTip,add_property_details);

		personInfo.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

				boolean tenureFilled=sqllite.IsTenureValue(featureId);
					//flag=sqllite.getFormValues(featureId);
					flag=sqllite.isGeneralAttributeSaved(featureId);
					long tenureId=0L;
					if(flag)
					{
						if(tenureFilled)
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

							String msg=getResources().getString(string.fill_tenure);
							String warning=getResources().getString(string.warning);
							cf.showMessage(context, warning, msg);
						}
					}
					else{

						String msg=getResources().getString(string.save_genral_attrribute);
						String warning=getResources().getString(string.warning);
						cf.showMessage(context, warning, msg);
					}




			}
		});





		propertyInfo.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//flag=sqllite.getFormValues(featureId);
				flag=sqllite.isGeneralAttributeSaved(featureId);
				if(flag){
					Intent myIntent = new Intent(context, AddGeneralPropertyActivity.class);
					myIntent.putExtra("featureid", featureId);
					startActivity(myIntent);
				}
				else{
					String msg=getResources().getString(string.save_genral_attrribute);
					String warning=getResources().getString(string.warning);
					cf.showMessage(context, warning, msg);
				}
			}
		});

		tenureInfo.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
			//	flag=sqllite.getFormValues(featureId);
				flag=sqllite.isGeneralAttributeSaved(featureId);
				if(flag)
				{
					Intent myIntent = new Intent(context, AddSocialTenureActivity.class);
					myIntent.putExtra("featureid", featureId);
					myIntent.putExtra("serverFeaterID",serverFeatureId);
					startActivity(myIntent);
				}
				else{

					String msg=getResources().getString(string.save_genral_attrribute);
					String warning=getResources().getString(string.warning);
					cf.showMessage(context, warning, msg);
				}
				/*View imageview=tenureInfo;
				showCheatSheet(imageview, "Tooltip for tenure");*/
			}
		});

		/*tenureInfo.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {


				View imageview=tenureInfo;
				showCheatSheet(imageview, "Social Tenure List");
				return true;
			}
		});
		*/



		multimedia.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

			//	flag=sqllite.getFormValues(featureId);
				flag=sqllite.isGeneralAttributeSaved(featureId);
				tenureType=sqllite.getTenureTypeOptionsValue(featureId);    //check tenure type

				long personCount=sqllite.getPersonCount(featureId);
				if(flag)
				{
					if (personCount!=0l) {
						Intent myIntent = new Intent(context,
								MediaListActivity.class);
						myIntent.putExtra("featureid", featureId);
						myIntent.putExtra("serverFeaterID", serverFeatureId);
						startActivity(myIntent);
					}
					else{
						//Toast.makeText(context, R.string.please_add_personDetails, Toast.LENGTH_LONG).show();
						String msg=getResources().getString(string.please_add_personDetails);
						String warning=getResources().getString(string.warning);
						cf.showMessage(context, warning, msg);
					}
					}

				else{

					String msg=getResources().getString(string.save_genral_attrribute);
					String warning=getResources().getString(string.warning);
					cf.showMessage(context, warning, msg);
				}
			}
		});

		custom.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				//flag=sqllite.getFormValues(featureId);
				flag=sqllite.isGeneralAttributeSaved(featureId);

				if(flag)
				{
					Intent myIntent = new Intent(context, AddCustomAttribActivity.class);
					myIntent.putExtra("featureid", featureId);
					startActivity(myIntent);
				}
				else{

					String msg=getResources().getString(string.save_genral_attrribute);
					String warning=getResources().getString(string.warning);
					cf.showMessage(context, warning, msg);
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.save, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if(id == R.id.action_save)
		{
			saveData();
		}
		if(id == android.R.id.home)
		{
			finish();
		}

		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("SimpleDateFormat")
	public void saveData()
	{
		if(validate())
 {
			// long hamletId=Long.parseLong(hamletName_Id);//Chagua chaguo

			try {
				if (groupId == 0)
					groupId = cf.getGroupId();

				DBController sqllite = new DBController(context);



					/*boolean saveResult = sqllite.saveFormDataTemp(attribList, groupId,
							featureId, personType);*/

					boolean saveResult = sqllite.saveAttributeData(attribList, groupId,
							featureId, personType);
					sqllite.close();

					if (saveResult) {
						Toast toast = Toast.makeText(context,
								string.data_saved, Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						Intent myIntent = new Intent(context,
								AddGeneralPropertyActivity.class);
						myIntent.putExtra("featureid", featureId);
						startActivity(myIntent);
					} else {
						Toast.makeText(context, string.unable_to_save_data,
								Toast.LENGTH_SHORT).show();
					}


			} catch (Exception e) {
				cf.appLog("", e);
				e.printStackTrace();
			}

		}
		else{
			Toast.makeText(context, string.fill_mandatory, Toast.LENGTH_SHORT).show();
		}
	}

	private void updateCount()
	{
		try 
		{
			
			DBController sqllite = new DBController(context);
		//	List<Attribute> tmpList = sqllite.getTenureList(featureId,null);
			List<Attribute> tmpList2 = sqllite.getPersonList(featureId);
			List<Attribute> tmpList3 = sqllite.getMediaList(featureId);
			sqllite.close();
			((TextView) findViewById(R.id.personCount)).setText(""+tmpList2.size());
			//((TextView) findViewById(R.id.tenureCount)).setText(""+tmpList.size());
			((TextView) findViewById(R.id.multimediaCount)).setText(""+tmpList3.size());
						
			
		} 
		catch (Exception e) 
		{
			cf.appLog("", e);e.printStackTrace();
		}		
	}

	@Override
	protected void onResume() 
	{
		updateCount();
		//flag=sqllite.getFormValues(featureId);
		//flag=sqllite.isGeneralAttributeSaved(featureId);
		/*if(flag)
		{
			spinnerPersonType.setEnabled(false);
		}*/
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
	private void showToast(String message, int length)
	{
		Toast toast = Toast.makeText(context,message, length);
		toast.setGravity(Gravity.CENTER, 0, 0);	        	  
		toast.show();
	}
	
	

}
