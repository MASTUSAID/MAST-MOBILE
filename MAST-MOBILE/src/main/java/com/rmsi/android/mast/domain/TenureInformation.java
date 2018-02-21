package com.rmsi.android.mast.domain;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.DateUtility;
import com.rmsi.android.mast.util.StringUtility;

import java.io.Serializable;

/**
 * Created by ambar.srivastava on 12/22/2017.
 */

public class TenureInformation implements Serializable {
    private String firstName;
    private String middleName;
    private String lastName;
    private String age;
    private String gender;
    private String maritalstatus;
    private String cityzenship;
    private String ethinicity;
    private String residential;
    private String address;
    private String country;
    private String community;
    private String region;
    private String mob_no;

    public String getMob_no() {
        return mob_no;
    }

    public void setMob_no(String mob_no) {
        this.mob_no = mob_no;
    }




    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }


    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }




    public String getResidential() {
        return residential;
    }

    public void setResidential(String residential) {
        this.residential = residential;
    }



    public String getEthinicity() {
        return ethinicity;
    }

    public void setEthinicity(String ethinicity) {
        this.ethinicity = ethinicity;
    }



    public String getCityzenship() {
        return cityzenship;
    }

    public void setCityzenship(String cityzenship) {
        this.cityzenship = cityzenship;
    }




    public String getMaritalstatus() {
        return maritalstatus;
    }

    public void setMaritalstatus(String maritalstatus) {
        this.maritalstatus = maritalstatus;
    }




    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }




    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }




    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }




    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }



    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddelName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public TenureInformation(){

    }

    public boolean validateBasicInfo(Context context, boolean b) {
        boolean result = true;
        String errorMessage = "";

        if (StringUtility.isEmpty(getFirstName())) {
            errorMessage = context.getResources().getString(R.string.SelectFirstName);
        } else if (StringUtility.isEmpty(getMiddelName())) {
            errorMessage = context.getResources().getString(R.string.SelectMiddleName);
        } else if (StringUtility.isEmpty(getLastName())) {
            errorMessage = context.getResources().getString(R.string.SelectLastName);
        }
        else if (StringUtility.isEmpty(getGender())) {
            errorMessage = context.getResources().getString(R.string.SelectGender);
        }else if (getGender().equalsIgnoreCase(context.getResources().getString(R.string.SelectOption))) {
            errorMessage = context.getResources().getString(R.string.SelectGender);
        }
        else if (StringUtility.isEmpty(getAge())) {
            errorMessage = context.getResources().getString(R.string.SelectAge);
        } else if (StringUtility.isEmpty(getMaritalstatus())) {
            errorMessage = context.getResources().getString(R.string.SelectMarital);
        }else if (getMaritalstatus().equalsIgnoreCase(context.getResources().getString(R.string.SelectOption))) {
            errorMessage = context.getResources().getString(R.string.SelectMarital);
        }
        else if (StringUtility.isEmpty(getCityzenship())) {
            errorMessage = context.getResources().getString(R.string.SelectCitizenship);
        } else if (StringUtility.isEmpty(getEthinicity())) {
            errorMessage = context.getResources().getString(R.string.SelectEthinicity);
        }
        else if (StringUtility.isEmpty(getResidential())) {
            errorMessage = context.getResources().getString(R.string.SelectResidency);
        }else if (getResidential().equalsIgnoreCase(context.getResources().getString(R.string.SelectOption))) {
            errorMessage = context.getResources().getString(R.string.SelectResidency);
        }
        else if (StringUtility.isEmpty(getAddress())) {
            errorMessage = context.getResources().getString(R.string.SelectAddress);
        }

        else if (StringUtility.isEmpty(getCommunity())) {
            errorMessage ="Enter Community";
        }

        else if (StringUtility.isEmpty(getRegion())) {
            errorMessage = context.getResources().getString(R.string.SelectRegion);
        }

        else if (StringUtility.isEmpty(getCountry())) {
            errorMessage = context.getResources().getString(R.string.SelectCountry);
        }

        else if (StringUtility.isEmpty(getMob_no())) {
            errorMessage = context.getResources().getString(R.string.SelectMobileNumber);
        }

        if (!errorMessage.equals("")) {
            result = false;
            if (b)
                CommonFunctions.getInstance().showToast(context, errorMessage, Toast.LENGTH_LONG, Gravity.CENTER);
        }
        return result;
    }
}
