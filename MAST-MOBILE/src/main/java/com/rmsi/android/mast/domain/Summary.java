package com.rmsi.android.mast.domain;

import android.view.View;

import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.StringUtility;

import java.io.Serializable;

/**
 * Created by Ambar.Srivastava on 1/5/2018.
 */

public class Summary implements Serializable
{

    private String nameLabel;
    private String value;
    transient private View view;


    public String getnameLabel()
    {
        return nameLabel;
    }
    public void setnameLabel(String nameLabel)
    {
        this.nameLabel = nameLabel;
    }

    public String getvalue() {
        return value;
    }
    public void setvalue(String value) {
        this.value = value;
    }

    @Override
    public String toString(){
        if(getnameLabel()!=null){
            return StringUtility.empty(getnameLabel());
        }
        return StringUtility.empty(getvalue());
    }
}
