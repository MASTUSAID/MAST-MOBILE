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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.Option;
import com.rmsi.android.mast.util.CommonFunctions;

/**
 * @author Amreen.S
 * 
 */
public class AttributeAdapter extends BaseAdapter {

	List<Attribute> attribList;
	Context context;
	LayoutInflater lInflator;

	int roleId = CommonFunctions.getRoleID();
	private List<Integer> errorList = new ArrayList<Integer>();

	public AttributeAdapter(Context contextAct, List<Attribute> attrib) {
		this.context = contextAct;
		this.lInflator = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.attribList = attrib;

	}

	private View createInputRow(View container, String attribLable,
			String AnsText, final int currentposition, boolean error) {
		TextView field = (TextView) container.findViewById(R.id.field);
		field.setText(attribLable);
		final EditText fieldValue = (EditText) container
				.findViewById(R.id.fieldValue);
		fieldValue.setTag(attribList.get(currentposition).getAttributeid());

		if (roleId == 2) {
			fieldValue.setEnabled(false);

		}

		if (AnsText != null) {
			fieldValue.setText(AnsText.toString(), BufferType.EDITABLE);
		} else {
			fieldValue.setEnabled(true);
			fieldValue.setText("", BufferType.EDITABLE);
		}

		if (error) {
			fieldValue.setBackgroundColor(context.getResources().getColor(
					R.color.lightred));
		}

		fieldValue.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					try {
						EditText editTxt = (EditText) v;
						Integer attribId = (Integer) editTxt.getTag();
						if (attribList.get(currentposition).getAttributeid() == attribId) {
							attribList.get(currentposition).setFieldValue(
									editTxt.getText().toString());
						}
					} catch (Exception e) {
					}
				}
			}
		});

		fieldValue.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				try {
					Integer attribId = (Integer) fieldValue.getTag();
					if (attribList.get(currentposition).getAttributeid() == attribId) {
						attribList.get(currentposition).setFieldValue(
								s.toString());
					}
				} catch (Exception e) {
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});

		return fieldValue;
	}

	Spinner createSpinnerViewForBoolean(View container, String attribLable,
			String selectedAns, final int position) {
		TextView fieldAlias = (TextView) container.findViewById(R.id.field);

		final Spinner spinner = (Spinner) container.findViewById(R.id.spinner1);
		fieldAlias.setText(attribLable);
		spinner.setPrompt(attribLable);

		if (roleId == 2) {
			spinner.setEnabled(false);

		}

		String[] list = context.getResources().getStringArray(
				R.array.booleanControlValues);
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
				this.context, android.R.layout.simple_spinner_item, list);
		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerAdapter);

		if (selectedAns != null && selectedAns != ""
				&& selectedAns != "Select an option") {
			if (selectedAns.equalsIgnoreCase("yes")
					|| selectedAns.equalsIgnoreCase("Ndiyo"))
				spinner.setSelection(0);
			if (selectedAns.equalsIgnoreCase("no")
					|| selectedAns.equalsIgnoreCase("Hapana"))
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

	Spinner createSpinnerViewFromArray(View container, List<Option> values,
			String attribLable, String selectedAns, final int position,
			boolean error) {
		TextView fieldAlias = (TextView) container.findViewById(R.id.field);

		final Spinner spinner = (Spinner) container.findViewById(R.id.spinner1);
		fieldAlias.setText(attribLable);
		spinner.setPrompt(attribLable);
		spinner.setTag(attribList.get(position).getAttributeid());
		SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this.context,
				android.R.layout.simple_spinner_item, values);
		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerAdapter);

		if (roleId == 2) // Hardcoded Id for Role (1=Trusted Intermediary,
							// 2=Adjudicator)
		{
			spinner.setEnabled(false);

		}

		if (error) {
			spinner.setBackgroundColor(context.getResources().getColor(
					R.color.lightred));
		}

		if (selectedAns != null && selectedAns != ""
				&& selectedAns != "Select an option") {
			int currentValue = Integer.parseInt(selectedAns);
			spinner.setSelection(spinnerAdapter.getPosition(currentValue));
		}
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				Option selecteditem = (Option) spinner.getSelectedItem();
				attribList.get(position).setFieldValue(
						selecteditem.getOptionId().toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		return spinner;
	}

	private View createTimePickerRow(View container, String attribLable,
			String AnsText, final int currentposition, boolean error) {
		final String dateSelected = AnsText;
		TextView field = (TextView) container.findViewById(R.id.field);
		field.setText(attribLable);

		final TextView textdatepicker = (TextView) container
				.findViewById(R.id.textview_datepicker);
		textdatepicker.setTag(attribList.get(currentposition).getAttributeid());

		if (roleId == 2) {
			textdatepicker.setEnabled(false);

		}

		if (error) {
			textdatepicker.setBackgroundColor(context.getResources().getColor(
					R.color.lightred));
		}

		// SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calender = Calendar.getInstance();
		if (dateSelected != null) {
			try {
				calender.setTime(sdf.parse(dateSelected));
			} catch (ParseException e) {

				e.printStackTrace();
			}

			int day = calender.get(Calendar.DAY_OF_MONTH);
			String strday = day < 10 ? "0" + String.valueOf(day) : String
					.valueOf(day);
			int month = calender.get(Calendar.MONTH) + 1; // adding 1 as month
															// starts with 0
			String strmonth = month < 10 ? "0" + String.valueOf(month) : String
					.valueOf(month);
			int year = calender.get(Calendar.YEAR);

			textdatepicker.setText(new StringBuilder().append(year).append("-")
					.append(strmonth).append("-").append(strday));
		}

		textdatepicker.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Dialog custom_timepicker = new Dialog(context,
						R.style.DialogTheme);
				custom_timepicker.setTitle("Select Date");
				custom_timepicker.setContentView(R.layout.dialog_time_picker);
				custom_timepicker.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
				final DatePicker datepicker = (DatePicker) custom_timepicker
						.findViewById(R.id.datePicker);

				Button btnSet = (Button) custom_timepicker
						.findViewById(R.id.button_set);

				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Calendar calender = Calendar.getInstance();
					if (dateSelected != null) {
						calender.setTime(sdf.parse(dateSelected));
					}
					int day = calender.get(Calendar.DAY_OF_MONTH);
					String strday = day < 10 ? "0" + String.valueOf(day)
							: String.valueOf(day);
					int month = calender.get(Calendar.MONTH) + 1;
					String strmonth = month < 10 ? "0" + String.valueOf(month)
							: String.valueOf(month);
					int year = calender.get(Calendar.YEAR);
					datepicker.init(year, month, day, null);
					if (dateSelected != null)
						textdatepicker.setText(new StringBuilder().append(year)
								.append("-").append(strmonth).append("-")
								.append(strday));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				custom_timepicker.show();

				btnSet.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						int day = datepicker.getDayOfMonth();
						String strday = day < 10 ? "0" + String.valueOf(day)
								: String.valueOf(day);
						int month = datepicker.getMonth() + 1;
						String strmonth = month < 10 ? "0"
								+ String.valueOf(month) : String.valueOf(month);
						int year = datepicker.getYear();

						textdatepicker.setText(new StringBuilder().append(year)
								.append("-").append(strmonth).append("-")
								.append(strday));
						custom_timepicker.dismiss();
					}
				});
			}
		});

		textdatepicker.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				try {
					Integer attribId = (Integer) textdatepicker.getTag();
					if (attribList.get(currentposition).getAttributeid() == attribId) {
						attribList.get(currentposition).setFieldValue(
								s.toString());
					}
				} catch (Exception e) {
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});

		return textdatepicker;
	}

	@Override
	public int getCount() {
		return attribList.size();
	}

	@Override
	public Object getItem(int position) {
		return attribList.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View container = null;
		Attribute attribItem = (Attribute) getItem(position);
		String attribLable = attribItem.getAttributeName();
		boolean error = false;

		View valueView = null;

		if (attribItem.getControlType() == 1) // Edittext(string)
		{
			if (errorList.contains(attribItem.getAttributeid())) {
				error = true;

			}

			String FieldValue = attribItem.getFieldValue();
			container = lInflator.inflate(R.layout.item_edit_text, parent,
					false);
			valueView = createInputRow(container, attribLable, FieldValue,
					position, error);

			attribItem.setView(valueView);
		} else if (attribItem.getControlType() == 2) // Date
		{
			if (errorList.contains(attribItem.getAttributeid())) {
				error = true;
			}

			String selectedDate = attribItem.getFieldValue();
			container = lInflator.inflate(R.layout.item_date, parent, false);
			valueView = createTimePickerRow(container, attribLable,
					selectedDate, position, error);
			attribItem.setView(valueView);
		} else if (attribItem.getControlType() == 3) // Boolean
		{

			if (errorList.contains(attribItem.getAttributeid())) {
				error = true;
			}

			String selectedAns = attribItem.getFieldValue(); // Spinner
			container = lInflator.inflate(R.layout.item_spinner, parent, false);
			Spinner spinner = createSpinnerViewForBoolean(container,
					attribLable, selectedAns, position);
			attribItem.setView(spinner);
		} else if (attribItem.getControlType() == 4) // Edittext(Numeric)
		{
			if (errorList.contains(attribItem.getAttributeid())) {
				error = true;
			}

			String FieldValue = attribItem.getFieldValue();
			container = lInflator.inflate(R.layout.item_edittext_numeric,
					parent, false);
			valueView = createInputRow(container, attribLable, FieldValue,
					position, error);

			attribItem.setView(valueView);
		} else if (attribItem.getControlType() == 5) // spinner
		{
			if (errorList.contains(attribItem.getAttributeid())) {
				error = true;
			}
			String selectedAns = attribItem.getFieldValue();

			container = lInflator.inflate(R.layout.item_spinner, parent, false);
			Spinner spinner = createSpinnerViewFromArray(container,
					attribItem.getOptionsList(), attribLable, selectedAns,
					position, error);
			attribItem.setView(spinner);
		}
		return container;
	}

	public void setErrorList(List<Integer> errorList) {
		this.errorList = errorList;
	}

}
