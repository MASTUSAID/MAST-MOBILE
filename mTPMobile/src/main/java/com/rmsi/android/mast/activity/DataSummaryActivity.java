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

//import com.google.android.gms.internal.db;


public class DataSummaryActivity extends ActionBarActivity {

	Long featureId=0L;
	String serverFeatureId=null;
	Context context = this;
	com.rmsi.android.mast.util.CommonFunctions cf = com.rmsi.android.mast.util.CommonFunctions.getInstance();
	TextView personCount,mediaCount,tenureRaltion,customStatus,propertyStatus,countPOITxt,txtPOIlbl;
	 com.rmsi.android.mast.domain.Option tenureType;
	 long tenureTypeId=0l;
	 String classname="summaryForAll";
	 Button save;
	 List<com.rmsi.android.mast.domain.Attribute> tmpList3 ;
	 boolean isCustomValue=false;
	boolean isCustomAttrib=false;
	boolean isNonNatural=false;
	//TableRow OwnerTblRow,adminTblRow,guardianTblRow,poiTblRow,nonNaturalTblRow;
	TableRow poiTblRow;
	String do_u_want_to_add_media,no_media_exist,infoMsg,msgStr,yesStr,noStr,no_custom_attribute_exist,do_u_want_to_add_custom_attribute,not_selected;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		//Initializing context in common functions in case of a crash
		try{
			com.rmsi.android.mast.util.CommonFunctions.getInstance().Initialize(getApplicationContext());}catch(Exception e){}
		cf.loadLocale(getApplicationContext());

		do_u_want_to_add_media=getResources().getString(R.string.do_u_want_to_add_media);
		no_media_exist=getResources().getString(R.string.no_media_exist);
		yesStr=getResources().getString(R.string.yes);
		noStr=getResources().getString(R.string.no);
		no_custom_attribute_exist=getResources().getString(R.string.you_did_not_enter_custum_attributes);
		do_u_want_to_add_custom_attribute=getResources().getString(R.string.want_to_add_custum_attributes);
		not_selected=getResources().getString(R.string.not_selected);
		
       final com.rmsi.android.mast.db.DBController db = new com.rmsi.android.mast.db.DBController(context);
       isCustomValue=db.IsCustomAttributeValue(featureId);
       tenureType=db.getTenureTypeOptionsValue(featureId); 
		Bundle extras = getIntent().getExtras();
		if (extras != null) 
		{
		  featureId = extras.getLong("featureid");
	      serverFeatureId=extras.getString("Server_featureid");
		  classname=extras.getString("className");
		  tenureType=db.getTenureTypeOptionsValue(featureId);  //check tenure type
			
		}
		
		setContentView(R.layout.activity_data_summary2);
		countPOITxt=(TextView) findViewById(R.id.TextView_countPOI);			
		poiTblRow=(TableRow) findViewById(R.id.tableRow7);
		txtPOIlbl=(TextView) findViewById(R.id.TextView14);	
		save=(Button) findViewById(R.id.btn_save);
		
		
		/*tenure types id for Individual=129
	    tenure types id for Collective=130
		tenure types id for Existing=131*/
		
		
	    if (classname != null) {
			if (classname.equalsIgnoreCase("draftSurveyFragment")) {

				save.setVisibility(View.GONE);

				if (tenureType.getOptionId() != null) {
					{

						if ((tenureType.getOptionId() == 129l || tenureType
								.getOptionId() == 131l)) {
							countPOITxt.setVisibility(View.GONE);
							poiTblRow.setVisibility(View.GONE);
							txtPOIlbl.setVisibility(View.GONE);
						}

						else if (tenureType.getOptionId() == 130l) {
							countPOITxt.setVisibility(View.VISIBLE);
							poiTblRow.setVisibility(View.VISIBLE);
							txtPOIlbl.setVisibility(View.VISIBLE);
						}

					}
				}
			}

			else if (classname.equalsIgnoreCase("Individual_ExistingListActivity")) {

				countPOITxt.setVisibility(View.GONE);
				poiTblRow.setVisibility(View.GONE);
				txtPOIlbl.setVisibility(View.GONE);
				save.setVisibility(View.VISIBLE);
			}

			else if (classname.equalsIgnoreCase("PersonListActivity")) {
				countPOITxt.setVisibility(View.VISIBLE);
				poiTblRow.setVisibility(View.VISIBLE);
				txtPOIlbl.setVisibility(View.VISIBLE);
				save.setVisibility(View.VISIBLE);
			}
		}
	    else if (classname == null) {
			if (tenureType.getOptionId() != null) {
				{

					if ((tenureType.getOptionId() == 129l || tenureType
							.getOptionId() == 131l)) {
						countPOITxt.setVisibility(View.GONE);
						poiTblRow.setVisibility(View.GONE);
						txtPOIlbl.setVisibility(View.GONE);
						save.setVisibility(View.VISIBLE);
					}

					else if (tenureType.getOptionId() == 130l) {
						countPOITxt.setVisibility(View.VISIBLE);
						poiTblRow.setVisibility(View.VISIBLE);
						txtPOIlbl.setVisibility(View.VISIBLE);
						save.setVisibility(View.VISIBLE);
					}

				}
			}
		}
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.title_activity_data_summary);
		personCount=(TextView) findViewById(R.id.TextView_countNaturalPerson);
		mediaCount=(TextView) findViewById(R.id.TextView_countMultimedia);
		tenureRaltion=(TextView) findViewById(R.id.TextView_tenureRelation);
		
		propertyStatus=(TextView) findViewById(R.id.TextView_propertyStatus);
	
		
	
		
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
						Intent myIntent = new Intent(context, com.rmsi.android.mast.activity.MediaListActivity.class);
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
							Intent myIntent = new Intent(context, LandingPageActivity.class);
							startActivity(myIntent);
							dialog.dismiss();
} 
					});  

					dialog.show();
					
					}	
					
					else{
						
						Intent myIntent = new Intent(context, LandingPageActivity.class);
						startActivity(myIntent);
						
					}
					
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
	private void updateCount()
	{
		try 
		{
			com.rmsi.android.mast.db.DBController sqllite = new com.rmsi.android.mast.db.DBController(context);
			List<com.rmsi.android.mast.domain.Attribute> tmpList2 = sqllite.getPersonList(featureId);
		    tmpList3 = sqllite.getMediaList(featureId);
			isCustomValue=sqllite.IsCustomAttributeValue(featureId);
			isCustomAttrib=sqllite.IsCustomAttribute();
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
				
		
				if(tenureType.getOptionId()!=null)
				{
				tenureRaltion.setText(tenureType.getOptionName());
				}else{
					tenureRaltion.setText(not_selected);
				}
				personCount.setText("0");
				personCount.setTextColor(getResources().getColor(R.color.red));
			


			if(tmpList2.size()==0)
			{
				personCount.setTextColor(getResources().getColor(R.color.red));
				personCount.setText(R.string.incomplete);
			}
			else{

				personCount.setTextColor(getResources().getColor(R.color.green));
				personCount.setText(R.string.completed);
			}
			if(tmpList3.size()==0)
			{
				mediaCount.setTextColor(getResources().getColor(R.color.red));
			}
			else{
				mediaCount.setTextColor(getResources().getColor(R.color.green));
			}
			
			
			
			// Update count
			com.rmsi.android.mast.db.DBController db = new com.rmsi.android.mast.db.DBController(context);
			
			int poiCount=db.getNextOfKinList(featureId).size();
			
			mediaCount.setText(""+tmpList3.size());
			
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
