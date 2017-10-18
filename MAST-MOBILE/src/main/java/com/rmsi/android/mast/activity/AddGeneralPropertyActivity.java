package com.rmsi.android.mast.activity;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rmsi.android.mast.adapter.AttributeAdapter;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.User;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.GuiUtility;

public class AddGeneralPropertyActivity extends ActionBarActivity {

	private Long featureId = 0L;
	private boolean isDispute = false;
	private List<Attribute> attributes;
	private ListView listView;
	private final Context context = this;
	private AttributeAdapter adapterList;
	private Button btnSave,btnBack;
	private CommonFunctions cf = CommonFunctions.getInstance();
	private boolean readOnly = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		//Initializing context in common functions in case of a crash
		try{CommonFunctions.getInstance().Initialize(getApplicationContext());}catch(Exception e){}
		cf.loadLocale(getApplicationContext());

		DbController db = DbController.getInstance(context);
		Bundle extras = getIntent().getExtras();

		if (extras != null) 
		{
			featureId = extras.getLong("featureid");
			isDispute = extras.getBoolean("isDispute");

			attributes = db.getPropAttributesByType(featureId, Attribute.TYPE_GENERAL_PROPERTY);
			if(attributes.size()<1) {
				// Try to get list of attributes of general type
				attributes = db.getAttributesByType(Attribute.TYPE_GENERAL_PROPERTY);
			}
		}

		readOnly = CommonFunctions.isFeatureReadOnly(featureId);

		setContentView(R.layout.activity_add_property_info);

		btnSave=(Button) findViewById(R.id.btn_save);
		btnBack=(Button) findViewById(R.id.btn_cancel);

		listView = (ListView)findViewById(android.R.id.list);
		TextView emptyText = (TextView)findViewById(android.R.id.empty);
		listView.setEmptyView(emptyText);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.AddNewProperty);
		if(toolbar!=null)
			setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		try {
			adapterList = new AttributeAdapter(context, attributes, readOnly);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}

		listView.setAdapter(adapterList);

		if(readOnly) {
			btnSave.setVisibility(View.GONE);
		}

		btnSave.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{             
				saveData();
			}			
		});
		btnBack.setOnClickListener(new OnClickListener()
		{			
			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}
	public void saveData() 
	{
		if(validate())
		{
			try {
				boolean saveResult = DbController.getInstance(context).savePropAttributes(attributes, featureId);

				if(saveResult){
					cf.showToast(context, R.string.data_saved, Toast.LENGTH_SHORT);

					if(isDispute){
						Intent myIntent = new Intent(context, AddDisputeActivity.class);
						myIntent.putExtra("featureid", featureId);
						startActivity(myIntent);
					} else {
						Intent myIntent = new Intent(context, AddSocialTenureActivity.class);
						myIntent.putExtra("featureid", featureId);
						startActivity(myIntent);
					}
				}else{
					cf.showToast(context, R.string.unable_to_save_data, Toast.LENGTH_SHORT);
				}
			} catch (Exception e) {
				cf.appLog("", e);e.printStackTrace();
				cf.showToast(context, R.string.unable_to_save_data, Toast.LENGTH_SHORT);
			}
		}
		else{
			cf.showToast(context, R.string.fill_mandatory, Toast.LENGTH_SHORT);
		}
	}

	public boolean validate()
	{
		return GuiUtility.validateAttributes(attributes, true);
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
}
