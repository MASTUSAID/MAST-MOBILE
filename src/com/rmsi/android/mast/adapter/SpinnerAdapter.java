package com.rmsi.android.mast.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.domain.Option;



//custom adapter
public class SpinnerAdapter extends ArrayAdapter<Option>
{

   private Context context;
   private List<Option> myObjs;

   public SpinnerAdapter(Context context, int textViewResourceId, List<Option> values) 
   {
       super(context, textViewResourceId, values);
       this.context = context;
       this.myObjs = values;
   }

   public int getCount(){
      return myObjs.size();
   }

   public Option getItem(int position)
   {
      return myObjs.get(position);
   }

   public long getItemId(int position)
   {
      return myObjs.get(position).getOptionId();
   }

   @Override
   public View getView(int position, View convertView, ViewGroup parent) 
   {

	   TextView label = null;

	   if(convertView==null)
	   {   
		   label = new TextView(context);
	   }
	   else
	   {
		   label = (TextView) convertView;
	   }
	   
	   label.setText(myObjs.get(position).getOptionName());
	   label.setTextSize(20);
	   return label;
   }

   @Override
   public View getDropDownView(int position, View convertView,ViewGroup parent) 
   {
       TextView label = new TextView(context);
       label.setText(myObjs.get(position).getOptionName());
       label.setTextSize(22);
       label.setTextColor(context.getResources().getColor(R.color.black));
       return label;
   }

	public int getPosition(int optionId) 
	{
		for (int i = 0; i < myObjs.size(); i++) 
		{
			if(myObjs.get(i).getOptionId() == optionId)
			{
				return i;
			}			
		}
		return 0;		
	}
}
