package com.rmsi.android.mast.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.BufferType;

//import com.google.android.gms.internal.db;
import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.activity.R.color;
import com.rmsi.android.mast.db.DBController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.Option;
import com.rmsi.android.mast.util.CommonFunctions;

/**
 * @author Amreen.S
 *
 */
public class AttributeAdapter extends BaseAdapter 
{

	List<Attribute> attribList;
	Context context;
	LayoutInflater lInflator;
	long featureId;
	int roleId=CommonFunctions.getRoleID();
	DBController db;
	private List<Integer> errorList = new ArrayList<Integer>();
	CommonFunctions cf = CommonFunctions.getInstance();
	
	
	public AttributeAdapter(Context contextAct, List<Attribute> attrib,long featureID) 
	{
		this.context = contextAct;
		this.lInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.attribList = attrib;
		this.featureId = featureID;
		this.db = new DBController(context);
		
	}
	
	@Override
	public int getCount() 
	{
		return attribList.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return attribList.get(position);
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{

		View container = null;
		Attribute attribItem = (Attribute) getItem(position);
		String attribLable=attribItem.getAttributeName();
		boolean error = false;
	

		View valueView = null;

		if (attribItem.getControlType()==1 ) //Edittext(string)
		{
			if(errorList.contains(attribItem.getAttributeid()))
			{
				error=true; 

			}	

			String FieldValue=attribItem.getFieldValue();
			container = lInflator.inflate(R.layout.item_edit_text, parent, false);
			valueView = createInputRow(container, attribLable,FieldValue,position,error);

			attribItem.setView(valueView);
		}
		else if (attribItem.getControlType()==2 )  //Date
		{
			if(errorList.contains(attribItem.getAttributeid()))
			{
				error=true; 
			}	

			String selectedDate = attribItem.getFieldValue();
			container = lInflator.inflate(R.layout.item_date, parent, false);
			valueView = createTimePickerRow(container, attribLable,selectedDate,position,error);
			attribItem.setView(valueView);
		} 
		else if (attribItem.getControlType()==3 )  //Boolean
		{

			if(errorList.contains(attribItem.getAttributeid()))
			{
				error=true; 
			}	

			String selectedAns = attribItem.getFieldValue();    // Spinner
			container = lInflator.inflate(R.layout.item_spinner, parent, false);
			Spinner spinner = createSpinnerViewForBoolean(container,attribLable,selectedAns,position);
			attribItem.setView(spinner);
		}
		else if (attribItem.getControlType()==4 )  //Edittext(Numeric)
		{
			if(errorList.contains(attribItem.getAttributeid()))
			{
				error=true; 
			}	

			String FieldValue=attribItem.getFieldValue();	 
			container = lInflator.inflate(R.layout.item_edittext_numeric, parent, false);
			valueView = createInputRow(container, attribLable,FieldValue,position,error);

			attribItem.setView(valueView);
		}
		else if (attribItem.getControlType()==5 )  //spinner
		{
			if(errorList.contains(attribItem.getAttributeid()))
			{
				error=true; 
			}	
			String selectedAns = attribItem.getFieldValue();

			container = lInflator.inflate(R.layout.item_spinner, parent, false);
			Spinner spinner = createSpinnerViewFromArray(container,attribItem.getOptionsList(),attribLable,selectedAns,position,error);
			attribItem.setView(spinner);
		}
		
		else if (attribItem.getControlType() == 6) // checkbox 
		{
			

			if (errorList.contains(attribItem.getAttributeid())) 
			{
				error = true;
			}
			String selectedAns = attribItem.getFieldValue();
			if(attribItem.getAttributeid()==16)
			{
			}
			container = lInflator.inflate(R.layout.item_button, parent, false);
			valueView = createCheckBoxButton(container, attribLable,selectedAns, attribItem.getOptionsList(),error, position);
			attribItem.setView(valueView);
		}
		
		return container ;
	}

	TextView createCheckBoxButton(View container, String attribLable,String selectedAns, List<Option> optionsList, boolean error,int position) {
		

		TextView attributeLable = (TextView) container.findViewById(R.id.field);
		TextView selectedOptionsText=(TextView)container.findViewById(R.id.selectoptionsText);
		TextView selectedOptionIds=(TextView)container.findViewById(R.id.selectoptionIds);
		EditText otherExistingUSe=(EditText)container.findViewById(R.id.editTextOtherExistingUse);
		attributeLable.setText(attribLable);
		List<String> optionTextList=new ArrayList<String>();
		
		Attribute attribItem = (Attribute) getItem(position);
		selectedOptionIds.setTag(R.id.multiselect_position, position);
		selectedOptionIds.setTag(R.id.chedkedId, selectedAns);
		String checkedOPtionsIdStr="";
		if(attribItem.getFieldValue()!=null)
		{
			checkedOPtionsIdStr=attribItem.getFieldValue().toString();			
			String optionIds[]=checkedOPtionsIdStr.split(",");
			for(int i=0; i<optionIds.length;i++)
			{
				String optionId=optionIds[i];
				optionTextList.add(db.getOptionText(optionId));
			}
						
			selectedOptionsText.setText("");
			selectedOptionsText.setText(optionTextList.toString());
			selectedOptionIds.setText(attribItem.getFieldValue().toString());
			
			if((attribItem.getFieldValue().toString()).contains("58"))
				{otherExistingUSe.setVisibility(View.VISIBLE);
				otherExistingUSe.setText(db.getExistingOtherUse(featureId));
			
				}else
				otherExistingUSe.setVisibility(View.GONE);
				
		}
		selectedOptionIds.setTag(R.id.multiselect_value, otherExistingUSe);
		
		addListenersToCheckBox(selectedOptionsText,selectedOptionIds,position,attribLable,otherExistingUSe,checkedOPtionsIdStr);

		if (error) 
		{
			selectedOptionIds.setBackgroundColor(context.getResources().getColor(color.lightred));
			String mandatory_fields=context.getResources().getString(R.string.fill_mandatory);
			selectedOptionsText.setError(mandatory_fields);
		}

		return selectedOptionIds;

	}
	void addListenersToCheckBox(final TextView selectedOption,final TextView selectedOptionIds,int position,final String  attribLable,final EditText otherExistingUSe,final String checkedOPtionsIdStr)
	{
		final int currposition = position;
		selectedOption.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				Attribute attribItem = (Attribute) getItem(currposition);
				ArrayList<String> list = new ArrayList<String>();
				ArrayList<String> selectedIdCheckboxtemp = new ArrayList<String>();
				ArrayList<String> selectedIdEdittxttemp=new ArrayList<String>();
				final List<Option> optionsList = attribItem.getOptionsList();

				for (int i = 0; i < optionsList.size(); i++)
				{
					list.add(optionsList.get(i).getOptionName().toString());
				}

				final Dialog dialog = new Dialog(context);
				dialog.setContentView(R.layout.item_multiselect);
				dialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
				dialog.setCanceledOnTouchOutside(false);
				dialog.setTitle(attribLable);
				final ListView mutilist = (ListView) dialog.findViewById(R.id.listviewAnswer);
				final CheckBoxAdapter multiadaptor=new CheckBoxAdapter(context,R.layout.multiselect_content,list,optionsList,selectedIdCheckboxtemp,selectedIdEdittxttemp,checkedOPtionsIdStr);
				mutilist.setAdapter(multiadaptor);
				Button button = (Button) dialog.findViewById(R.id.btn_SubmitAnswer);
				button.setOnClickListener(new OnClickListener()
				{
					public void onClick(View v)
					{

						ArrayList<String> selectedIdCheckbox=new ArrayList<String>();
						ArrayList<String> selectedCheckboxText=new ArrayList<String>();
						StringBuilder optiontextselected=new StringBuilder();
						StringBuilder optionSelectedId=new StringBuilder();
						Boolean dialog_status=true;

						for (int i = 0; i < optionsList.size(); i++)
						{
							LinearLayout view = (LinearLayout) mutilist.getChildAt(i);
							if(dialog_status==false)
								break;
							if(view!=null)
								for (int j = 0; j < optionsList.size(); j++)
								{
									try
									{
										View child = view.getChildAt(j);

										if(child instanceof CheckBox)
										{
											CheckBox cb=(CheckBox)child;
											if(cb.isChecked() )
											{
												String str =cb.getText().toString().trim();
												selectedIdCheckbox.add(optionsList.get(i).getOptionId().toString());
												selectedCheckboxText.add(optionsList.get(i).getOptionName());
												optiontextselected.append(optionsList.get(i).getOptionName());
												optiontextselected.append(",");
												optionSelectedId.append(optionsList.get(i).getOptionId());
												optionSelectedId.append(",");
											}
										}
									}
									catch(Exception e){

										e.getStackTrace();
									}
								}
							selectedOption.setText(selectedCheckboxText.toString());
							selectedOptionIds.setText(optionSelectedId.toString());
							if((optionSelectedId.toString()).contains("58"))
								otherExistingUSe.setVisibility(View.VISIBLE);
							else
								otherExistingUSe.setVisibility(View.GONE);

							dialog.dismiss();

						}
					}
				});
				dialog.show();

			}
		});
	}

	private View createInputRow(View container, String attribLable,String AnsText,final int currentposition,boolean error)
	{
		TextView field=(TextView)container.findViewById(R.id.field);
		field.setText(attribLable);
		final EditText fieldValue=(EditText)container.findViewById(R.id.fieldValue);
		fieldValue.setTag(attribList.get(currentposition).getAttributeid());
		String mandatory_fields=context.getResources().getString(R.string.fill_mandatory);

		if(roleId==2)
		  {
			fieldValue.setEnabled(false);
		  }

		if (AnsText != null)
		{
			fieldValue.setText(AnsText.toString(), BufferType.EDITABLE);
		} else
		{
			fieldValue.setEnabled(true);
			fieldValue.setText("", BufferType.EDITABLE);
		}

		if (error)
		{
			fieldValue.setError(mandatory_fields);
		}

		fieldValue.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {


			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {


			}

			@Override
			public void afterTextChanged(Editable s) {

				String valide_Entry = context.getResources().getString(R.string.val_entry);
				   String str = s.toString();
		            if(str.length() > 0 && str.startsWith(" ")){
		            	fieldValue.setError(valide_Entry);
		                fieldValue.setText("");
		            }


			}
		});

		fieldValue.setOnFocusChangeListener(new OnFocusChangeListener()
		{
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus)
				{
					try
					{
						EditText editTxt = (EditText) v;
						Integer attribId = (Integer) editTxt.getTag();
						if(attribList.get(currentposition).getAttributeid() == attribId)
						{
							attribList.get(currentposition).setFieldValue(editTxt.getText().toString());
						}
					}catch(Exception e){}
				}
			}
		});

		fieldValue.addTextChangedListener(new TextWatcher()
		{

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}

			@Override
			public void afterTextChanged(Editable s) {
				try
				{
					Integer attribId = (Integer) fieldValue.getTag();
					if(attribList.get(currentposition).getAttributeid() == attribId)
					{
						attribList.get(currentposition).setFieldValue(s.toString());
					}
				}catch(Exception e){}
			}
		});



		return fieldValue;
	}

	private View createTimePickerRow(View container, String attribLable,String AnsText,final int currentposition,boolean error)
	{
		final String dateSelected = AnsText;
		TextView field=(TextView)container.findViewById(R.id.field);
		field.setText(attribLable);
		String notApplicableStr=context.getResources().getString(R.string.not_applicable);

		final TextView textdatepicker=(TextView)container.findViewById(R.id.textview_datepicker);
		textdatepicker.setTag(attribList.get(currentposition).getAttributeid());

		if (attribList.get(currentposition).getAttributeid()==294) {
			long powerOfApplication = cf.getPowerOfApplication();
			if (powerOfApplication == 122l || powerOfApplication == 124l) {
				textdatepicker.setText("");
				textdatepicker.setHint(notApplicableStr);
				textdatepicker.setText(notApplicableStr);
			}
		}
		if(roleId==2)
		  {
			textdatepicker.setEnabled(false);

		  }

		if (error)
		{
			textdatepicker.setBackgroundColor(context.getResources().getColor(color.lightred));
		}

		//SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calender = Calendar.getInstance();
		if (dateSelected != null)
		{
			try {
				calender.setTime(sdf.parse(dateSelected));
				int   day  = calender.get(Calendar.DAY_OF_MONTH);
				String strday = day < 10 ? "0" + String.valueOf(day) : String.valueOf(day);
				int   month= calender.get(Calendar.MONTH) +1; // adding 1 as month starts with 0
				String strmonth = month < 10 ? "0" + String.valueOf(month) : String.valueOf(month);
				int   year = calender.get(Calendar.YEAR);


				textdatepicker.setText(new StringBuilder()
				.append(year).append("-").append(strmonth).append("-").append(strday));
			} catch (ParseException e) {
				long powerOfApplication1=cf.getPowerOfApplication();
				if( attribList.get(currentposition).getAttributeid()==294 &&  (powerOfApplication1==122l || powerOfApplication1==124l))
				{
					textdatepicker.setText(notApplicableStr);
					textdatepicker.setHint(notApplicableStr);

				}
					e.printStackTrace();
			}


		}



		textdatepicker.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String notApplicableStr=context.getResources().getString(R.string.not_applicable);
				long powerOfApplication=cf.getPowerOfApplication();
				if( attribList.get(currentposition).getAttributeid()==294 &&  (powerOfApplication==122l || powerOfApplication==124l))
				{
					textdatepicker.setText(notApplicableStr);
					textdatepicker.setHint(notApplicableStr);

				}
				else {
					final Dialog custom_timepicker = new Dialog(context,
							R.style.DialogTheme);
					custom_timepicker.setTitle(R.string.selectDate);
					custom_timepicker
							.setContentView(R.layout.dialog_time_picker);
					custom_timepicker.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
					final DatePicker datepicker = (DatePicker) custom_timepicker
							.findViewById(R.id.datePicker);
					Button btnSet = (Button) custom_timepicker
							.findViewById(R.id.button_set);
					try {
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd");
						Calendar calender = Calendar.getInstance();
						if (dateSelected != null) {
							calender.setTime(sdf.parse(dateSelected));
						}
						int day = calender.get(Calendar.DAY_OF_MONTH);
						String strday = day < 10 ? "0" + String.valueOf(day)
								: String.valueOf(day);
						int month = calender.get(Calendar.MONTH) + 1;
						String strmonth = month < 10 ? "0"
								+ String.valueOf(month) : String.valueOf(month);
						int year = calender.get(Calendar.YEAR);
						datepicker.init(year, month, day, null);
						if (dateSelected != null)
							textdatepicker.setText(new StringBuilder()
									.append(year).append("-").append(strmonth)
									.append("-").append(strday));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					custom_timepicker.show();
					btnSet.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							int day = datepicker.getDayOfMonth();
							String strday = day < 10 ? "0"
									+ String.valueOf(day) : String.valueOf(day);
							int month = datepicker.getMonth() + 1;
							String strmonth = month < 10 ? "0"
									+ String.valueOf(month) : String
									.valueOf(month);
							int year = datepicker.getYear();

							textdatepicker.setText(new StringBuilder()
									.append(year).append("-").append(strmonth)
									.append("-").append(strday));
							custom_timepicker.dismiss();
						}
					});
				}
			}
		});


		textdatepicker.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}

			@Override
			public void afterTextChanged(Editable s) {
				try
				{
					Integer attribId = (Integer) textdatepicker.getTag();
					if(attribList.get(currentposition).getAttributeid() == attribId)
					{
						attribList.get(currentposition).setFieldValue(s.toString());
					}
				}catch(Exception e){}
			}
		});



		return textdatepicker;
	}




	Spinner createSpinnerViewFromArray(View container,List<Option> values,String attribLable,String selectedAns,final int position,boolean error)
	{
		TextView fieldAlias = (TextView) container.findViewById(R.id.field);

		final Spinner spinner = (Spinner) container.findViewById(R.id.spinner1);
		fieldAlias.setText(attribLable);
		spinner.setPrompt(attribLable);
		spinner.setTag(attribList.get(position).getAttributeid());
		SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this.context,android.R.layout.simple_spinner_item, values);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerAdapter);

		if(roleId==2)  // (1=Trusted Intermediary, 2=Adjudicator)
		  {
			spinner.setEnabled(false);

		  }



		boolean isPersonExist=CommonFunctions.personExist(featureId);

		if(attribList.get(position).getAttributeid()==31)
		{
		if(isPersonExist)
		  {
			spinner.setEnabled(false);

		  }
		}

		if (error)
		{
			spinner.setBackgroundColor(context.getResources().getColor(color.lightred));
			
		}
		
		
		
		if (selectedAns != null && selectedAns != "" && !selectedAns.equalsIgnoreCase("Select an option")) 
		{
			int currentValue = Integer.parseInt(selectedAns);
			spinner.setSelection(spinnerAdapter.getPosition(currentValue));
		}
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
			
				Option selecteditem = (Option) spinner.getSelectedItem();
				attribList.get(position).setFieldValue(selecteditem.getOptionId().toString());
				if(attribList.get(position).getAttributeid()==269)
				{
					cf.savePowerOfApplication(selecteditem.getOptionId());
					
					
							
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		
		return spinner;
	}
	
	Spinner createSpinnerViewForBoolean(View container,String attribLable,String selectedAns,final int position) 
	{
		TextView fieldAlias = (TextView) container.findViewById(R.id.field);

		final Spinner spinner = (Spinner) container.findViewById(R.id.spinner1);
		fieldAlias.setText(attribLable);
		spinner.setPrompt(attribLable);

		if(roleId==2)
		  {
			spinner.setEnabled(false);
			
		  }
		
		String[] list = context.getResources().getStringArray(R.array.booleanControlValues);
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this.context,android.R.layout.simple_spinner_item, list);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerAdapter);
		
		if (selectedAns != null && selectedAns != "" && (selectedAns!="Select an option" || !selectedAns.equalsIgnoreCase("Chagua chaguo"))) 
		{
			if(selectedAns.equalsIgnoreCase("yes")|| selectedAns.equalsIgnoreCase("Ndiyo"))  
			spinner.setSelection(0);
			if(selectedAns.equalsIgnoreCase("no") || selectedAns.equalsIgnoreCase("Hapana"))
				spinner.setSelection(1);
		}
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
			
				String selecteditem = (String) spinner.getSelectedItem();
				attribList.get(position).setFieldValue(selecteditem);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
		
				
			}
		});
		return spinner;
	}
	
	
	public void setErrorList(List<Integer> errorList) 
	{
		this.errorList = errorList;
	}
	
}
