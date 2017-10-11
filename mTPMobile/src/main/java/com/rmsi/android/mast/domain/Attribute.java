package com.rmsi.android.mast.domain;

import java.util.ArrayList;
import java.util.List;

import android.view.View;

public class Attribute 
{
	private int ATTRIB_ID;
	private String ATTRIBUTE_TYPE;
	private int ATTRIBUTE_CONTROLTYPE;
	private String ATTRIBUTE_NAME;
	private View view;
	private String field_value;
	private int GROUP_ID;
	private int listing;
	private String PARCEL_TYPE;
	List<Option> OptionsList = new ArrayList<Option>();
	private int PERSON_ID;
	private String optionText;	
	private long DATE,FEATURE_ID;
	private String VALIDATION;
	private String nextOfKinName;
	private int NextOfKinId;
	private int HamletId;
	private String witness1,witness2,HamletName,personSubType;
	
	

	public String getHamletName() {
		return HamletName;
	}

	public void setHamletName(String hamletName) {
		HamletName = hamletName;
	}

	public int getHamletId() {
		return HamletId;
	}

	public void setHamletId(int hamletId) {
		HamletId = hamletId;
	}

	public String getWitness1() {
		return witness1;
	}

	public void setWitness1(String witness1) {
		this.witness1 = witness1;
	}

	public String getWitness2() {
		return witness2;
	}

	public void setWitness2(String witness2) {
		this.witness2 = witness2;
	}

	public int getAttributeid() 
	{
		return ATTRIB_ID;
	}
	
	public void setAttributeid(int ATTRIB_ID) 
	{
		this.ATTRIB_ID = ATTRIB_ID;
	}
	public String getNextOfKinName() {
		return nextOfKinName;
	}

	public void setNextOfKinName(String nextOfKinName) {
		this.nextOfKinName = nextOfKinName;
	}

	public int getNextOfKinId() {
		return NextOfKinId;
	}

	public void setNextOfKinId(int NextOfKinId) {
		this.NextOfKinId = NextOfKinId;
	}

	public String getAttributeType() 
	{
		return ATTRIBUTE_TYPE;
	}
	public void setAttributeType(String ATTRIBUTE_TYPE) 
	{
		this.ATTRIBUTE_TYPE = ATTRIBUTE_TYPE;
	}
	public int getControlType() 
	{
		return ATTRIBUTE_CONTROLTYPE;
	}
	public void setControlType(int ATTRIBUTE_CONTROLTYPE) 
	{
		this.ATTRIBUTE_CONTROLTYPE = ATTRIBUTE_CONTROLTYPE;
	}
	public String getAttributeName() 
	{
		return ATTRIBUTE_NAME;
	}
	public void setAttributeName(String ATTRIBUTE_NAME) 
	{
		this.ATTRIBUTE_NAME = ATTRIBUTE_NAME;
	}
	public View getView() {
		return view;
	}
	public void setView(View view) {
		this.view = view;
	}
	
	public List<Option> getOptionsList() {
		return OptionsList;
	}
	public void setOptionsList(List<Option> OptionsList) {
		this.OptionsList = OptionsList;
	}
	
	public String getFieldValue() 
	{
		return field_value;
	}
	public void setFieldValue(String field_value) 
	{
		this.field_value = field_value;
	}
	//GROUP_ID
	
	public int getGroupId() 
	{
		return GROUP_ID;
	}
	public void setGroupId(int GROUP_ID) 
	{
		this.GROUP_ID = GROUP_ID;
	}
	public int getListing() {
		return listing;
	}
	public void setListing(int listing) {
		this.listing = listing;
	}
	
	public String getParcelType() 
	{
		return PARCEL_TYPE;
	}
	public void setParcelType(String PARCEL_TYPE) 
	{
		this.PARCEL_TYPE = PARCEL_TYPE;
	}
	public int getPersonId() 
	{
		return PERSON_ID;
	}
	public void setPeronId(int PERSON_ID) 
	{
		this.PERSON_ID = PERSON_ID;
	}
	public String getOptionText() {
		return optionText;
	}
	public void setOptionText(String optionText) {
		this.optionText = optionText;
	}
	public long getDATE() {
		return DATE;
	}
	public void setDATE(long DATE) {
		this.DATE = DATE;
	}

	public String getValidation() {
		return VALIDATION;
	}

	public void setValidation(String vALIDATION) {
		VALIDATION = vALIDATION;
	}
	
	public String getPersonSubType() {
		return personSubType;
	}

	public void setPersonSubType(String personSubType) {
		this.personSubType = personSubType;
	}

	public long getFEATURE_ID() {
		return FEATURE_ID;
	}

	public void setFEATURE_ID(long fEATURE_ID) {
		FEATURE_ID = fEATURE_ID;
	}

	public void setView(String[] valueView1) {
		// TODO Auto-generated method stub
		
	}
	
	
}


