package com.rmsi.android.mast.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.rmsi.android.mast.activity.R;
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
		toolbar.setTitle("Configure Map Settings");
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
                if(position==0)	  // for Snapping
                {
                    openSnappingDialog();
                }
				if(position==1)	  // for labeling
				{
					openLabelingDialog();
				}
			}
		});
	}

	private void openLabelingDialog()
	{
		try
		{
			final Dialog dialog = new Dialog(context, R.style.DialogTheme);
			dialog.setContentView(R.layout.dialog_labeling_settings);
			dialog.setTitle(getResources().getString(R.string.labelingSettingsTitle));

			final CheckBox chbxEnableLabeling = (CheckBox) dialog.findViewById(R.id.chbxEnableLabeling);
			final CheckBox chbxEnableVertextDrawing = (CheckBox) dialog.findViewById(R.id.chbxEnableVertexDrawing);
			Button btn_ok =(Button)dialog.findViewById(R.id.btn_ok);

			chbxEnableLabeling.setChecked(cf.getEnableLabeling());
			chbxEnableVertextDrawing.setChecked(cf.getEnableVertexDrawing());

			btn_ok.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					cf.saveEnableLabeling(chbxEnableLabeling.isChecked());
					cf.saveEnableVertexDrawing(chbxEnableVertextDrawing.isChecked());
					dialog.dismiss();
				}
			});

			dialog.show();
		}
		catch(Exception e)
		{
			e.printStackTrace();cf.appLog("", e);
		}
	}

    private void openSnappingDialog()
    {
        try
        {
            final Dialog dialog = new Dialog(context, R.style.DialogTheme);
            dialog.setContentView(R.layout.dialog_snapping_settings);
            dialog.setTitle(getResources().getString(R.string.snappingSettingsTitle));

            final CheckBox chbxSnapToVertex = (CheckBox) dialog.findViewById(R.id.chbxSnapToVertex);
            final CheckBox chbxSnapToSegment = (CheckBox) dialog.findViewById(R.id.chbxSnapToSegment);
            final EditText txtSnappingTolerance = (EditText) dialog.findViewById(R.id.txtSnapTolerance);
            Button btn_ok =(Button)dialog.findViewById(R.id.btn_ok);

            chbxSnapToVertex.setChecked(cf.getSnapToVertex());
            chbxSnapToSegment.setChecked(cf.getSnapToSegment());
            txtSnappingTolerance.setText(Integer.toString(cf.getSnapTolerance()));

            txtSnappingTolerance.setEnabled(chbxSnapToVertex.isChecked() || chbxSnapToSegment.isChecked());

            chbxSnapToVertex.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    txtSnappingTolerance.setEnabled(isChecked || chbxSnapToSegment.isChecked());
                }
            });

            chbxSnapToSegment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    txtSnappingTolerance.setEnabled(isChecked || chbxSnapToVertex.isChecked());
                }
            });

            btn_ok.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(txtSnappingTolerance.getText() == null || txtSnappingTolerance.getText().toString().equals("")){
                        cf.showToast(ConfigureMapDisplay.this, "Enter tolerance", 3, 0);
                        return;
                    }

                    cf.saveSnapToVertex(chbxSnapToVertex.isChecked());
                    cf.saveSnapToSegment(chbxSnapToSegment.isChecked());
                    cf.saveSnapTolerance(Integer.parseInt(txtSnappingTolerance.getText().toString()));

                    dialog.dismiss();
                }
            });

            dialog.show();
        }
        catch(Exception e)
        {
            e.printStackTrace();cf.appLog("", e);
        }
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
}
