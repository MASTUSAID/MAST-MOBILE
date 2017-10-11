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


/**
 * 
 * @author prashant.nigam
 *
 */
public class Individual_ExistingListActivity extends ActionBarActivity 
{

	Button addnewPerson,addPOI,nextBtn;
	Context context;
	ListView listofPersonView,listOfPOIView,listOfDeceasedPerson;
	List<com.rmsi.android.mast.domain.Attribute> personAttribute = new ArrayList<com.rmsi.android.mast.domain.Attribute>();
	List<com.rmsi.android.mast.domain.Attribute> nextOfKin=new ArrayList<com.rmsi.android.mast.domain.Attribute>();
	List<com.rmsi.android.mast.domain.Attribute> deceasedPerson=new ArrayList<com.rmsi.android.mast.domain.Attribute>();
	com.rmsi.android.mast.adapter.MediaListingAdapterTemp personAdapter,poiAdapter ;
	//CustomArrayAdapter customArrayAdapter;
	com.rmsi.android.mast.adapter.CustomArrayAdapter customPOIArrayAdapter;
	Long featureId=0L;
	String mediaFolderName = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+
			com.rmsi.android.mast.util.CommonFunctions.parentFolderName+File.separator+ com.rmsi.android.mast.util.CommonFunctions.mediaFolderName;
	String timeStamp = new SimpleDateFormat("MMdd_HHmmss").format(new Date().getTime());
	private File file;
	FileOutputStream fo;
	com.rmsi.android.mast.util.CommonFunctions cf = com.rmsi.android.mast.util.CommonFunctions.getInstance();
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
    com.rmsi.android.mast.domain.Option tenureType;
    String warningStr,infoSingleOccupantStr,infoMultipleJointStr,infoMultipleTeneancyStr,infoTenancyInProbateStr,infoGuardianMinorStr,infoStr,tenureTypeStr;
    String personPhotoStr,personStr,person_of_InterestStr,saveStr,backStr,addPersonStr;
    String PersonFormType="PERSON";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		//Initializing context in common functions in case of a crash
		try{
			com.rmsi.android.mast.util.CommonFunctions.getInstance().Initialize(getApplicationContext());}catch(Exception e){}
		cf.loadLocale(getApplicationContext());
		
		setContentView(R.layout.activity_person_individual_existing_list);

		roleId= com.rmsi.android.mast.util.CommonFunctions.getRoleID();
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
		addPersonStr=getResources().getString(R.string.AddPerson);
		
		
		
		final com.rmsi.android.mast.db.DBController db = new com.rmsi.android.mast.db.DBController(context);
		
		
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
		nextBtn=(Button)findViewById(R.id.btn_nextPersonList);              //will be displayed as "Save" Button
		
		listofPersonView = (ListView)findViewById(R.id.list_Person);
		personCountLable=(TextView) findViewById(R.id.Person);
		personAdapter = new com.rmsi.android.mast.adapter.MediaListingAdapterTemp(context,this,personAttribute,"individualExistingPerson");
		listofPersonView.setAdapter(personAdapter);
    
		if(personCount<=0)
		{			
			addnewPerson.setText(addPersonStr);			
		}
		else
		{
			addnewPerson.setText(backStr);				
		}


		addnewPerson.setOnClickListener(new OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{
				
					 if(personCount<=0)
					 {
				Intent myIntent = new Intent(context, com.rmsi.android.mast.activity.AddPersonActivity.class);
				myIntent.putExtra("groupid", 0);
				myIntent.putExtra("featureid", featureId);
				myIntent.putExtra("tenureTypeID",tenureTypeId);
				myIntent.putExtra("PersonFormType",PersonFormType);
				startActivity(myIntent);
					 }
					 else if(personCount>=1)
					 {
						 Intent myIntent = new Intent(context,CaptureAttributesActivity.class);
							myIntent.putExtra("featureid", featureId);
							myIntent.putExtra("Server_featureid",serverFeatureId);
							startActivity(myIntent);
					 }


			}
		});

		/*if(roleId==2)  // Hardcoded Id for Adjudicator
		{
			addnewPerson.setEnabled(false);
			addPOI.setEnabled(false);
			back.setText(backStr);

		}*/
	 if(roleId == 1)
		{
			openAdd = true;
		}

	 nextBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				/*DBController db = new DBController(context);
				Option tenureType=db.getTenureTypeOptionsValue(featureId);
				 long tenureTypeId=tenureType.getOptionId();*/

				 Intent myIntent;
				  myIntent  =  new Intent(context, com.rmsi.android.mast.activity.DataSummaryActivity.class);
					myIntent.putExtra("featureid", featureId);
					myIntent.putExtra("Server_featureid", serverFeatureId);
					myIntent.putExtra("className", "Individual_ExistingListActivity");
					startActivity(myIntent);

				/*

				if(roleId==1)
				{
				 DBController db = new DBController(context);
				 List<Attribute> personList = db.getPersonList(featureId);
				 int personMediacount = db.getPersonMediaCount(featureId);
				 List<Attribute> deceasedList = db.getDeceasedPersonList(featureId);
				 long personCount=db.getOwnerAndGuardianCount(featureId);
				 int ownerCount=db.getOwnerCount(featureId);
				if(personList.size()!=0 )
				{

				// checks starts from here

					 isOwnerExist=db.isOwnerExist(featureId);


				 if(isOwnerExist)
				{

					if(personMediacount==personCount)
					{
						  if(tenureTypeId==70)  // for staging
					//	if(tenureTypeId==71)	//for live
						 {
							allowToBack();
						 }
						 else if(tenureTypeId==71) // for staging
						//else if(tenureTypeId==72)  //for live
						 {
							if(ownerCount==2)
							{
								allowToBack();
							}
							else if(ownerCount<2)
							{
								infoMultipleJointStr=getResources().getString(R.string.warning_multipleJoint);
								cf.showMessage(context,warningStr,infoMultipleJointStr);
							}

						 }
						 else if(tenureTypeId==72)
						 //else if(tenureTypeId==70)
						 {
								if(ownerCount==2)
								{
									allowToBack();
								}
								if(ownerCount>2)
								{
									allowToBack();
								}
								else if(ownerCount<2)
								{
									infoMultipleTeneancyStr=getResources().getString(R.string.warning_multipleTenancy);
									cf.showMessage(context,warningStr,infoMultipleTeneancyStr);
								}
						 }
						 else if(tenureTypeId==99)
						 {
							int deceasedPersonCount= deceasedList.size();
							if(deceasedPersonCount==1)
							{
								allowToBack();

							}
							else if(deceasedPersonCount<1 || deceasedPersonCount>1)
							{
								infoTenancyInProbateStr=getResources().getString(R.string.warning_tenancyProbate);
								cf.showMessage(context,warningStr,infoTenancyInProbateStr);

							}
						 }
						else if(tenureTypeId==100)
						 {
							 isGuardianExist=db.isGuardianExist(featureId);
							 if(isGuardianExist)
								{
								allowToBack();
							    }
								else{
									infoGuardianMinorStr=getResources().getString(R.string.warning_pleaseAddGuardianMinor);
									 cf.showMessage(context,warningStr,infoGuardianMinorStr);

								}
						 }
						 }
					else{
						personPhotoStr=getResources().getString(R.string.warning_addPersonPhoto);
						cf.showMessage(context,warningStr,personPhotoStr);
					}
				}


			}else{

				finish();
			}
			}
			else{

				finish();
			}



			*/}
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

					com.rmsi.android.mast.db.DBController db = new com.rmsi.android.mast.db.DBController(context);
					int personCount=db.getPersonList(featureId).size();
					//personSubType=db.getPersonSubType(groupId);
					//Open attributes form to edit --------------
					Intent myIntent  =  new Intent(context, com.rmsi.android.mast.activity.AddPersonActivity.class);
					myIntent.putExtra("groupid", groupId);
					myIntent.putExtra("featureid", featureId);
					//myIntent.putExtra("tenureTypeID",tenureTypeId);
					myIntent.putExtra("PersonFormType",PersonFormType);
					startActivity(myIntent);
					return true;
				/*case R.id.add_image:

					DBController sqllite = new DBController(context);
					int count = sqllite.getMediaCount(groupId);
					if(count==1)
					{
						msg=getResources().getString(R.string.you_can_add_only_one_photo);
						info=getResources().getString(R.string.info);
						cf.showMessage(context,info,msg);
					}
					else
					{
						timeStamp = new SimpleDateFormat("MMdd_HHmmss").format(new Date().getTime());
						Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
						file = new File(mediaFolderName+ File.separator +"mast_"+ timeStamp + ".jpg");
						if(file!=null)
						{
							cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
							cameraIntent.putExtra("ID", groupId);
							startActivityForResult(cameraIntent, 1);
						}
						else
						{
							msg=getResources().getString(R.string.unable_to_capture);
							Toast.makeText(context,msg, Toast.LENGTH_LONG).show();
						}
					}
					return true;

				case R.id.delete_photo:
					deletePhoto(groupId);
					return true;
*/
				case R.id.delete_entry:
					deleteEntry(groupId);
					return true;

				case R.id.view_attributes:
					//Open attributes form to edit --------------
					Intent intent  =  new Intent(context, com.rmsi.android.mast.activity.AddPersonActivity.class);
					intent.putExtra("groupid", groupId);
					intent.putExtra("featureid", featureId);
					//intent.putExtra("personSubType",personSubType);
					intent.putExtra("PersonFormType",PersonFormType);
					startActivity(intent);
					return true;
				default:
					return false;
				}
			}
		});
		popup.show();
	}





	private void deleteEntry(final int groupId)
	{
		com.rmsi.android.mast.db.DBController sqllite = new com.rmsi.android.mast.db.DBController(context);
		List<com.rmsi.android.mast.domain.Attribute> personList = new ArrayList<com.rmsi.android.mast.domain.Attribute>();
		List<com.rmsi.android.mast.domain.Attribute> tenureList = new ArrayList<com.rmsi.android.mast.domain.Attribute>();
		personList=sqllite.getAssociatedPersonWithtenure(groupId);
		tenureList=sqllite.getTenureList(featureId,null);

		if(tenureList.size()!=0)
		{
			if(nextOfKinCount!=0)
			{
				msg=getResources().getString(R.string.delete_poi_first);
				cf.showMessage(context,warningStr, msg);

			}
			else{
			Builder alertDialogBuilder = new Builder(context);
			alertDialogBuilder.setMessage(R.string.deleteEntryMsg);
			alertDialogBuilder.setPositiveButton(R.string.btn_ok,
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1)
				{
					com.rmsi.android.mast.db.DBController sqllite = new com.rmsi.android.mast.db.DBController(context);
					String keyword="person";
					boolean  result = sqllite.deleteRecord(groupId,keyword);
					if(result){
						refereshList();
						//Toast.makeText(context,"Deleted", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(context,"Unable to delete", Toast.LENGTH_SHORT).show();
					}
					sqllite.close();
					/*
					DBController sqllite = new DBController(context);
					List<Attribute> tenureList = new ArrayList<Attribute>();
					//tenureList=sqllite.getTenureList(featureId,null);

					if(tenureList.size()!=0)
					{
							String keyword="person";
							boolean  result = sqllite.deleteRecord(groupId,keyword);
							if(result){
								refereshList();
								//Toast.makeText(context,"Deleted", Toast.LENGTH_SHORT).show();
							}else{
								Toast.makeText(context,"Unable to delete", Toast.LENGTH_SHORT).show();
							}
					}

					sqllite.close();
				*/}
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
			sqllite.close();

		}
		}

	private void deletePhoto(final int groupId)
	{
		com.rmsi.android.mast.db.DBController sqllite = new com.rmsi.android.mast.db.DBController(context);
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
					com.rmsi.android.mast.db.DBController sqllite = new com.rmsi.android.mast.db.DBController(context);
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
		personAttribute.clear();
		//nextOfKin.clear();
		com.rmsi.android.mast.db.DBController sqllite = new com.rmsi.android.mast.db.DBController(context);
		personAttribute.addAll(sqllite.getPersonList(featureId));
		personAttribute.size();		
		personCount=sqllite.getPersonList(featureId).size();
		personCountLable.setText(personStr+" ("+personCount+")");
		personAdapter.notifyDataSetChanged();		
		sqllite.close();
		if(personCount<=0)
		{			
			addnewPerson.setText(addPersonStr);			
		}
		else
		{
			addnewPerson.setText(backStr);				
		}
	
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
					com.rmsi.android.mast.domain.Media media = new com.rmsi.android.mast.domain.Media();
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
						boolean result = new com.rmsi.android.mast.db.DBController(context).inserPersontMedia(media);

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
	Intent myIntent  =  new Intent(context, com.rmsi.android.mast.activity.DataSummaryActivity.class);
	myIntent.putExtra("featureid", featureId);
	myIntent.putExtra("Server_featureid", serverFeatureId);
	myIntent.putExtra("className", "Individual_ExistingListActivity");
	startActivity(myIntent); 
	}
	
}
