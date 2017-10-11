package com.rmsi.android.mast.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.rmsi.android.mast.util.CommonFunctions;

public class ConfigureMapDisplay extends ActionBarActivity 
{
	ListView listView;
	Context context = this;
	CommonFunctions cf = CommonFunctions.getInstance();
	String selectedColor="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		//Initializing context in common functions in case of a crash
		try{CommonFunctions.getInstance().Initialize(getApplicationContext());}catch(Exception e){}
		cf.loadLocale(getApplicationContext());

		setContentView(R.layout.activity_config_map_display);
		
		String[] mapDisplayitems = getResources().getStringArray(R.array.configureMapToolsitems);
		cf = CommonFunctions.getInstance();
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.title_activity_configure_map_display);
		if(toolbar!=null)
			setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		listView = (ListView) findViewById(R.id.mainListView);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.item_text_view, R.id.text1, mapDisplayitems);

		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() 
		{		
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
			{
				/*if(position==0)	  // for point
				{ 
					selectedColor = cf.getPointColor();
					openColorDialog("point");
				}
				if(position==1)	  // for Line
				{ 
					selectedColor = cf.getLineColor();
					openColorDialog("line");
				}*/
				if(position==0)	  // for Polygon
				{ 
					selectedColor = cf.getPolygonColor();
					openColorDialog("polygon");
				}
			}
		});
	}
	
	private void openColorDialog(final String geomtype)
	{
		try
		{
		String[] mapDisplayColors = getResources().getStringArray(R.array.mapDisplayColors);

		final Dialog dialog = new Dialog(context,R.style.DialogTheme);
		dialog.setContentView(R.layout.dialog_show_list);
		dialog.setTitle(getResources().getString(R.string.colorDialogTitle));
		ListView listViewForColor = (ListView) dialog.findViewById(R.id.commonlistview);
		Button btn_ok =(Button)dialog.findViewById(R.id.btn_ok);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, 
				R.layout.item_list_single_choice,mapDisplayColors);

		listViewForColor.setAdapter(adapter);

		btn_ok.setOnClickListener(new OnClickListener() 
		{					 
			@Override
			public void onClick(View v) 
			{
				dialog.dismiss();
				if(geomtype.equalsIgnoreCase("point"))
					cf.savePointColor(selectedColor);	
				else if(geomtype.equalsIgnoreCase("line"))
					cf.saveLineColor(selectedColor);	
				else if(geomtype.equalsIgnoreCase("polygon"))
					cf.savePolygonColor(selectedColor);	
			} 

		});  
		listViewForColor.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> parent,
					View view, int position, long id) 
			{
				int itemPosition = position;

				//Yellow,Red,Green,White,Cyan
				if(itemPosition==0) 
				{
					selectedColor="Yellow";
				}
				else if(itemPosition==1)
				{
					selectedColor= "Red";
				}
				else if(itemPosition==2)
				{
					selectedColor= "Green";
				}
				else if(itemPosition==3)
				{
					selectedColor= "White";
				}
				else if(itemPosition==4)
				{
					selectedColor= "Cyan";
				}
				else if(itemPosition==5)
				{
					selectedColor= "Blue";
				}
			}});
		
		// Yellow,Red,Green,White,Cyan
		if(selectedColor.equalsIgnoreCase("Yellow"))
		{
			listViewForColor.setItemChecked(0, true);
		}
		else if(selectedColor.equalsIgnoreCase("Red"))
		{
			listViewForColor.setItemChecked(1, true);
		}
		else if(selectedColor.equalsIgnoreCase("Green"))
		{
			listViewForColor.setItemChecked(2, true);
		}
		else if(selectedColor.equalsIgnoreCase("White"))
		{
			listViewForColor.setItemChecked(3, true);
		}
		else if(selectedColor.equalsIgnoreCase("Cyan"))
		{
			listViewForColor.setItemChecked(4, true);
		}
		else if(selectedColor.equalsIgnoreCase("Blue"))
		{
			listViewForColor.setItemChecked(5, true);
		}

		dialog.show();
		}
		catch(Exception e)
		{
			e.printStackTrace();cf.appLog("", e);
		}
		
		
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		onBackPressed();
		return true;
	}
	
	@Override
	public void onBackPressed() {
		Intent i = new Intent(context,LandingPageActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(i);
	}
}
