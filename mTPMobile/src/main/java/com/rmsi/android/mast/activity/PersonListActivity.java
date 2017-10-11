package com.rmsi.android.mast.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.internal.db;
import com.rmsi.android.mast.adapter.CustomArrayAdapter;
import com.rmsi.android.mast.adapter.MediaListingAdapterTemp;
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
public class PersonListActivity extends ActionBarActivity 
{

	Button addnewPerson,back,addPOI,nextBtn;
	Context context;
	ListView listofPersonView,listOfPOIView,listOfDeceasedPerson;
	List<Attribute> personAttribute = new ArrayList<Attribute>();
	List<Attribute> poiAttribute=new ArrayList<Attribute>();
	
	MediaListingAdapterTemp personAdapter,poiAdapter ;
	//CustomArrayAdapter customArrayAdapter;
	CustomArrayAdapter customPOIArrayAdapter;
	Long featureId=0L;
	String mediaFolderName = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+
			CommonFunctions.parentFolderName+File.separator+CommonFunctions.mediaFolderName;
	String timeStamp = new SimpleDateFormat("MMdd_HHmmss").format(new Date().getTime());
	private File file;
	FileOutputStream fo;
	CommonFunctions cf = CommonFunctions.getInstance();
	List<File> Imagearray=new ArrayList<File>();
	String msg,info,warning;
	int position;
	int roleId=0;
	String personType,personSubType="Owner",personSubTypeValue="10";
	String serverFeatureId;
	boolean openAdd = false;
	int pos=0,personCount=0;
	TextView txtviewNextOfKin,personCountLable,txtView_deceased_person;
    View divider1,divider2;
    int nextOfKinCount,deceasedCount,AdminCount,ownerCount;
    boolean isGuardianExist=false,isAdminExist=false,isOwnerExist=false,isPOIExist=false,IsDeceasedPesrsonExist=false;
    long tenureTypeId=0l;
    Dialog dialogForPersonSubType ;
    Option tenureType;
    String warningStr,infoSingleOccupantStr,infoMultipleJointStr,infoMultipleTeneancyStr,infoTenancyInProbateStr,infoGuardianMinorStr,infoStr,tenureTypeStr;
    String personPhotoStr,personStr,person_of_InterestStr,saveStr,backStr,addRepStr,repStr,addPoi;
    String PersonFormType="COLLECTIVE";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		//Initializing context in common functions in case of a crash
		try{CommonFunctions.getInstance().Initialize(getApplicationContext());}catch(Exception e){}
		cf.loadLocale(getApplicationContext());
		
		setContentView(R.layout.activity_person_list);

		roleId=CommonFunctions.getRoleID();
		TextView spatialunitValue = (TextView) findViewById(R.id.spatialunit_lbl);
		TextView tenure_type=(TextView) findViewById(R.id.tenureType_lbl);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.title_person);
		if(toolbar!=null)
			setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);	

		context=this;
		
		warningStr=getResources().getString(R.string.warning);
		infoStr=getResources().getString(R.string.info);
		tenureTypeStr=getResources().getString(R.string.tenureType);
		personStr=getResources().getString(R.string.person);
		person_of_InterestStr=getResources().getString(R.string.person_of_interest);
		saveStr=getResources().getString(R.string.save);
		backStr=getResources().getString(R.string.back);
		addRepStr=getResources().getString(R.string.add_representative);
		repStr=getResources().getString(R.string.representative);
		addPoi=getResources().getString(R.string.addPOI);
		
		nextBtn=(Button)findViewById(R.id.btn_next);  
				
		
		final DBController db = new DBController(context);	
		Bundle extras = getIntent().getExtras();
		if (extras != null) 
		{
			featureId = extras.getLong("featureid");
			personType=extras.getString("persontype");	
			serverFeatureId=extras.getString("serverFeaterID");
			tenureType=db.getTenureTypeOptionsValue(featureId);    //check tenure type
			tenureTypeId=tenureType.getOptionId();
			PersonFormType=extras.getString("PersonFormType");
		}

		if(!TextUtils.isEmpty(serverFeatureId) && serverFeatureId !=null)
		{	
			spatialunitValue.setText("USIN"+" "+serverFeatureId.toString());
		}
		else
		{
			spatialunitValue.setText(spatialunitValue.getText()+" "+featureId.toString());
		}
		
		
		if(!TextUtils.isEmpty(tenureType.getOptionName()) && tenureType.getOptionName() !=null)
		{	
			tenure_type.setText(tenureTypeStr+" "+tenureType.getOptionName());
		}

		addnewPerson=(Button)findViewById(R.id.btn_addNewPerson);
		back=(Button)findViewById(R.id.btn_backPersonList);              //will be displayed as "Save" Button
		listofPersonView = (ListView)findViewById(R.id.list_Person);
		personCountLable=(TextView) findViewById(R.id.Person);
		listOfPOIView=(ListView)findViewById(R.id.list_POI);  
		personAdapter = new MediaListingAdapterTemp(context,this,personAttribute,"personlist");
		listofPersonView.setAdapter(personAdapter);
        poiAdapter = new MediaListingAdapterTemp(context,this,poiAttribute,"poiList");
        listOfPOIView.setAdapter(poiAdapter);
		
		if(personCount<=0)
		{			
			addnewPerson.setText(addRepStr);			
		}
		else
		{
			addnewPerson.setText(addPoi);	
		
		}


		addnewPerson.setOnClickListener(new OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{
				
					 if(personCount<=0)
					 {
				Intent myIntent = new Intent(context, AddPersonActivity.class);
				myIntent.putExtra("groupid", 0);
				myIntent.putExtra("featureid", featureId);				
				myIntent.putExtra("tenureTypeID",tenureTypeId);
				myIntent.putExtra("PersonFormType","COLLECTIVE");				
				startActivity(myIntent);
					 }
					 else if(personCount>=1)
					 {					 
						 Intent myIntent = new Intent(context, AddPersonActivity.class);
							myIntent.putExtra("groupid", 0);
							myIntent.putExtra("featureid", featureId);				
							myIntent.putExtra("tenureTypeID",tenureTypeId);
							myIntent.putExtra("PersonFormType","POI");
							
							startActivity(myIntent); 
					 }
				
			}
		});

		if(roleId==2)  // Hardcoded Id for Adjudicator
		{
			addnewPerson.setEnabled(false);
			addPOI.setEnabled(false);
			back.setText(backStr);
			
		}
		else if(roleId == 1)
		{
			openAdd = true;
		}

		back.setOnClickListener(new OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{				
				finish();
			}
		});
		
		 nextBtn.setOnClickListener(new OnClickListener() 
			{			
				@Override
				public void onClick(View v) 
				{
					 Intent myIntent;
					  myIntent  =  new Intent(context, DataSummaryActivity.class);
						myIntent.putExtra("featureid", featureId);
						myIntent.putExtra("Server_featureid", serverFeatureId);
						myIntent.putExtra("className", "PersonListActivity");
						startActivity(myIntent); 
					 
		}
			});
			
		

	}

	
	public void showPopup(View v, Object object) 
	{
		PopupMenu popup = new PopupMenu(context, v);
		MenuInflater inflater = popup.getMenuInflater();
		if(roleId==1)  // Hardcoded Id for Adjudicator
		{
			inflater.inflate(R.menu.attribute_listing_options_for_person, popup.getMenu());
		}
		else
		{
			inflater.inflate(R.menu.attribute_listing_options_to_view_details, popup.getMenu());
		}

		position  = (Integer) object;
		final int groupId = personAttribute.get(position).getGroupId();

		popup.setOnMenuItemClickListener(new OnMenuItemClickListener() 
		{			
			@Override
			public boolean onMenuItemClick(MenuItem item) 
			{
				switch (item.getItemId()) 
				{
				case R.id.edit_attributes:
					
					DBController db = new DBController(context);				
					//Open attributes form to edit --------------
					Intent myIntent  =  new Intent(context, AddPersonActivity.class);
					myIntent.putExtra("groupid", groupId);
					myIntent.putExtra("featureid", featureId);
					myIntent.putExtra("tenureTypeID",tenureTypeId);
					myIntent.putExtra("PersonFormType","COLLECTIVE");
					startActivity(myIntent);
					return true;
				case R.id.delete_entry:
					deleteEntry(groupId);
					return true;

				case R.id.view_attributes:
					//Open attributes form to edit --------------
					Intent intent  =  new Intent(context, AddPersonActivity.class);
					intent.putExtra("groupid", groupId);
					intent.putExtra("featureid", featureId);
					intent.putExtra("personSubType",personSubType);
					intent.putExtra("personSubTypeValue",personSubTypeValue);
					startActivity(intent);
					return true;
				default:
					return false;
				}
			}
		});
		popup.show();
	}
	
	
	
	public void showPopupForPOI(View v, Object object) 
	{
		PopupMenu popup = new PopupMenu(context, v);
		MenuInflater inflater = popup.getMenuInflater();
		
			inflater.inflate(R.menu.attribute_listing_options_for_poi, popup.getMenu());
		
		position  = (Integer) object;
		final int groupId = poiAttribute.get(position).getGroupId();

		popup.setOnMenuItemClickListener(new OnMenuItemClickListener() 
		{			
			@Override
			public boolean onMenuItemClick(MenuItem item) 
			{
				switch (item.getItemId()) 
				{
				case R.id.edit:
					
					
					DBController db = new DBController(context);			
					Intent myIntent  =  new Intent(context, AddPersonActivity.class);
					myIntent.putExtra("groupid", groupId);
					myIntent.putExtra("featureid", featureId);
					myIntent.putExtra("tenureTypeID",tenureTypeId);
					myIntent.putExtra("PersonFormType","POI");
					startActivity(myIntent);
					return true;
			
				case R.id.delete_entry:
					deletePOIEntry(groupId);
					return true;

				default:
					return false;
				}
			}
		});
		
		if(roleId==1)
		{
		popup.show();
		}
		else{
			//nothing to show
		}
		
	}
	
	private void deleteEntry(final int groupId)
	{
		DBController sqllite = new DBController(context);
		List<Attribute> personList = new ArrayList<Attribute>();
		
		String msg=	getString(R.string.deleteEntryMsg);
		final List<Attribute> poiLst = sqllite.getPoiList(featureId);
				if(poiLst.size()>0)
				msg=	getString(R.string.deletePersonEntryMsg);
			Builder alertDialogBuilder = new Builder(context);
			alertDialogBuilder.setMessage(msg);
			alertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1)
				{
					DBController sqllite = new DBController(context);
					int tenureCount=sqllite.getTenureCount(featureId);

					if(tenureCount>0)
					{
							String keyword="person";
							boolean  result = sqllite.deleteRecord(groupId,keyword);
							if(result){
								if(poiLst.size()>0){
									for (Attribute attribute : poiLst) {
										sqllite.deleteRecord(attribute.getGroupId(),"POI");
									}
								}

								addnewPerson.setText(addRepStr);
								refereshList();
							}else{
								Toast.makeText(context,"Unable to delete", Toast.LENGTH_SHORT).show();
							}
					}

					sqllite.close();
				}
			});

			alertDialogBuilder.setNegativeButton(R.string.no,
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
				}
			});

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();

			sqllite.close();

		}


	private void deletePOIEntry(final int groupId)
	{
		DBController sqllite = new DBController(context);
		List<Attribute> personList = new ArrayList<Attribute>();

			Builder alertDialogBuilder = new Builder(context);
			alertDialogBuilder.setMessage(R.string.deleteEntryMsg);
			alertDialogBuilder.setPositiveButton(R.string.btn_ok,
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1)
				{
					DBController sqllite = new DBController(context);
					int tenureCount=sqllite.getTenureCount(featureId);
					if(tenureCount>0)
					{
							String keyword="POI";
							boolean  result = sqllite.deleteRecord(groupId,keyword);
							if(result){
								refereshList();
							}else{
								Toast.makeText(context,"Unable to delete", Toast.LENGTH_SHORT).show();
							}
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

			sqllite.close();

		}

	private void deletePhoto(final int groupId)
	{
		DBController sqllite = new DBController(context);
		int mediaSize = sqllite.getMediaPathByGroupId(groupId).size();
		sqllite.close();
		if(mediaSize>0)
		{
			Builder alertDialogBuilder = new Builder(context);
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
						refereshList();
						Toast.makeText(context, R.string.pic_delete_msg,Toast.LENGTH_LONG).show(); 					 
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
		}
		else
		{
			Toast.makeText(context, R.string.no_pic_person, Toast.LENGTH_LONG).show();
		}
	}
	private void refereshList()
	{   
		DBController sqllite = new DBController(context);
		personAttribute.clear();
		personAttribute.addAll(sqllite.getPersonList(featureId));
		personAttribute.size();	
		
		poiAttribute.clear();		
		poiAttribute.addAll(sqllite.getPoiList(featureId));
		poiAttribute.size();		
		personCount=sqllite.getPersonList(featureId).size();
		personCountLable.setText(personStr+" ("+personCount+")");
		personAdapter.notifyDataSetChanged();
		poiAdapter.notifyDataSetChanged();
		if(personCount<=0)
		{			
			addnewPerson.setText(addRepStr);			
		}
		else
		{
			addnewPerson.setText(addPoi);	
		
		}
		sqllite.close();
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


	@Override
	protected void onResume() 
	{
		
		
				refereshList();

		super.onResume();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{	
		ProgressDialog ringProgressDialog = null;
		if(requestCode==1) //Image
		{
			if (resultCode == RESULT_OK) 
			{
				try {
					//photo = rotate(photo, 90);  
					Media media = new Media();
					if(file==null)
					{
						String filename = mediaFolderName+ File.separator +"mast_"+ timeStamp + ".jpg";
						System.out.println("filename="+filename);
						file = new File(filename);
						if(file!=null && file.exists())
							cf.addErrorMessage("PersonListActivity", "Problem Adding file with name:"+ filename);
					}
					if(file!=null && file.exists())
					{
						/*if (file.length()>200000) 
						{*/
						//picking the file and compressing it.
						compressImage();
						/*	}*/
						int groupId = personAttribute.get(position).getGroupId();
						media.setMediaPath(file.getAbsolutePath());
						media.setFeatureId(featureId);
						media.setMediaType("Image");
						media.setMediaId(groupId);
						boolean result = new DBController(context).inserPersontMedia(media);

						if(result)
							Toast.makeText(getApplicationContext(), R.string.pic_added_successfully,Toast.LENGTH_LONG).show();
						else
							Toast.makeText(getApplicationContext(), R.string.unable_to_capture,Toast.LENGTH_LONG).show();
					}
					else{
						Toast.makeText(getApplicationContext(), R.string.unable_to_capture,Toast.LENGTH_LONG).show();
					}
				} 
				catch (Exception e1) {
					cf.appLog("", e1);e1.printStackTrace();
				}
				finally{
					if(ringProgressDialog!=null)   
						ringProgressDialog.dismiss();
				}
			}  
		}
	}

	private void compressImage()
	{
		try {	
			BitmapFactory.Options options=new BitmapFactory.Options();
			options.inJustDecodeBounds=true;
			BitmapFactory.decodeFile(file.getAbsolutePath(), options);
			options.inJustDecodeBounds=false;
			Bitmap resizedPhoto=BitmapFactory.decodeFile(file.getAbsolutePath(), options);
			Bitmap resizedPhoto1 = Bitmap.createScaledBitmap(resizedPhoto, 768, 1024, true);
			ByteArrayOutputStream outFile = new ByteArrayOutputStream();
			resizedPhoto1.compress(Bitmap.CompressFormat.JPEG,60, outFile);
			outFile.size();

			if((outFile.size()/1024)>150)
			{
				Toast.makeText(getApplicationContext(), "File Length-->"+(outFile.size()/1024),Toast.LENGTH_LONG).show();
				resizedPhoto=BitmapFactory.decodeByteArray(outFile.toByteArray(), 0, outFile.toByteArray().length);
				outFile = new ByteArrayOutputStream();
				resizedPhoto.compress(Bitmap.CompressFormat.JPEG,40, outFile);
			}

			fo = new FileOutputStream(file.getAbsolutePath());
			fo.write(outFile.toByteArray());
			fo.flush();
			fo.close();


		} catch (Exception e) {
			Toast.makeText(context, "unable to compress image", Toast.LENGTH_SHORT).show();
			cf.appLog("", e);
			e.printStackTrace();
		}
	}

	public static int calculateInSampleSize(
			BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}
	

	
	public void allowToBack()
	{
	Intent myIntent  =  new Intent(context, DataSummaryActivity.class);
	myIntent.putExtra("featureid", featureId);
	myIntent.putExtra("Server_featureid", serverFeatureId);
	myIntent.putExtra("className", "PersonListActivity");
	startActivity(myIntent); 
	}
	
}
