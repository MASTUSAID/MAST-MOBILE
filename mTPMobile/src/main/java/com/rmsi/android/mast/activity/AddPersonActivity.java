package com.rmsi.android.mast.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rmsi.android.mast.adapter.AttributeAdapter;
import com.rmsi.android.mast.db.DBController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.Media;
import com.rmsi.android.mast.domain.Option;
import com.rmsi.android.mast.util.CommonFunctions;

/**
 * 
 * @author prashant.nigam
 *
 */
public class AddPersonActivity extends ActionBarActivity 
{

	List<Attribute> attribList;
	List<Media> mediaPathList;
	List<Option> optionList;
	ListView listView;
	final Context context = this;
	AttributeAdapter adapterList;
	Button btnSave,btnCancel;
	String FieldValue;	
	CommonFunctions cf = CommonFunctions.getInstance();
	int groupId = 0;
	Long featureId = 0L,tenureTypeID;
	myImageAdapter adapter;
	List<Bitmap> imageFile = new ArrayList<Bitmap>();
	private List<Integer> errorList = new ArrayList<Integer>();
	int roleId=0;
	String personSubType="",personSubTypeValue="";
	String warningStr,msgStr;
	String  PersonFormType="Person";	
	//boolean isPersonExist=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{		
		super.onCreate(savedInstanceState);

		//Initializing context in common functions in case of a crash
		try{CommonFunctions.getInstance().Initialize(getApplicationContext());}catch(Exception e){}
		cf.loadLocale(getApplicationContext());

		setContentView(R.layout.activity_add_person);

		roleId=CommonFunctions.getRoleID();
		btnSave=(Button)findViewById(R.id.btn_savePerson);
		btnCancel = (Button)findViewById(R.id.btn_cancelPerson);


		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.AddPerson);
		if(toolbar!=null)
			setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		warningStr=getResources().getString(R.string.warning);
		
		DBController sqllite = new DBController(context);		
		Bundle extras = getIntent().getExtras();
		if (extras != null) 
		{
			int gId = extras.getInt("groupid");
			featureId = extras.getLong("featureid");			
			//personSubType=extras.getString("personSubType");
			//personSubTypeValue=extras.getString("personSubTypeValue");
			tenureTypeID=extras.getLong("tenureTypeID");
			PersonFormType=extras.getString("PersonFormType");
			
			if(PersonFormType!=null)
			if(PersonFormType.equalsIgnoreCase("POI"))
			toolbar.setTitle(R.string.perso_of_interest);
			
			if (gId != 0) 
			{
				attribList = sqllite.getFormDataByGroupId(gId,cf.getLocale(),PersonFormType);
				processPersonPhoto(gId,sqllite);
				groupId  = gId;
			}
			else
			{
				attribList = sqllite.getPersonAttribute(sqllite.getReadableDatabase(),cf.getLocale(),PersonFormType);			
			}
		}

		sqllite.close();
		listView = (ListView) findViewById(R.id.list_view_person);
		GridView gridView=(GridView) findViewById(R.id.gridView_image);
		adapter = new myImageAdapter(context);
		gridView.setAdapter(adapter);

		try {
			adapterList = new AttributeAdapter(context, attribList,featureId);
		} 
		catch (Exception e) {

			e.printStackTrace();
		}
		listView.setAdapter(adapterList);

		if(roleId==2)  // Hardcoded Id for Role (1=Trusted Intermediary, 2=Adjudicator)
		{
			btnSave.setEnabled(false);

		}
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
			public void onClick(View v) {
				finish();				
			}
		});
	}




	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		int id = item.getItemId();
		if(id == android.R.id.home)
		{
			finish();
		}
		return super.onOptionsItemSelected(item);
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
				String keyword="PERSON";
				if(PersonFormType.equalsIgnoreCase("POI"))
					keyword=PersonFormType;
				boolean saveResult = sqllite.saveFormDataTemp(attribList,groupId,featureId,keyword);
				sqllite.close();
				if(saveResult)
				{	
					Toast toast = Toast.makeText(context,R.string.data_saved, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					finish();
					
				}else{	
					Toast.makeText(context,R.string.unable_to_save_data, Toast.LENGTH_SHORT).show();
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

	public class myImageAdapter extends BaseAdapter
	{
		private Context mContext;

		public int getCount() 
		{
			return imageFile.size();
		}                               

		public Object getItem(int position) 
		{
			return imageFile.get(position);
		}   

		public long getItemId(int position) 
		{
			return 0;
		}

		public myImageAdapter(Context c) 
		{
			mContext = c;
		}                         



		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			final int pos = position;
			ImageView imageView;
			if (convertView == null)
			{  
				imageView = new ImageView(mContext);
				//  imageView.setRotation(90);
				imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(8, 8, 8, 8);
			} 
			else
			{
				imageView = (ImageView) convertView;
			}
			imageView.setImageBitmap(imageFile.get(position));

			imageView.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					File file = new File(mediaPathList.get(pos).getMediaPath());
					intent.setDataAndType(Uri.fromFile(file), "image/*");
					startActivity(intent); 
				}
			});

			imageView.setOnLongClickListener(new OnLongClickListener() 
			{				
				@Override
				public boolean onLongClick(View arg0) 
				{

					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
					alertDialogBuilder.setMessage(R.string.alert_delete_photo);
					alertDialogBuilder.setPositiveButton(R.string.btn_ok, 
							new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) 
						{
							DBController sqllite = new DBController(context);
							boolean isDeleted=sqllite.deletePersonPhoto(groupId);
							if(isDeleted)
							{
								Toast.makeText(context, R.string.pic_delete_msg,Toast.LENGTH_LONG).show(); 	
								processPersonPhoto(groupId, sqllite);
								adapter.notifyDataSetChanged();
							}
							else{					 
								Toast.makeText(context, "error",Toast.LENGTH_LONG).show();
							}
							sqllite.close();
						}
					});

					alertDialogBuilder.setNegativeButton(R.string.btn_cancel, 
							new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							dialog.dismiss();
						}
					});

					AlertDialog alertDialog = alertDialogBuilder.create();	
					alertDialog.show();

					return true;
				}
			});

			return imageView;
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
						
						if(item.getAttributeid()==21)
						{/*
							if(tenureTypeID==70 || tenureTypeID==71 || tenureTypeID==72 || tenureTypeID==99)
							{
								 long age = Long.parseLong(value);
								if(age<18)
								{
									isValid = false;
									errorList.add(item.getAttributeid());
									attribList.get(i).setFieldValue(null);
									msgStr=getResources().getString(R.string.minorNotAllowed); //"Minor not allowed.Age should not be less than 18."
									cf.showMessage(context,warningStr,msgStr);
								}
								else{
									attribList.get(i).setFieldValue(value);
								}
							}
							else if(tenureTypeID==100)
							{ int age = Integer.parseInt(value);
							if(age>=18 && personSubType.equalsIgnoreCase("Owner"))
							{
								isValid = false;
								errorList.add(item.getAttributeid());
								attribList.get(i).setFieldValue(null);
								msgStr=getResources().getString(R.string.ownerShouldBeMinor);  //"Owner should be minor in case of Guardian(Minor) tenure type"
								cf.showMessage(context,warningStr,msgStr);
							}
							if(age<18 && personSubType.equalsIgnoreCase("Guardian"))
							{
								isValid = false;
								errorList.add(item.getAttributeid());
								attribList.get(i).setFieldValue(null);
								msgStr=getResources().getString(R.string.guardianShudBeAdult);
								cf.showMessage(context,"Warning",msgStr); //"Guardian should be Adult."
							}
							else{
								attribList.get(i).setFieldValue(value);
							}
								
							}
													*/}
											
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
	
	private void processPersonPhoto(int gId, DBController sqllite)
	{
		mediaPathList=sqllite.getMediaPathByGroupId(gId);
		imageFile.clear();
		for(int i=0;i<mediaPathList.size();i++)	
		{					
			try {
				Bitmap ThumbImage = cf.getSampleBitmapFromFile(mediaPathList.get(i).getMediaPath(),48,48);
				imageFile.add(ThumbImage);
			} catch (Exception e) {
				cf.appLog("", e);e.printStackTrace();
			} 					
		}
	}
}