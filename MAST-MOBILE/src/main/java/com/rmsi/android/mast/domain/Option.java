package com.rmsi.android.mast.domain;

import android.view.View;

import com.rmsi.android.mast.util.StringUtility;

import java.io.Serializable;

public class Option  implements Serializable
{
	private Long id;
	private Long attributeId;
	private String name;
	private String nameOtherLang;
	transient private View view;

	public static String TABLE_NAME = "OPTIONS";
	public static String COL_ID = "OPTION_ID";
	public static String COL_ATTRIBUTE_ID = "ATTRIB_ID";
	public static String COL_NAME = "OPTION_NAME";
	public static String COL_NAME_OTHER_LANG = "OPTION_NAME_OTHER";

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getAttributeId()
	{
		return attributeId;
	}
	public void setAttributeId(Long attributeId)
	{
		this.attributeId = attributeId;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public View getView() {
		return view;
	}
	public void setView(View view) {
		this.view = view;
	}
	public String getNameOtherLang() {
		return nameOtherLang;
	}
	public void setNameOtherLang(String nameOtherLang) {
		this.nameOtherLang = nameOtherLang;
	}

    @Override
	public String toString(){
		return StringUtility.empty(getName());
	}
}
