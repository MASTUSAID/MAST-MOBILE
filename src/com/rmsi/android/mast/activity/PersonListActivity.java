package com.rmsi.android.mast.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.ls.LSInput;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.internal.db;
import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.adapter.CustomArrayAdapter;
import com.rmsi.android.mast.adapter.MediaListingAdapterTemp;
import com.rmsi.android.mast.db.DBController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.Media;
import com.rmsi.android.mast.domain.Option;
import com.rmsi.android.mast.util.CommonFunctions;

/**
 * 
 * @author Amreen.s
 *
 */
public class PersonListActivity extends ActionBarActivity 
{

	Button addnewPerson,back,addPOI;
	Context context;
	ListView listView,listOfKin,listOfDeceasedPerson;
	List<Attribute> attribute = new ArrayList<Attribute>();
	List<Attribute> nextOfKin=new ArrayList<Attribute>();
	List<Attribute> deceasedPerson=new ArrayList<Attribute>();
	MediaListingAdapterTemp adapter ;
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
	int pos=0;
	TextView txtviewNextOfKin,personCountLable,txtView_deceased_person;
    View divider1,divider2;
    int nextOfKinCount,deceasedCount,AdminCount,ownerCount;
    boolean isGuardianExist=false,isAdminExist=false,isOwnerExist=false,isPOIExist=false,IsDeceasedPesrsonExist=false;
    long tenureTypeId=0l;
    Dialog dialogForPersonSubType ;
    Option tenureType;
    String warningStr,infoSingleOccupantStr,infoMultipleJointStr,infoMultipleTeneancyStr,infoTenancyInProbateStr,infoGuardianMinorStr,infoStr,tenureTypeStr;
    String personPhotoStr,personStr,person_of_InterestStr,saveStr,backStr;
  
    
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		//Initializing context in common functions in case of a crash
		try{CommonFunctions.getInstance().Initialize(getApplicationContext());}catch(Exception e){}
		cf.loadLocale(getApplicationContext());
		
		setContentView(R.layout.activity_list);

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
		
		final DBController db = new DBController(context);
		
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) 
		{
			featureId = extras.getLong("featureid");
			personType=extras.getString("persontype");	
			serverFeatureId=extras.getString("serverFeaterID");
			tenureType=db.getTenureTypeOptionsValue(featureId);    //check tenure type
			tenureTypeId=tenureType.getOptionId();
		}

		if(!TextUtils.isEmpty(serverFeatureId) && serverFeatureId !=null)
		{	
			spatialunitValue.setText("USIN"+" : "+serverFeatureId.toString());
		}
		else
		{
			spatialunitValue.setText(spatialunitValue.getText()+"   :  "+featureId.toString());
		}
		
		
		if(!TextUtils.isEmpty(tenureType.getOptionName()) && tenureType.getOptionName() !=null)
		{	
			tenure_type.setText(tenureTypeStr+" : "+tenureType.getOptionName());
		}
		
		
		

		addnewPerson=(Button)findViewById(R.id.btn_addNewPerson);
		back=(Button)findViewById(R.id.btn_backPersonList);              //will be displayed as "Save" Button
		addPOI=(Button)findViewById(R.id.btn_addNextKin);
		listView = (ListView)findViewById(android.R.id.list);
		personCountLable=(TextView) findViewById(R.id.Person);
		listOfKin=(ListView)findViewById(R.id.list_of_kin);  
		txtviewNextOfKin=(TextView)findViewById(R.id.txtView_nextOfKin);
		adapter = new MediaListingAdapterTemp(context,this,attribute,"personlist");
		listView.setAdapter(adapter);
		customPOIArrayAdapter=new CustomArrayAdapter(context,this, android.R.layout.simple_list_item_1, nextOfKin,"personlist");
		listOfKin.setAdapter(customPOIArrayAdapter);	
		


		addnewPerson.setOnClickListener(new OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{
				if(attribute.size()==1 && personType.equalsIgnoreCase("nonNatural"))
				{
					msg=getResources().getString(R.string.can_add_only_one_person_with_non_natural_person);
					info=getResources().getString(R.string.info);
					cf.showMessage(context,info,msg);	
				}
				else
				{		
					try	
					{
						 // Changes for new flow on 19/10/2015 by amreen
					     // Added dialog for person sub-type
						
						String[] person_subType = getResources().getStringArray(R.array.person_sub_type);
						final Dialog dialogForPersonSubType = new Dialog(context,R.style.DialogTheme);
						dialogForPersonSubType.setContentView(R.layout.dialog_show_list);
						dialogForPersonSubType.setTitle(getResources().getString(R.string.select_person_subtype));
						dialogForPersonSubType.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
						ListView listViewForPersonSubtype = (ListView) dialogForPersonSubType.findViewById(R.id.commonlistview);
						Button save =(Button)dialogForPersonSubType.findViewById(R.id.btn_ok);
						save.setText(saveStr);

						ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, 
								R.layout.item_list_single_choice,person_subType);

						listViewForPersonSubtype.setAdapter(adapter);
						listViewForPersonSubtype.setItemChecked(0, false);

						listViewForPersonSubtype.setOnItemClickListener(new OnItemClickListener() 
						{
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) 
							{
								int itemPosition = position;


								if(itemPosition==0) // for Owner
								{
									personSubType="Owner";
									personSubTypeValue="3";
								}
								else if(itemPosition==1) // for Administrator
								{
									personSubType="Administrator";	
									personSubTypeValue="4";
								}
								else if(itemPosition==2) // for Guardian
								{
									personSubType="Guardian";	
									personSubTypeValue="5";
								}


							}});
						
						
						save.setOnClickListener(new OnClickListener() 
						{					 
							//Run when button is clicked
							@Override
							public void onClick(View v) 
							{
								 int personListCount=listView.getAdapter().getCount();
									personListCount++;	
									boolean ifAllowed=false;
									ownerCount=db.getOwnerCount(featureId);
									
									//if(tenureTypeId==70)   // Single Occupant 
										if(tenureTypeId==71)   // Single Occupant  //for live
										{								
										ifAllowed=checkSingleOccupancyType(featureId, personSubType,ownerCount);
										if(ifAllowed)
										{Intent myIntent = new Intent(context, AddPersonActivity.class);
										myIntent.putExtra("groupid", 0);
										myIntent.putExtra("featureid", featureId);
										myIntent.putExtra("PersonCount",personListCount);
										myIntent.putExtra("personSubType",personSubType);
										myIntent.putExtra("personSubTypeValue",personSubTypeValue);
										myIntent.putExtra("tenureTypeID",tenureTypeId);
										startActivity(myIntent);
										dialogForPersonSubType.dismiss();
										}
										}
										
//										else if(tenureTypeId==71)   // Multiple Occupancy(Joint)
										else if(tenureTypeId==72)   // Multiple Occupancy(Joint)  for live
										{								
										ifAllowed=checkMultipleOcuupany_Joint(featureId, personSubType,ownerCount);
										if(ifAllowed)
										{Intent myIntent = new Intent(context, AddPersonActivity.class);
										myIntent.putExtra("groupid", 0);
										myIntent.putExtra("featureid", featureId);
										myIntent.putExtra("PersonCount",personListCount);
										myIntent.putExtra("personSubType",personSubType);
										myIntent.putExtra("personSubTypeValue",personSubTypeValue);
										myIntent.putExtra("tenureTypeID",tenureTypeId);
										startActivity(myIntent);
										dialogForPersonSubType.dismiss();
										}
										}
										
//										else if(tenureTypeId==72)   // Multiple Occupancy(Tenancy in common)
										else if(tenureTypeId==70)   // Multiple Occupancy(Tenancy in common) for live
										{								
										ifAllowed=checkMultipleOcuupany_TenancyInCommon(featureId, personSubType,ownerCount);
										if(ifAllowed)
										{Intent myIntent = new Intent(context, AddPersonActivity.class);
										myIntent.putExtra("groupid", 0);
										myIntent.putExtra("featureid", featureId);
										myIntent.putExtra("PersonCount",personListCount);
										myIntent.putExtra("personSubType",personSubType);
										myIntent.putExtra("personSubTypeValue",personSubTypeValue);
										myIntent.putExtra("tenureTypeID",tenureTypeId);
										startActivity(myIntent);
										dialogForPersonSubType.dismiss();
										}
										}
								
								else if(tenureTypeId==100)   // Guardian (minor)
								{								
								ifAllowed=checkOccupancyType_GuardianMinor(featureId, personSubType,ownerCount);
								if(ifAllowed)
								{Intent myIntent = new Intent(context, AddPersonActivity.class);
								myIntent.putExtra("groupid", 0);
								myIntent.putExtra("featureid", featureId);
								myIntent.putExtra("PersonCount",personListCount);
								myIntent.putExtra("personSubType",personSubType);
								myIntent.putExtra("personSubTypeValue",personSubTypeValue);
								myIntent.putExtra("tenureTypeID",tenureTypeId);
								startActivity(myIntent);
								dialogForPersonSubType.dismiss();
								}
								}
							} 
						});  

						dialogForPersonSubType.show();
	
					}catch(Exception e)
					{
						e.printStackTrace();
						System.out.println(e.getMessage());
					}
				
				
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
						 // if(tenureTypeId==70)
						if(tenureTypeId==71)	//for live  
						 {
							allowToBack();
						 }
						// else if(tenureTypeId==71)
						else if(tenureTypeId==72)  //for live
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
						 //else if(tenureTypeId==72)
						 else if(tenureTypeId==70)
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
				
				
				
			}
		});
		
		
		
		addPOI.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				 isOwnerExist=db.isOwnerExist(featureId);
				 if(isOwnerExist)
				 {
				if(tenureTypeId==70 || tenureTypeId==71 || tenureTypeId==72)   // Single Occupant
				{	

					final Dialog dialog = new Dialog(context,R.style.DialogTheme);
					dialog.setContentView(R.layout.dialog_person_of_interest);
					dialog.setTitle(getResources().getString(R.string.nextKin));
					dialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
				
					Button save =(Button)dialog.findViewById(R.id.btn_ok);
					final EditText firstName=(EditText)dialog.findViewById(R.id.editTextFirstName);
					final EditText middleName=(EditText)dialog.findViewById(R.id.editTextMiddleName);
					final EditText lastName=(EditText)dialog.findViewById(R.id.editTextLastName);
					save.setText(saveStr);
				
					save.setOnClickListener(new OnClickListener() 
					{					 
						//Run when button is clicked
						@Override
						public void onClick(View v) 
						{
							
							String poi_fName = firstName.getText().toString();
							String poi_middleName=middleName.getText().toString();
							String poi_lastName=lastName.getText().toString();
							String personOfInterest = firstName.getText().toString()+" " +middleName.getText().toString()+" "+lastName.getText().toString(); 	
				        	if (!TextUtils.isEmpty(poi_fName) || !TextUtils.isEmpty(poi_middleName) || !TextUtils.isEmpty(poi_lastName)) 
				        	{						
								DBController db = new DBController(context);
								boolean result = db.saveNextOfKin(personOfInterest,featureId);
								db.close();
								if (result) {
									customPOIArrayAdapter.notifyDataSetChanged();
									msg=getResources().getString(R.string.AddedSuccessfully);
									Toast.makeText(PersonListActivity.this,msg,Toast.LENGTH_LONG).show();
									// cf.showMessage(context,"Info",msg);									
									refereshList();
								} else {
									warning=getResources().getString(R.string.UnableToSave);
									Toast.makeText(PersonListActivity.this,warning,Toast.LENGTH_LONG).show();
								}
								dialog.dismiss();
							}
				        	else
				        	{
				        		msg=getResources().getString(R.string.nextOfKin);
				        		Toast.makeText(PersonListActivity.this,msg,Toast.LENGTH_LONG).show();
				        	}
						} 
					});  

					dialog.show();
									
				}
				else if(tenureTypeId==100){
					
					msg=getResources().getString(R.string.can_not_add_poi);
					cf.showMessage(context,warningStr,msg);
					
				}
			 
				 }
				 else{
					 msg=getResources().getString(R.string.add_owner_first);
					 cf.showMessage(context,warning,msg);
					 
				 }
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
		final int groupId = attribute.get(position).getGroupId();

		popup.setOnMenuItemClickListener(new OnMenuItemClickListener() 
		{			
			@Override
			public boolean onMenuItemClick(MenuItem item) 
			{
				switch (item.getItemId()) 
				{
				case R.id.edit_attributes:
					
					DBController db = new DBController(context);
					int personCount=db.getPersonList(featureId).size();
					personSubType=db.getPersonSubType(groupId);
					//Open attributes form to edit --------------
					Intent myIntent  =  new Intent(context, AddPersonActivity.class);
					myIntent.putExtra("groupid", groupId);
					myIntent.putExtra("featureid", featureId);
					myIntent.putExtra("PersonCount",personCount);
					myIntent.putExtra("personSubType",personSubType);
					myIntent.putExtra("personSubTypeValue",personSubTypeValue);
					myIntent.putExtra("tenureTypeID",tenureTypeId);
					startActivity(myIntent);
					return true;
				case R.id.add_image:

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
		final int groupId = nextOfKin.get(position).getNextOfKinId();

		popup.setOnMenuItemClickListener(new OnMenuItemClickListener() 
		{			
			@Override
			public boolean onMenuItemClick(MenuItem item) 
			{
				switch (item.getItemId()) 
				{
				case R.id.edit:
					
					edit_POI(groupId);
					return true;
			
				case R.id.delete_entry:
					delete_POI(groupId);
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
		List<Attribute> tenureList = new ArrayList<Attribute>();
		personList=sqllite.getAssociatedPersonWithtenure(groupId);
		tenureList=sqllite.getTenureList(featureId,null);

		if(tenureList.size()!=0)
		{
			if(nextOfKinCount!=0 || (deceasedCount!=0))
			{						
				if(deceasedCount!=0)
				{
					msg=getResources().getString(R.string.delete_deceased_first);
					Toast.makeText(context,msg, Toast.LENGTH_SHORT).show();
				}
				if(nextOfKinCount!=0 )
				{
				msg=getResources().getString(R.string.delete_poi_first);
				cf.showMessage(context,warningStr, msg);
				//Toast.makeText(context,msg, Toast.LENGTH_SHORT).show();
				}
			}
			else{	
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
			alertDialogBuilder.setMessage(R.string.deleteEntryMsg);
			alertDialogBuilder.setPositiveButton(R.string.btn_ok, 
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) 
				{
					DBController sqllite = new DBController(context);					
					List<Attribute> tenureList = new ArrayList<Attribute>();					
					tenureList=sqllite.getTenureList(featureId,null);

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
			sqllite.close();
			
		}
		}

	private void deletePhoto(final int groupId)
	{
		DBController sqllite = new DBController(context);
		int mediaSize = sqllite.getMediaPathByGroupId(groupId).size();
		sqllite.close();
		if(mediaSize>0)
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
		attribute.clear();
		nextOfKin.clear();
		DBController sqllite = new DBController(context);
		attribute.addAll(sqllite.getPersonList(featureId));
		attribute.size();		
		int personCount=sqllite.getPersonList(featureId).size();
		personCountLable.setText(personStr+" ("+personCount+")");
		adapter.notifyDataSetChanged();
		nextOfKin.addAll(sqllite.getNextOfKinList(featureId));
		customPOIArrayAdapter.notifyDataSetChanged();
		nextOfKinCount=nextOfKin.size();
		txtviewNextOfKin.setText(person_of_InterestStr+" (" +nextOfKinCount+ ")");
		sqllite.close();
		//customArrayAdapter.notifyDataSetChanged();
		
		/*if(openAdd && attribute.size()==0)
		{
			openAdd= false;
			addnewPerson.callOnClick();
		}*/
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
						int groupId = attribute.get(position).getGroupId();
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
	
	
	public void edit_POI(final int groupId)
	{
		
		final Dialog dialog = new Dialog(context,R.style.DialogTheme);
		dialog.setContentView(R.layout.dialog_person_of_interest);
		dialog.setTitle(getResources().getString(R.string.nextKin));
		dialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
	
		Button save =(Button)dialog.findViewById(R.id.btn_ok);
		final EditText firstName=(EditText)dialog.findViewById(R.id.editTextFirstName);
		final EditText middleName=(EditText)dialog.findViewById(R.id.editTextMiddleName);
		final EditText lastName=(EditText)dialog.findViewById(R.id.editTextLastName);
		save.setText(saveStr);

		final DBController db = new DBController(context);
		String POI=db.getPOIforEditing(groupId);


       String[] separated = POI.split(" ");
      
      if(separated.length==1)
       {    	   
       String fName =separated[0];
       firstName.setText(fName);
       middleName.setText("");
       lastName.setText("");
       }
      else if(separated.length==2)
      {    	   
      String fName =separated[0];
      String mName =separated[1];
      firstName.setText(fName);
      middleName.setText(mName);
      lastName.setText("");
      }
      else if(separated.length==3)
      {    	   
      String fName =separated[0];
      String mName =separated[1];
      String lName =separated[2];
      firstName.setText(fName);
      middleName.setText(mName);
      lastName.setText(lName);
      }
      

     
		
		save.setOnClickListener(new OnClickListener() 
		{					 
			//Run when button is clicked
			@Override
			public void onClick(View v) 
			{
				
				String personOfInterest = firstName.getText().toString()+" " +middleName.getText().toString()+" "+lastName.getText().toString(); 		        	
	        	if (!TextUtils.isEmpty(personOfInterest)) 
	        	{
	        		
	        		boolean result = db.editPOI(personOfInterest, groupId);
					db.close();
					if (result) {
						customPOIArrayAdapter.notifyDataSetChanged();
						//msg=getResources().getString(R.string.AddedSuccessfully);
						Toast.makeText(PersonListActivity.this,"Edited",Toast.LENGTH_LONG).show();
						//cf.showMessage(context,"Info","updated");							
						refereshList();
					} else {
						warning=getResources().getString(R.string.UnableToSave);
						Toast.makeText(PersonListActivity.this,warning,Toast.LENGTH_LONG).show();
					}
					dialog.dismiss();	
	        	}
	        	else
	        	{
	        		msg=getResources().getString(R.string.nextOfKin);
	        		Toast.makeText(PersonListActivity.this,msg,Toast.LENGTH_LONG).show();
	        	}
			} 
		});  

		dialog.show();
	}
	
	public void edit_DP(final int groupId)    //edit deceased person's details
	{
		
		final Dialog dialog = new Dialog(context,R.style.DialogTheme);
		dialog.setContentView(R.layout.dialog_person_of_interest);
		dialog.setTitle(getResources().getString(R.string.nextKin));
		dialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
	
		Button save =(Button)dialog.findViewById(R.id.btn_ok);
		final EditText firstName=(EditText)dialog.findViewById(R.id.editTextFirstName);
		final EditText middleName=(EditText)dialog.findViewById(R.id.editTextMiddleName);
		final EditText lastName=(EditText)dialog.findViewById(R.id.editTextLastName);
		save.setText(saveStr);

		final DBController db = new DBController(context);
		String POI=db.getDPforEditing(groupId);


       String[] separated = POI.split(" ");
      
      if(separated.length==1)
       {    	   
       String fName =separated[0];
       firstName.setText(fName);
       middleName.setText("");
       lastName.setText("");
       }
      else if(separated.length==2)
      {    	   
      String fName =separated[0];
      String mName =separated[1];
      firstName.setText(fName);
      middleName.setText(mName);
      lastName.setText("");
      }
      else if(separated.length==3)
      {    	   
      String fName =separated[0];
      String mName =separated[1];
      String lName =separated[2];
      firstName.setText(fName);
      middleName.setText(mName);
      lastName.setText(lName);
      }
      

     
		
		save.setOnClickListener(new OnClickListener() 
		{					 
			//Run when button is clicked
			@Override
			public void onClick(View v) 
			{
				
				String personOfInterest = firstName.getText().toString()+" " +middleName.getText().toString()+" "+lastName.getText().toString(); 		        	
	        	if (!TextUtils.isEmpty(personOfInterest)) 
	        	{
	        		
	        		boolean result = db.editDeceasedPerson(firstName.getText().toString(), middleName.getText().toString(), lastName.getText().toString(), groupId);
					db.close();
					if (result) {
						
						//msg=getResources().getString(R.string.AddedSuccessfully);
						Toast.makeText(PersonListActivity.this,"Edited",Toast.LENGTH_LONG).show();
						//cf.showMessage(context,"Info","updated");							
						refereshList();
					} else {
						warning=getResources().getString(R.string.UnableToSave);
						Toast.makeText(PersonListActivity.this,warning,Toast.LENGTH_LONG).show();
					}
					dialog.dismiss();	
	        	}
	        	else
	        	{
	        		msg=getResources().getString(R.string.nextOfKin);
	        		Toast.makeText(PersonListActivity.this,msg,Toast.LENGTH_LONG).show();
	        	}
			} 
		});  

		dialog.show();
	}
	
	public void delete_POI(int groupId)
	{
		warning=getResources().getString(R.string.info);
		info=getResources().getString(R.string.unable_delete);
		String keyword="POI";
		DBController db = new DBController(context);
		boolean result = db.deleteRecord(groupId, keyword);
				if (result) {
					msg=getResources().getString(R.string.deleted);
					Toast.makeText(context,msg, Toast.LENGTH_LONG).show();
					refereshList();
				} else {
					Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
				}
			
		
		
	
	}
	
	public void delete_DP(int groupId)
	{
		msg=getResources().getString(R.string.please_delete_Social_Tenure_first);
		warning=getResources().getString(R.string.warning);
		info=getResources().getString(R.string.unable_delete);
		
			String keyword="DeceasedPerson";
			DBController db = new DBController(context);
			boolean  result = db.deleteRecord(groupId,keyword);
			if(result){
				msg=getResources().getString(R.string.deleted);
				Toast.makeText(context,msg, Toast.LENGTH_LONG).show();
				refereshList();
			}else{
				Toast.makeText(context,info, Toast.LENGTH_SHORT).show();			}
		
		
	
	}
	
	
	public boolean checkSingleOccupancyType(long featureId,String personSubType,int ownerCount)   //Only 1 owner can be added in single occupancy
	{
		DBController db = new DBController(context);
		boolean flag=false;
		if(ownerCount==0)
		{
			//allow	
			if(personSubType.equalsIgnoreCase("Owner"))
			{ 
				return flag=true;
			}
			else if(personSubType.equalsIgnoreCase("Administrator") || personSubType.equalsIgnoreCase("Guardian"))
	      	{
		   msg=getResources().getString(R.string.can_not_add_adminGuardian_in_SingleOccuapncy);
		   cf.showMessage(context,infoStr,msg);

	       return flag=false;
	      	}
		}		
		else if(ownerCount==1)
		{	
			if(personSubType.equalsIgnoreCase("Owner"))
	          	{
				 msg=getResources().getString(R.string.can_not_add_more_than_one_owner_in_SingleOccuapncy);
				cf.showMessage(context,infoStr,msg);
				 return flag=false;
	          	}
			else if(personSubType.equalsIgnoreCase("Administrator") || personSubType.equalsIgnoreCase("Guardian"))
	      	{
				 msg=getResources().getString(R.string.can_not_add_adminGuardian_in_SingleOccuapncy);
				 cf.showMessage(context,infoStr,msg);

	        return flag=false;
	      	}
			
		}
		
		db.close();
		return flag;
		
	}
	
	public boolean checkMultipleOcuupany_Joint(long featureId,String personSubType,int ownerCount)   //Only 2 owner can be added in case of Multiple Occupancy(Joint Tenancy)
	{
		boolean flag=false;
		if(ownerCount<=1)
		{
			//allow	
			if(personSubType.equalsIgnoreCase("Owner"))
			{ 
				return flag=true;
			}
			else if(personSubType.equalsIgnoreCase("Administrator") || personSubType.equalsIgnoreCase("Guardian"))
	      	{
				msg=getResources().getString(R.string.can_not_add_adminGuardian_in_multipleOccuapncy_joint);
				 cf.showMessage(context,infoStr,msg);
			// cf.showMessage(context,infoStr, "You can not add Administrator or Guardian in case of Multiple Occupancy(Joint Tenancy)");

	       return flag=false;
	      	}
		}		
		else if(ownerCount==2)
		{	
			if(personSubType.equalsIgnoreCase("Owner"))
	          	{
				msg=getResources().getString(R.string.can_not_add_more_than_two_owners_in_multipleOccuapncy_joint);
				 cf.showMessage(context,infoStr,msg);
			 //cf.showMessage(context,infoStr,"You can not add more than 2 owner in case of Multiple Occupancy(Joint Tenancy)");
			 return flag=false;
	          	}
			else if(personSubType.equalsIgnoreCase("Administrator") || personSubType.equalsIgnoreCase("Guardian"))
	      	{
			msg=getResources().getString(R.string.can_not_add_adminGuardian_in_multipleOccuapncy_joint);
			cf.showMessage(context,infoStr,msg);	
		   //cf.showMessage(context,infoStr,"You can not add Administrator or Guardian in case of Multiple Occupancy(Joint Tenancy)");
	        return flag=false;
	      	}
			}		
		
		return flag;
	}
	
	public boolean checkMultipleOcuupany_TenancyInCommon(long featureId,String personSubType,int ownerCount)   //more than 2 owner only can be added in case of Multiple Occupancy(Tenancy in Common)
	{
		boolean flag=false;
		
			//allow	
			if(personSubType.equalsIgnoreCase("Owner"))
			{ 
				return flag=true;
			}
			else if(personSubType.equalsIgnoreCase("Administrator") || personSubType.equalsIgnoreCase("Guardian"))
	      	{
				msg=getResources().getString(R.string.can_not_add_adminGuardian_in_multipleOccuapncy_common);
				cf.showMessage(context,infoStr,msg);			
		  	 //cf.showMessage(context,infoStr,"You can not add Administrator or Guardian in case of Multiple Occupancy(Tenancy in common)");

	       return flag=false;
	      	}		
			
		return flag;
	}
	
	public boolean checkOccupancyType_GuardianMinor(long featureId,String personSubType,int ownerCount)   //more than 2 owner only can be added in case of Multiple Occupancy(Tenancy in Common)
	{
		DBController db = new DBController(context);
		int minorCount=db.getMinorCount(featureId);
		int guardianCount=db.getGuardianCount(featureId);
		boolean flag=false;		
		if(personSubType.equalsIgnoreCase("Owner") || personSubType.equalsIgnoreCase("Mmiliki"))
		{ 
			return flag=true;
		}
		/*
		else if(ownerCount>=1)
		{
		  if(personSubType.equalsIgnoreCase("Guardian"))
			{ 
			  if(guardianCount<ownerCount)			
			    {
			   if(guardianCount<2)			
			    {
				return flag=true;
				}
			   else if(guardianCount==2)
				{
				   msg=getResources().getString(R.string.guardian_can_not_more_than_two);
					cf.showMessage(context,infoStr,msg);	   //
				//cf.showMessage(context,infoStr, "Guardian can not be more than two in case of Guardian(Minor)");
				return false;
				}
			}
			   else{
				   msg=getResources().getString(R.string.guardian_can_not_more_than_owners);
					cf.showMessage(context,infoStr,msg);//
				   //cf.showMessage(context,infoStr, "Guardian can not be more than Owner");
			   }
			   
			}
		  else if(personSubType.equalsIgnoreCase("Administrator"))
	      	{
			  msg=getResources().getString(R.string.can_not_add_admin_in_cas_of_guardian_minor);
				cf.showMessage(context,infoStr,msg);
		  //cf.showMessage(context,infoStr,"You can not add Administrator in case of Guardian(Minor)");
	       return flag=false;
	      	}
	    }*/
		else if(personSubType.equalsIgnoreCase("Administrator"))
      	{
			 msg=getResources().getString(R.string.can_not_add_admin_in_cas_of_guardian_minor);
				cf.showMessage(context,infoStr,msg);
		// cf.showMessage(context,infoStr,"You can not add Administrator in case of Guardian(Minor)");

       return flag=false;
      	}
		else if(personSubType.equalsIgnoreCase("Guardian"))
      	{
			
			if(ownerCount>=1)
			{
				   if(guardianCount<ownerCount && guardianCount<2 )			
				    {
					return flag=true;
					}
				   else if(guardianCount==2)
					{
					   msg=getResources().getString(R.string.guardian_can_not_more_than_two);
						cf.showMessage(context,infoStr,msg);	   //
					//cf.showMessage(context,infoStr, "Guardian can not be more than two in case of Guardian(Minor)");
					return false;
					}
				
			}
			else{
				 msg=getResources().getString(R.string.add_owner_first);
					cf.showMessage(context,infoStr,msg);
				 return flag=false;
			}
      
      	}
			
		return flag;
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
