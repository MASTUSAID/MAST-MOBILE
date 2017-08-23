package com.rmsi.android.mast.domain;

import java.util.ArrayList;
import java.util.List;

import android.view.View;

public class Attribute {
	private int ATTRIB_ID;
	private String ATTRIBUTE_TYPE;
	private int ATTRIBUTE_CONTROLTYPE;
	private String ATTRIBUTE_NAME;
	private View view;
	private String field_value;
	private int GROUP_ID;
	private int listing;
	private String PERSON_TYPE;
	List<Option> OptionsList = new ArrayList<Option>();
	private int PERSON_ID;
	private String optionText;
	private long DATE;
	private String VALIDATION;

	public int getAttributeid() {
		return ATTRIB_ID;
	}

	public String getAttributeName() {
		return ATTRIBUTE_NAME;
	}

	public String getAttributeType() {
		return ATTRIBUTE_TYPE;
	}

	public int getControlType() {
		return ATTRIBUTE_CONTROLTYPE;
	}

	public long getDATE() {
		return DATE;
	}

	public String getFieldValue() {
		return field_value;
	}

	public int getGroupId() {
		return GROUP_ID;
	}

	public int getListing() {
		return listing;
	}

	public List<Option> getOptionsList() {
		return OptionsList;
	}

	public String getOptionText() {
		return optionText;
	}

	public int getPersonId() {
		return PERSON_ID;
	}

	public String getPersonType() {
		return PERSON_TYPE;
	}

	public String getValidation() {
		return VALIDATION;
	}

	public View getView() {
		return view;
	}

	// GROUP_ID

	public void setAttributeid(int ATTRIB_ID) {
		this.ATTRIB_ID = ATTRIB_ID;
	}

	public void setAttributeName(String ATTRIBUTE_NAME) {
		this.ATTRIBUTE_NAME = ATTRIBUTE_NAME;
	}

	public void setAttributeType(String ATTRIBUTE_TYPE) {
		this.ATTRIBUTE_TYPE = ATTRIBUTE_TYPE;
	}

	public void setControlType(int ATTRIBUTE_CONTROLTYPE) {
		this.ATTRIBUTE_CONTROLTYPE = ATTRIBUTE_CONTROLTYPE;
	}

	public void setDATE(long DATE) {
		this.DATE = DATE;
	}

	public void setFieldValue(String field_value) {
		this.field_value = field_value;
	}

	public void setGroupId(int GROUP_ID) {
		this.GROUP_ID = GROUP_ID;
	}

	public void setListing(int listing) {
		this.listing = listing;
	}

	public void setOptionsList(List<Option> OptionsList) {
		this.OptionsList = OptionsList;
	}

	public void setOptionText(String optionText) {
		this.optionText = optionText;
	}

	public void setPeronId(int PERSON_ID) {
		this.PERSON_ID = PERSON_ID;
	}

	public void setPersonType(String PERSON_TYPE) {
		this.PERSON_TYPE = PERSON_TYPE;
	}

	public void setValidation(String vALIDATION) {
		VALIDATION = vALIDATION;
	}

	public void setView(View view) {
		this.view = view;
	}

}
