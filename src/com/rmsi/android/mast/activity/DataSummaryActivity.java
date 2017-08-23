package com.rmsi.android.mast.activity;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.internal.db;
import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.db.DBController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.Option;
import com.rmsi.android.mast.util.CommonFunctions;

public class DataSummaryActivity extends ActionBarActivity {

	Long featureId=0L;
	String serverFeatureId=null;
	Context context = this;
	CommonFunctions cf = CommonFunctions.getInstance();
	TextView personCount,mediaCount,tenureRaltion,customStatus,adminCountTxt,propertyStatus,guardianCountTxt,countPOITxt,ownerCountTxt,nonNaturalCount;
	 Option tenureType;
	 long tenureTypeId=0l;
	 String classname="summaryForAll";
	 Button save;
	 List<Attribute> tmpList3 ;
	 boolean isCustomValue=false;
	boolean isCustomAttrib=false;
	boolean isNonNatural=false;
	TableRow OwnerTblRow,adminTblRow,guardianTblRow,poiTblRow,nonNaturalTblRow;
	String do_u_want_to_add_media,no_media_exist,infoMsg,msgStr,yesStr,noStr,no_custom_attribute_exist,do_u_want_to_add_custom_attribute;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		//Initializing context in common functions in case of a crash
		try{CommonFunctions.getInstance().Initialize(getApplicationContext());}catch(Exception e){}
		cf.loadLocale(getApplicationContext());

		do_u_want_to_add_media=getResources().getString(R.string.do_u_want_to_add_media);
		no_media_exist=getResources().getString(R.string.no_media_exist);
		yesStr=getResources().getString(R.string.yes);
		noStr=getResources().getString(R.string.no);
		no_custom_attribute_exist=getResources().getString(R.string.you_did_not_enter_custum_attributes);
		do_u_want_to_add_custom_attribute=getResources().getString(R.string.want_to_add_custum_attributes);
		
       final DBController db = new DBController(context);
       isCustomValue=db.IsCustomAttributeValue(featureId);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) 
		{
			featureId = extras.getLong("featureid");
			serverFeatureId=extras.getString("Server_featureid");
			classname=extras.getString("className");
			tenureType=db.getTenureTypeOptionsValue(featureId);    //check tenure type
			
		}
		
		
		
		if(classname.equalsIgnoreCase("draftSurveyFragment") && classname!=null)
		{
		setContentView(R.layout.activity_data_summary);
		}
		else if(classname.equalsIgnoreCase("PersonListActivity") && classname!=null)
		{
			setContentView(R.layout.activity_data_summary2);
			
		}

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.title_activity_data_summary);
		personCount=(TextView) findViewById(R.id.TextView_countNaturalPerson);
		mediaCount=(TextView) findViewById(R.id.TextView_countMultimedia);
		tenureRaltion=(TextView) findViewById(R.id.TextView_tenureRelation);
		customStatus=(TextView) findViewById(R.id.TextView_custom);
		adminCountTxt=(TextView) findViewById(R.id.TextView_admin_count);
		propertyStatus=(TextView) findViewById(R.id.TextView_propertyStatus);
		guardianCountTxt=(TextView) findViewById(R.id.TextView_countGuardian);
		countPOITxt=(TextView) findViewById(R.id.TextView_countPOI);
		ownerCountTxt=(TextView) findViewById(R.id.TextView_owner_count);
		nonNaturalTblRow=(TableRow) findViewById(R.id.tableRow8);
		OwnerTblRow=(TableRow) findViewById(R.id.tableRow4);
		adminTblRow=(TableRow) findViewById(R.id.tableRow5);
		guardianTblRow=(TableRow) findViewById(R.id.tableRow6);
		poiTblRow=(TableRow) findViewById(R.id.tableRow7);
		nonNaturalCount=(TextView) findViewById(R.id.TextView_countNonNatural);
		//OwnerTblRow,adminTblRow,guardianTblRow,poiTblRow,nonNaturalTblRow;
		
		isNonNatural=db.IsNonNaturalPerson(featureId);
		if(isNonNatural)
		{
			nonNaturalTblRow.setVisibility(View.VISIBLE);
			OwnerTblRow.setVisibility(View.GONE);
			adminTblRow.setVisibility(View.GONE);
			guardianTblRow.setVisibility(View.GONE);
			poiTblRow.setVisibility(View.GONE);
		}
		else{
			nonNaturalTblRow.setVisibility(View.GONE);
			OwnerTblRow.setVisibility(View.VISIBLE);
			adminTblRow.setVisibility(View.VISIBLE);
			guardianTblRow.setVisibility(View.VISIBLE);
			poiTblRow.setVisibility(View.VISIBLE);
		}
		
		if(toolbar!=null)
			setSupportActionBar(toolbar);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);	
		
		
		
		
		Button btn_edit=(Button) findViewById(R.id.edit_attributes);
		btn_edit.setOnClickListener(new OnClickListener() 
		{			
			@Override
			public void onClick(View arg0) 
			{
				Intent myIntent  =  new Intent(context, CaptureAttributesActivity.class);
				myIntent.putExtra("featureid", featureId);
				myIntent.putExtra("Server_featureid", serverFeatureId);
				startActivity(myIntent);
			}
		});
		
		
		
		if(classname.equalsIgnoreCase("PersonListActivity") && classname!=null)
		{
			save=(Button) findViewById(R.id.btn_save);
			save.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					int media_count=tmpList3.size();
					if(media_count==0)
					{	
					infoMsg=do_u_want_to_add_media;		
					final Dialog dialog = new Dialog(context,R.style.DialogTheme);
					dialog.setContentView(R.layout.dialog_for_info);
					dialog.setTitle(getResources().getString(R.string.info));
					dialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
					Button yesBtn =(Button)dialog.findViewById(R.id.btn_proceed);
					Button noBtn =(Button)dialog.findViewById(R.id.btn_cancel);
					final TextView msg=(TextView)dialog.findViewById(R.id.textView_tenure_type);	
					final TextView txtInfoMsg=(TextView)dialog.findViewById(R.id.textView_infoMsg);	
					msg.setText(no_media_exist);
					txtInfoMsg.setText(infoMsg);
					yesBtn.setText(yesStr);
					noBtn.setText(noStr);
					yesBtn.setOnClickListener(new OnClickListener() 
					{					 
						//Run when button is clicked
						@Override
						public void onClick(View v) 
						{
						Intent myIntent = new Intent(context, MediaListActivity.class);
						myIntent.putExtra("featureid", featureId);
						myIntent.putExtra("serverFeaterID",serverFeatureId);
						startActivity(myIntent);
						dialog.dismiss();							
						} 
					});
					
					noBtn.setOnClickListener(new OnClickListener() 
					{					      
						//Run when button is clicked
						@Override
						public void onClick(View v) 
						{
							if(isCustomAttrib)
							{

								if(!isCustomValue)
								{			
									final Dialog subdialog = new Dialog(context,R.style.DialogTheme);
									subdialog.setContentView(R.layout.dialog_for_info);
									subdialog.setTitle(getResources().getString(R.string.info));
									subdialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
									Button yesBtn =(Button)subdialog.findViewById(R.id.btn_proceed);
									Button noBtn =(Button)subdialog.findViewById(R.id.btn_cancel);
									final TextView txtTenureType=(TextView)subdialog.findViewById(R.id.textView_tenure_type);	
									final TextView txtInfoMsg=(TextView)subdialog.findViewById(R.id.textView_infoMsg);	
									txtTenureType.setText(no_custom_attribute_exist);
									txtInfoMsg.setText(do_u_want_to_add_custom_attribute);
									yesBtn.setText(yesStr);
									noBtn.setText(noStr);
									yesBtn.setOnClickListener(new OnClickListener() 
									{					 
										//Run when button is clicked
										@Override
										public void onClick(View v) 
										{
										Intent myIntent = new Intent(context, AddCustomAttribActivity.class);
										myIntent.putExtra("featureid", featureId);
										myIntent.putExtra("serverFeaterID",serverFeatureId);
										startActivity(myIntent);
										subdialog.dismiss();							
										} 
									});
									
									noBtn.setOnClickListener(new OnClickListener() 
									{					 
										//Run when button is clicked
										@Override
										public void onClick(View v) 
										{
											Intent myIntent = new Intent(context, LandingPageActivity.class);
											startActivity(myIntent);
											subdialog.dismiss();
											
										} 
									});  

									subdialog.show();
									
									}
								
								else{
									
									Intent myIntent = new Intent(context, LandingPageActivity.class);
									startActivity(myIntent);
									
								}
							
                            }
					else{
						
						Intent myIntent = new Intent(context, LandingPageActivity.class);
						startActivity(myIntent);
						dialog.dismiss();
					}
						} 
					});  

					dialog.show();
					
					}				
					
					else if(isCustomAttrib)
					{
						if(!isCustomValue)
						{			
							final Dialog subdialog = new Dialog(context,R.style.DialogTheme);
							subdialog.setContentView(R.layout.dialog_for_info);
							subdialog.setTitle(getResources().getString(R.string.info));
							subdialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
							Button yesBtn =(Button)subdialog.findViewById(R.id.btn_proceed);
							Button noBtn =(Button)subdialog.findViewById(R.id.btn_cancel);
							final TextView txtTenureType=(TextView)subdialog.findViewById(R.id.textView_tenure_type);	
							final TextView txtInfoMsg=(TextView)subdialog.findViewById(R.id.textView_infoMsg);	
							txtTenureType.setText(no_custom_attribute_exist);
							txtInfoMsg.setText(do_u_want_to_add_custom_attribute);
							yesBtn.setText(yesStr);
							noBtn.setText(noStr);
							yesBtn.setOnClickListener(new OnClickListener() 
							{					 
								//Run when button is clicked
								@Override
								public void onClick(View v) 
								{
								Intent myIntent = new Intent(context, AddCustomAttribActivity.class);
								myIntent.putExtra("featureid", featureId);
								myIntent.putExtra("serverFeaterID",serverFeatureId);
								startActivity(myIntent);
								subdialog.dismiss();							
								} 
							});
							
							noBtn.setOnClickListener(new OnClickListener() 
							{					 
								//Run when button is clicked
								@Override
								public void onClick(View v) 
								{
									Intent myIntent = new Intent(context, LandingPageActivity.class);
									startActivity(myIntent);
									subdialog.dismiss();
									
								} 
							});  

							subdialog.show();
							
							}
						
						else{
							
							Intent myIntent = new Intent(context, LandingPageActivity.class);
							startActivity(myIntent);
							
						}
					}
					
					else{
						
						Intent myIntent = new Intent(context, LandingPageActivity.class);
						startActivity(myIntent);
						
					}
					
				}
			});

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
	private void updateCount()
	{
		try 
		{
			DBController sqllite = new DBController(context);
			//List<Attribute> tmpList = sqllite.getTenureList(featureId,null);
			//List<Attribute> tmpList1 = sqllite.getPropertyList(featureId);
			List<Attribute> tmpList2 = sqllite.getPersonList(featureId);
		     tmpList3 = sqllite.getMediaList(featureId);
			boolean isNonNatural=sqllite.IsNonNaturalPerson(featureId);
			isCustomValue=sqllite.IsCustomAttributeValue(featureId);
			isCustomAttrib=sqllite.IsCustomAttribute();
			//boolean ispropertyAtttrib=sqllite.IsPropertyAttribute();
			boolean isPropertyValue=sqllite.IsPropertyAttribValue(featureId);
			sqllite.close();
			
			if(isPropertyValue)
			{
				propertyStatus.setText(R.string.completed);
				propertyStatus.setTextColor(getResources().getColor(R.color.green));  

				
			}else{
				propertyStatus.setText(R.string.incomplete);
				propertyStatus.setTextColor(getResources().getColor(R.color.red));  
			}	
				
			
			
			
			if(isNonNatural)
			{
				tenureRaltion.setText(R.string.nonnatural);
				nonNaturalCount.setText("1");
				nonNaturalCount.setTextColor(getResources().getColor(R.color.green));
				
			}
			else{
				if(tenureType.getOptionId()!=null)
				{
				tenureRaltion.setText(tenureType.getOptionName());
				}else{
					tenureRaltion.setText("");
				}
				personCount.setText("0");
				personCount.setTextColor(getResources().getColor(R.color.red));
			}
			
			
			if(isCustomAttrib)
			{
			if(isCustomValue)
			{
				
				customStatus.setText(R.string.completed);
				customStatus.setTextColor(getResources().getColor(R.color.green));   //for green color

				
			}else{
				customStatus.setText(R.string.incomplete);
				customStatus.setTextColor(getResources().getColor(R.color.red));  //red color
			}
			
			}else
			{
				customStatus.setText(R.string.not_defined);
				customStatus.setTextColor(getResources().getColor(R.color.red));
			}
			
			// Update color of the count
			
			

			if(tmpList2.size()==0)
			{
				personCount.setTextColor(getResources().getColor(R.color.red));
			}
			else{

				personCount.setTextColor(getResources().getColor(R.color.green));
			}
			if(tmpList3.size()==0)
			{
				mediaCount.setTextColor(getResources().getColor(R.color.red));
			}
			else{
				mediaCount.setTextColor(getResources().getColor(R.color.green));
			}
			
			
			
			// Update count
			DBController db = new DBController(context);
			int admin_count=db.getAdminCount(featureId);
			int guardian_count=db.getGuardianCount(featureId);
			int poiCount=db.getNextOfKinList(featureId).size();
			int ownerCount=db.getOwnerCount(featureId);
			personCount.setText(""+tmpList2.size());
			
			
			//((TextView) findViewById(R.id.propertyCount)).setText(""+tmpList1.size());
		
			mediaCount.setText(""+tmpList3.size());
			if(ownerCount==0)
			{
				ownerCountTxt.setTextColor(getResources().getColor(R.color.red));
				ownerCountTxt.setText("0");
			}
			else{
				ownerCountTxt.setTextColor(getResources().getColor(R.color.green));
				ownerCountTxt.setText(""+ownerCount);
			}
			
			if(admin_count==0)
			{
				adminCountTxt.setTextColor(getResources().getColor(R.color.red));
				adminCountTxt.setText("0");
			}
			else{
				adminCountTxt.setTextColor(getResources().getColor(R.color.green));
				adminCountTxt.setText(""+admin_count);
			}
			
			if(guardian_count==0)
			{
				guardianCountTxt.setTextColor(getResources().getColor(R.color.red));
				guardianCountTxt.setText("0");
			}
			else{
				guardianCountTxt.setTextColor(getResources().getColor(R.color.green));
				guardianCountTxt.setText(""+guardian_count);
			}
			
			if(poiCount==0)
			{
				countPOITxt.setTextColor(getResources().getColor(R.color.red));
				countPOITxt.setText("0");
			}
			else{
				countPOITxt.setTextColor(getResources().getColor(R.color.green));
				countPOITxt.setText(""+poiCount);
			}
			
			
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
		super.onResume();
	}
}
