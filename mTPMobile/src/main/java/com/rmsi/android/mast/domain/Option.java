package com.rmsi.android.mast.domain;

import android.view.View;

public class Option 
{
	private Long optionId; 
	private int ATTRIB_ID;
	private String OPTION_NAME;
	private String optionMultiLang;
	private View view;

	public Long getOptionId() {
		return optionId;
	}
	public void setOptionId(Long optionId) {
		this.optionId = optionId;
	}
	public int getAttributeid() 
	{
		return ATTRIB_ID;
	}
	public void setAttributeid(int ATTRIB_ID) 
	{
		this.ATTRIB_ID = ATTRIB_ID;
	}
	public String getOptionName() 
	{
		return OPTION_NAME;
	}
	public void setOptionName(String OPTION_NAME) 
	{
		this.OPTION_NAME = OPTION_NAME;
	}
	public View getView() {
		return view;
	}
	public void setView(View view) {
		this.view = view;
	}
	public String getOptionMultiLang() {
		return optionMultiLang;
	}
	public void setOptionMultiLang(String optionMultiLang) {
		this.optionMultiLang = optionMultiLang;
	}
}
