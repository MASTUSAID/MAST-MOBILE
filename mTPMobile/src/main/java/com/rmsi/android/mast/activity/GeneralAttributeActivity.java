package com.rmsi.android.mast.activity;

import java.util.ArrayList;
import java.util.List;

import com.rmsi.android.mast.adapter.AttributeAdapter;
import com.rmsi.android.mast.db.DBController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.Option;
import com.rmsi.android.mast.util.CommonFunctions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class GeneralAttributeActivity extends ActionBarActivity {

	Context context=this;
	Spinner villageSpinner,parcelTypeSpinner;
	ListView listView;
	AttributeAdapter adapterList;
	List<Attribute> attribList;
	Long featureId=0L;
	int groupId =0;
	DBController sqllite = new DBController(context);
	CommonFunctions cf = CommonFunctions.getInstance();
	private List<Integer> errorList = new ArrayList<Integer>();
	String personType="Select an option",hamletName_Id;
	static String serverFeatureId=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_general_attribute);
		
		//villageSpinner= (Spinner) findViewById(R.id.spinner_village);
		//parcelTypeSpinner=(Spinner) findViewById(R.id.spinner_parcelType);
		listView = (ListView) findViewById(R.id.list_view);
		List<Attribute> attribList; 
		TextView projectnameValue = (TextView) findViewById(R.id.projectname_lbl);
		TextView spatialunitValue = (TextView) findViewById(R.id.spatialunit_lbl);
		TextView communeValue = (TextView) findViewById(R.id.CommuneName_lbl); // added by Vaibhav
		
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) 
		{
			featureId = extras.getLong("featureid");
		   serverFeatureId=extras.getString("Server_featureid");
			
		}

		//flag=sqllite.getFormValues(featureId);
		boolean flag=sqllite.isGeneralAttributeSaved(featureId);
		if(flag)
		{
		//	spinnerPersonType.setEnabled(false);
		}
		
		projectnameValue.setText(projectnameValue.getText()+"   :  "+sqllite.getProjectname());
		communeValue.setText(communeValue.getText()+"   :  "+sqllite.getCommune());
		//VillageName.setText(VillageName.getText()+"   :  "+sqllite.villageName());
		
		if(!TextUtils.isEmpty(serverFeatureId) && serverFeatureId !=null)
		{	
		spatialunitValue.setText("USIN"+"   :  "+serverFeatureId.toString());
		}
		else
		{
		spatialunitValue.setText(spatialunitValue.getText()+"   :  "+featureId.toString());
		}

		

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.title_activity_capture_attributes);
		if(toolbar!=null)
			setSupportActionBar(toolbar);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);	
		
		if(featureId==0)
		{
			
			attribList = sqllite.getGeneralAttribute(cf.getLocale());
		}
		else
		{
			
			String keyword="general";
			//attribList = sqllite.getFeatureGenaralInfo(featureId,keyword,cf.getLocale());
			attribList = sqllite.getGneralAttributeData(featureId,keyword,cf.getLocale());
			if(attribList.size()>0)
			{
				groupId = attribList.get(0).getGroupId();
			}
			
		}
		
		sqllite.close();
		try {
			adapterList = new AttributeAdapter(context, attribList,featureId);
		} 
		catch (Exception e) {

			e.printStackTrace();
		}
		
		listView.setAdapter(adapterList);
	
/*   BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

                     Set<BluetoothDevice> mDevices = mBluetoothAdapter.getBondedDevices();

                     Iterator<BluetoothDevice> itr = mDevices.iterator();

                     while(itr.hasNext()){

                           BluetoothDevice bluetoothDevice = itr.next();

                          

                           if(bluetoothDevice.getName().contains("Garmin")){

                                  UUID uuid = UUID

                                          .fromString("00001101-0000-1000-8000-00805F9B34FB");

                                 

                                 

                                  try {

                                         BluetoothSocket bluetoothSocket  = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);

                                         bluetoothSocket.connect();

                                          InputStream in = bluetoothSocket.getInputStream();

                                         InputStreamReader isr = new InputStreamReader(in);

                                         BufferedReader br = new BufferedReader(isr);

 

                                         while (true) {

                                              String nmeaMessage = br.readLine();
                                              CommonFunctions cf = CommonFunctions.getInstance();
                                             cf.gpsLog("NMEA", nmeaMessage);
                                              Log.d("NMEA", nmeaMessage);
                                              // parse NMEA messages
                                              
                                         }

                                  } catch (IOException e) {

                                  System.out.println(e); 

                                  }                                       
                           }
                     }*/

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
			//saveData();
			Intent myIntent = new Intent(context, com.rmsi.android.mast.activity.AddGeneralPropertyActivity.class);
			myIntent.putExtra("featureid", featureId);
			startActivity(myIntent);
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
				try {
					if(groupId==0)
						groupId = cf.getGroupId();

					DBController sqllite = new DBController(context);
					boolean	saveResult = sqllite.saveFormDataTemp(attribList,groupId,featureId,"General");
						sqllite.close();

						if(saveResult)
						{
							Toast toast = Toast.makeText(context,R.string.data_saved, Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
							Intent myIntent = new Intent(context, AddGeneralPropertyActivity.class);
							myIntent.putExtra("featureid", featureId);
							startActivity(myIntent);
						}else{									
							Toast.makeText(context,R.string.unable_to_save_data, Toast.LENGTH_SHORT).show();
						}


				} catch (Exception e) {
					cf.appLog("", e);e.printStackTrace();
				}

				
		}
		else{				
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
