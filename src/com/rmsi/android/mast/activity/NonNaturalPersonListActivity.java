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
public class NonNaturalPersonListActivity extends ActionBarActivity 
{

	Button addnewPerson,back;
	Context context;
	ListView listView;
	List<Attribute> attribute = new ArrayList<Attribute>();
	MediaListingAdapterTemp adapter ;
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
	String personType;
	String serverFeatureId;
	boolean openAdd = false;
	int pos=0;
	TextView personCountLable;
    View divider1,divider2;
     String tenureTypeStr,personStr,backStr;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		//Initializing context in common functions in case of a crash
		try{CommonFunctions.getInstance().Initialize(getApplicationContext());}catch(Exception e){}
		cf.loadLocale(getApplicationContext());
		
		setContentView(R.layout.activity_list_non_natural_person);

		roleId=CommonFunctions.getRoleID();
		TextView spatialunitValue = (TextView) findViewById(R.id.spatialunit_lbl);
		TextView tenureType_type=(TextView) findViewById(R.id.tenureType_lbl);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.title_person);
		if(toolbar!=null)
			setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);	

		context=this;
				
		tenureTypeStr=getResources().getString(R.string.tenureType);
		personStr=getResources().getString(R.string.person);
		backStr=getResources().getString(R.string.back);
		
		final DBController db = new DBController(context);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) 
		{
			featureId = extras.getLong("featureid");
			personType=extras.getString("persontype");	
			serverFeatureId=extras.getString("serverFeaterID");
		}

		if(!TextUtils.isEmpty(serverFeatureId) && serverFeatureId !=null)
		{	
			spatialunitValue.setText("USIN"+" : "+serverFeatureId.toString());
		}
		else
		{
			spatialunitValue.setText(spatialunitValue.getText()+"   :  "+featureId.toString());
		}
		
		Option tenureType=db.getTenureTypeOptionsValue(featureId);
		if(!TextUtils.isEmpty(tenureType.getOptionName()) && tenureType.getOptionName() !=null)
		{	
			tenureType_type.setText(tenureTypeStr+" : "+tenureType.getOptionName());
		}
		
		addnewPerson=(Button)findViewById(R.id.btn_addNewPerson);
		back=(Button)findViewById(R.id.btn_backPersonList);                  //text changed as Save		
		listView = (ListView)findViewById(android.R.id.list);
		personCountLable=(TextView) findViewById(R.id.Person);
		
		
		adapter = new MediaListingAdapterTemp(context,this,attribute,"NonNaturalPersonlist");
		listView.setAdapter(adapter);
		
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
				
				else{
					Intent myIntent = new Intent(context, AddPersonActivity.class);
					myIntent.putExtra("groupid", 0);
					myIntent.putExtra("featureid", featureId);
					myIntent.putExtra("personSubType","");
					myIntent.putExtra("personSubTypeValue","");
					startActivity(myIntent);
					
				}
				
				
					}
		});

		if(roleId==2)  // Hardcoded Id for Adjudicator
		{
			addnewPerson.setEnabled(false);
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
				if(roleId==2){
					
					finish();
				}
				
			else{
				 DBController db = new DBController(context);
				 List<Attribute> personList = db.getPersonList(featureId);
				 int personMediacount = db.getPersonMediaCount(featureId);
				 int personcount=personList.size();
				if(personList.size()!=0 )
				{
			
				
						 if(personMediacount==personcount)
							{
									 Intent myIntent  =  new Intent(context, DataSummaryActivity.class);
										myIntent.putExtra("featureid", featureId);
										myIntent.putExtra("Server_featureid", serverFeatureId);
										myIntent.putExtra("className", "PersonListActivity");
										startActivity(myIntent);
								 						 	
							 }
							
						 else{
							String personPhotoStr=getResources().getString(R.string.warning_addPersonPhoto);
							String warningStr=getResources().getString(R.string.warning);
								cf.showMessage(context,warningStr,personPhotoStr);					
							} 
				
			}else{
				
				finish();	
			}	
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
					//Open attributes form to edit --------------
					Intent myIntent  =  new Intent(context, AddPersonActivity.class);
					myIntent.putExtra("groupid", groupId);
					myIntent.putExtra("featureid", featureId);
					myIntent.putExtra("PersonCount",personCount);
					
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
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setMessage(R.string.deleteEntryMsg);
		alertDialogBuilder.setPositiveButton(R.string.btn_ok, 
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) 
			{
				DBController sqllite = new DBController(context);
				List<Attribute> personList = new ArrayList<Attribute>();
				List<Attribute> tenureList = new ArrayList<Attribute>();
				personList=sqllite.getAssociatedPersonWithtenure(groupId);
				tenureList=sqllite.getTenureList(featureId,null);

				if(tenureList.size()!=0)
				{
					msg=getResources().getString(R.string.please_delete_Social_Tenure_first);
					warning=getResources().getString(R.string.warning);
					info=getResources().getString(R.string.unable_delete);
					if(personList.size()>0)

						cf.showMessage(context,warning, msg);

					else{

						String keyword="person";
						boolean  result = sqllite.deleteRecord(groupId,keyword);
						if(result){
							refereshList();
						}else{
							Toast.makeText(context,info, Toast.LENGTH_SHORT).show();
						}
					}
				}
				else{
					String keyword="person";
					boolean  result = sqllite.deleteRecord(groupId,keyword);
					if(result){
						refereshList();
					}else{
						Toast.makeText(context,info, Toast.LENGTH_SHORT).show();
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
		DBController sqllite = new DBController(context);
		attribute.addAll(sqllite.getPersonList(featureId));
		attribute.size();		
		int personCount=sqllite.getPersonList(featureId).size();
		personCountLable.setText(personStr+" ("+personCount+")");
		adapter.notifyDataSetChanged();
		sqllite.close();
		
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
			//options.inSampleSize =2;   //calculateInSampleSize(options,768,1024);

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
	
	
	
	
}
