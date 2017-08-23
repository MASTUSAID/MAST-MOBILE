package com.rmsi.android.mast.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.adapter.AttributeAdapter;
import com.rmsi.android.mast.adapter.SpinnerAdapter;
import com.rmsi.android.mast.db.DBController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.Option;
import com.rmsi.android.mast.util.CommonFunctions;

/**
 * @author Amreen.s
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
		
		spinnerResidentValue=(Spinner) findViewById(R.id.spinner_resident);
		
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
		
		
		
		spinnerResidentValue.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				String residentValue="Select an option";
				
				if(pos==1)
				{
					residentValue="Yes";
					sqllite.setResidentValue(residentValue, featureId);
					adapterList.notifyDataSetChanged();
					refereshList();
					
				}
				else if(pos==2)
				{
					residentValue="No";
					sqllite.setResidentValue(residentValue, featureId);
					adapterList.notifyDataSetChanged();
					refereshList();
				}
			
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
						
		
		
		if(role==2)  // Hardcoded Id for Role (1=Trusted Intermediary, 2=Adjudicator)
		  {
			
			btnSave.setEnabled(false);
			spinnerResidentValue.setEnabled(false);
			//selectTenure.setVisibility(View.GONE);
			//radioSelectTenure.setVisibility(View.GONE);
			
		  }
		
		
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
		
		

		sqllite.close();

		String residentValue=CommonFunctions.getResidentValue(featureId);
		if(residentValue.equalsIgnoreCase("Yes"))
		{
			spinnerResidentValue.setSelection(1);
		}
		else if(residentValue.equalsIgnoreCase("No"))
		{
			spinnerResidentValue.setSelection(2);
		} 
		else
		{
			spinnerResidentValue.setSelection(0);
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
					saveResult = sqllite.saveSocialTenureFormData(attribList,groupId,featureId,keyword,person_Id);

					sqllite.close();
					if(saveResult)
					{
						/*
						Toast toast = Toast.makeText(context,R.string.data_saved, Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						finish();*/

						Option tenureType=sqllite.getTenureTypeOptionsValue(featureId);
						final long tenureTypeId=tenureType.getOptionId();
						if(tenureTypeId==70 || tenureTypeId==71 || tenureTypeId==72 || tenureTypeId==100 || tenureTypeId==99)
						{
						
						String infoMsg="No msg";
						//info message
						switch ((int) tenureTypeId) {
						case 70:
							//cf.showMessage(context,"Info","You can add only one adult owner & multiple person of interests");
							//infoMsg=infoSingleOccupantStr;
							infoMsg=infoMultipleTeneancyStr; //for live

							break;
						case 71:
							//cf.showMessage(context,"Info","You can add two adult owners & multiple person of interests");
							//infoMsg=infoMultipleJointStr;
							infoMsg=infoSingleOccupantStr; //for live
							break;
						case 72:
							//cf.showMessage(context,"Info","You can add two or more adult owners & multiple person of interests");
							//infoMsg=infoMultipleTeneancyStr;
							infoMsg=infoMultipleJointStr; //for live
							break;
							
						case 99:
							//cf.showMessage(context,"Info","You can add multiple minor owners & two guardian");
							infoMsg=infoTenancyInProbateStr;
							break;	
							
						case 100:
							//cf.showMessage(context,"Info","You can add multiple minor owners & two guardian");
							infoMsg=infoGuardianMinorStr;
							break;

						default:
							break;
					}
                           
							final Dialog dialog = new Dialog(context,R.style.DialogTheme);
							dialog.setContentView(R.layout.dialog_for_info);
							dialog.setTitle(getResources().getString(R.string.info));
							dialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
							Button proceed =(Button)dialog.findViewById(R.id.btn_proceed);
							Button cancel =(Button)dialog.findViewById(R.id.btn_cancel);
							final TextView txtTenureType=(TextView)dialog.findViewById(R.id.textView_tenure_type);	
							final TextView txtInfoMsg=(TextView)dialog.findViewById(R.id.textView_infoMsg);
							final TextView cnfrmMsg=(TextView)dialog.findViewById(R.id.textView_cnfrm_msg);	
							cnfrmMsg.setVisibility(View.VISIBLE);
							
							txtTenureType.setText(You_have_selected+tenureType.getOptionName());
							txtInfoMsg.setText(infoMsg);
							proceed.setText(yesStr);
							cancel.setText(noStr);
							
							proceed.setOnClickListener(new OnClickListener() 
							{					 
								//Run when button is clicked
								@Override
								public void onClick(View v) 
								{
									Intent myIntent;
									if(tenureTypeId==99)
									{
									myIntent = new Intent(context, PersonListWithDPActivity.class);
									}
									else{
									myIntent = new Intent(context, PersonListActivity.class);	
									}
									
									myIntent.putExtra("featureid", featureId);
									myIntent.putExtra("persontype", "natural");
									myIntent.putExtra("serverFeaterID",serverFeatureId);						
									startActivity(myIntent);
									dialog.dismiss();
									
								} 
							});
							
							cancel.setOnClickListener(new OnClickListener() 
							{					 
								//Run when button is clicked
								@Override
								public void onClick(View v) 
								{
									
									
										dialog.dismiss();
									
								} 
							});  

							dialog.show();
							
												
						
						}					
						
						if(tenureTypeId==106)
						{
							Intent myIntent = new Intent(context, AddNonNaturalPersonActivity.class);
							myIntent.putExtra("featureid", featureId);
							myIntent.putExtra("persontype", "natural");
							myIntent.putExtra("serverFeaterID",serverFeatureId);	
							startActivity(myIntent);
						}	
	/*					else if(tenureTypeId==99)
						{
							
							
							String	infoMsg="You can add one or more owners and up to two Administrators, you must indicated the name of the deceased person, and can add one or more persons of interest.";

							
							final Dialog dialog = new Dialog(context,R.style.DialogTheme);
							dialog.setContentView(R.layout.dialog_for_info);
							dialog.setTitle(getResources().getString(R.string.info));
							dialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
							Button proceed =(Button)dialog.findViewById(R.id.btn_proceed);
							Button cancel =(Button)dialog.findViewById(R.id.btn_cancel);
							final TextView txtTenureType=(TextView)dialog.findViewById(R.id.textView_tenure_type);	
							final TextView txtInfoMsg=(TextView)dialog.findViewById(R.id.textView_infoMsg);	
							final TextView cnfrmMsg=(TextView)dialog.findViewById(R.id.textView_cnfrm_msg);	
							cnfrmMsg.setVisibility(View.VISIBLE);
							txtTenureType.setText("You have selected: "+tenureType.getOptionName());
							txtInfoMsg.setText(infoMsg);
							proceed.setText("Yes");
							cancel.setText("No");
							
							proceed.setOnClickListener(new OnClickListener() 
							{					 
								//Run when button is clicked
								@Override
								public void onClick(View v) 
								{
									
									Intent myIntent = new Intent(context, PersonListWithDPActivity.class);
									myIntent.putExtra("featureid", featureId);
									myIntent.putExtra("persontype", "natural");
									myIntent.putExtra("serverFeaterID",serverFeatureId);						
									startActivity(myIntent);
									dialog.dismiss();
									
								} 
							});
							
							cancel.setOnClickListener(new OnClickListener() 
							{					 
								//Run when button is clicked
								@Override
								public void onClick(View v) 
								{
									
									
										dialog.dismiss();
									
								} 
							});  

							dialog.show();
							
							
							}*/							
					
				    
						
					}else{			
						Toast.makeText(context,R.string.unable_to_save_data, Toast.LENGTH_SHORT).show();
					}
				
						}
				else // EDIT CASE
				{
					DBController sqllite = new DBController(context);
					boolean saveResult = sqllite.saveSocialTenureFormData(attribList,groupId,featureId,keyword,personId);
					sqllite.close();
					if(saveResult)
					{

						/*
						Toast toast = Toast.makeText(context,R.string.data_saved, Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						finish();*/

						Option tenureType=sqllite.getTenureTypeOptionsValue(featureId);
						final long tenureTypeId=tenureType.getOptionId();
						if(tenureTypeId==70 || tenureTypeId==71 || tenureTypeId==72 || tenureTypeId==100 || tenureTypeId==99)
						{
							
							String infoMsg="No msg";
							//info message
							switch ((int) tenureTypeId) {
							case 70:
								//cf.showMessage(context,"Info","You can add only one adult owner & multiple person of interests");
								//infoMsg=infoSingleOccupantStr;
								infoMsg=infoMultipleTeneancyStr; //for live

								break;
							case 71:
								//cf.showMessage(context,"Info","You can add two adult owners & multiple person of interests");
								//infoMsg=infoMultipleJointStr;
								infoMsg=infoSingleOccupantStr; //for live
								break;
							case 72:
								//cf.showMessage(context,"Info","You can add two or more adult owners & multiple person of interests");
								//infoMsg=infoMultipleTeneancyStr;
								infoMsg=infoMultipleJointStr; //for live
								break;
								
							case 99:
								//cf.showMessage(context,"Info","You can add multiple minor owners & two guardian");
								infoMsg=infoTenancyInProbateStr;
								break;	
								
							case 100:
								//cf.showMessage(context,"Info","You can add multiple minor owners & two guardian");
								infoMsg=infoGuardianMinorStr;
								break;

							default:
								break;
						}


								final Dialog dialog = new Dialog(context,R.style.DialogTheme);
								dialog.setContentView(R.layout.dialog_for_info);
								dialog.setTitle(getResources().getString(R.string.info));
								dialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
								Button proceed =(Button)dialog.findViewById(R.id.btn_proceed);
								Button cancel =(Button)dialog.findViewById(R.id.btn_cancel);
								final TextView txtTenureType=(TextView)dialog.findViewById(R.id.textView_tenure_type);	
								final TextView txtInfoMsg=(TextView)dialog.findViewById(R.id.textView_infoMsg);
								final TextView cnfrmMsg=(TextView)dialog.findViewById(R.id.textView_cnfrm_msg);	
								cnfrmMsg.setVisibility(View.VISIBLE);
								txtTenureType.setText(You_have_selected+tenureType.getOptionName());
								txtInfoMsg.setText(infoMsg);
								proceed.setText(yesStr);
								cancel.setText(noStr);
								
								proceed.setOnClickListener(new OnClickListener() 
								{					 
									//Run when button is clicked
									@Override
									public void onClick(View v) 
									{
										Intent myIntent;
										if(tenureTypeId==99)
										{
										myIntent = new Intent(context, PersonListWithDPActivity.class);
										}
										else{
										myIntent = new Intent(context, PersonListActivity.class);	
										}
										
										myIntent.putExtra("featureid", featureId);
										myIntent.putExtra("persontype", "natural");
										myIntent.putExtra("serverFeaterID",serverFeatureId);						
										startActivity(myIntent);
										dialog.dismiss();
										
									} 
								});
								
								cancel.setOnClickListener(new OnClickListener() 
								{					 
									//Run when button is clicked
									@Override
									public void onClick(View v) 
									{
										
										
											dialog.dismiss();
										
									} 
								});  

								dialog.show();
								
						}
						else if(tenureTypeId==106)
							{
								Intent myIntent = new Intent(context, AddNonNaturalPersonActivity.class);
								myIntent.putExtra("featureid", featureId);
								myIntent.putExtra("persontype", "natural");
								myIntent.putExtra("serverFeaterID",serverFeatureId);	
								startActivity(myIntent);
							}	
/*						else if(tenureTypeId==99)
						{
							
							
							String	infoMsg="You can add one or more owners and up to two Administrators, you must indicated the name of the deceased person, and can add one or more persons of interest.";

							
							final Dialog dialog = new Dialog(context,R.style.DialogTheme);
							dialog.setContentView(R.layout.dialog_for_info);
							dialog.setTitle(getResources().getString(R.string.info));
							dialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
							Button proceed =(Button)dialog.findViewById(R.id.btn_proceed);
							Button cancel =(Button)dialog.findViewById(R.id.btn_cancel);
							final TextView txtTenureType=(TextView)dialog.findViewById(R.id.textView_tenure_type);	
							final TextView txtInfoMsg=(TextView)dialog.findViewById(R.id.textView_infoMsg);	
							final TextView cnfrmMsg=(TextView)dialog.findViewById(R.id.textView_cnfrm_msg);	
							cnfrmMsg.setVisibility(View.VISIBLE);
							txtTenureType.setText("You have selected: "+tenureType.getOptionName());
							txtInfoMsg.setText(infoMsg);
							proceed.setText("Yes");
							cancel.setText("No");
							
							proceed.setOnClickListener(new OnClickListener() 
							{					 
								//Run when button is clicked
								@Override
								public void onClick(View v) 
								{
									
									Intent myIntent = new Intent(context, PersonListWithDPActivity.class);
									myIntent.putExtra("featureid", featureId);
									myIntent.putExtra("persontype", "natural");
									myIntent.putExtra("serverFeaterID",serverFeatureId);						
									startActivity(myIntent);
									dialog.dismiss();
									
								} 
							});
							
							cancel.setOnClickListener(new OnClickListener() 
							{					 
								//Run when button is clicked
								@Override
								public void onClick(View v) 
								{
									
									
										dialog.dismiss();
									
								} 
							});  

							dialog.show();
							
							
							}	*/						
		
				    
						
					
					}else{			
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
							/*if(selecteditem.getOptionId()==106)
							{
								msgStr=getResources().getString(R.string.select_other_option);
							cf.showMessage(context,infoStr, msgStr);	
							isValid = false;
							errorList.add(item.getAttributeid());
							attribList.get(i).setFieldValue(null);
							}
							else{*/
								attribList.get(i).setFieldValue(selecteditem.getOptionId().toString());
								//}
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
